package com.example.admin;
//인스턴스 :클래스에서 생성된 구체적인 객체
//인스턴스 변수 :클래스 내에 선언된 변수로, 각 인스턴스마다 별도로 존재

//baglistviewItem : 장바구니 리스트뷰에 표시될 아이템
public class listviewItem {
    //이미지,이름,가격,개수를 필드(인스턴스 변수:클래스 내부에 선언된 변수,클래스의 인스턴스를 생성할 때마다 개별 객체가 가지는 필드)로 지정
//    private String image;
    private String name,market,customer;
    private String price,state,density,shot,syrup,key,destination,id,marketAccept,adminAccept,mdestination;

    private int count;
    private int firstRadioGroupId,secondRadioGroupId,thirdRadioGroupId,fourthRadioGroupId;// 추가된 키

    public int getFirstRadioGroupId() {
        return firstRadioGroupId;
    }

    public void setFirstRadioGroupId(int firstRadioGroupId) {
        this.firstRadioGroupId = firstRadioGroupId;
    }
    public listviewItem(String name,String price,int count, String key,String density,String state,String shot,String syrup,String id,String marketAccept,String mdestination) {
        this.name= name;
        this.price = price;
        this.id = id;
        this.count = count;
        this.key = key;
        this.density = density;
        this.state= state;
        this.shot = shot;
        this.syrup=syrup;
        this.marketAccept=marketAccept;
        this.mdestination=mdestination;
    }

    public listviewItem(){};
    //Drawable image : 매개변수
    //설정자(setter) 메서드(클래스가 수행할 동작을 정의)와 접근자(getter) 메서드를 사용
//    public void setImage(String image) {
//        this.image = image;
//    }
    //set:클래스의 인스턴스 변수(필드)에 값을 설정
    //this. : 클래스의 현재 인스턴스를 참조하는 데 사용-> 인스턴스 변수와 매서드 매개변수를 구별하는 역할을 함
    //this.imageDrawable : 인스턴스 변수 , image : 매서드 매개변수
    public void setName(String name){
        this.name = name;
    }
    public void setPrice(String price){
        this.price = price;
    }
    public void setKey(String key){
        this.key = key;
    }
    public void setCount(int count){
        this.count =count;
    }
    public void setState(String state){
        this.state = state;
    }
    public void setDensity(String density){
        this.density = density;
    }
    public void setShot(String shot){
        this.shot = shot;
    }
    public void setSyrup(String syrup){
        this.syrup = syrup;
    }
    public void setDestination(String destination){
        this.destination = destination;
    }
    public void setId(String id){
        this.id = id;
    }
    public void setMarketAccept(String marketAccept){
        this.marketAccept = marketAccept;
    }
    public void setAdminAccept(String adminAccept){
        this.adminAccept = adminAccept;
    }
    public void setMdestination(String mdestination){
        this.mdestination = mdestination;
    }


    public void setSecondRadioGroupId(int secondRadioGroupId) {
        this.secondRadioGroupId = secondRadioGroupId;
    }
    public void setThirdRadioGroupId(int thirdRadioGroupId) {
        this.thirdRadioGroupId = thirdRadioGroupId;
    }
    public void setFourthRadioGroupId(int fourthRadioGroupId) {
        this.fourthRadioGroupId = fourthRadioGroupId;
    }
    //get : 클래스의 인스턴스 변수(필드)의 값을 반환
//    public String getImage() {
//        return this.image;
//    }
    public  String getName(){
        return this.name;
    }
    public  String getPrice(){
        return this.price;
    }
    public  String getKey(){
        return this.key;
    }
    public  int getCount(){
        return this.count;
    }
    public  String getState(){
        return this.state;
    }
    public  String getDensity(){
        return this.density;
    }
    public  String getShot(){
        return this.shot;
    }
    public  String getSyrup(){
        return this.syrup;
    }
    public  String getDestination(){
        return this.destination;
    }
    public  String getId(){
        return this.id;
    }
    public  String getMarketAccept(){
        return this.marketAccept;
    }
    public  String getAdminAccept(){
        return this.adminAccept;
    }
    public  String getMdestination(){
        return this.mdestination;
    }
    public int getSecondRadioGroupId() {
        return secondRadioGroupId;
    }
    public int getThirdRadioGroupId() {
        return thirdRadioGroupId;
    }
    public int getFourthRadioGroupId() {
        return fourthRadioGroupId;
    }
}
