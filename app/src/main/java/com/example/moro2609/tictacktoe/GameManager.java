package com.example.moro2609.tictacktoe;

import android.graphics.Point;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by moro2609 on 29.01.2018.
 */



public class GameManager {

    private static int rowsCount;
    private static int columnsCount;

    private boolean isXMove;

    private char[][] moves;

    private int playerIndex;
    private boolean isPlayer0Connected;
    private boolean isPlayer1Connected;

    public GameManager(int rCount, int cCount) {
        rowsCount = rCount;
        columnsCount = cCount;
        playerIndex = -1;
        initData();
    }

    public boolean isMyMove() {
        //return true;

        if (!isPlayer0Connected || !isPlayer1Connected) return false;

        return (isXMove && playerIndex == 0 || !isXMove && playerIndex == 1);

        //if (playerStatus == PlayerStatus.Observer) return false;
        //return (isX && playerStatus == PlayerStatus.Cross || !isX && playerStatus == PlayerStatus.Zero);

        //return playerIndex < 2 && (isX && playerIndex == 0 || !isX && playerIndex == 1);
    }

//    public int getPlayerIndex() {
//        return playerIndex;
//    }

    public void updateIsPlayerConnected(int playerId, boolean isConnected){
        if (playerId == 0) isPlayer0Connected = isConnected;
        else if (playerId == 1) isPlayer1Connected = isConnected;
    }

    public boolean isPlayer0Connected() {
        return isPlayer0Connected;
    }

    public boolean isPlayer1Connected() {
        return isPlayer1Connected;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public void setPlayerIndex(int playerIndex) {
        this.playerIndex = playerIndex;
    }

    public void initData(){
        isXMove = true;
        moves = new char[rowsCount][columnsCount];
    }

    public void makeMove(int cellId, boolean value){
        Point point = getRowAndColumn(cellId);
        moves[point.x][point.y] = value ? 'X' : '0';
    }

    public void updateMoves(HashMap<String, String> values){
        moves = new char[rowsCount][columnsCount];
        for (Map.Entry<String, String> item : values.entrySet()){
            int key =  Integer.valueOf(item.getKey());
            boolean value = Boolean.valueOf(item.getValue());

            Point point = getRowAndColumn(key);
            moves[point.x][point.y] = value ? 'X' : '0';
        }
    }

    public boolean getIsXMove(){return isXMove;}
    public void setIsXMove(boolean isXMove){this.isXMove = isXMove;}

    public static int getCellId(int i, int j) {
        return i * 1000 + j;
    }

    public static Point getRowAndColumn(int id){
        int rowPart = id / 1000;
        int columnPart = id % 1000;
        return new Point(rowPart, columnPart);
    }



    public List<Point> getWinRow(){
        char symbol = isXMove ? 'X' : '0';

        for (int i=0;i<rowsCount; ++i){
            for(int j = 0; j < columnsCount;++j){
                if (moves[i][j] != symbol) continue;

                if (j <= columnsCount - 5){
                    boolean isGameFinished = true;
                    List<Point> winRow = new ArrayList<>();
                    winRow.add(new Point(i,j));
                    for (int k = j + 1; k < j + 5; ++k){
                        winRow.add(new Point(i,k));
                        if (moves[i][k] != symbol){
                            isGameFinished = false;
                            break;
                        }
                    }
                    if (isGameFinished){
                        return winRow;
                    }
                }

                if (i <= rowsCount - 5){
                    boolean isGameFinished = true;
                    List<Point> winRow = new ArrayList<>();
                    winRow.add(new Point(i,j));
                    for (int k = i + 1; k < i + 5; ++k){
                        winRow.add(new Point(k,j));
                        if (moves[k][j] != symbol){
                            isGameFinished = false;
                            break;
                        }
                    }
                    if (isGameFinished) return winRow;
                }

                if ((i <= rowsCount - 5) && (j <= columnsCount - 5)){
                    boolean isGameFinished = true;
                    List<Point> winRow = new ArrayList<>();
                    winRow.add(new Point(i,j));
                    for (int k = 1; k < 5; ++k){
                        winRow.add(new Point(i + k,j+k));
                        if (moves[i + k][j + k] != symbol){
                            isGameFinished = false;
                            break;
                        }
                    }
                    if (isGameFinished) return winRow;
                }

                if ((i >= 4) && (j <= columnsCount - 5)){
                    boolean isGameFinished = true;
                    List<Point> winRow = new ArrayList<>();
                    winRow.add(new Point(i,j));
                    for (int k = 1; k < 5; ++k){
                        winRow.add(new Point(i-k,j+k));
                        if (moves[i - k][j + k] != symbol){
                            isGameFinished = false;
                            break;
                        }
                    }
                    if (isGameFinished) return winRow;
                }
            }
        }
        return null;
    }
}
