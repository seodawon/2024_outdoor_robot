package com.example.customer.pay;


import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.customer.Item;
import com.example.customer.R;
import com.example.customer.bag.bag_main;
import com.example.customer.paylast.paylast_main;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class pay_main extends AppCompatActivity {
    private ListView payList ;
    private DatabaseReference oldDatabaseReference = FirebaseDatabase.getInstance("https://dbtest-customer.firebaseio.com/").getReference();
    // 새로운 데이터베이스 URL
    private DatabaseReference newDatabaseReference = FirebaseDatabase.getInstance("https://dbtest-market.firebaseio.com/").getReference();
    Button button;
    ImageButton back_icon;
    paylistviewAdapter adapter;
    TextView totalcount;
    Spinner address;
    String destination,id;
    String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_floor_customer);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        id = sharedPreferences.getString("id", "");
        // Adapter 생성
        calculateAndDisplayTotal();
        adapter = new paylistviewAdapter(this,id);
        // 리스트뷰 참조 및 Adapter달기
        payList = (ListView) findViewById(R.id.pay_list);
        payList.setAdapter(adapter);
        button = (Button) findViewById(R.id.button);
        totalcount = (TextView)findViewById(R.id.kakao1);
        back_icon = (ImageButton) findViewById(R.id.back_icon);
        address = findViewById(R.id.address);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.address_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        address.setAdapter(spinnerAdapter);

        // Spinner 선택 리스너 설정
        address.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                destination = parent.getItemAtPosition(position).toString();
                number = String.valueOf(position);  // 실제 주소 값 저장
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                destination = "";
                number="";
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyUserData();
                // Spinner에서 선택된 destination 값이 비어있지 않은 경우에만 처리
                if (!destination.isEmpty()) {
                    Intent intent = new Intent(getApplicationContext(), paylast_main.class);
                    // Firebase에 destination 값 저장
                    saveDestinationToFirebase(destination,number);
                    startActivity(intent);
                } else {
                    // 사용자에게 선택하라는 메시지를 보여줄 수 있음
                    Toast.makeText(pay_main.this, "도착지를 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), bag_main.class);
                startActivity(intent);
            }
        });
//        loadDataFromFirebase();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // 장바구니 화면으로 돌아왔을 때 데이터 갱신
//        loadDataFromFirebase();
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
                            Double price = Double.parseDouble(pricestr);

                            // 소숫점 제거
                            int priceInt = (int) Math.round(price);

                            // 총 가격 및 총 수량 계산
                            totalPrice += priceInt * count;
                        } catch (NumberFormatException e) {
                            // 변환 오류 로그
                            Log.w(TAG, "Price conversion error for value: " + priceStr, e);
                            Toast.makeText(pay_main.this, "가격 변환 오류: " + priceStr, Toast.LENGTH_LONG).show();
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
                Toast.makeText(pay_main.this, "데이터를 읽어오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveDestinationToFirebase(String destination,String number) {
        // Firebase Database 인스턴스 가져오기
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://dbtest-customer.firebaseio.com/");
        // 데이터베이스 참조 설정 (예: "destination")
        DatabaseReference inRef = database.getReference("id").child(id).child("information");
        // 정보를 Firebase에 저장
        inRef.child("destination").setValue(destination);
        inRef.child("number").setValue(number);
//        inRef.child("id").setValue(id);
    }
    private void copyUserData() {
        // 현재 사용자 정보 가져오기
        DatabaseReference sourceRef = oldDatabaseReference.child("id").child(id);
        DatabaseReference targetRef = newDatabaseReference.child("market").child("id").child(id);

        sourceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    targetRef.setValue(dataSnapshot.getValue()).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                        } else {

                        }
                    });
                } else {
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadUserData:onCancelled", databaseError.toException());
                Toast.makeText(pay_main.this, "데이터 읽기에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
