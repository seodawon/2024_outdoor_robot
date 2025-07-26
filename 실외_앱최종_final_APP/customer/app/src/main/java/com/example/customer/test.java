package com.example.customer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class test extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView imageView;
    private EditText editText;
    private String tlqkf = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
// Firebase 초기화
        FirebaseApp.initializeApp(this);
        imageView = findViewById(R.id.show_image);
        editText = findViewById(R.id.filename);
        Button selectImageButton = findViewById(R.id.select_image_button);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        Button uploadImageButton = findViewById(R.id.upload_image_button);
        uploadImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tlqkf = editText.getText().toString();
                if (imageUri != null) {
                    uploadImageToFirebase(imageUri, tlqkf );
                } else {
                    Log.e("MainActivity", "No image selected");
                }
            }
        });
// Firestore에서 이미지 로드
// loadImageFromFirestore();
    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            imageUri = data.getData();
        }
    }
    private void uploadImageToFirebase(Uri imageUri, String fileName) {
// Firestorage 참조 생성
        StorageReference storageReference =
                FirebaseStorage.getInstance().getReference().child("images/" + fileName);
// 이미지 업로드
        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
// 업로드 성공 후URL 가져오기
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                                           @Override
                                                                                           public void onSuccess(Uri uri) {
                                                                                               String imageUrl = uri.toString();
// URL을Firestore에 저장하는 메서드 호출
                                                                                               saveImageInfoToFirestore(imageUrl);
                                                                                           }
                                                                                       }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
// URL 가져오기 실패 시 처리
                                Log.e("Get URL Error", e.getMessage());
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
// 업로드 실패 시 처리
                        Log.e("Upload Error", e.getMessage());
                    }
                });
    }
    private void saveImageInfoToFirestore(String imageUrl) {
// Firestore 인스턴스 가져오기
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        tlqkf = editText.getText().toString();
// 저장할 데이터 생성
        Map<String, Object> imageData = new HashMap<>();
        imageData.put("url", imageUrl);
        imageData.put("productName",editText.getText().toString() );
//imageData.put("timestamp", FieldValue.serverTimestamp());
// Firestore에 데이터 저장
        db.collection("images")
                .add(imageData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
// 데이터 저장 성공 시 처리
                        Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
// 이미지를Firestore에 저장한 후 다시 이미지 불러오기
//loadImageFromFirestore();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
// 데이터 저장 실패 시 처리
                        Log.e("Firestore", "Error adding document", e);
                    }
                });
    }
}
