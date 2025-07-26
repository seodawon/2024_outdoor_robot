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

public class join_Market extends AppCompatActivity {

    private EditText user_id, user_password, user_name, user_age;
    private Button join_icon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_market);
        //아이디값 찾아주기
        user_id = findViewById(R.id.user_id);
        user_password = findViewById(R.id.user_password);
        user_name = findViewById(R.id.user_name);
        user_age = findViewById(R.id.user_age);

        //회원가입 버튼 클릭 시 수행
        join_icon = findViewById(R.id.join_icon);
        join_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //EditText에 현재 입력되어있는 값을 get(가져온다)해온다
                String userID = user_id.getText().toString();
                String userPassword = user_password.getText().toString();
                String userName = user_name.getText().toString();
                int userAge = Integer.parseInt(user_age.getText().toString());

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                Toast.makeText(getApplicationContext(), "회원 등록 성공", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(join_Market.this, login_Market.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "회원 등록 실패", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };

                RegisterRequest registerRequest = new RegisterRequest(userID, userPassword, userName, userAge, responseListener);
                RequestQueue queue = Volley.newRequestQueue(join_Market.this);
                queue.add(registerRequest);
            }
        });


        //뒤로 가기 버튼 클릭 시 로그인창으로 이동
        ImageButton back_icon = findViewById(R.id.back_icon);
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), login_Market.class);
                startActivity(intent);
            }
        });

        //회원가입 버튼 누르면 로그인창 이동
        join_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), login_Market.class);
                startActivity(intent);
            }
        });

    }
}
