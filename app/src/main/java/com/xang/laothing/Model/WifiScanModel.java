package com.xang.laothing.Model;

public class WifiScanModel {

    private String ssid ;
    private String bssid;
    private boolean status;

    public WifiScanModel(){}


    public void setSsid(String ssid){
        this.ssid = ssid;
    }

    public void setBssid(String bssid){
        this.bssid = bssid;
    }


    public void setStatus(boolean status){
        this.status = status;
    }

    public String getSsid(){
        return this.ssid;
    }

    public boolean getstatus(){
        return this.status;
    }

    public String getBssid(){
        return this.bssid;
    }


}
