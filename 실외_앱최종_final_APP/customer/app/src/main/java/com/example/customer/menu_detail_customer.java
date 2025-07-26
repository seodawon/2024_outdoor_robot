package com.example.customer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.customer.bag.bag_main;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class menu_detail_customer extends AppCompatActivity {

    ImageButton back_icon,user,add_icon, minus_icon;
    TextView number_icon;
    String image;
    int count = 1,number;
    Button bag_button;
    String name,price,imageUrl;
    String state,density,shot,syrup,destination,id;
    RadioGroup radiogroup1, radiogroup2, radiogroup3, radiogroup4;
    RadioButton firstRadioButton,secondRadioButton,thirdRadioButton,fourthRadioButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_detail_customer);
        // SharedPreferences에서 ID 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        id = sharedPreferences.getString("id", "");
        // saveDestinationToFirebase 메서드 호출

        number_icon = findViewById(R.id.number_icon);
        //setText() 메서드는 매개변수로 문자열을 받기 때문에 문자열로 바꿈
        number_icon.setText(String.valueOf(count));
        add_icon = findViewById(R.id.add_icon);
        minus_icon = findViewById(R.id.minus_icon);
        back_icon = (ImageButton) findViewById(R.id.back_icon);
        bag_button = (Button) findViewById(R.id.bag_button);
        user = (ImageButton) findViewById(R.id.user);
        ImageView imageImage = findViewById(R.id.detail_image);
        TextView nameText = findViewById(R.id.name);
        TextView priceText = findViewById(R.id.price);
        radiogroup1 = findViewById(R.id.radiogroup1);
        radiogroup2 = findViewById(R.id.radiogroup2);
        radiogroup3 = findViewById(R.id.radiogroup3);
        radiogroup4 = findViewById(R.id.radiogroup4);
        //보낸 intent 객체를 반환
        Intent intent = getIntent();
        //Int 형식의 image 값을 반환

        image = intent.getStringExtra("image");
        name = intent.getStringExtra("name");
        price = intent.getStringExtra("price");
//        id = intent.getStringExtra("id"); // ID 가져오기
//        saveDestinationToFirebase(id);

        Glide.with(this).load(image).into(imageImage);
        nameText.setText(name);
        priceText.setText(price);

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backIntent = new Intent(getApplicationContext(), menu_choice_customer.class);
                startActivity(backIntent);
            }
        });
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userIntent = new Intent(getApplicationContext(), my_customer.class);
                startActivity(userIntent);

            }
        });
        add_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                number_icon.setText(String.valueOf(count));
            }
        });

        minus_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count > 1) {
                    count--;
                    number_icon.setText(String.valueOf(count));
                }
            }
        });

        bag_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstRadioButton = findViewById(radiogroup1.getCheckedRadioButtonId());
                secondRadioButton = findViewById(radiogroup2.getCheckedRadioButtonId());
                thirdRadioButton = findViewById(radiogroup3.getCheckedRadioButtonId());
                fourthRadioButton = findViewById(radiogroup4.getCheckedRadioButtonId());

                // 선택된 라디오 버튼의 텍스트 가져오기
                state = firstRadioButton != null ? firstRadioButton.getText().toString() : "";
                density = secondRadioButton != null ? secondRadioButton.getText().toString() : "";
                shot = thirdRadioButton != null ? thirdRadioButton.getText().toString() : "";
                syrup = fourthRadioButton != null ? fourthRadioButton.getText().toString() : "";

                // 데이터베이스 참조
                DatabaseReference databaseRef = FirebaseDatabase.getInstance("https://dbtest-customer.firebaseio.com/").getReference().child("id").child(id).child("menu");
                // 새로운 주문 추가
                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // 기존 데이터베이스의 주문 개수를 확인하여 새로운 주문의 번호를 정합니다.
                        DatabaseReference dataRef = FirebaseDatabase.getInstance("https://dbtest-customer.firebaseio.com/").getReference();
                        DatabaseReference newRef = dataRef.child("id").child(id).child("menu").push();
                        String key = newRef.getKey();
                        //Item 객체 생성
                        Item Item = new Item(image, name, price, count,key,state,density,shot,syrup);

                        // 데이터베이스에 주문 추가
                        databaseRef.child(key).setValue(Item)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // 성공적으로 저장된 경우 사용자에게 알림
                                        Toast.makeText(getApplicationContext(), "장바구니에 담겼습니다.", Toast.LENGTH_SHORT).show();
                                        Intent bagIntent = new Intent(getApplicationContext(), bag_main.class);
                                        startActivity(bagIntent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // 저장에 실패한 경우 에러 메시지 출력
                                        Toast.makeText(getApplicationContext(), "장바구니 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w("menu_detail_customer", "loadPost:onCancelled", databaseError.toException());
                        Toast.makeText(getApplicationContext(), "데이터베이스 오류 발생.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}