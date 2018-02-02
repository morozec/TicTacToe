package com.example.moro2609.tictacktoe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static final int BUTTON_WIDTH = 100;
    public static final int BUTTON_HEIGHT = 100;
    public static final String IS_X = "isX";
    public static final String PLAYER_INDEX = "playerIndex";
    public static final int COLUMNS_COUNT = 36;
    public static final int ROWS_COUNT = 54;

    //private static int width;
    //private static int height;
    //private int rowsCount;
    //private int columnsCount;

    private GameManager gameManager;
    private FirebaseManager firebaseManager;



    @Override
    protected void onDestroy() {
        int playerIndex = gameManager.getPlayerIndex();
        if (playerIndex == 0 || playerIndex == 1){
            firebaseManager.updatePlayer(gameManager.getPlayerIndex(), false);
        }
        gameManager.initData();
        super.onDestroy();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //final Context context = getApplicationContext();
        //initWidthAndHeight(context);

        //columnsCount = COLUMNS_Count;
        //rowsCount = ROWS_COUNT;

        firebaseManager = new FirebaseManager();

        ChildEventListener childEventListener = new ChildEventListener() {

            private void UpdateData(DataSnapshot dataSnapshot){
                String key = dataSnapshot.getKey();
                if (Objects.equals(key, IS_X)) {
                    String value = dataSnapshot.getValue().toString();
                    gameManager.setIsXMove(Boolean.valueOf(value));
                }

            }

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                UpdateData(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                UpdateData(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        ChildEventListener playersChildEventListener = new ChildEventListener() {


            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                int playerId = Integer.parseInt(dataSnapshot.getKey());
                boolean isConnected = Boolean.parseBoolean(dataSnapshot.getValue().toString());
                gameManager.updateIsPlayerConnected(playerId, isConnected);

                if (!isConnected && gameManager.getPlayerIndex() == -1){
                    gameManager.setPlayerIndex(playerId);
                    firebaseManager.updatePlayer(playerId, true);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int playerId = Integer.parseInt(dataSnapshot.getKey());
                boolean isConnected = Boolean.parseBoolean(dataSnapshot.getValue().toString());
                gameManager.updateIsPlayerConnected(playerId, isConnected);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        ChildEventListener movesChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                String value = dataSnapshot.getValue().toString();

                int id = Integer.parseInt(key);
                gameManager.makeMove(id, Boolean.valueOf(value));

                Button btn = (Button) findViewById(id);
                btn.setText(Boolean.valueOf(value) ? "X" : "0");

                List<Point> winRow = gameManager.getWinRow();

                if (winRow != null){
                    String winner = gameManager.getIsXMove() ? "X" : "0";

                    firebaseManager.updatePlayer(gameManager.getPlayerIndex(), false);
                    //Toast.makeText(getApplicationContext(),"Game finished. The winner is: " + winner,Toast.LENGTH_SHORT).show();

                    for (Point p: winRow){
                        Button b = (Button)findViewById( GameManager.getCellId(p.x, p.y));
                        b.setBackgroundColor(getResources().getColor(R.color.winRowColor));
                    }

                    if(!((Activity) MainActivity.this).isFinishing()) {
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(MainActivity.this);
                        }
                        builder.setTitle("Game finished")
                                .setMessage("The winner is: " + winner)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        clearData();
                                        firebaseManager.clearData();
                                        if (!gameManager.isPlayer0Connected()) {
                                            gameManager.setPlayerIndex(0);
                                            firebaseManager.updatePlayer(0, true);
                                        } else if (!gameManager.isPlayer1Connected()) {
                                            gameManager.setPlayerIndex(1);
                                            firebaseManager.updatePlayer(1, true);
                                        } else {
                                            gameManager.setPlayerIndex(-1);
                                        }
                                        gameManager.initData();
                                    }
                                })
                                .show();
                    }

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        gameManager = new GameManager(ROWS_COUNT, COLUMNS_COUNT);

        firebaseManager.addChildEventListener(childEventListener);
        firebaseManager.addChildEventListener("moves", movesChildEventListener);
        firebaseManager.addChildEventListener("players", playersChildEventListener);



        Resources r = getResources();
        int leftRightPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -3, r.getDisplayMetrics());
        int topBottomPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -5, r.getDisplayMetrics());
//        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
//                TableRow.LayoutParams.MATCH_PARENT, 1);
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(BUTTON_WIDTH, BUTTON_HEIGHT);
        rowParams.setMargins(leftRightPx,topBottomPx,leftRightPx,topBottomPx);

        TableLayout layout = (TableLayout) findViewById(R.id.myTable);

        for (int i = 0; i < ROWS_COUNT; ++i){
            TableRow row = new TableRow(this);

            for (int j = 0; j < COLUMNS_COUNT; ++j){
                Button button = new Button(this);
                button.setTextColor(getResources().getColor(R.color.textColor));
                button.setBackgroundResource(android.R.drawable.btn_default);

                //button.setBackgroundColor(getResources().getColor(R.color.commonCellColor));
                int buttonId = GameManager.getCellId(i,j);
                button.setId(buttonId);

                button.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View view) {
                        if (!gameManager.isMyMove()) return;

                        Button button = (Button) view;
                        int viewId = view.getId();
                        button.setEnabled(false);

                        firebaseManager.saveUserMoveToDataBase(viewId,gameManager.getIsXMove());
                        button.setText(gameManager.getIsXMove() ? "X" : "0");

                        firebaseManager.changePlayer(!gameManager.getIsXMove());

                    }
                });

                row.addView(button, rowParams);
            }

            layout.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 1));
        }
    }

    private void clearData(){
        for (int i = 0; i < ROWS_COUNT; ++i) {
            for (int j = 0; j < COLUMNS_COUNT; ++j) {
                int id = GameManager.getCellId(i,j);
                Button button = (Button) findViewById(id);
                button.setText("");
                button.setEnabled(true);
                button.setBackgroundResource(android.R.drawable.btn_default);

            }
        }
    }

/*
    private void initWidthAndHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        width = point.x;
        height = point.y;
    }
    */












}
