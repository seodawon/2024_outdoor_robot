package com.example.admin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class main_admin extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100; // 퍼미션 코드
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_admin);

        Button login_button = findViewById(R.id.login_button);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), login_admin.class);
                startActivity(intent);
                finish();
            }

        });
    }
    // 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 사용자가 권한을 부여한 경우
            } else {
                // 사용자가 권한을 거부한 경우
                Toast toast = Toast.makeText(getApplicationContext(), "권한이 없으므로 현재 위치를 사용할 수 없음", Toast.LENGTH_SHORT);
                toast.show();
                // 필요한 처리 작업 수행 (예: 권한이 없으므로 현재 위치를 사용할 수 없음을 사용자에게 알림)
            }
        }
    }
}
