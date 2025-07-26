package com.example.admin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class test extends AppCompatActivity {
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    private static final int REQUEST_ENABLE_BT = 10;
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> devices;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket = null;
    private OutputStream outputStream = null;
    private InputStream inputStream = null;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        AppCompatButton startButton = findViewById(R.id.start);
        AppCompatButton controlButton = findViewById(R.id.control);
        AppCompatButton bluetoothButton = findViewById(R.id.bluetooth);
        Button upButton = findViewById(R.id.button_up);
        Button downButton = findViewById(R.id.button_down);
        Button leftButton = findViewById(R.id.button_left);
        Button rightButton = findViewById(R.id.button_right);
        RelativeLayout directionKeysLayout = findViewById(R.id.direction_keys_layout);
        AppCompatSpinner spinner = findViewById(R.id.spinner);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.array8, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (bluetoothAdapter == null) {
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivityForResult(intent, REQUEST_ENABLE_BT);
            }
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSpeed = parent.getItemAtPosition(position).toString(); // 선택된 속도 값

                // 속도 값을 블루투스를 통해 전송합니다.
                sendData("v", selectedSpeed);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        controlButton.setOnClickListener(view -> {
            if (controlButton.getText().toString().equals("수동제어")) {
                controlButton.setText("자동제어");
                controlButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
                directionKeysLayout.setVisibility(View.VISIBLE);
                sendData("w", "3");  // 자동제어 시작 명령 -> 수동제어 아님?
            } else {
                controlButton.setText("수동제어");
                controlButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.original_color));
                directionKeysLayout.setVisibility(View.GONE);
                sendData("w","2");
            }
        });
       Handler handler1 = new Handler();
        Runnable sendDataRunnable1 = new Runnable() {
            @Override
            public void run() {
                sendData("s", "5"); // 주기적으로 "3"을 보냅니다.
                handler1.postDelayed(this, 100); // 100ms 후에 다시 실행합니다.
            }
        };
        upButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        handler1.post(sendDataRunnable1); // 버튼을 누르면 Runnable을 시작합니다.
                        return true;
                    case MotionEvent.ACTION_UP:
                        handler1.removeCallbacks(sendDataRunnable1); // 버튼을 떼면 Runnable을 중지합니다.
                        sendData("s", "0"); // 멈추는 명령을 보냅니다.
                        return true; // 이벤트를 처리했음을 알림
                }
                return false;
            }
        });
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
//        Handler handler2 = new Handler();
//        Runnable sendDataRunnable2 = new Runnable() {
//            @Override
//            public void run() {
//                sendData("s", "4"); // 주기적으로 "3"을 보냅니다.
//                handler2.postDelayed(this, 100); // 100ms 후에 다시 실행합니다.
//            }
//        };
//        downButton.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        handler2.post(sendDataRunnable2);
//                        return true;
//                    case MotionEvent.ACTION_UP:
//                        handler2.removeCallbacks(sendDataRunnable2); // 버튼을 떼면 Runnable을 중지합니다.
//                        sendData("s", "0"); // 멈추는 명령을 보냅니다.
//                        return true; // 이벤트를 처리했음을 알림
//                }
//                return false;
//            }
//        });
//        downButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });
        downButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        sendData("s","6"); // 앞으로 가는 명령
                        return true;
                    case MotionEvent.ACTION_UP:
                        sendData("s","0"); // 멈추는 명령
                        return true; // 이벤트를 처리했음을 알림
                }
                return false;
            }
        });
        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        Handler handler3 = new Handler();
        Runnable sendDataRunnable3 = new Runnable() {
            @Override
            public void run() {
                sendData("s", "3"); // 주기적으로 "3"을 보냅니다.
                handler3.postDelayed(this, 100); // 100ms 후에 다시 실행합니다.
            }
        };
        leftButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        handler3.post(sendDataRunnable3);
                        return true;
                    case MotionEvent.ACTION_UP:
                        handler3.removeCallbacks(sendDataRunnable3); // 버튼을 떼면 Runnable을 중지합니다.
                        sendData("s", "7"); // 멈추는 명령을 보냅니다.
                        return true; // 이벤트를 처리했음을 알림
                }
                return false;
            }
        });
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        Handler handler4 = new Handler();
        Runnable sendDataRunnable4 = new Runnable() {
            @Override
            public void run() {
                sendData("s", "4"); // 주기적으로 "3"을 보냅니다.
                handler4.postDelayed(this, 100); // 100ms 후에 다시 실행합니다.
            }
        };
        rightButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        handler4.post(sendDataRunnable4);
                        return true;
                    case MotionEvent.ACTION_UP:
                        handler4.removeCallbacks(sendDataRunnable4); // 버튼을 떼면 Runnable을 중지합니다.
                        sendData("s", "7"); // 멈추는 명령을 보냅니다.
                        return true; // 이벤트를 처리했음을 알림
                }
                return false;
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        startButton.setOnClickListener(view -> {
                sendData("s","2");
                handler1.removeCallbacks(sendDataRunnable1);
//                handler2.removeCallbacks(sendDataRunnable2);
                handler3.removeCallbacks(sendDataRunnable3);
                handler4.removeCallbacks(sendDataRunnable4);
                startButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.original_color));
        });

        bluetoothButton.setOnClickListener(view -> {
            checkBluetoothPermissions();
        });
    }

    private void checkBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN},
                        REQUEST_BLUETOOTH_PERMISSIONS);
            } else {
                selectBluetoothDevice();
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_BLUETOOTH_PERMISSIONS);
            } else {
                selectBluetoothDevice();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectBluetoothDevice();
            } else {
                Toast.makeText(this, "Bluetooth 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(test.this, "취소되었습니다", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        String selectedDevice = charSequences[which].toString();
                        Toast.makeText(test.this, selectedDevice + "가 연결 중입니다.", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "디바이스 연결 성공", Toast.LENGTH_SHORT).show();

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

    void sendData(String commandType, String value) {
        if (outputStream == null) {
            Toast.makeText(this, "블루투스 연결이 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String text  = "*" + commandType + "=" + value + "\n";
        try {
            outputStream.write(text.getBytes());
            Log.d("Bluetooth", "Data sent: " + text);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "데이터 송신 실패", Toast.LENGTH_SHORT).show();
        }
    }
}
