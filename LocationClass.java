package com.john.android.tsi.utilities;

import org.json.JSONArray;
import org.json.JSONObject;

public class LocationClass {
    private static String CLASS_NAME = "LOCATION_CLASS";
    private double[] mLatLong ={0,0};
    public String locationString="Earth",urlBase;
    private String testUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=29.744292,-95.392361&sensor=true&key=AIzaSyDJu5bIXp-ZNvZ8Ao1dLJQgaA2SMXZyA8o";

    public LocationClass(){

    }
    public static String parseStaticJsonAddress(String address){
        if(address.equals("")) return "Location parseJsonAddress";
        try{
            JSONObject locationJson = new JSONObject(address);
            JSONArray resultsArray = locationJson.getJSONArray("results");
            JSONObject firstResult = resultsArray.getJSONObject(0);
            return firstResult.getString("formatted_address");
        }catch (Exception e){
        }
        return "\"Unable to determine location.-parseJSON";
    }
    public static String determineSystem(int systemIndex){
        switch(systemIndex){
            case 0: return "CCTV";
            case 1: return "Cabinets";
            case 2: return "ETV";
            case 3: return "LAN";
            case 4: return "PAGA";
            case 5: return "Radar";
            case 6: return "Radio";
            case 7: return "SCS";
            case 8: return "UPS";
            case 9: return "Misc";
            default: return "Misc.";
        }
    }
}
