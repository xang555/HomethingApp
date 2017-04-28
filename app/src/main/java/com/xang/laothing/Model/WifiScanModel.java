package com.xang.laothing.Model;

public class WifiScanModel {

    private String ssid ;
    private String bssid;
    private String status;

    public WifiScanModel(){}


    public void setSsid(String ssid){
        this.ssid = ssid;
    }

    public void setBssid(String bssid){
        this.bssid = bssid;
    }


    public String getSsid(){
        return this.ssid;
    }

    public String getBssid(){
        return this.bssid;
    }


}
