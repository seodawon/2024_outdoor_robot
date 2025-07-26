package com.example.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class deliverylist_admin extends AppCompatActivity {
    private ListView deliveryList;
    private ImageButton back_icon;
    private ArrayList<listviewItem> listviewItemList = new ArrayList<listviewItem>();

    private deliveryListAdapter adapter;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deliverylist_floor);

        deliveryList = (ListView) findViewById(R.id.list_admin);
//        back_icon = (ImageButton) findViewById(R.id.back_icon);
        adapter = new deliveryListAdapter(this, listviewItemList);
        deliveryList.setAdapter(adapter);

    }

}
