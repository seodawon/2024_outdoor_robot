package com.example.customer;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class navi extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {
    private Context context;
    private String id;
    private boolean isTaskInProgress = true;
    String destination, adminAccept, marketAccept;
    CheckBox admin, market;
    TextView destinationText, timeText, reason, dis;
    ImageButton back;
    DatabaseReference aRef, inRef, bRef, reasonRef,atimeRef,mtimeRef;
    private DatabaseReference
    oldDatabaseReference = FirebaseDatabase.getInstance("https://dbtest-customer.firebaseio.com/").getReference()
    , data = FirebaseDatabase.getInstance("https://dbtest-customer.firebaseio.com/").getReference();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100; // 퍼미션 코드
    private LinearLayout linearLayoutTmap; // 지도 띄우는 곳
    private TMapView tmapView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navi);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        id = sharedPreferences.getString("id", "");
        back = (ImageButton) findViewById(R.id.back);
        admin = (CheckBox) findViewById(R.id.admin);
        market = (CheckBox) findViewById(R.id.market);
        destinationText = (TextView) findViewById(R.id.eTxt5);
        reason = (TextView) findViewById(R.id.reason);
        timeText = (TextView) findViewById(R.id.timeText);
        dis = (TextView) findViewById(R.id.disText);
        linearLayoutTmap = findViewById(R.id.linearLayoutTmap);

        loadDataFromFirebase();
        updateBackButtonVisibility();
        displayTime();
        checkLocationPermission(); // 퍼미션
        initMap();

        Timer timer = new Timer();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (admin.getText().equals("배달수락")) {
//                1초마다 실행
                    DatabaseReference subReference = data.child("id").child(id).child("information");
                    subReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            String robotData = dataSnapshot.child("RobottoApp").getValue(String.class);
                            String distance = dataSnapshot.child("distance").getValue(String.class);
                            // 데이터가 null이 아닌 경우 처리
                            if (robotData != null) {
                                // 데이터를 받아온 후에 여기서 처리
                                Log.d("ReceivedData", robotData);
                                robotDataParse(robotData);
                                dis.setText(distance.substring(0,5) + "m");
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
            }
        };
        timer.schedule(timerTask,0,1000);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DatabaseReference 객체를 생성하여 해당 사용자의 노드를 삭제합니다.
                DatabaseReference userRef = oldDatabaseReference.child("id").child(id);

                // 사용자의 정보를 모두 삭제합니다.
                userRef.removeValue();
                Toast.makeText(getApplicationContext(), "주문이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                moveToMarketChoiceCustomer();
            }
        });
        // OnBackPressedCallback 등록
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // 여기에 원하는 동작을 추가합니다.
                // 예시: 특정 조건에서만 뒤로가기 버튼을 막는 경우
                showBackConfirmationDialog();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없는 경우 권한 요청
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // 권한이 있는 경우
        }
    }
    // 지도 초기화 메서드
    private void initMap() {

        tmapView = new TMapView(navi.this);
        tmapView.setSKTMapApiKey("sUOU1N4rua5t1sl5S13vD3Qu4A0CcZcx9zqtSUos"); // 캡스톤용
//        tmapView.setSKTMapApiKey("eBDT1Fxjkj7ANUzp9jtQT8M4kWivWNUma3eis7OB"); // 동아리용
        linearLayoutTmap.addView(tmapView);

        // 지도 초기화 관련 코드
        tmapView.setMapType(tmapView.MAPTYPE_STANDARD);
        tmapView.setLanguage(tmapView.LANGUAGE_KOREAN);
        tmapView.setZoomLevel(17);
        tmapView.setIconVisibility(true);
        TMapGpsManager tmapGpsManager = new TMapGpsManager(navi.this);
        tmapGpsManager.setMinTime(1000);
        tmapGpsManager.setMinDistance(3);

        tmapGpsManager.setProvider(com.skt.Tmap.TMapGpsManager.NETWORK_PROVIDER); // 연결된 인터넷으로 현 위치 받음. 실내일때 유용

//        tmapGpsManager.setProvider(com.skt.Tmap.TMapGpsManager.GPS_PROVIDER); // gps로 현 위치 받음 -> 오차 심함

        tmapGpsManager.OpenGps();

        // 화면중심을 단말의 현재위치로 이동
        tmapView.setTrackingMode(true);
        tmapView.setSightVisible(true);
    }

    @Override
    public void onLocationChange(Location location) {
        // 위치가 변경될 때 호출되는 메서드
        double nowlatitude = location.getLatitude();
        double nowlongitude = location.getLongitude();

        // 현재 위치를 지도에 표시하는 코드 추가
        tmapView.setLocationPoint(nowlongitude, nowlatitude); // 현재 위치로 표시될 좌표의 위도, 경도를 설정
    }
    private void robotDataParse(String data){
        if (data == null || data.isEmpty()) {
            Log.e("robotDataParse", "Data string is empty or null");
        }
        String[] parsedata = data.split(",");
        double robotlatitude = Double.parseDouble(parsedata[0]);
        double robotlongitude = Double.parseDouble(parsedata[1]);
        double yaw = Double.parseDouble(parsedata[2]);

        TMapPoint robot = new TMapPoint(robotlatitude, robotlongitude);
        TMapMarkerItem robotMarker = new TMapMarkerItem();
        robotMarker.setTMapPoint(robot);
        tmapView.addMarkerItem("로봇위치", robotMarker);

    }

    private void showBackConfirmationDialog() {
        if (back.getVisibility() == View.GONE) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("안내");
        builder.setMessage("이전 화면으로 돌아가시겠습니까?");
        builder.setPositiveButton("이동", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                moveToHomeScreen();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 취소 버튼을 눌렀을 때는 아무 동작도 하지 않음
            }
        });
        builder.show();
        } else {
            Toast.makeText(navi.this, "화면상의 뒤로가기 버튼을 눌러주세요.", Toast.LENGTH_SHORT).show();
            // back 버튼이 보이지 않는 경우에는 바로 뒤로가기 처리합니다.
        }
    }
    private void moveToHomeScreen() {
        Intent intent = new Intent(navi.this, market_choice_customer.class); // 홈 화면으로 이동할 Activity를 지정해주세요
        startActivity(intent);
        finish(); // 현재 Activity 종료
    }

    private void moveToMarketChoiceCustomer() {
        // market_choice_customer 액티비티로 이동합니다.
        Intent intent = new Intent(navi.this, main2_customer.class);
        startActivity(intent);
        finish();
    }

    private void loadDataFromFirebase() {
        inRef = oldDatabaseReference.child("id").child(id).child("information").child("destination");
        aRef = oldDatabaseReference.child("id").child(id).child("information").child("adminAccept");
        bRef = oldDatabaseReference.child("id").child(id).child("information").child("marketAccept");
        reasonRef = oldDatabaseReference.child("id").child(id).child("information").child("reason");
        inRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                destination = dataSnapshot.getValue(String.class);
                if (destination != null) {
                    destinationText.setText(destination);
                } else {
                    // Handle the case where destination is null
                    Log.w("navi", "Destination is null");
                    Toast.makeText(navi.this, "목적지 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("navi", "Failed to read value.", databaseError.toException());
                Toast.makeText(navi.this, "데이터 읽기에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        aRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adminAccept = dataSnapshot.getValue(String.class);
                switch (adminAccept) {
                    case "true":
                        admin.setChecked(true);
                        admin.setText("배달수락");
                        admin.setEnabled(false);
                        break;
                    case "false":
                        admin.setChecked(true);
                        admin.setText("배달거절");
                        admin.setEnabled(false);
                        break;
                    case "기본":
                        admin.setChecked(false);
                        admin.setText("배달수락/거절");
                        admin.setEnabled(false);
                        break;
                    default:
                        // Handle default case (if adminAccept is null or unexpected value)
                        Log.w("navi", "Unexpected adminAccept value: " + adminAccept);
                        admin.setChecked(false); // Default to unchecked if value is unexpected
                        admin.setText("배달수락");
                        break;
                }
                updateBackButtonVisibility();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("baglistviewAdapter", "Failed to read value.", databaseError.toException());
                Toast.makeText(context, "데이터 읽기에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        bRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                marketAccept = dataSnapshot.getValue(String.class);
                if (marketAccept != null) {
                    switch (marketAccept) {
                        case "true":
                            market.setChecked(true);
                            market.setText("주문수락");
                            market.setEnabled(false);
                            break;
                        case "false":
                            market.setChecked(true);
                            market.setText("주문거절");
                            market.setEnabled(false);
                            admin.setChecked(true);
                            admin.setText("배달거절");
                            break;
                        case "기본":
                            market.setChecked(false);
                            market.setText("주문수락/거절");
                            market.setEnabled(false);
                            break;
                        default:
                            Log.w("navi", "Unexpected marketAccept value: " + marketAccept);
                            market.setChecked(false);
                            market.setText("주문수락");
                            market.setEnabled(false);
                            break;
                    }
                } else {
                    Log.w("navi", "marketAccept is null");
//                    Toast.makeText(navi.this, "주문 수락 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
                updateBackButtonVisibility(); // 버튼 가시성 업데이트
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("navi", "Failed to read value.", databaseError.toException());
                Toast.makeText(navi.this, "데이터 읽기에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        reasonRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String reasonText = dataSnapshot.getValue(String.class);
                if (reasonText != null && ("false".equals(adminAccept) || "false".equals(marketAccept))) {
                    // reason 값이 존재하고, admin 또는 market이 거절한 경우에만 표시
                    reason.setText("사유: " + reasonText);
                    reason.setVisibility(View.VISIBLE);
                } else {
                    reason.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("navi", "Failed to read value.", databaseError.toException());
                Toast.makeText(navi.this, "데이터 읽기에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateBackButtonVisibility() {
        // 버튼을 보이거나 숨기는 조건을 설정합니다.
        if (("false".equals(adminAccept) || "false".equals(marketAccept))) {
            back.setVisibility(View.VISIBLE);
        } else {
            back.setVisibility(View.GONE);
        }
    }

    private void displayTime() {
        // Firebase에서 atime과 mtime 값을 가져오기 위한 레퍼런스 설정
        atimeRef = oldDatabaseReference.child("id").child(id).child("information").child("atime");
        mtimeRef = oldDatabaseReference.child("id").child(id).child("information").child("mtime");

        // atime 데이터 변경 이벤트 리스너 설정
        atimeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 데이터 스냅샷에서 문자열로 atime 가져오기
                String atimeStr = dataSnapshot.getValue(String.class);
                if (atimeStr != null) {
                    try {
                        // 문자열을 정수로 변환
                        int atime = Integer.parseInt(atimeStr);

                        // atime 값이 0이 아닌 경우에만 mtime 데이터 변경 이벤트 리스너 설정
                        if (atime != 0) {
                            mtimeRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    // 데이터 스냅샷에서 문자열로 mtime 가져오기
                                    String mtimeStr = dataSnapshot.getValue(String.class);
                                    if (mtimeStr != null) {
                                        try {
                                            // 문자열을 정수로 변환
                                            int mtime = Integer.parseInt(mtimeStr);

                                            // mtime 값도 정상적으로 읽어왔을 때 총 소요 시간 계산
                                            int totalTimeMinutes = atime + mtime;
                                            // 현재 시간을 가져오기
                                            Calendar currentTime = Calendar.getInstance();
                                            int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
                                            int currentMinute = currentTime.get(Calendar.MINUTE);

                                            // 총 소요 시간을 현재 시간에 더함
                                            currentTime.add(Calendar.MINUTE, totalTimeMinutes);
                                            int finalHour = currentTime.get(Calendar.HOUR_OF_DAY);
                                            int finalMinute = currentTime.get(Calendar.MINUTE);

                                            // 시간을 시:분 형식으로 포맷팅
                                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                            Date finalTime = currentTime.getTime();
                                            String formattedTime = sdf.format(finalTime);

                                            // TextView에 시간 표시
                                            timeText.setText(formattedTime);
                                            timeText.setVisibility(View.VISIBLE); // TextView를 보이도록 설정
                                        } catch (NumberFormatException e) {
                                            // mtime을 정수로 변환할 수 없는 경우 예외 처리
                                            Log.e("navi", "Failed to parse mtime: " + mtimeStr, e);
                                            timeText.setVisibility(View.GONE); // TextView를 숨기도록 설정
                                        }
                                    } else {
                                        // mtime이 null인 경우 처리
                                        timeText.setVisibility(View.GONE); // TextView를 숨기도록 설정
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.w("navi", "Failed to read mtime value.", databaseError.toException());
                                    Toast.makeText(navi.this, "mtime 데이터 읽기에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // atime이 0인 경우 시간을 숨김
                            timeText.setVisibility(View.GONE); // TextView를 숨기도록 설정
                        }
                    } catch (NumberFormatException e) {
                        // atime을 정수로 변환할 수 없는 경우 예외 처리
                        Log.e("navi", "Failed to parse atime: " + atimeStr, e);
                        timeText.setVisibility(View.GONE); // TextView를 숨기도록 설정
                    }
                } else {
                    // atime이 null인 경우 처리
                    timeText.setVisibility(View.GONE); // TextView를 숨기도록 설정
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("navi", "Failed to read atime value.", databaseError.toException());
                Toast.makeText(navi.this, "atime 데이터 읽기에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
