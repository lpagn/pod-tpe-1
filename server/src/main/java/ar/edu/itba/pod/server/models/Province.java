package ar.edu.itba.pod.server.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Province {

    private final Map<Integer,PollingPlace> pollingPlaces = new ConcurrentHashMap<>();

    public Map<Integer, PollingPlace> getPollingPlaces(){
        return pollingPlaces;
    }

    public void vote(int pollingPlaceNumber, Vote vote){
        pollingPlaces.get(pollingPlaceNumber).vote(vote);
    }

    public Map<String, Integer> STAR1(){
        Map<String, Integer> total = new HashMap<>();
        for (PollingPlace p : pollingPlaces.values()){
            Map<String,Integer> subtotal = p.getTotalSTAR();
            Set<String> candidates = subtotal.keySet();
            for(String candidate : candidates){
                total.put(candidate, total.getOrDefault(candidate,0) + subtotal.get(candidate));
            }
        }

        return total;
    }

    public Map<String,Integer> STAR2(String c1, String c2){
        Map<String,Integer> total = new HashMap<>();
        total.put(c1, 0);
        total.put(c2, 0);

        for(PollingPlace p : pollingPlaces.values()){
            Map<String,Integer> subtotal = p.STAR2(c1,c2);
            total.put(c1,subtotal.get(c1)+total.get(c1));
            total.put(c2,subtotal.get(c2)+total.get(c2));
        }

        return total;
    }


}
