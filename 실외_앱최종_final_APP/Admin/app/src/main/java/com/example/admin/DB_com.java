package com.example.admin;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DB_com implements Runnable {
    DatabaseReference adminDatabase, data;
    private OnDataUpdateListener listener;
    private String id;
    private Handler handler = new Handler(Looper.getMainLooper());
    double robotlatitude, robotlongitude, yaw, i = 0.000001, j = 1;
    String rodata = "";

    public interface OnDataUpdateListener {
        void robotSetLocation(double robotlatitude, double robotlongitude, double yaw);
        void onDataUpdate(double robotlatitude, double robotlongitude, double yaw);
    }
    public void PubData(String pathdata) {
        adminDatabase.child("admin").child("id").child(id).child("information").child("ApptoRobot").setValue(pathdata);
    }
    public void disData(double distance) {
        String disdata = String.valueOf(distance);
        data.child("id").child(id).child("information").child("distance").setValue(disdata);
    }
    public DB_com(OnDataUpdateListener listener) {
        this.listener = listener;
    }
    public void init(String id){
        this.id = id;
        data = FirebaseDatabase.getInstance("https://dbtest-customer.firebaseio.com/").getReference();
        adminDatabase = FirebaseDatabase.getInstance("https://dbtest-admin.firebaseio.com/").getReference();
        initData();
    }

    // run() 메소드를 오버라이드
    public void run() {
        while (true) {
            // 디비 앱 연결 부분
            SubData();
            try {
                Thread.sleep(500); //  딜레이
            } catch (InterruptedException e) {
                Log.e("Therror",e.toString());
                break;
            }
        }
    }

    private void robotDataParse(String data){
        if (data == null || data.isEmpty()) {
            Log.e("robotDataParse", "Data string is empty or null");
        }
        String[] parsedata = data.split(",");
        robotlatitude = Double.parseDouble(parsedata[0]);
        robotlongitude = Double.parseDouble(parsedata[1]);
        yaw = Double.parseDouble(parsedata[2]);

        // dmm -> degree
//        robotlatitude = (int)(Math.floor(orglat) / 100) + (orglat - (double)((int)(Math.floor(orglat) / 100) * 100)) / 60;
//        robotlongitude = (int)(Math.floor(orglon) / 100) + (orglon - (double)((int)(Math.floor(orglon) / 100) * 100)) / 60;
//        yaw = yaw - j;
//        robotlongitude = robotlongitude - i;
//        i = i+0.00001;
//        j += 1;
    }

    public void SubData() {
        DatabaseReference subReference = adminDatabase.child("admin").child("id").child(id).child("information").child("RobottoApp");
        subReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                rodata = dataSnapshot.getValue(String.class);
                // 데이터가 null이 아닌 경우 처리
                if (rodata != null) {
                    // 데이터를 받아온 후에 여기서 처리
                    Log.d("ReceivedData", rodata);
                    data.child("id").child(id).child("information").child("RobottoApp").setValue(rodata);

                    robotDataParse(rodata);

                    handler.post(() -> {
                        if (listener != null) {
                            listener.onDataUpdate(robotlatitude, robotlongitude, yaw);
                        }
                    });
                } else {
                    Log.d("ReceivedData", "Data is null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터베이스 접근이 실패한 경우 로그에 오류를 기록함
                Log.w("FailedToReadData", databaseError.toException());
            }
        });
    }
    public void initData() {
        Log.d("db id", id);
        DatabaseReference subReference = adminDatabase.child("admin").child("id").child(id).child("information").child("RobottoApp");
        subReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rodata = dataSnapshot.getValue(String.class);
                // 데이터가 null이 아닌 경우 처리
                if (rodata != null) {
                    // 데이터를 받아온 후에 여기서 처리
                    Log.d("Received Init Data", rodata);
                    robotDataParse(rodata);
                    handler.post(() -> {
                        if (listener != null) {
                            listener.robotSetLocation(robotlatitude, robotlongitude, yaw);
                        }
                    });
                } else {
                    Log.d("Received Init Data", "Data is null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터베이스 접근이 실패한 경우 로그에 오류를 기록함
                Log.w("FailedToReadData", databaseError.toException());
            }
        });
    }
}

