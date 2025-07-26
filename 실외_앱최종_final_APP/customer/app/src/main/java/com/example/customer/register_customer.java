package com.example.customer;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.customer.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class register_customer extends AppCompatActivity {
    private FirebaseFirestore db;
    String name,id,pw,pw2,birthdate,phonenumber;
    boolean idChecked = false;
    boolean idDuplicate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_customer);
        db = FirebaseFirestore.getInstance();

        EditText etname = findViewById(R.id.nameEditText);
        EditText etid = findViewById(R.id.idEditText);
        EditText etpw = findViewById(R.id.editTextText5);
        EditText etpw2 = findViewById(R.id.editTextTextPassword2);
        EditText etbirthdate = findViewById(R.id.editTextText6);
        EditText etphonenumber = findViewById(R.id.editTextText14);
        Button btn_register = (Button) findViewById(R.id.btn_register);
        Button id_button = (Button) findViewById(R.id.button5);
        ImageButton back_icon = (ImageButton) findViewById(R.id.back_icon);
        // 아이디 입력란에 텍스트가 변경될 때마다 호출되는 TextWatcher
        etid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 변경 전에 수행할 작업
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 변경되는 중에 호출됨
                if (containsHangul(charSequence.toString())) {
                    // 한글이 포함되어 있으면 Toast 메시지 출력
                    Toast.makeText(getApplicationContext(), "아이디에는 한글을 포함할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 변경 후에 수행할 작업
            }
        });


        // 비밀번호 일치 여부를 확인하는 TextWatcher 추가
        TextWatcher passwordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = etpw.getText().toString();
                String confirmPassword = etpw2.getText().toString();
                if (!password.equals(confirmPassword)) {
                    etpw2.setError("비밀번호가 일치하지 않습니다.");
                    btn_register.setEnabled(false);
                } else {
                    etpw2.setError(null);
                    btn_register.setEnabled(true);
                }
            }
        };

        etpw.addTextChangedListener(passwordWatcher);
        etpw2.addTextChangedListener(passwordWatcher);

        id_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = etid.getText().toString();
                checkDuplicateId(id);
            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = etname.getText().toString();
                id = etid.getText().toString();
                pw = etpw.getText().toString();
                pw2 = etpw2.getText().toString();
                birthdate = etbirthdate.getText().toString();
                phonenumber = etphonenumber.getText().toString();
                if (name.isEmpty() || id.isEmpty() || pw.isEmpty() || pw2.isEmpty() || birthdate.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "모든 필수 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!idChecked) {
                    Toast.makeText(getApplicationContext(), "아이디 중복 체크 버튼을 눌러주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (idDuplicate) {
                    Toast.makeText(getApplicationContext(), "이미 사용 중인 아이디입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 아이디에 한글이 포함되어 있는 경우
                if (containsHangul(id)) {
                    // 한글이 포함되어 있으면 Toast 메시지 출력
                    Toast.makeText(getApplicationContext(), "아이디에는 한글을 포함할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    return; // 더 이상 진행하지 않고 함수 종료
                }
                if (containsHangul(pw)) {
                    // 한글이 포함되어 있으면 Toast 메시지 출력
                    Toast.makeText(getApplicationContext(), "비밀번호에는 한글을 포함할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    return; // 더 이상 진행하지 않고 함수 종료
                }
                if (birthdate.length() != 8) {
                    Toast.makeText(getApplicationContext(), "생년월일은 8자로 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                checkAlreadyRegistered(name, birthdate, new AlreadyRegisteredCallback() {
                    @Override
                    public void onCheckComplete(boolean alreadyRegistered) {
//                        alreadyRegistered = false;
                        if (alreadyRegistered) {
                            Toast.makeText(getApplicationContext(), "이미 회원가입된 고객입니다.", Toast.LENGTH_SHORT).show();

                        } else {
                            addDataToFirestore(name, id, pw, birthdate, phonenumber);
                            Intent loginintent = new Intent(getApplicationContext(), login_customer.class);
                            startActivity(loginintent);
                        }
                    }
                });

            }

        });

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), login_customer.class);
                startActivity(intent);
            }

        });
    }
    // 문자열에 한글이 포함되어 있는지 확인하는 메서드
    private boolean containsHangul(String str) {
        for (char c : str.toCharArray()) {
            if ('ㄱ' <= c && c <= '힣') {
                return true;
            }
        }
        return false;
    }
    private void checkDuplicateId(String id) {
        db.collection("user")
                .document("info")
                .collection("id")
                .document(id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Toast.makeText(getApplicationContext(), "이미 사용 중인 아이디입니다.", Toast.LENGTH_SHORT).show();
                            idChecked = true;
                            idDuplicate = true;
                        } else {
                            Toast.makeText(getApplicationContext(), "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
                            idChecked = true;
                            idDuplicate = false;
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        Toast.makeText(getApplicationContext(), "아이디 확인 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                        idChecked = false;
                        idDuplicate = false;
                    }
                });
    }
    // Firestore에서 해당 아이디와 생년월일이 일치하는 회원이 있는지 확인하는 메서드
    private void checkAlreadyRegistered(String name, String birthdate, AlreadyRegisteredCallback callback) {
        db.collection("user").document("info").collection("id")
                .whereEqualTo("name", name)
                .whereEqualTo("birthdate", birthdate)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            callback.onCheckComplete(false);
                        } else {
                            callback.onCheckComplete(true);
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        callback.onCheckComplete(true);
                    }
                });
    }
        public void addDataToFirestore (String name, String id, String pw,String birthdate,String phonenumber){
            Map<String, Object> user = new HashMap<>();
            user.put("name", name);
            user.put("id", id);
            user.put("pw", pw);
            user.put("birthdate", birthdate);
            user.put("phonenumber", phonenumber);

            db.collection("user")
                    .document("info")
                    .collection("id")
                    .document(id)
                    .set(user)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

    }
    // 데이터베이스에 저장된 값과 입력한 이름과 생년월일이 일치하는지 확인하는 메서드
    interface AlreadyRegisteredCallback {
        void onCheckComplete(boolean alreadyRegistered);
    }

}
