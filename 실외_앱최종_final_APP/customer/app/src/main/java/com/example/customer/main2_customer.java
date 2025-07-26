package com.example.customer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.customer.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class main2_customer extends AppCompatActivity {
    private DatabaseReference oldDatabaseReference = FirebaseDatabase.getInstance("https://dbtest-customer.firebaseio.com/").getReference();
    String id2,id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main2_customer);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        id2 = sharedPreferences.getString("id", "");
        Intent intent = getIntent();
        //hasExtra는 intent에 id의 값이 포함되어있는지 여부를 확인하는 매서드
        if (intent.hasExtra("id")) {
            id = intent.getStringExtra("id");
        } else {
            // "id"라는 키에 해당하는 Extra가 없는 경우, default 값을 설정합니다.
            id = id2; // 여기서 id2는 원하는 default 값입니다.
        }
        Log.d("dlfksjdfsdf",id);
        saveDestinationToFirebase(id);
        Handler handler = new Handler(){
            public void handleMessage (Message msg) {
                super.handleMessage(msg) ;
                Intent intent = new Intent(main2_customer.this, market_choice_customer.class);
                startActivity(intent);
                finish();
            }
        };
        handler.sendEmptyMessageDelayed(0,2000);
    }private void saveDestinationToFirebase(String id) {
        DatabaseReference inRef =oldDatabaseReference.child("id").child(id).child("information");
        Log.d("dlfksjdlfk",id);
//        DatabaseReference inRef = database.getReference("id").child(id).child("information");
        inRef.child("destination").setValue("");
        inRef.child("adminAccept").setValue("기본");
        inRef.child("marketAccept").setValue("기본");
        inRef.child("mtime").setValue("0");
        inRef.child("atime").setValue("0");
        inRef.child("reason").setValue("");
//        inRef.child("id").setValue(id);
    }
}
