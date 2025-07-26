package com.example.market;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class login_Market extends AppCompatActivity {
    private EditText user_id, user_password;
    private Button login_button, join_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_market);

        user_id = findViewById(R.id.user_id);
        user_password = findViewById(R.id.user_password);
        login_button = findViewById(R.id.login_button);
        join_button = findViewById(R.id.join_button);
        //회원가입 버튼을 클릭 시 수행
        join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login_Market.this, join_Market.class);
                startActivity(intent);
            }
        });


        // 로그인 버튼 클릭시 로그인 수행
        login_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //EditText에 현재 입력된 값을 갖고옴
                String userID = user_id.getText().toString();
                String userPassword = user_password.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                String userID = jsonObject.getString("userID");
                                String userPassword = jsonObject.getString("userPassword");
                                Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(login_Market.this, MainActivity.class);
                                intent.putExtra("userID",userID);
                                intent.putExtra("userPassword",userPassword);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(userID, userPassword,responseListener);
                RequestQueue queue = Volley.newRequestQueue(login_Market.this);
                queue.add(loginRequest);
            }
        });



         //로그인 버튼 클릭시 마켓선택창으로 이동
        Button login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login_Market.this, marketChoice_Market.class);
                startActivity(intent);
            }
        });

        // 회원가입 버튼 클릭시 회원가입창으로 이동
        Button join_button = findViewById(R.id.join_button);
        join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login_Market.this, join_Market.class);
                startActivity(intent);
            }
        });



// 뒤로 가기 버튼 클릭시 메인창으로 이동
        ImageButton back_icon = findViewById(R.id.back_icon);
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login_Market.this, main_Market.class);
                startActivity(intent);
            }
        });

    }
}
