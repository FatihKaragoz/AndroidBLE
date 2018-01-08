package com.example.hmtteol.bleyoklama;


public class user {
    private String ad,no,pass;

    public user(){}


    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public user (String ad, String no, String pass) {
        this.ad = ad;
        this.no = no;
        this.pass = pass;
    }
}
