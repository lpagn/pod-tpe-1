package ar.edu.itba.pod.server.models;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Vote {

    private final Map<String, Boolean> SPAV = new HashMap<>();
    private final Map<String,Integer> STAR = new HashMap<>();
    private String FPTP;
    private String province;
    private int pollingPlaceNumber;

    public Map<String, Integer> getSTAR() {
        return STAR;
    }

    public String getFPTP() {
        return FPTP;
    }

    public void setFPTP(String FPTP) {
        this.FPTP = FPTP;
    }

    public void addSPAV(String key , Boolean value){
        SPAV.putIfAbsent(key, value);
    }

    public void addSTAR(String key , Integer value){
        STAR.putIfAbsent(key,value);
    }

    public Map<String, Boolean> getSPAV() {
        return SPAV;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public int getPollingPlaceNumber() {
        return pollingPlaceNumber;
    }

    public void setPollingPlaceNumber(int pollingPlaceNumber) {
        this.pollingPlaceNumber = pollingPlaceNumber;
    }
}
