package com.example.admin;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class test2 extends AppCompatActivity {
    private SeekBar seekBar1;
    private SeekBar seekBar2;
    private TextView seekBar1Value;
    private TextView seekBar2Value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test2);

//        seekBar1 = findViewById(R.id.seekBar1);
//        seekBar2 = findViewById(R.id.seekBar2);
//
//        // SeekBar 값 표시용 TextView 초기화
////        seekBar1Value = findViewById(R.id.seekBar1Value);
////        seekBar2Value = findViewById(R.id.seekBar2Value);
//
//        // SeekBar1에 OnSeekBarChangeListener 설정
//        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                // SeekBar1의 현재 값을 TextView에 업데이트
//                seekBar1Value.setText(String.valueOf(progress));
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                // 터치 시작 시의 처리
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                // 터치 종료 시의 처리
//            }
//        });
//
//        // SeekBar2에 OnSeekBarChangeListener 설정
//        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                // SeekBar2의 현재 값을 TextView에 업데이트
//                seekBar2Value.setText(String.valueOf(progress));
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                // 터치 시작 시의 처리
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                // 터치 종료 시의 처리
//            }
//        });
//
//        // SeekBar1에 OnTouchListener 설정
//        seekBar1.setOnTouchListener((v, event) -> {
//            v.getParent().requestDisallowInterceptTouchEvent(true);
//            return false; // 기본 터치 이벤트 처리를 유지
//        });
//
//        // SeekBar2에 OnTouchListener 설정
//        seekBar2.setOnTouchListener((v, event) -> {
//            v.getParent().requestDisallowInterceptTouchEvent(true);
//            return false; // 기본 터치 이벤트 처리를 유지
//        });
    }
}
