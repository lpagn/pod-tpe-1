package ar.edu.itba.pod.server.utils;

import ar.edu.itba.pod.server.models.Vote;

public class VoteParser {
    public static Vote parse(String line){
        Vote vote = new Vote();
        String[] arr = line.split(";");
        vote.setFPTP(arr[3]);
        vote.setPollingPlaceNumber(Integer.parseInt(arr[0]));
        vote.setProvince(arr[1]);

        String [] temp = arr[2].split(",");
        String [] candidatos = new String[temp.length];
        String [] puntajes = new String[temp.length];

        for(int i = 0 ; i < temp.length ; i++){
            String [] t = temp[i].split("\\|");
            candidatos[i] = t[0];
            puntajes[i]   = t[1];
            vote.addSTAR(candidatos[i],Integer.parseInt(puntajes[i]));
            vote.addSPAV(candidatos[i],true);
        }

        return vote;
    }
}
