package com.example.customer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.customer.paylast.paylast_main;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class market_choice_customer extends AppCompatActivity {
    private DatabaseReference oldDatabaseReference = FirebaseDatabase.getInstance("https://dbtest-customer.firebaseio.com/").getReference();
    String id2,id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.market_choice_customer);
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
        ImageButton cafe_choice = (ImageButton) findViewById(R.id.cafe_choice);
        ImageButton back_icon = (ImageButton) findViewById(R.id.back_icon);
        ImageButton delivery= (ImageButton) findViewById(R.id.delivery);

        cafe_choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // delivery 버튼이 보이는지 여부 확인
                if (delivery.getVisibility() == View.VISIBLE) {
                    // 주문 진행 중임을 안내하는 AlertDialog 표시
                    showOrderInProgressDialog();
                } else {
                    // delivery 버튼이 보이지 않는 경우 다음 화면으로 이동
                    moveToNextScreen();
                }
            }
        });

// 주문 진행 중임을 안내하는 AlertDialog 표시 메서드

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),main2_customer.class);
                startActivity(intent);
            }
        });
        delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), paylast_main.class);
                startActivity(intent);
            }
        });
        DatabaseReference userRef =oldDatabaseReference.child("id").child(id).child("information").child("destination");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String destination = dataSnapshot.getValue(String.class);
                Log.d("dlfkjsljf",destination);
                if (destination != null && !destination.equals("")) {
                    // destination 값이 null이 아닌 경우 asd 이미지 버튼을 보이도록 설정
                    delivery.setVisibility(View.VISIBLE);
                } else {
                    // destination 값이 null인 경우 asd 이미지 버튼을 숨김
                    delivery.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터 불러오기 실패 시 처리할 내용
            }
        });
    }
        private void showOrderInProgressDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("안내");
            builder.setMessage("주문이 진행 중입니다.");
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 확인 버튼을 클릭한 경우 다음 화면으로 이동
                }
            });
            builder.setCancelable(false); // 사용자가 다른 곳을 터치해도 창이 닫히지 않도록 설정
            builder.show();
        }

// 다음 화면으로 이동하는 메서드
        private void moveToNextScreen() {
            // 원하는 다음 화면으로 이동하는 코드를 여기에 작성하세요
            Intent intent = new Intent(market_choice_customer.this, menu_choice_customer.class);
            startActivity(intent);
            // finish(); // 현재 Activity를 종료할 필요가 있는 경우 Uncomment
        }
    }

