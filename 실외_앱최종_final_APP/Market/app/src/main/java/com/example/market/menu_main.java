package com.example.market;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class menu_main extends AppCompatActivity {
    private ListView bagList;

    Button orderbutton;
    ImageButton imageButton5, imageButton4;
    String key,id;
    menulistviewAdapter adapter;
    private ArrayList<listviewItem> listviewItemList = new ArrayList<listviewItem>();
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_detail);
        Intent intent = getIntent();
        id = intent.getStringExtra("userId");

        // ID 값이 올바르게 가져와졌는지 확인
        if (id == null || id.isEmpty()) {
            Toast.makeText(this, "ID가 설정되지 않았습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        adapter = new menulistviewAdapter(this, listviewItemList, id);
        // 리스트뷰 참조 및 Adapter달기
        bagList = (ListView) findViewById(R.id.menu_list);
        bagList.setAdapter(adapter);
        imageButton4 = (ImageButton) findViewById(R.id.imageButton4);


        imageButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent menuIntent = new Intent(getApplicationContext(), orderlist_mar.class);
                startActivity(menuIntent);
            }
        });
    }
}
