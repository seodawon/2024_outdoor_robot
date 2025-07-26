package com.example.market;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.market.R;
import com.example.market.listviewItem;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

// ListView의 각 아이템을 표시하는 ArrayAdapter를 구현한 클래스
public class orderListAdapter extends BaseAdapter {

    private Context context;
    private String id;
    String time1;
    int selectedCardValue;
    String selectedCard,selectedCard2;
    Spinner cardSpinner;
    private DatabaseReference database,data,managerDatabase;
    DatabaseReference currentItemRef;
    String marketAccept,mdestination,adminAccept;
    private boolean dataLoaded = false; // 데이터가 로드되었는지 여부를 추적하는 변수
    private ArrayList<listviewItem> listviewItemList = new ArrayList<listviewItem>();
    public orderListAdapter(Context context, ArrayList<listviewItem> listviewItemList) {
        this.context = context;
        this.listviewItemList=listviewItemList;
        this.database = FirebaseDatabase.getInstance("https://dbtest-market.firebaseio.com/").getReference();
        this.data = FirebaseDatabase.getInstance("https://dbtest-customer.firebaseio.com/").getReference();
        this.managerDatabase = FirebaseDatabase.getInstance("https://dbtest-admin.firebaseio.com/").getReference();
        loadDataFromFirebase();
    }

    private void loadDataFromFirebase() {
        if (dataLoaded) return; // 이미 데이터가 로드되었으면 리턴
        DatabaseReference database = FirebaseDatabase.getInstance("https://dbtest-market.firebaseio.com/").getReference();

        database.child("market").child("id").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<listviewItem> updatedList = new ArrayList<>();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey(); // 사용자 아이디
                    listviewItem item = new listviewItem();
                    item.setId(userId);
                    // marketAccept 초기값 설정
                    marketAccept = userSnapshot.child("information").child("marketAccept").getValue(String.class);
                    mdestination = userSnapshot.child("information").child("mdestination").getValue(String.class);
                    item.setMdestination("1847"); // 항상 "1847"로 값을 설정합니다.
                    database.child("market").child("id").child(userId).child("information").child("mdestination").setValue("1847");

                    if (marketAccept == null) {
                        item.setMarketAccept("기본");
                        database.child("market").child("id").child(userId).child("information").child("marketAccept").setValue("기본");
                        data.child("id").child(userId).child("information").child("marketAccept").setValue("기본");
                    } else {
                        item.setMarketAccept(marketAccept);
                    }
                    //가변적인 문자열을 다루며, 문자열을 추가하거나 수정할 때 유용하게 사용
                    StringBuilder nameBuilder = new StringBuilder();
                    int menuCount = 0;
                    for (DataSnapshot menuSnapshot : userSnapshot.child("menu").getChildren()) {
                        String name = menuSnapshot.child("name").getValue(String.class);
                        if (name != null) {
                            if (menuCount > 0) {
                                nameBuilder.append(" / ");
                            }
                            nameBuilder.append(name);
                            menuCount++;

                            int maxLength = 12;
                            if (nameBuilder.length() > maxLength) {
                                nameBuilder.setLength(maxLength);
                                nameBuilder.append("...");
                                break;
                            }
                        }
                    }
                    item.setName(nameBuilder.toString());
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
            convertView = inflater.inflate(R.layout.orderlist_market, parent, false);

            holder = new ViewHolder();
            holder.cardSpinner = convertView.findViewById(R.id.card);
            holder.card2Spinner = convertView.findViewById(R.id.card2);
            holder.menuTextView = convertView.findViewById(R.id.menu);
            holder.checkBox = convertView.findViewById(R.id.checkBox);
            holder.cardlayout = convertView.findViewById(R.id.cardlayout);
            holder.agreerefuse = convertView.findViewById(R.id.agreerefuse);
            holder.card2layout = convertView.findViewById(R.id.card2layout);
            holder.deliveryRequest = convertView.findViewById(R.id.deliveryRequest);
            holder.deliveryRequested = convertView.findViewById(R.id.deliveryRequested);
            holder.refused = convertView.findViewById(R.id.refused);
            holder.orderlist_item_button = convertView.findViewById(R.id.orderlist_item_button);
            holder.agree_button = convertView.findViewById(R.id.agree_button);
            holder.refuse_button = convertView.findViewById(R.id.refuse_button);
            holder.deliveryRequest_button = convertView.findViewById(R.id.deliveryRequest_button);
            holder.confirm_button1 = convertView.findViewById(R.id.confirm_button1);
            holder.confirm_button2 = convertView.findViewById(R.id.confirm_button2);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        listviewItem listViewItem = listviewItemList.get(position);
        holder.menuTextView.setText(listViewItem.getName());

        adminAccept = listViewItem.getAdminAccept();
        if (adminAccept != null) {
            switch (adminAccept) {
                case "true":
                    holder.checkBox.setText("배달수락"); // 체크박스 텍스트 설정
                    holder.checkBox.setChecked(true);
                    holder.checkBox.setEnabled(false);// 체크박스 체크
                    break;
                case "false":
                    holder.checkBox.setText("배달거절"); // 체크박스 텍스트 설정
                    holder.checkBox.setChecked(true);
                    holder.checkBox.setEnabled(false);// 체크박스 체크 해제
                    break;
                default:
                    holder.checkBox.setText("배달수락/거절"); // 체크박스 텍스트 설정
                    holder.checkBox.setChecked(false);
                    holder.checkBox.setEnabled(false);// 기본적으로 체크박스 체크 해제
                    break;
            }
        } else {
            holder.checkBox.setText("배달"); // 체크박스 텍스트 설정
            holder.checkBox.setChecked(false); // 기본적으로 체크박스 체크 해제
        }
        marketAccept = listViewItem.getMarketAccept();
        // 초기 버튼 상태 설정
        holder.cardlayout.setVisibility(View.GONE);
        holder.agreerefuse.setVisibility(View.VISIBLE);
        holder.card2layout.setVisibility(View.GONE);
        holder.deliveryRequest.setVisibility(View.GONE);
        holder.deliveryRequested.setVisibility(View.GONE);
        holder.refused.setVisibility(View.GONE);
        // Firebase 경로 설정
        DatabaseReference itemRef = database.child("market").child("id").child(listViewItem.getId()).child("information");
        // Firebase에서 가져온 상태에 따라 버튼 상태 업데이트
        switch (marketAccept) {
            case "기본":
                holder.cardlayout.setVisibility(View.GONE);
                holder.agreerefuse.setVisibility(View.VISIBLE);
                holder.card2layout.setVisibility(View.GONE);
                holder.deliveryRequest.setVisibility(View.GONE);
                holder.deliveryRequested.setVisibility(View.GONE);
                holder.refused.setVisibility(View.GONE);
                break;
            case "거절":
                holder.cardlayout.setVisibility(View.GONE);
                holder.agreerefuse.setVisibility(View.GONE);
                holder.card2layout.setVisibility(View.VISIBLE);
                holder.deliveryRequest.setVisibility(View.GONE);
                holder.deliveryRequested.setVisibility(View.GONE);
                holder.refused.setVisibility(View.GONE);
                break;
            case "배달거절됨":
                holder.cardlayout.setVisibility(View.GONE);
                holder.agreerefuse.setVisibility(View.GONE);
                holder.card2layout.setVisibility(View.GONE);
                holder.deliveryRequest.setVisibility(View.GONE);
                holder.deliveryRequested.setVisibility(View.GONE);
                holder.refused.setVisibility(View.VISIBLE);
                break;
            case "수락":
                holder.cardlayout.setVisibility(View.VISIBLE);
                holder.agreerefuse.setVisibility(View.GONE);
                holder.card2layout.setVisibility(View.GONE);
                holder.deliveryRequest.setVisibility(View.GONE);
                holder.deliveryRequested.setVisibility(View.GONE);
                holder.refused.setVisibility(View.GONE);
                break;
            case "배달요청됨":
                holder.cardlayout.setVisibility(View.GONE);
                holder.agreerefuse.setVisibility(View.GONE);
                holder.card2layout.setVisibility(View.GONE);
                holder.deliveryRequest.setVisibility(View.GONE);
                holder.deliveryRequested.setVisibility(View.VISIBLE);
                holder.refused.setVisibility(View.GONE);
                break;
            case "배달요청":
                holder.cardlayout.setVisibility(View.GONE);
                holder.agreerefuse.setVisibility(View.GONE);
                holder.card2layout.setVisibility(View.GONE);
                holder.deliveryRequest.setVisibility(View.VISIBLE);
                holder.deliveryRequested.setVisibility(View.GONE);
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
                itemRef.child("marketAccept").setValue("거절").addOnCompleteListener(task -> {
                });
            }
        });

        holder.confirm_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCard2 = holder.card2Spinner.getSelectedItem().toString();
                currentItemRef = database.child("market").child("id").child(listViewItem.getId());
                currentItemRef.child("information").child("reason").setValue(selectedCard2);
                currentItemRef.child("information").child("mtime").setValue("");
                // Firebase에 상태 저장
                itemRef.child("marketAccept").setValue("배달거절됨").addOnCompleteListener(task -> {
                    data.child("id").child(listViewItem.getId()).child("information").child("marketAccept").setValue("false");
                    data.child("id").child(listViewItem.getId()).child("information").child("reason").setValue(selectedCard2);
                });
            }
        });

        holder.agree_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Firebase에 상태 저장
                itemRef.child("marketAccept").setValue("수락").addOnCompleteListener(task -> {
                });
            }
        });

        holder.confirm_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCard = holder.cardSpinner.getSelectedItem().toString();
                selectedCardValue = Integer.parseInt(selectedCard.replaceAll("[^0-9]", ""));
                time1 = String.valueOf(selectedCardValue);
                // Firebase에 상태 저장
                itemRef.child("marketAccept").setValue("배달요청").addOnCompleteListener(task -> {
                });
            }
        });

        holder.deliveryRequest_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Firebase에 상태 저장
                itemRef.child("marketAccept").setValue("배달요청됨").addOnCompleteListener(task -> {
                    currentItemRef = database.child("market").child("id").child(listViewItem.getId());
                    currentItemRef.child("information").child("mtime").setValue(time1);
                    currentItemRef.child("information").child("reason").setValue("");
                    currentItemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // 하위 데이터 복사
                            managerDatabase.child("admin").child("id").child(listViewItem.getId()).setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if (databaseError != null) {
                                        Log.d("orderListAdapter", "Copy failed", databaseError.toException());
                                    } else {
                                        Log.d("orderListAdapter", "Copy success");
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("orderListAdapter", "Copy canceled", databaseError.toException());
                        }
                    });
                    data.child("id").child(listViewItem.getId()).child("information").child("marketAccept").setValue("true");
                    managerDatabase.child("admin").child("id").child(listViewItem.getId()).child("information").child("marketAccept").setValue("배달요청됨");
                    data.child("id").child(listViewItem.getId()).child("information").child("mtime").setValue(time1);
                });
            }
        });
        holder.orderlist_item_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, menu_main.class);
                intent.putExtra("userId", listViewItem.getId()); // 클릭한 아이템의 ID를 Intent에 추가
                context.startActivity(intent);
            }
        });

        return convertView;
    }
    static class ViewHolder {
        TextView menuTextView,blankTextView;
        LinearLayout cardlayout, agreerefuse, card2layout, deliveryRequest, deliveryRequested, refused;
        ImageButton orderlist_item_button;
        Button agree_button, refuse_button, deliveryRequest_button, confirm_button1, confirm_button2,deliveryRequested_button;
        CheckBox checkBox;
        Spinner cardSpinner,card2Spinner;
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