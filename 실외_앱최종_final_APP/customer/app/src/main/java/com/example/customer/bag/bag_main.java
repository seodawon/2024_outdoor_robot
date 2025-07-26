package com.example.customer.bag;

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

import com.example.customer.Item;
import com.example.customer.listviewItem;
import com.example.customer.R;
import com.example.customer.menu_choice_customer;
import com.example.customer.menu_detail2_customer;
import com.example.customer.my_customer;
import com.example.customer.pay.pay_main;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class bag_main extends AppCompatActivity {
    private ListView bagList;

    Button orderbutton;
    ImageButton imageButton5, imageButton4;
    String key,id;
    baglistviewAdapter adapter;

    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bag_floor_customer);
// Adapter 생성
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        id = sharedPreferences.getString("id", "");
        adapter = new baglistviewAdapter(this,id);
        // 리스트뷰 참조 및 Adapter달기
        bagList = (ListView) findViewById(R.id.bag_list);
        bagList.setAdapter(adapter);
//        // Intent로부터 count 값을 받습니다
//        id = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("id", "");
        orderbutton = (Button) findViewById((R.id.orderbutton));
        imageButton5 = (ImageButton) findViewById(R.id.imageButton5);
        imageButton4 = (ImageButton) findViewById(R.id.imageButton4);

        orderbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent orderIntent = new Intent(getApplicationContext(), pay_main.class);
                startActivity(orderIntent);
            }
        });
        imageButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userIntent = new Intent(getApplicationContext(), my_customer.class);
                startActivity(userIntent);
            }
        });
        imageButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent menuIntent = new Intent(getApplicationContext(), menu_choice_customer.class);
                startActivity(menuIntent);
            }
        });
            loadDataFromFirebase();
        }
        @Override
        protected void onResume() {
            super.onResume();
            // 장바구니 화면으로 돌아왔을 때 데이터 갱신
            loadDataFromFirebase();
        }

    private void loadDataFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://dbtest-customer.firebaseio.com/");
        DatabaseReference myRef = database.getReference("id").child(id).child("menu");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // 데이터를 초기화
                adapter.clearItems();
                // 데이터가 변경될 때 호출
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // 각 데이터에 대해 반복
                    Item Item = snapshot.getValue(Item.class);
                    if (Item != null) {
                        adapter.addItem(Item.getImage(), Item.getName(), Item.getPrice(), Item.getCount(),Item.getKey(), Item.getState(), Item.getDensity(), Item.getShot(), Item.getSyrup());
                    }
                }
                adapter.notifyDataSetChanged(); // 데이터 변경을 알림
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // 데이터를 가져오는 데 실패한 경우 호출됩니다.
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }
}
