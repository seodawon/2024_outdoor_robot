package com.example.customer;

import static android.content.ContentValues.TAG;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import com.example.customer.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.customer.R;
import com.example.customer.bag.bag_main;
import com.example.customer.market_choice_customer;
import com.example.customer.menu_detail_customer;
import com.example.customer.my_customer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class menu_choice_customer extends AppCompatActivity {
    private  Button coffee, beverage,ade,tea,smoothie,frappe;
    ImageButton user, back_icon, esfreso_image, americano_image, caffelatte_image,
            banailla_latte_image, caramel_latte_image, nut_latte_image, sugar_latte_image, caffe_mocca_image,
            caramel_mocca_image, java_mocca_image, mint_caffe_mocca_image, caramel_makki_image, caffuchino_image,
            moccapanfra_image, mintchopan_image, javapan_image, cookiepan_image, banilliapan_image, caramelpan_image,
            chocolatte_image, javachocolatte_image, mintchocolatte_image, sweetpotapolatte_image, banillatte_image, glainlatte_image,
            greenlatte_image, cookielatte_image, blueberrylatte_image, icetea_image, roisbostea_image, camomile_image,
            pepermint_image, combtea_image, earlgray_image, grapefruittea_image, lemontea_image, lemonappletea_image, naveltea_image,
            redapple_image, grapefruitaid_image, lemonade_image, citronade_image, greengrapeade_image, yogurtsmot_image, strawsmot_image,
            blueberrysmot_image, bag;
    private RecyclerView recyclerView;
    private com.example.customer.MenuAdapter adapter; // 수정된 부분
    private ArrayList<MenuItem> menuItems;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_choice_floor_customer);
        db = FirebaseFirestore.getInstance();
        coffee = findViewById((R.id.coffee));
        beverage = findViewById((R.id.beverage));
        ade = findViewById((R.id.ade));
        tea = findViewById((R.id.tea));
        smoothie = findViewById((R.id.smoothie));
        frappe = findViewById((R.id.frappe));
        user = (ImageButton) findViewById(R.id.user);
        bag = (ImageButton) findViewById(R.id.bag);
        back_icon = (ImageButton) findViewById(R.id.back_icon);
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        menuItems = new ArrayList<>();
        adapter =new com.example.customer.MenuAdapter(menuItems);
        recyclerView = findViewById(R.id.framelayout);
//        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
//        recyclerView.setAdapter(adapter);
//        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
//        recyclerView.setAdapter(adapter);
////        recyclerView.setLayoutManager(layoutManager);
//
//        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                // position에 따라서 아이템의 너비 비율을 반환
//                switch (position % 4) { // 4개의 아이템마다 너비 비율을 조절
//                    case 0:
//                        return 2; // 첫 번째 아이템은 전체 너비의 2/3
//                    case 1:
//                    case 2:
//                        return 1; // 두 번째와 세 번째 아이템은 각각 전체 너비의 1/3
//                    case 3:
//                        return 3; // 네 번째 아이템은 전체 너비의 1
//                    default:
//                        return 1;
//                }
//            }
//        });
        // GridLayoutManager 설정
        int spanCount = 3; // 한 줄에 표시할 아이템 수
        GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        recyclerView.setAdapter(adapter);

// SpanSizeLookup 설정
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1; // 모든 아이템의 너비는 전체 너비의 1/3으로 설정
            }
        });

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), market_choice_customer.class);
                startActivity(intent);
            }
        });
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), my_customer.class);
                startActivity(intent);
            }
        });
        bag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), bag_main.class);
                startActivity(intent);
            }
        });
      // 각 버튼에 대한 클릭 리스너 설정
        coffee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchMenuItems("coffee"); // 커피 메뉴 데이터 조회
            }
        });

        beverage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchMenuItems("beverage"); // 음료 메뉴 데이터 조회
            }
        });

        ade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchMenuItems("ade"); // 에이드 메뉴 데이터 조회
            }
        });

        tea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchMenuItems("tea"); // 차 메뉴 데이터 조회
            }
        });

        smoothie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchMenuItems("smoothie"); // 스무디 메뉴 데이터 조회
            }
        });

        frappe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchMenuItems("frappe"); // 프라페 메뉴 데이터 조회
            }
        });

        // 초기 데이터 로드 (기본적으로 coffee 컬렉션을 조회)
        fetchMenuItems("coffee");
    }

    private void fetchMenuItems(String collectionName) {
        menuItems.clear(); // 기존 메뉴 아이템 클리어

        db.collection("1847").document("menu_items").collection(collectionName).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {

                                String name = document.getString("name");
                                String price = document.getString("price");
                                String imageUrl = document.getString("imageUrl"); // 이미지 URL 가져오기
                                MenuItem menuItem = new MenuItem(name, price,imageUrl);
                                menuItems.add(menuItem);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}