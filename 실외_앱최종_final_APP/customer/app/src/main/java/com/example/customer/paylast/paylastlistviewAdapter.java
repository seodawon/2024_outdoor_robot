package com.example.customer.paylast;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.customer.R;
import com.example.customer.listviewItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class paylastlistviewAdapter extends BaseAdapter {
    private ArrayList<listviewItem> listviewItemList = new ArrayList<listviewItem>();
    private ArrayList<String> keys = new ArrayList<>();
    private Context context;
    private String id;
    public paylastlistviewAdapter(Context context,String id){
        this.context = context;
        this.id=id;
        loadDataFromFirebase();
    }

    //디비에서 데이터 꺼내오는 코드
    private void loadDataFromFirebase() {
        DatabaseReference database = FirebaseDatabase.getInstance("https://dbtest-customer.firebaseio.com/").getReference().child("id").child(id).child("menu");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listviewItemList.clear();
                keys.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> data = (Map<String, Object>) snapshot.getValue();
                    if (data != null) {
                        listviewItem item = new listviewItem();
                        item.setImage((String) data.get("image"));  //Firebase Realtime Database에서 데이터를 읽을 때 데이터의 형식이 Long으로 반환될 수 있기 때문
                        item.setName((String) data.get("name"));
                        item.setPrice((String) data.get("price"));
                        item.setCount(((Long) data.get("count")).intValue());
                        item.setKey((String) data.get("key"));
                        item.setState((String) data.get("state"));
                        item.setDensity((String) data.get("density"));
                        item.setShot((String) data.get("shot"));
                        item.setSyrup((String) data.get("syrup"));
                        item.setDestination((String) data.get("destination"));
                        item.setId((String) data.get("id"));

                        listviewItemList.add(item);
                        keys.add(snapshot.getKey());
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("baglistviewAdapter", "Failed to read value.", databaseError.toException());
                Toast.makeText(context, "데이터 읽기에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listviewItemList.size() ;
    }
    @Override
    public Object getItem(int position) {
        return listviewItemList.get(position) ;
    }
    @Override
    public long getItemId(int position) {
        return position ;
    }
    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.pay_last_customer, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView ImageView = (ImageView) convertView.findViewById(R.id.image) ;
        TextView nameTextView = (TextView) convertView.findViewById(R.id.name) ;
        TextView priceTextView = (TextView) convertView.findViewById(R.id.price) ;
        TextView number = convertView.findViewById(R.id.number);
        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        //현재 위치의 데이터를 baglistviewItemList에서 가져옵니다.
        listviewItem listViewItem = listviewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
//        ImageView.setImageResource(listViewItem.getImage());
        String imageUrl = listViewItem.getImage(); // assuming this returns the URL of the image

        Glide.with(context) // context를 적절한 것으로 대체해야 합니다.
                .load(imageUrl)
                .into(ImageView);

        nameTextView.setText(listViewItem.getName());
        priceTextView.setText(listViewItem.getPrice());
        number.setText(String.valueOf(listViewItem.getCount()));
        return convertView;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    //새로운 아이템을 생성하고, 이미지, 이름, 가격, 개수를 설정한 후 리스트에 추가
    public void addItem(String image, String name, String price, int count,String key,String state,String density,String shot,String syrup,String destination, String id) {
        listviewItem item = new listviewItem();

        item.setImage(image);
        item.setName(name);
        item.setPrice(price);
        item.setKey(key);
        item.setCount(count);
        item.setState(state);
        item.setDensity(density);
        item.setShot(shot);
        item.setSyrup(syrup);
        item.setDestination(destination);
        item.setId(id);
        listviewItemList.add(item);
        notifyDataSetChanged();
    }
    // 아이템 데이터 전체 삭제를 위한 함수
    public void clearItems() {
        listviewItemList.clear();
        notifyDataSetChanged();
    }
}