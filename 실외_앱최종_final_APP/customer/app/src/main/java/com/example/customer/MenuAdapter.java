package com.example.customer;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;

import com.bumptech.glide.Glide;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.customer.R;
import com.example.customer.bag.bag_main;
import com.example.customer.menu_detail_customer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//RecyclerView에 표시할 메뉴 데이터의 리스트를 담고 있습니다.->menuItems
    private ArrayList<MenuItem> menuItems;
    private Context context;
    int image;
    String name,price;
    public MenuAdapter(ArrayList<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.page, parent, false);
        return new ViewHolder(view);
    }
    @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        MenuItem menuItem = menuItems.get(position);
        String name = menuItem.getName();
        viewHolder.nameText.setText(name);
//        viewHolder.nameText.setText(menuItem.getName());
        String price = menuItem.getPrice();
        viewHolder.priceText.setText(price);
//        viewHolder.priceText.setText(menuItem.getPrice());
        String image = menuItem.getImage();
        Glide.with(viewHolder.imageImage.getContext())
                .load(image)
                .into(viewHolder.imageImage);

        // 이미지 버튼 클릭 시 상세 페이지로 이동하는 코드 추가
        viewHolder.imageImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), menu_detail_customer.class);
                intent.putExtra("image", image);  // 이미지 URL 또는 리소스 ID
                intent.putExtra("name", name);       // name 변수 추가
                intent.putExtra("price", price);     // price 변수 추가
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton imageImage;
        TextView nameText, priceText;
        int count,image;
        String state,density,shot,syrup,destination,id,name,price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageImage = itemView.findViewById(R.id.image);
            nameText = itemView.findViewById(R.id.name);
            priceText = itemView.findViewById(R.id.price);

        }
    }
}