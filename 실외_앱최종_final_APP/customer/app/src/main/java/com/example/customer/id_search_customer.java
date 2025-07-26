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

public class id_search_customer extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText etName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.id_search_customer);

        db = FirebaseFirestore.getInstance();
        etName = findViewById(R.id.editTextText7);  // 이름 입력 필드

        Button button7 = findViewById(R.id.button7);
        ImageButton imageButton4 = findViewById(R.id.imageButton4);

        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                searchIdByName(name);
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

    private void searchIdByName(String name) {
        db.collection("user")
                .document("info")
                .collection("id")
                .whereEqualTo("name", name)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getString("id");
                                Intent intent = new Intent(getApplicationContext(), id_customer.class);
                                intent.putExtra("id", id);
                                startActivity(intent);
                                return;
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "아이디가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "아이디 검색 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
