package ar.edu.itba.pod.server.models;

import ar.edu.itba.pod.services.NotificationCallBack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PollingPlace {
    private static final Logger logger = LoggerFactory.getLogger(PollingPlace.class);
    private final Map<String, Integer> FPTP = new ConcurrentHashMap<>();
    private final Map<String, Integer> totalSTAR = new ConcurrentHashMap<>();
    private final List<Vote> votes = new ArrayList<>();
    private final Map<String, List<NotificationCallBack>> fiscals = new ConcurrentHashMap<>();

    public Map<String, Integer> getFPTP() {
        return FPTP;
    }

    public void vote(Vote vote) {
        synchronized (votes){
            votes.add(vote);
        }
        synchronized (FPTP) {
            if (FPTP.containsKey(vote.getFPTP())) {
                FPTP.put(vote.getFPTP(), FPTP.get(vote.getFPTP()) + 1);
            } else {
                FPTP.put(vote.getFPTP(), 1);
            }
        }

        /* Notification task works in another thread */
        if(fiscals.containsKey(vote.getFPTP())){
            for(NotificationCallBack callback : fiscals.get(vote.getFPTP())){
                Runnable notify = () -> {
                    try {
                        callback.notify(vote.getFPTP(),vote.getPollingPlaceNumber());
                    } catch (RemoteException e) {
                        logger.error("NotificationCallback not responding");
                    }
                };

                Thread t = new Thread(notify);
                t.start();
            }
        }

        Set<String> candidates = vote.getSTAR().keySet();
        for(String candidate : candidates){
            synchronized (totalSTAR) {
                if (totalSTAR.containsKey(candidate)) {
                    totalSTAR.put(candidate, totalSTAR.get(candidate) + vote.getSTAR().get(candidate));

                } else {
                    totalSTAR.put(candidate, vote.getSTAR().get(candidate));
                }
            }
        }
    }

    public Map<String, Integer> getTotalSTAR() {
        return totalSTAR;
    }

    public Map<String, Integer> STAR2(String c1, String c2) {
        Map<String,Integer> total = new HashMap<>();
        total.put(c1, 0);
        total.put(c2, 0);
        for(Vote v : votes){
            if(v.getSTAR().containsKey(c1) && v.getSTAR().containsKey(c2)){
                if(v.getSTAR().get(c1)>v.getSTAR().get(c2)){
                    total.put(c1,total.get(c1)+1);
                }
                else if (v.getSTAR().get(c1).equals(v.getSTAR().get(c2))){
                    if(c1.compareTo(c2)<0){
                        total.put(c1,total.get(c1)+1);
                    }
                }
                else{
                    total.put(c2,total.get(c2)+1);
                }
            }
            else if(v.getSTAR().containsKey(c1)){
                total.put(c1,total.get(c1)+1);
            }
            else if(v.getSTAR().containsKey(c2)){
                total.put(c2,total.get(c2)+1);
            }
        }

        return total;
    }

    public List<Vote> getVotes(){
        return votes;
    }

    public void addFiscal(String party,NotificationCallBack callBack){
        if(!fiscals.containsKey(party)){
            fiscals.put(party, new ArrayList<>());
        }

        fiscals.get(party).add(callBack);
    }
}
