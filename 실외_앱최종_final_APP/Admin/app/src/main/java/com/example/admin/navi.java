package com.example.admin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class navi extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback,
        ActivityCompat.OnRequestPermissionsResultCallback, DB_com.OnDataUpdateListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100; // 퍼미션 코드
    private LinearLayout linearLayoutTmap; // 지도 띄우는 곳
    DB_com db_com; // 쓰레드
    private double roDistance;

    private SeekBar seekBar1,seekBar2;
    private Handler handler;
    private TextView LS, RS, L1, L2, R1, R2;
    private AppCompatButton startButton;
    private AppCompatButton controlButton;
    private AppCompatButton start2Button;
    private int startValue = 0;    // start 버튼 값 (0: 출발, 2: 비상정지)
    private int controlValue = 1;  // control 버튼 값 (1: 수동제어, 2: 자동제어)
    private int start2Value = 2;
    private double[][] destination =
            {{37.631313, 127.054890}, // 인관
                    {37.632615, 127.055481}, // 2공
                    {37.632097, 127.054278}, // 1공
                    {37.630361, 127.054629}, // 덕관
                    {37.630559, 127.055100},  //운동장
//                    {37.632104, 127.054712}, //매장
//                    {37.630979, 127.054118}, // 은봉관
                    {37.630885, 127.054073}, //매장
                    {37.630966, 127.054124}, // 은봉관
                    {37.629972,127.055624}}; // 연지스퀘어

    // 티맵 설정 부분
    private TMapView tmapView;
    private String id, market, customer;
    private TMapPoint robotPoint, rxPoint; // 로봇위치
    private ArrayList<TMapPoint> pathPoint, store, finalPath; // tMap 경로, 매장 위치, 가공된 경로
    int pointState = 1, dnum = 0; // 라인 스텟
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    private static final int REQUEST_ENABLE_BT = 10; // 블루투스 활성화 상태
    private BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터
    private Set<BluetoothDevice> devices; // 블루투스 디바이스 데이터 셋
    private BluetoothDevice bluetoothDevice; // 블루투스 디바이스
    private BluetoothSocket bluetoothSocket = null; // 블루투스 소켓
    private OutputStream outputStream = null; // 블루투스에 데이터를 출력하기 위한 출력 스트림
    private InputStream inputStream = null; // 블루투스에 데이터를 입력하기 위한 입력 스트림
    private Thread workerThread = null; // 문자열 수신에 사용되는 쓰레드
    //    private byte[] readBuffer; // 수신 된 문자열을 저장하기 위한 버퍼
    private int readBufferPosition; // 버퍼 내 문자 저장 위치


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navi);
        seekBar1 = findViewById(R.id.seekBar1);
        seekBar2 = findViewById(R.id.seekBar2);
        LS = findViewById(R.id.LS);
        RS = findViewById(R.id.RS);
        L1 = findViewById(R.id.L1);
        L2 = findViewById(R.id.L2);
        R1 = findViewById(R.id.R1);
        R2 = findViewById(R.id.R2);
        startButton = findViewById(R.id.start);
        start2Button = findViewById(R.id.start2);
        controlButton = findViewById(R.id.control);
        AppCompatButton bluetoothButton = findViewById(R.id.bluetooth);
        TextView markettext = findViewById(R.id.market);
        TextView customertext = findViewById(R.id.customer);
        ImageButton back_button = findViewById(R.id.btn1);
        AppCompatButton controlButton = findViewById(R.id.control);
        AppCompatButton startButton = findViewById(R.id.start);
        RelativeLayout directionKeysLayout = findViewById(R.id.direction_keys_layout);
        linearLayoutTmap = findViewById(R.id.linearLayoutTmap);
        handler = new Handler(Looper.getMainLooper());
        // 초기 상태 - 비상정지(빨간색) / 수동제어(빨간색)
        updateButtonStyles();
        // SeekBar1 설정 (10 단위 증가)
        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int adjustedProgress = (progress / 10) * 10;
                LS.setText(String.valueOf(adjustedProgress));
                sendBluetoothData();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // SeekBar2 설정 (10 단위 증가)
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int adjustedProgress = (progress / 10) * 10;
                RS.setText(String.valueOf(adjustedProgress));
                sendBluetoothData();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        bluetoothButton.setOnClickListener(view -> checkBluetoothPermissions());
        // 블루투스 활성화하기
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // 블루투스 어댑터를 디폴트 어댑터로 설정
        if (bluetoothAdapter == null) { // 디바이스가 블루투스를 지원하지 않을 때
            Toast.makeText(getApplicationContext(), "Bluetooth 미지원", Toast.LENGTH_SHORT).show();
        } else { // 디바이스가 블루투스를 지원 할 때
            if (bluetoothAdapter.isEnabled()) { // 블루투스가 활성화 상태 (기기에 블루투스가 켜져있음)
                selectBluetoothDevice(); // 블루투스 디바이스 선택 함수 호출
            } else { // 블루투스가 비 활성화 상태 (기기에 블루투스가 꺼져있음)
                // 블루투스를 활성화 하기 위한 다이얼로그 출력
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                // 선택한 값이 onActivityResult 함수에서 콜백된다.
                startActivityForResult(intent, REQUEST_ENABLE_BT);
                selectBluetoothDevice();
            }
        }

        Intent intent = getIntent();
        market = intent.getStringExtra("market"); // 매장 위치 텍스트 불러오기
        id = intent.getStringExtra("userId"); // id 불러오기
        customertext.setText(intent.getStringExtra("customer")); // 목적지 위치 불러오기
        markettext.setText(market); // 매장 위치 텍스트
        db_com = new DB_com(this);
        db_com.init(id); // db에 id 값 보냄
        pathPoint = new ArrayList<>();

        DatabaseReference adminDatabase = FirebaseDatabase.getInstance("https://dbtest-admin.firebaseio.com/")
                .getReference().child("admin")
                .child("id").child(id).child("information").child("number");
        adminDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String data = dataSnapshot.getValue(String.class);
                Log.d("UserID", id);

                if (data != null) dnum = Integer.valueOf(data) - 1; // 어레이는 0부터이기 때문
                rxPoint = new TMapPoint(destination[dnum][0],destination[dnum][1]); // 목적지 좌표 세팅
                pathPoint.add(1, rxPoint);
                Log.d("rx data on", "rx data on");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터베이스 접근이 실패한 경우 로그에 오류를 기록함
                Log.w("FailedToReadData", databaseError.toException());
            }
        });

        TMapPoint storeP = new TMapPoint(destination[5][0],destination[5][1]); // 매장위치는 고정
        store = new ArrayList<>();
        store.add(storeP); // 경유지는 배열로 줘야함
        Log.d("store data on", "store data on");

        TMapMarkerItem storePMarker = new TMapMarkerItem();
        storePMarker.setTMapPoint(storeP);
        storePMarker.setPosition((float)0.5, (float)1.0);


        pathPoint.add(0, storeP);

        checkLocationPermission(); // 퍼미션
        initMap();
        tmapView.addMarkerItem("매장위치", storePMarker);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), deliverylist_admin.class);
                startActivity(intent);
                finish();
            }
        });
        controlButton.setOnClickListener(view -> {
            LinearLayout top2 = findViewById(R.id.top2);
            LinearLayout top3 = findViewById(R.id.top3);

            if (controlValue == 1) {
                controlValue = 2; // 자동제어
                top2.setVisibility(View.VISIBLE);
                top3.setVisibility(View.VISIBLE);
            } else {
                controlValue = 1; // 수동제어 (빨간색)
                top2.setVisibility(View.INVISIBLE);
                top3.setVisibility(View.INVISIBLE);

                // SeekBar 값을 0으로 초기화
                seekBar1.setProgress(0);
                seekBar2.setProgress(0);
                LS.setText("0");
                RS.setText("0");
            }

            updateButtonStyles();
            sendBluetoothData();
        });
        startButton.setOnClickListener(view -> {
            if (startValue == 0) {
                startValue = 2; // 비상정지 (빨간색)
            } else {
                startValue = 0; // 출발 (파란색)
            }
            updateButtonStyles();
            sendBluetoothData();
        });
        start2Button.setOnClickListener(view -> {
            if (start2Value == 0) {
                start2Value = 2; // 시작
            } else {
                start2Value = 0; // 꿑
                Thread thread = new Thread(db_com);
                thread.start();
                ArrayList<TMapPoint> path = new ArrayList<>(pathPoint);
                sendDataToDBCom(path.toString());
                //            ArrayList<TMapPoint> path = new ArrayList<>(pathPoint);
                //            path.add(0, robotPoint);
                //            drawPath(path);
                tmapView.setUserScrollZoomEnable(true);
                tmapView.removeTMapPath();
            }
            updateButtonStyles();
        });
//       for(int i = 0; i < 10;i++) {
//           TMapPoint point = new TMapPoint(destination[i][0],destination[i][1]);
//           TMapMarkerItem marker = new TMapMarkerItem();
//           marker.setTMapPoint(point);
//           tmapView.addMarkerItem("목적지위치"+i, marker);
//       }
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

    private void checkBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN},
                        REQUEST_BLUETOOTH_PERMISSIONS);
            } else {
                selectBluetoothDevice();
            }
        } else {
            selectBluetoothDevice();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectBluetoothDevice();
            } else {
                // 권한이 거부되었을 때 처리할 내용
            }
        }
    }

    public void selectBluetoothDevice() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        devices = bluetoothAdapter.getBondedDevices();
        int pairedDeviceCount = devices.size();
        if (pairedDeviceCount == 0) {
            Toast.makeText(this, "페어링된 블루투스 디바이스가 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("페어링 되어있는 블루투스 디바이스 목록");
            List<String> list = new ArrayList<>();
            for (BluetoothDevice device : devices) {
                list.add(device.getName());
            }
            list.add("취소");
            final CharSequence[] charSequences = list.toArray(new CharSequence[0]);

            builder.setItems(charSequences, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == charSequences.length - 1) {
                        Toast.makeText(navi.this, "취소되었습니다", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        String selectedDevice = charSequences[which].toString();
                        Toast.makeText(navi.this, selectedDevice + "가 연결 중입니다.", Toast.LENGTH_SHORT).show();
                        connectDevice(selectedDevice);
                    }
                }
            });
            builder.setCancelable(false);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public void connectDevice(String deviceName) {
        if (devices == null) {
            Toast.makeText(this, "디바이스 목록이 비어 있습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        for (BluetoothDevice tempDevice : devices) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (deviceName.equals(tempDevice.getName())) {
                bluetoothDevice = tempDevice;
                break;
            }
        }

        if (bluetoothDevice == null) {
            Toast.makeText(this, "디바이스를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();  // Bluetooth 장치와 연결을 시도

            outputStream = bluetoothSocket.getOutputStream();  // 출력 스트림 얻기
            inputStream = bluetoothSocket.getInputStream();    // 입력 스트림 얻기
            Toast.makeText(this, "디바이스 연결 성공", Toast.LENGTH_SHORT).show();
            startListeningForData();
        } catch (IOException e) {
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("already connected")) {
                // 이미 연결된 경우 처리
                Toast.makeText(this, "이미 연결된 디바이스입니다.", Toast.LENGTH_SHORT).show();
            } else {
                // 일반적인 연결 실패 처리
                e.printStackTrace();
                Toast.makeText(this, "연결 실패", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void startListeningForData() {
        new Thread(() -> {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    if (inputStream != null) {
                        // 데이터를 읽어서 버퍼에 저장
                        bytes = inputStream.read(buffer);
                        String incomingData = new String(buffer, 0, bytes);

                        // 수신된 데이터를 핸들러를 통해 UI로 전달
                        handler.post(() -> processIncomingData(incomingData));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }).start();
    }
    private void processIncomingData(String data) {
        // 수신된 데이터를 L1, L2, R1, R2로 분할한다고 가정
        // 예시: "L1,L2,R1,R2" 형식의 데이터
        String[] values = data.trim().split(",");
        if (values.length == 4) {
            L1.setText(values[0]);  // L1 값 설정
            L2.setText(values[1]);  // L2 값 설정
            R1.setText(values[2]);  // R1 값 설정
            R2.setText(values[3]);  // R2 값 설정
        }
    }
    void sendBluetoothData() {
        if (outputStream == null) {
            Toast.makeText(this, "블루투스가 연결되지 않았습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        short sum = (short) (startValue + controlValue);
        Log.d("Bluetooth", "Data sent: " + sum);

        short lsValue;
        short rsValue;
        try {
            lsValue = Short.parseShort(LS.getText().toString());
            rsValue = Short.parseShort(RS.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(this, "숫자 형식 변환 오류", Toast.LENGTH_SHORT).show();
            return;  // 변환에 실패하면 데이터 전송 중지
        }

        String data = String.format("*%d,%d,%d\n", sum, lsValue, rsValue);

        try {
            outputStream.write(data.getBytes());
            Log.d("Bluetooth", "Data sent: " + data);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "데이터 전송 실패", Toast.LENGTH_SHORT).show();
        }

    }
    void sendData(String text) {
        // 문자열에 개행문자("\n")를 추가해줍니다.
        text += "\n";
        try{
            // 데이터 송신
            outputStream.write(text.getBytes());
        }catch(Exception e) {
            e.printStackTrace();
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
        // 주소 넘겨줄때 숫자로 가능?
    }
    @Override
    public void onLocationChange(Location location) {
        // 위치가 변경될 때 호출되는 메서드
        double nowlatitude = location.getLatitude();
        double nowlongitude = location.getLongitude();
        // 현재 위치를 지도에 표시하는 코드 추가
        tmapView.setLocationPoint(nowlongitude, nowlatitude); // 현재 위치로 표시될 좌표의 위도, 경도를 설정

//        if(robotPoint != null && rxPoint != null && store != null) {
//            Log.d("asdsadadasdsa", rxPoint.toString());
//            Log.d("asdsadadasdsa", store.get(0).toString());
//            Log.d("asdsadadasdsa", robotPoint.toString());
//            goRoute(robotPoint, rxPoint, store);
//        }
    }
//
//    private void goRoute(TMapPoint start, TMapPoint end, ArrayList<TMapPoint> pass) {
//        TMapData tmapData = new TMapData();
//        tmapData.findPathDataWithType(
//                TMapData.TMapPathType.PEDESTRIAN_PATH,
//                start,
//                end,
//                pass,
//                10,
//                new TMapData.FindPathDataListenerCallback() {
//                    @Override
//                    public void onFindPathData(final TMapPolyLine path) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    // path point 저장
//                                    pathPoint = path.getLinePoint();
//
//                                    // 경로 선 표시
//                                    path.setLineWidth(10);
//                                    path.setLineColor(Color.GRAY);
//                                    tmapView.addTMapPath(path);
//                                }
//                                catch (Exception e){
//                                    Log.e("er", e.toString());
//                                    Toast toast = Toast.makeText(getApplicationContext(), "목적지 및 경유지를 입력해주세요!", Toast.LENGTH_SHORT);
//                                    toast.show();
//                                }
//                            }
//                        });
//                    }
//                });
//
//    }
//
//    public void drawRobotAngle(double robotlatitude, double robotlongitude, double yaw){
//        // 두 점과 각도 지정
//        int robotx = tmapView.getMapXForPoint(robotlongitude, robotlatitude);
//        int roboty = tmapView.getMapYForPoint(robotlongitude, robotlatitude);
//
//        int xaxis = robotx + 100;
//        int yaxis = roboty;
//
//        // 각도를 라디안으로 변환
//        double angleRadians = Math.toRadians(yaw);
//        double backangleRadians = Math.toRadians(180);
//
//        // headingangle의 x 좌표 계산
//        int headingx = (int)(robotx + (xaxis - robotx) * Math.cos(angleRadians) - (yaxis - roboty) * Math.sin(angleRadians));
//        // headingangle의 y 좌표 계산
//        int headingy = (int)(roboty + (xaxis - robotx) * Math.sin(angleRadians) + (yaxis - roboty) * Math.cos(angleRadians));
//
//        // 처음 회전각 구하기 위함
//        int backx = (int)(robotx + (headingx - robotx) * Math.cos(backangleRadians) - (headingy - roboty) * Math.sin(backangleRadians));
//        int backy = (int)(roboty + (headingy - robotx) * Math.sin(backangleRadians) + (headingy - roboty) * Math.cos(backangleRadians));
//
//        Point back = new Point();
//        back.set(backx,backy);
//
//        TMapPoint headingpoint = tmapView.convertPointToGps(headingx, headingy);
//
//        TMapPolyLine roline = new TMapPolyLine();
//        roline.addLinePoint(robotPoint);
//        roline.addLinePoint(headingpoint);
//        tmapView.addTMapPolyLine("robot", roline);
//    }
//    public void drawPath(ArrayList<TMapPoint> path){
//        for (int i = 0; i < path.size(); i++) {
//            // 같은 위치 거르기
//            if (i >= 1 && path.get(i - 1).equals(path.get(i))) {
//                path.remove(i);
//            }
//        }
//        finalPath = new ArrayList<>(path);
//        sendDataToDBCom(path.toString());
//
//        for (int i = 0; i < path.size() - 1; i++) {
//            // 경로 그리기
//            TMapPolyLine line = new TMapPolyLine();
//            line.setLineWidth(10);
//            line.setLineColor(Color.RED);
//            line.setOutLineColor(Color.RED);
//            line.addLinePoint(path.get(i));
//            line.addLinePoint(path.get(i + 1));
//            tmapView.addTMapPolyLine("path"+i, line);
//        }
//    }

    @Override
    public void onDataUpdate(double robotlatitude, double robotlongitude, double yaw) {
        robotPoint = new TMapPoint(robotlatitude, robotlongitude); // 로봇 좌표
        TMapMarkerItem robotMarker = new TMapMarkerItem();
        robotMarker.setTMapPoint(robotPoint);
        robotMarker.setPosition((float)0.5, (float)1.0);
        tmapView.addMarkerItem("로봇위치", robotMarker);
//
//        drawRobotAngle(robotlatitude, robotlongitude, yaw);
//
//        TMapPolyLine line = new TMapPolyLine();
//        line.setLineWidth(10);
//        line.setLineColor(Color.RED);
//        line.setOutLineColor(Color.RED);
//        line.addLinePoint(robotPoint);
//        line.addLinePoint(finalPath.get(pointState)); // 0은 로봇 위치
//        tmapView.addTMapPolyLine("path" + pointState,line);
//        TMapPolyLine distance = new TMapPolyLine();
//        distance.addLinePoint(robotPoint);
//        for(int i = 1; finalPath.size() - pointState >= i; i++){
//            distance.addLinePoint(finalPath.get(finalPath.size()- i));
//        }
//        roDistance = distance.getDistance();
//        db_com.disData(roDistance);
//        if (line.getDistance() < 1.0) {
//            tmapView.removeTMapPolyLine("path" + pointState);
//            pointState += 1;
//        }
    }

    @Override
    public void robotSetLocation(double robotlatitude, double robotlongitude, double yaw){
        robotPoint = new TMapPoint(robotlatitude, robotlongitude); // 로봇 좌표
        TMapMarkerItem robotMarker = new TMapMarkerItem();
        robotMarker.setTMapPoint(robotPoint);
        robotMarker.setPosition((float)0.5, (float)1.0);
        tmapView.addMarkerItem("로봇위치", robotMarker);
    }

    private void sendDataToDBCom(String pathdata) {
        if (db_com != null) {
            db_com.PubData(pathdata);
        }
    }
    private void updateButtonStyles() {
        if (startValue == 0) {
            startButton.setText("출발");
            startButton.setBackgroundColor(Color.parseColor("#142265")); // 파란색 배경
        } else {
            startButton.setText("비상정지");
            startButton.setBackgroundColor(Color.parseColor("#750B29")); // 빨간색 배경
        }

        if (controlValue == 1) {
            controlButton.setText("수동");
            controlButton.setBackgroundColor(Color.parseColor("#750B29")); // 빨간색 배경
        } else {
            controlButton.setText("자동");
            controlButton.setBackgroundColor(Color.parseColor("#142265")); // 파란색 배경
        }

        if (start2Value == 2) {
            start2Button.setText("시작");
            start2Button.setBackgroundColor(Color.parseColor("#750B29")); // 빨간색 배경
        } else {
            start2Button.setText("꿋~!");
            start2Button.setBackgroundColor(Color.parseColor("#142265")); // 파란색 배경
        }
    }
}