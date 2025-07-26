package com.example.market;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class marketChoice_Market extends AppCompatActivity {

    //로그인 창에서 로그인 버튼을 누르면 매장 선택창으로 이동!
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.market_choice_market);

        // 매장선택 창에서 뒤로 가기 버튼을 눌렀을 때 메인2창으로 이동!
        ImageButton back_icon = findViewById(R.id.back_icon);
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), main_Market.class);
                startActivity(intent);
            }
        });

        // 매장선택 창에서 매장 버튼을 눌렀을 때 고객주문내역창으로 이동!
        ImageButton coffee_button = findViewById(R.id.coffe_button);
        coffee_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), orderlist_mar.class);
                startActivity(intent);
            }
        });

        // 영수증 아이콘을 클릭 시 주문기록창으로 이동(현재는 창이 없어 메인창으로 이동)
        ImageButton user_button = findViewById(R.id.user);
        user_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ordercheck_Market.class);
                startActivity(intent);
            }
        });

    }
}
