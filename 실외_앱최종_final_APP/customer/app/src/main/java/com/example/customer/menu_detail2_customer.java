package com.example.customer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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

import java.util.ArrayList;
import java.util.Map;

public class menu_detail2_customer extends AppCompatActivity {

    ImageButton back_icon,user;
    private ArrayList<listviewItem> listviewItemList = new ArrayList<listviewItem>();
    private ArrayList<String> keys = new ArrayList<>();
    private ImageButton add_icon, minus_icon;
    private Context context;
    private TextView number_icon;
    ImageView imageImage;
    RadioButton radiobutton1_1,radiobutton1_2,radiobutton2_1,radiobutton2_2,radiobutton3_1,radiobutton3_2,
            radiobutton4_1,radiobutton4_2,radiobutton4_3,radiobutton4_4;
    TextView nameText,priceText;
    Button rewrite_button;
    RadioGroup firstText,secondText,thirdText,fourthText;
    RadioButton firstRadioButton,secondRadioButton,thirdRadioButton,fourthRadioButton;
    String image;
    String name,state,density,shot,syrup;
    String price, key,destination,id;
    int count,number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_detail2_customer);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        id = sharedPreferences.getString("id", "");
        number_icon = findViewById(R.id.number_icon);
        //setText() 메서드는 매개변수로 문자열을 받기 때문에 문자열로 바꿈
        add_icon = findViewById(R.id.add_icon);
        minus_icon = findViewById(R.id.minus_icon);
        back_icon = (ImageButton) findViewById(R.id.back_icon);
        rewrite_button= (Button) findViewById(R.id.rewrite_button);
        user= (ImageButton) findViewById(R.id.user);
        imageImage = findViewById(R.id.detail_image);
        nameText = findViewById(R.id.name);
        priceText = findViewById(R.id.price);
        firstText = findViewById(R.id.radiogroup1);
        secondText = findViewById(R.id.radiogroup2);
        thirdText = findViewById(R.id.radiogroup3);
        fourthText = findViewById(R.id.radiogroup4);
        radiobutton1_1=findViewById(R.id.radiobutton1_1);
        radiobutton1_2=findViewById(R.id.radiobutton1_2);
        radiobutton2_1=findViewById(R.id.radiobutton2_1);
        radiobutton2_2=findViewById(R.id.radiobutton2_2);
        radiobutton3_1=findViewById(R.id.radiobutton3_1);
        radiobutton3_2=findViewById(R.id.radiobutton3_2);
        radiobutton4_1=findViewById(R.id.radiobutton4_1);
        radiobutton4_2=findViewById(R.id.radiobutton4_2);
        radiobutton4_3=findViewById(R.id.radiobutton4_3);
        radiobutton4_4=findViewById(R.id.radiobutton4_4);
        Intent bagintent = getIntent();
        key = bagintent.getStringExtra("key");
        // Firebase에서 데이터 로드 및 UI 업데이트
        loadDataFromFirebase();
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
        rewrite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent rewriteintent= new Intent(getApplicationContext(),bag_main.class);
                firstRadioButton = findViewById(firstText.getCheckedRadioButtonId());
                secondRadioButton = findViewById(secondText.getCheckedRadioButtonId());
                thirdRadioButton = findViewById(thirdText.getCheckedRadioButtonId());
                fourthRadioButton = findViewById(fourthText.getCheckedRadioButtonId());

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
                        //Item 객체 생성
                        Item Item = new Item(image,name,price,count,key,state,density,shot,syrup);
                        // 데이터베이스에 주문 추가
                        databaseRef.child(key).setValue(Item)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // 성공적으로 저장된 경우 사용자에게 알림
                                        Toast.makeText(getApplicationContext(), "수정되었습니다.", Toast.LENGTH_SHORT).show();
                                        startActivity(rewriteintent);
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
                    public void onCancelled(DatabaseError databaseError) {
                        // 오류 발생 시 처리
                        Toast.makeText(getApplicationContext(), "데이터베이스 연동이 불안정합니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void loadDataFromFirebase() {
        DatabaseReference database = FirebaseDatabase.getInstance("https://dbtest-customer.firebaseio.com/").getReference().child("id").child(id).child("menu");

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Item Item = snapshot.getValue(Item.class);
                        if (Item != null) {
                            if(key.equals(Item.getKey())) {
//                                image = Item.getImage();
//                                imageImage.setImageResource(image);
                                image = Item.getImage(); // assuming this returns the URL of the image
                                Glide.with(menu_detail2_customer.this) // context를 적절한 것으로 대체해야 합니다.
                                        .load(image)
                                        .into(imageImage);
                                name = Item.getName();
                                nameText.setText(name);
                                price = Item.getPrice();
                                priceText.setText(price);
                                count=Item.getCount();
                                number_icon.setText(String.valueOf(count));
                                state=Item.getState();
                                if(state.equals("아이스")){
                                    radiobutton1_1.setChecked(true);
                                    radiobutton1_2.setChecked(false);
                                } else{
                                    radiobutton1_1.setChecked(false);
                                    radiobutton1_2.setChecked(true);
                                }
                                density= Item.getDensity();
                                if(density.equals("선택없음")){
                                    radiobutton2_1.setChecked(true);
                                    radiobutton2_2.setChecked(false);
                                } else{
                                    radiobutton2_2.setChecked(true);
                                    radiobutton2_1.setChecked(false);
                                }
                                shot = Item.getShot();
                                if(shot.equals("선택없음")){
                                    radiobutton3_1.setChecked(true);
                                    radiobutton3_2.setChecked(false);
                                } else{
                                    radiobutton3_1.setChecked(false);
                                    radiobutton3_2.setChecked(true);
                                }
                                syrup = Item.getSyrup();
                                if(syrup.equals("선택없음")){
                                    radiobutton4_1.setChecked(true);
                                    radiobutton4_2.setChecked(false);
                                    radiobutton4_3.setChecked(false);
                                    radiobutton4_4.setChecked(false);
                                } else if (syrup.equals("바닐라시럽추가")){
                                    radiobutton4_1.setChecked(false);
                                    radiobutton4_2.setChecked(true);
                                    radiobutton4_3.setChecked(false);
                                    radiobutton4_4.setChecked(false);
                                } else if(syrup.equals("카라멜시럽추가")){
                                    radiobutton4_1.setChecked(false);
                                    radiobutton4_2.setChecked(false);
                                    radiobutton4_3.setChecked(true);
                                    radiobutton4_4.setChecked(false);
                                } else{
                                    radiobutton4_1.setChecked(false);
                                    radiobutton4_2.setChecked(false);
                                    radiobutton4_3.setChecked(false);
                                    radiobutton4_4.setChecked(true);
                                }
                            }
                        }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("baglistviewAdapter", "Failed to read value.", databaseError.toException());
                Toast.makeText(context, "데이터 읽기에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
