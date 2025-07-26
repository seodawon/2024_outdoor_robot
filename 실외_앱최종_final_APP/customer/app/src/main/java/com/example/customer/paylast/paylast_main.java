package com.example.customer.paylast;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.customer.Item;
import com.example.customer.R;
import com.example.customer.listviewItem;
import com.example.customer.market_choice_customer;
import com.example.customer.navi;
import com.example.customer.pay.pay_main;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class paylast_main extends AppCompatActivity {
    // 기존 데이터베이스 URL
    private DatabaseReference oldDatabaseReference = FirebaseDatabase.getInstance("https://dbtest-customer.firebaseio.com/").getReference();
    // 새로운 데이터베이스 URL
    private DatabaseReference newDatabaseReference = FirebaseDatabase.getInstance("https://dbtest-market.firebaseio.com/").getReference();

    private ListView payLastList;
    Button button13;
    ImageButton imageButton4;
    paylastlistviewAdapter adapter;
    TextView address,totalcount;
    String destination;
    private Context context;
    private String id,number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_last_floor_customer);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        id = sharedPreferences.getString("id", "");
        // Adapter 생성
        adapter = new paylastlistviewAdapter(this, id);
        // 리스트뷰 참조 및 Adapter달기
        payLastList = (ListView) findViewById(R.id.pay_last_list);
        payLastList.setAdapter(adapter);
        button13 = (Button) findViewById(R.id.button13);
        imageButton4 = (ImageButton) findViewById(R.id.imageButton4);
        address = (TextView) findViewById(R.id.address);
        totalcount = (TextView) findViewById(R.id.totalcount);

        loadDataFromFirebase();
        calculateAndDisplayTotal();
        imageButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), market_choice_customer.class);
                showBackConfirmationDialog();
                startActivity(intent);
            }
        });
        button13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                copyUserData();
                Intent intent = new Intent(getApplicationContext(), navi.class);
                startActivity(intent);
            }
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // 여기에 원하는 동작을 추가합니다.
                // 예시: 특정 조건에서만 뒤로가기 버튼을 막는 경우
                showBackConfirmationDialog();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadDataFromFirebase() {
        DatabaseReference inRef = oldDatabaseReference.child("id").child(id).child("information").child("destination");

        inRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String destination = dataSnapshot.getValue(String.class);
                address.setText("도착지 : " + destination);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("baglistviewAdapter", "Failed to read value.", databaseError.toException());
                Toast.makeText(context, "데이터 읽기에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showBackConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("안내");
        builder.setMessage("이전 화면으로 돌아가시겠습니까?");
        builder.setPositiveButton("이동", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                moveToHomeScreen();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 취소 버튼을 눌렀을 때는 아무 동작도 하지 않음
            }
        });
        builder.show();
    }
    private void moveToHomeScreen() {
        Intent intent = new Intent(paylast_main.this, market_choice_customer.class); // 홈 화면으로 이동할 Activity를 지정해주세요
        startActivity(intent);
        finish(); // 현재 Activity 종료
    }
    private void calculateAndDisplayTotal() {
        // Firebase 데이터베이스에서 메뉴 노드 참조 가져오기
        DatabaseReference menuRef = oldDatabaseReference.child("id").child(id).child("menu");

        menuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double totalPrice = 0;

                // 메뉴 항목을 반복하면서 가격과 수량을 가져옴
                for (DataSnapshot menuSnapshot : dataSnapshot.getChildren()) {
                    // 문자열로 price 값 가져오기
                    String priceStr = menuSnapshot.child("price").getValue(String.class);
                    Integer count = menuSnapshot.child("count").getValue(Integer.class);

                    if (priceStr != null && count != null) {
                        try {
                            // 숫자만 추출 (정규 표현식 사용)
                            String pricestr = priceStr.replaceAll("[^0-9]", "");

                            // 문자열을 숫자로 변환
                            int price = Integer.parseInt(pricestr);

                            // 총 가격 및 총 수량 계산
                            totalPrice += price * count;
                        } catch (NumberFormatException e) {
                            // 변환 오류 로그
                            Log.w(TAG, "Price conversion error for value: " + priceStr, e);
                            Toast.makeText(paylast_main.this, "가격 변환 오류: " + priceStr, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // 가격 또는 수량이 null인 경우 로그
                        Log.w(TAG, "Price or count is null for menu item: " + menuSnapshot.getKey());
                    }
                }

                // 총 가격과 총 수량을 포맷하여 TextView에 설정
                String displayText = String.format("총 가격: %,d 원", (int) totalPrice);
                totalcount.setText(displayText);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadData:onCancelled", databaseError.toException());
                Toast.makeText(paylast_main.this, "데이터를 읽어오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
