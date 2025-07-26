package com.example.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class pw_search_customer extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText etName, etId, etBirthdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pw_search_customer);

        db = FirebaseFirestore.getInstance();
        etName = findViewById(R.id.editTextText10);  // 이름 입력 필드
        etId = findViewById(R.id.editTextText11);    // 아이디 입력 필드
        etBirthdate = findViewById(R.id.editTextText12);        // 생년월일 입력 필드

        Button button1 = findViewById(R.id.button1);
        ImageButton imageButton4 = findViewById(R.id.imageButton4);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString();
                String id = etId.getText().toString();
                String birthdate = etBirthdate.getText().toString();

                if (name.isEmpty() || id.isEmpty() || birthdate.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "정보를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                checkInformation(name, id, birthdate);
            }
        });

        imageButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), login_customer.class);
                startActivity(intent);
            }
        });
    }

    private void checkInformation(String name, String id, String birthdate) {
        db.collection("user")
                .document("info")
                .collection("id")
                .whereEqualTo("name", name)
                .whereEqualTo("id", id)
                .whereEqualTo("birthdate", birthdate)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // 정보 일치
                            Intent intent = new Intent(getApplicationContext(), pw_data_customer.class);
                            startActivity(intent);
                        } else {
                            // 정보 불일치
                            Toast.makeText(getApplicationContext(), "정보가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // 검색 오류
                        Toast.makeText(getApplicationContext(), "정보 확인 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
