package com.example.moro2609.tictacktoe;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by moro2609 on 31.01.2018.
 */

public class FirebaseManager {
    private FirebaseDatabase mDatabase;

    public FirebaseManager() {
        mDatabase = FirebaseDatabase.getInstance();
    }

    public void changePlayer(boolean newIsX) {
        DatabaseReference ref = mDatabase.getReference();
        ref.child("isX").setValue(newIsX);
    }

    public void saveUserMoveToDataBase(int cellId, boolean isX) {
        DatabaseReference ref = mDatabase.getReference();
        ref.child("moves").child(String.valueOf(cellId)).setValue(isX);
    }

    public void clearData(){
        DatabaseReference ref  = mDatabase.getReference();

        ref.child("moves").removeValue();
        ref.child("isX").setValue(true);

    }

    public void addChildEventListener(ChildEventListener childEventListener) {
        DatabaseReference ref  = mDatabase.getReference();
        ref.addChildEventListener(childEventListener);
    }


    public void addChildEventListener(String child, ChildEventListener childEventListener){
        DatabaseReference ref = mDatabase.getReference();
        ref.child(child).addChildEventListener(childEventListener);
    }

    public void updatePlayer(int newPlayerIndex, boolean value) {
        DatabaseReference ref = mDatabase.getReference();
        ref.child("players").child(String.valueOf(newPlayerIndex)).setValue(value);
    }




}
