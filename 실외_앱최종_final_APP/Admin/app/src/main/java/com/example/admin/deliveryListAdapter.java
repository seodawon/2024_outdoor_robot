package com.example.admin;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.admin.R;
import com.example.admin.listviewItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

// ListView의 각 아이템을 표시하는 ArrayAdapter를 구현한 클래스
public class deliveryListAdapter extends BaseAdapter {

    private Context context;
    int selectedCardValue;
    String time2;
    private String id;
    String selectedCard,selectedCard2;
    private DatabaseReference database,data,adminDatabase,currentItemRef;
    String  adminAccept,mdestination,destination;
    private boolean dataLoaded = false; // 데이터가 로드되었는지 여부를 추적하는 변수
    private ArrayList<listviewItem> listviewItemList = new ArrayList<listviewItem>();
    public deliveryListAdapter(Context context, ArrayList<listviewItem> listviewItemList) {
        this.context = context;
        this.listviewItemList=listviewItemList;
        this.database = FirebaseDatabase.getInstance("https://dbtest-market.firebaseio.com/").getReference();
        this.data = FirebaseDatabase.getInstance("https://dbtest-customer.firebaseio.com/").getReference();
        this.adminDatabase = FirebaseDatabase.getInstance("https://dbtest-admin.firebaseio.com/").getReference();
        loadDataFromFirebase();
    }

    private void loadDataFromFirebase() {
        if (dataLoaded) return; // 이미 데이터가 로드되었으면 리턴
        DatabaseReference adminDatabase = FirebaseDatabase.getInstance("https://dbtest-admin.firebaseio.com/").getReference();

        adminDatabase.child("admin").child("id").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<listviewItem> updatedList = new ArrayList<>();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey(); // 사용자 아이디
                    listviewItem item = new listviewItem();
                    item.setId(userId);
//                    Log.d("dlfksjd","uynynhyhbtbgtvg");
                    // marketAccept 초기값 설정
                    adminAccept = userSnapshot.child("information").child("adminAccept").getValue(String.class);
                    if ( adminAccept == null) {
                        item.setAdminAccept("기본");
                        adminDatabase.child("admin").child("id").child(userId).child("information").child("adminAccept").setValue("기본");
                        database.child("market").child("id").child(userId).child("information").child("adminAccept").setValue("기본");
                        data.child("id").child(userId).child("information").child("adminAccept").setValue("기본");
                    } else {
                        item.setAdminAccept( adminAccept);
                    }
                    // destination 및 mdestination 설정
                    destination = userSnapshot.child("information").child("destination").getValue(String.class);
                    mdestination = userSnapshot.child("information").child("mdestination").getValue(String.class);
                    item.setDestination(destination);
                    item.setMdestination(mdestination);

                    adminAccept = userSnapshot.child("information").child("adminAccept").getValue(String.class);
                    item.setAdminAccept(adminAccept);
                    // 새로운 데이터를 리스트의 맨 앞에 추가
                    updatedList.add(0, item);
                }

                listviewItemList.clear();
                listviewItemList.addAll(updatedList);
                notifyDataSetChanged();
                dataLoaded = true; // 데이터 로드 완료 상태로 변경
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("baglistviewAdapter", "Failed to read value.", databaseError.toException());
                Toast.makeText(context, "데이터 읽기에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public int getCount() {
        return listviewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listviewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (!dataLoaded) {
            loadDataFromFirebase(); // 데이터가 로드되지 않았으면 로드
        }
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.deliverylist_detail, parent, false);

            holder = new ViewHolder();
            holder.cardSpinner = convertView.findViewById(R.id.card);
            holder.card2Spinner = convertView.findViewById(R.id.card2);
            holder.marketTextView = convertView.findViewById(R.id.market);
            holder.customerTextView = convertView.findViewById(R.id.customer);
            holder.cardlayout = convertView.findViewById(R.id.cardlayout);
            holder.agreerefuse = convertView.findViewById(R.id.agreerefuse);
            holder.card2layout = convertView.findViewById(R.id.card2layout);
            holder.card3layout = convertView.findViewById(R.id.card3layout);
            holder.robotRequested = convertView.findViewById(R.id.robotRequested);
            holder.refused = convertView.findViewById(R.id.refused);
            holder.orderlist_item_button = convertView.findViewById(R.id.orderlist_item_button);
            holder.agree_button = convertView.findViewById(R.id.agree_button);
            holder.refuse_button = convertView.findViewById(R.id.refuse_button);
            holder.deliveryRequest_button = convertView.findViewById(R.id.deliveryRequest_button);
            holder.confirm_button1 = convertView.findViewById(R.id.confirm_button1);
            holder.confirm_button2 = convertView.findViewById(R.id.confirm_button2);
            holder.confirm_button3 = convertView.findViewById(R.id.confirm_button3);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        listviewItem listViewItem = listviewItemList.get(position);

        adminAccept = listViewItem.getAdminAccept();
        holder.marketTextView.setText(listViewItem.getMdestination());
        holder.customerTextView.setText(listViewItem.getDestination());
        DatabaseReference itemRef = adminDatabase.child("admin").child("id").child(listViewItem.getId()).child("information");
        // Firebase에서 가져온 상태에 따라 버튼 상태 업데이트
        switch (adminAccept) {
            case "기본":
                holder.cardlayout.setVisibility(View.GONE);
                holder.agreerefuse.setVisibility(View.VISIBLE);
                holder.card2layout.setVisibility(View.GONE);
                holder.card3layout.setVisibility(View.GONE);
                holder.robotRequested.setVisibility(View.GONE);
                holder.refused.setVisibility(View.GONE);
                break;
            case "거절":
                holder.cardlayout.setVisibility(View.GONE);
                holder.agreerefuse.setVisibility(View.GONE);
                holder.card2layout.setVisibility(View.VISIBLE);
                holder.robotRequested.setVisibility(View.GONE);
                holder.card3layout.setVisibility(View.GONE);
                holder.refused.setVisibility(View.GONE);
                break;
            case "배달거절됨":
                holder.cardlayout.setVisibility(View.GONE);
                holder.agreerefuse.setVisibility(View.GONE);
                holder.card2layout.setVisibility(View.GONE);
                holder.robotRequested.setVisibility(View.GONE);
                holder.card3layout.setVisibility(View.GONE);
                holder.refused.setVisibility(View.VISIBLE);
                break;
            case "수락":
                holder.cardlayout.setVisibility(View.VISIBLE);
                holder.agreerefuse.setVisibility(View.GONE);
                holder.card2layout.setVisibility(View.GONE);
                holder.robotRequested.setVisibility(View.GONE);
                holder.card3layout.setVisibility(View.GONE);
                holder.refused.setVisibility(View.GONE);
                break;
            case "로봇배정됨":
                holder.cardlayout.setVisibility(View.GONE);
                holder.agreerefuse.setVisibility(View.GONE);
                holder.card2layout.setVisibility(View.GONE);
                holder.robotRequested.setVisibility(View.VISIBLE);
                holder.card3layout.setVisibility(View.GONE);
                holder.refused.setVisibility(View.GONE);
                break;
            case "로봇선택":
                holder.cardlayout.setVisibility(View.GONE);
                holder.agreerefuse.setVisibility(View.GONE);
                holder.card2layout.setVisibility(View.GONE);
                holder.robotRequested.setVisibility(View.GONE);
                holder.card3layout.setVisibility(View.VISIBLE);
                holder.refused.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        // 버튼 클릭 이벤트 처리
        holder.refuse_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Firebase에 상태 저장
                itemRef.child("adminAccept").setValue("거절").addOnCompleteListener(task -> {
                });
            }
        });
        holder.confirm_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCard = holder.cardSpinner.getSelectedItem().toString();
                selectedCardValue = Integer.parseInt(selectedCard.replaceAll("[^0-9]", ""));
                time2 = String.valueOf(selectedCardValue);
                // Firebase에 상태 저장
                itemRef.child("adminAccept").setValue("로봇선택").addOnCompleteListener(task -> {
                });
            }
        });
        holder.confirm_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCard2 = holder.card2Spinner.getSelectedItem().toString();
                currentItemRef = adminDatabase.child("admin").child("id").child(listViewItem.getId());
                currentItemRef.child("information").child("reason").setValue(selectedCard2);
                // Firebase에 상태 저장
                // Firebase에 상태 저장
                itemRef.child("adminAccept").setValue("배달거절됨").addOnCompleteListener(task -> {
                    data.child("id").child(listViewItem.getId()).child("information").child("adminAccept").setValue("false");
                    data.child("id").child(listViewItem.getId()).child("information").child("reason").setValue(selectedCard2);
                    database.child("market").child("id").child(listViewItem.getId()).child("information").child("adminAccept").setValue("false");
                });
            }
        });
        holder.confirm_button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Firebase에 상태 저장
                itemRef.child("adminAccept").setValue("로봇배정됨").addOnCompleteListener(task -> {
                    currentItemRef = adminDatabase.child("admin").child("id").child(listViewItem.getId());
                    currentItemRef.child("information").child("atime").setValue(time2);
                    data.child("id").child(listViewItem.getId()).child("information").child("adminAccept").setValue("true");
                    data.child("id").child(listViewItem.getId()).child("information").child("atime").setValue(time2);
                    database.child("market").child("id").child(listViewItem.getId()).child("information").child("adminAccept").setValue("true");
                });
            }
        });

        holder.agree_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Firebase에 상태 저장


                itemRef.child("adminAccept").setValue("수락").addOnCompleteListener(task -> {
                });
            }
        });

        holder.deliveryRequest_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Firebase에 상태 저장
                itemRef.child("adminAccept").setValue("로봇배정됨").addOnCompleteListener(task -> {
                });
            }
        });
        holder.orderlist_item_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, navi.class);
                intent.putExtra("market", listViewItem.getMdestination());
                intent.putExtra("customer", listViewItem.getDestination());// 클릭한 아이템의 ID를 Intent에 추가
                intent.putExtra("userId",listViewItem.getId());
                adminDatabase.child("admin").child("id").child(listViewItem.getId()).child("information").child("state").setValue("true");
                context.startActivity(intent);
            }
        });

        return convertView;
    }
    static class ViewHolder {
        TextView marketTextView,customerTextView;
        LinearLayout cardlayout, agreerefuse, card2layout, deliveryRequest, robotRequested, refused,card3layout;
        ImageButton orderlist_item_button;
        Spinner cardSpinner,card2Spinner;
        Button agree_button, refuse_button, deliveryRequest_button, confirm_button1, confirm_button2,deliveryRequested_button,confirm_button3;
    }

    public void addItem(String id, String name, String price, int count, String key, String state, String density, String shot, String syrup) {
        listviewItem item = new listviewItem();
        item.setId(id);
        item.setName(name);
        item.setPrice(price);
        item.setKey(key);
        item.setCount(count);
        item.setState(state);
        item.setDensity(density);
        item.setShot(shot);
        item.setSyrup(syrup);

        listviewItemList.add(item);
        notifyDataSetChanged();
    }

    public void clearItems() {
        listviewItemList.clear();
        notifyDataSetChanged();
    }
}