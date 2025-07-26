
package com.example.customer.bag;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.customer.Item;
import com.example.customer.R;
import com.example.customer.listviewItem;
import com.example.customer.menu_detail2_customer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//ListView의 각 아이템을 표시하는 BaseAdapter를 구현한 클래스
public class baglistviewAdapter extends BaseAdapter {

    private ArrayList<listviewItem> listviewItemList = new ArrayList<listviewItem>();
    private ArrayList<String> keys = new ArrayList<>();
    private Context context;
    private String id;

    public baglistviewAdapter(Context context, String id){
        //기본생성자
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
    // getCount() 메서드를 구현하여 아이템의 개수를 반환
    @Override
    public int getCount() {
        return listviewItemList.size() ;
    }
    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listviewItemList.get(position) ;
    }
    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    //getView() 메서드를 구현. 이 메서드는 각 아이템을 표시하기 위한 뷰를 반환
    //리스트뷰의 각 아이템을 표시하기 위한 뷰를 생성하거나 재사용하는 메서드
    //이 메서드는 리스트의 각 아이템을 화면에 표시할 때 호출되며, 해당 아이템의 위치(position), 이전에 사용한 뷰(convertView), 그리고 아이템의 부모 뷰 그룹(parent)을 매개변수로 받음
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        //인플레이터는 XML 레이아웃 파일을 실제 뷰 객체로 변환
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.bag_detail_customer, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        //convertView는 재사용 가능한 기존 뷰를 나타내며, 이 뷰가 null이면 새로운 뷰를 생성
        ImageView ImageView = (ImageView) convertView.findViewById(R.id.image) ;
        TextView nameTextView = (TextView) convertView.findViewById(R.id.name) ;
        TextView priceTextView = (TextView) convertView.findViewById(R.id.price) ;
        TextView number_icon = convertView.findViewById(R.id.number_icon);
        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        //현재 위치의 데이터를 baglistviewItemList에서 가져옵니다.
        listviewItem listViewItem = listviewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
//        ImageView.setImageResource(listViewItem.getImage());
        String image = listViewItem.getImage(); // assuming this returns the URL of the image

        Glide.with(context) // context를 적절한 것으로 대체해야 합니다.
                .load(image)
                .into(ImageView);
        nameTextView.setText(listViewItem.getName());
        priceTextView.setText(listViewItem.getPrice());
        number_icon.setText(String.valueOf(listViewItem.getCount()));
        ImageButton add_icon = (ImageButton)convertView.findViewById(R.id.add_icon);
        ImageButton trash_icon = (ImageButton)convertView.findViewById(R.id.trash_icon);
        ImageButton pencil_icon = (ImageButton) convertView.findViewById(R.id.pencil_icon);

        pencil_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bagIntent = new Intent(context, menu_detail2_customer.class);
                listviewItemList.get(position).getKey();
                bagIntent.putExtra("key",listviewItemList.get(position).getKey());
                context.startActivity(bagIntent);
            }
        });

        trash_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemKey = keys.get(position);
                // 아이템 삭제
                listviewItemList.remove(position);
                keys.remove(position);
                // 데이터베이스에서도 정보 삭제
                DatabaseReference databaseRef = FirebaseDatabase.getInstance("https://dbtest-customer.firebaseio.com/").getReference().child("id").child(id).child("menu");
                databaseRef.child(itemKey).removeValue();
                // 어댑터 갱신
                notifyDataSetChanged();
            }
        });
        add_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count= listViewItem.getCount();
                count++;
                listViewItem.setCount(count);
                number_icon.setText(String.valueOf(count)); // 변경된 개수 반영
                DatabaseReference databaseRef = FirebaseDatabase.getInstance("https://dbtest-customer.firebaseio.com/").getReference().child("id").child(id).child("menu").child(listViewItem.getKey());
                databaseRef.child("count").setValue(count);
                notifyDataSetChanged(); // 아이템 뷰 갱신
            }
        });
        ImageButton minus_icon = (ImageButton)convertView.findViewById(R.id.minus_icon);
        minus_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = listViewItem.getCount();
                if (count > 1) {
                    count--;
                    listViewItem.setCount(count);
                    number_icon.setText(String.valueOf(count)); // 변경된 개수 반영
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance("https://dbtest-customer.firebaseio.com/").getReference().child("id").child(id).child("menu").child(listViewItem.getKey());
                    databaseRef.child("count").setValue(count);
                    notifyDataSetChanged(); // 아이템 뷰 갱신
                }
            }
        });

        return convertView;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    //새로운 아이템을 생성하고, 이미지, 이름, 가격, 개수를 설정한 후 리스트에 추가
    public void addItem(String image, String name, String price, int count,String key,String state,String density,String shot,String syrup) {
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
//        item.setDestination(destination);
//        item.setId(id);
        listviewItemList.add(item);
        notifyDataSetChanged();
    }
    // 아이템 데이터 전체 삭제를 위한 함수
    public void clearItems() {
        listviewItemList.clear();
        notifyDataSetChanged();
    }
}