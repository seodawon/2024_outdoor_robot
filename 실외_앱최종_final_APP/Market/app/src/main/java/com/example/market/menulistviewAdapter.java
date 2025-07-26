package com.example.market;

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
public class menulistviewAdapter extends BaseAdapter {
    private ArrayList<listviewItem> listviewItemList = new ArrayList<listviewItem>();
    private ArrayList<String> keys = new ArrayList<>();
    private Context context;
    private String id;

    public menulistviewAdapter(Context context,  ArrayList<listviewItem> listviewItemList, String id){
        //기본생성자
        this.context = context;
        this.listviewItemList = listviewItemList;
        this.id = id;
        loadDataFromFirebase();
    }
    // 디비에서 데이터 꺼내오는 코드
    private void loadDataFromFirebase() {
        if (id == null || id.isEmpty()) {
            Toast.makeText(context, "ID가 설정되지 않았습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference database = FirebaseDatabase.getInstance("https://dbtest-market.firebaseio.com/").getReference();
        database.child("market").child("id").child(id).child("menu").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listviewItemList.clear();
                keys.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    listviewItem item = snapshot.getValue(listviewItem.class);
                    if (item != null) {
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
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.menu_floor, parent, false);
            holder = new ViewHolder();
            holder.nameTextView = convertView.findViewById(R.id.name);
            holder.priceTextView = convertView.findViewById(R.id.price);
            holder.countTextView = convertView.findViewById(R.id.count);
            holder.stateTextView = convertView.findViewById(R.id.state);
            holder.densityTextView = convertView.findViewById(R.id.density);
            holder.shotTextView = convertView.findViewById(R.id.shot);
            holder.syrupTextView = convertView.findViewById(R.id.syrup);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        listviewItem item = listviewItemList.get(position);
        holder.nameTextView.setText(item.getName());
        holder.priceTextView.setText(item.getPrice());
        holder.countTextView.setText(String.valueOf(item.getCount()));
        holder.stateTextView.setText(item.getState());
        holder.densityTextView.setText(item.getDensity());
        holder.shotTextView.setText(item.getShot());
        holder.syrupTextView.setText(item.getSyrup());

        return convertView;
    }

    static class ViewHolder {
        TextView nameTextView;
        TextView priceTextView;
        TextView countTextView;
        TextView stateTextView;
        TextView densityTextView;
        TextView shotTextView;
        TextView syrupTextView;
    }

    // 아이템 추가 메서드
    public void addItem(listviewItem item) {
        listviewItemList.add(item);
        notifyDataSetChanged();
    }

    // 아이템 전체 삭제 메서드
    public void clearItems() {
        listviewItemList.clear();
        notifyDataSetChanged();
    }
}