package com.example.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class id_customer extends AppCompatActivity {

    private TextView tvUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.id_customer);

        tvUserId = findViewById(R.id.textView35); // ID를 표시할 TextView

        // 이전 액티비티에서 전달된 userId 받기
        String id = getIntent().getStringExtra("id");
        if (id != null) {
            tvUserId.setText(id); // 사용자 ID를 TextView에 설정
        }

        Button button10 = findViewById(R.id.button10);
        ImageButton imageButton4 = findViewById(R.id.imageButton4);

        button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), login_customer.class);
                startActivity(intent);
            }
        });

        imageButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), id_search_customer.class);
                startActivity(intent);
            }
        });
    }
}
