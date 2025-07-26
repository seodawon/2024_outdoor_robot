package com.example.market;

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


public class orderlist_mar extends AppCompatActivity {
    private ListView orderList;
    private ImageButton back_icon;
    private ArrayList<listviewItem> listviewItemList = new ArrayList<listviewItem>();

    private orderListAdapter adapter;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderlist_floor_market);

       orderList = (ListView) findViewById(R.id.order_list);
        back_icon = (ImageButton) findViewById(R.id.back_icon);
        adapter = new orderListAdapter(this, listviewItemList);
        orderList.setAdapter(adapter);

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), marketChoice_Market.class);
                startActivity(intent);
            }
        });
    }


}
