package com.example.customer;

//BagItem : 쇼핑 앱의 장바구니에 담기는 아이템의 정보를 관리하기 위한 클래스
public class Item {
    //3가지 필드 가짐 : 클래스 내에서 데이터를 저장하는 변수들을 선언하는 것을 의미
    private String image; // 이미지 리소스 ID
    private String name,state,density,shot,syrup,destination,id, price,key,number,adminAccept,marketAccept;
    private int count;
    // Firebase에서 객체를 가져오기 위해 기본 생성자가 필요합니다.
    public Item() {
    }
    public Item(String image, String name, String price, int count, String key,String state, String density, String shot, String syrup) {
        this.image = image;
        this.name = name;
        this.price = price;
        this.count = count;
        this.key = key;
        this.state = state;
        this.density = density;
        this.shot = shot;
        this.syrup = syrup;
    }

    public Item(String destination, String id,String number) {
        this.destination = destination;
        this.id=id;
        this.number=number;
    }

    //drawable 타입의 객체-> image
    public String getImage() {
    return image;
}

    public String getName() {
        return name;
    }
    public String getKey() {
        return key;
    }

    public String getState() {
        return state;
    }

    public String getDensity() {
        return density;
    }

    public String getShot() {
        return shot;
    }

    public String getSyrup() {
        return syrup;
    }

    public String getPrice() {
        return price;
    }

    public int getCount() {
        return count;
    }
    public String getDestination() {
        return destination;
    }
    public String getId() {
        return id;
    }
    public String getAdminAccept() {
        return adminAccept;
    }
    public String getMarketAccept() {
        return marketAccept;
    }
    public String getNumber() {
        return number;
    }



    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setDensity(String density) {
        this.density = density;
    }
    public void setShot(String shot) {
        this.shot = shot;
    }
    public void setSyrup(String syrup) {
        this.syrup= syrup;
    }

    public void setPrice(String price) {
        this.price = price;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setAdminAccept(String adminAccept) {
        this.adminAccept =adminAccept;
    }
    public void setMarketAccept(String marketAccept) {
        this.marketAccept = marketAccept;
    }
    public void setNumber(String number) {
        this.number= number;
    }
}
