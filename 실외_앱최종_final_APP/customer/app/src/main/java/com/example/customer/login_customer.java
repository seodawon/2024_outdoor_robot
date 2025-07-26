package com.example.customer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.customer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class login_customer extends AppCompatActivity {

    Button button6,button4,button3,btn_register1;
    ImageButton imageButton4;
    FirebaseFirestore db;
    EditText id3, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_customer);
        FirebaseApp.initializeApp(this);

        db = FirebaseFirestore.getInstance();
        button6 = (Button) findViewById(R.id.button6);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        btn_register1 = (Button) findViewById(R.id.btn_register1);
        imageButton4 = (ImageButton) findViewById(R.id.imageButton4);
        id3 = findViewById(R.id.id);
        password = findViewById(R.id.password);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), id_search_customer.class);
                startActivity(intent);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), pw_search_customer.class);
                startActivity(intent);
            }
        });

        imageButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), main1_customer.class);

                startActivity(intent);
            }
        });
        btn_register1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), register_customer.class);
                startActivity(intent);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginUser(view); // 로그인 버튼 대신 메인화면으로 바로 이동
            }
        });
    }
    public void loginUser(View view) {
        String id = id3.getText().toString().trim();
        String pw = password.getText().toString().trim();

        if (id.isEmpty() || pw.isEmpty()) {
            Toast.makeText(getApplicationContext(), "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firestore에서 사용자 정보를 가져와서 비교
        db.collection("user")
                .document("info")
                .collection("id")
                .document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String storedPw = document.getString("pw");
                                if (storedPw != null && storedPw.equals(pw)) {
                                    // 로그인 성공
                                    Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                                    saveUserIdToDatabase(id);

                                    Intent intent = new Intent(getApplicationContext(), main2_customer.class);
                                    intent.putExtra("id",id);
                                    startActivity(intent);
                                } else {
                                    // 비밀번호 불일치
                                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // 해당 아이디가 없음
                                Toast.makeText(getApplicationContext(), "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Firestore에서 문서 가져오기 실패
                            Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void saveUserIdToDatabase(String id) {
        // Firebase Realtime Database에 사용자 ID 저장
        DatabaseReference databaseRef = FirebaseDatabase.getInstance("https://dbtest-customer.firebaseio.com/").getReference();
        databaseRef.child("id").child(id).setValue(id)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("login_customer", "사용자 ID 저장 성공");
                            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            Log.d("jdlfksj",id);
                            editor.putString("id", id);
                            editor.apply();

                        } else {
                            Log.d("login_customer", "사용자 ID 저장 실패");
                        }
                    }
                });
    }
}