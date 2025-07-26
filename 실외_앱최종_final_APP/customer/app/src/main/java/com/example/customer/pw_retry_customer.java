package com.example.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.customer.R;

public class pw_retry_customer extends AppCompatActivity {
    Button button1;
    ImageButton imageButton4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pw_retry_customer);
        button1= findViewById((R.id.button1));
        imageButton4 = (ImageButton) findViewById(R.id.imageButton4);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), pw_data_customer.class);
                startActivity(intent);
            }
        });
        imageButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), my_customer.class);
                startActivity(intent);
            }
        });
    }

}
