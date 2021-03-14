package ar.edu.itba.pod.server.servant;

import ar.edu.itba.pod.server.Election;
import ar.edu.itba.pod.server.models.PollingPlace;
import ar.edu.itba.pod.server.models.Province;
import ar.edu.itba.pod.server.models.Vote;
import ar.edu.itba.pod.server.utils.VoteParser;
import ar.edu.itba.pod.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ElectionService implements FiscalService, QueryService, VoteService, ManagementService {

    // Add a fiscal to specified polling place
    @Override
    public void addFiscal(int pollingPlaceNumber, String party, NotificationCallBack callBack) {
        if(Election.getInstance().getStarted() || Election.getInstance().getDone()){
            throw new IllegalStateException("A fiscal can not be added if the election has already started or if the election has finished");
        }
        else{
            Election.getInstance().addFiscalToPollingPlace(pollingPlaceNumber, party, callBack);
        }
    }

    // Start elections
    @Override
    public void open() {
        if(Election.getInstance().getDone() || Election.getInstance().getStarted()){
            throw new IllegalStateException("Elections are closed or already started");
        }
        else{
            Election.getInstance().startElection();
        }
    }

    // Election status
    @Override
    public String state() {
        if(Election.getInstance().getStarted()){
            return "Ongoing Election";
        }
        else if(Election.getInstance().getDone()){
            return "Election Completed";
        }
        else{
            return "Election Not Started";
        }
    }

    // Close elections
    @Override
    public void close() {
        if(Election.getInstance().getDone() || !Election.getInstance().getStarted()){
            throw new IllegalStateException("Elections are closed or not started");
        }
        else{
            Election.getInstance().closeElection();
        }
    }

    // Query elections (nation)
    @Override
    public String getNation() {
        String csv;

        if(Election.getInstance().getDone()){
            csv = Election.getInstance().computeSTAR();
        }
        else if(Election.getInstance().getStarted()){
            Map<String,Integer> result = new HashMap<>();

            for(Province province : Election.getInstance().getProvinces().values()){
                for(PollingPlace pp : province.getPollingPlaces().values()){
                    pp.getFPTP().forEach((key, value) -> result.merge(key, value, Integer::sum));
                }
            }

            Integer totalVotes = result.values().stream().reduce(Integer::sum).orElse(1);
            StringBuilder s = new StringBuilder("Percentage;Party\n");
            result.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.<String,Integer>comparingByValue()).thenComparing(Map.Entry.comparingByKey())).forEach(entry -> s.append(String.format("%2.2f%%;%s\n", (double) 100*entry.getValue()/totalVotes, entry.getKey())));

            csv = s.toString();
        }
        else{
            throw new IllegalStateException("Elections has not been started yet");
        }

        return csv;
    }

    // Query elections (province)
    @Override
    public String getProvince(String province) {
        String csv;

        if(Election.getInstance().getDone()){
            csv = Election.getInstance().computeSPAV(province, 3);
        }
        else if(Election.getInstance().getStarted()){
            //If a province has no votes, an exception must be thrown
            if(Election.getInstance().getProvinces().get(province).getPollingPlaces().size() == 0){
                throw new IllegalStateException("No Votes");
            }

            Map<String,Integer> result = new HashMap<>();
            for(PollingPlace pp : Election.getInstance().getProvinces().get(province).getPollingPlaces().values()){
                pp.getFPTP().forEach((key, value) -> result.merge(key, value, Integer::sum));
            }

            Integer totalVotes = result.values().stream().reduce(Integer::sum).orElse(1);
            StringBuilder s = new StringBuilder("Percentage;Party\n");
            result.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.<String,Integer>comparingByValue()).thenComparing(Map.Entry.comparingByKey())).forEach(entry -> s.append(String.format("%2.2f%%;%s\n", (double) 100*entry.getValue()/totalVotes, entry.getKey())));

            csv = s.toString();
        }
        else{
            throw new IllegalStateException("Elections has not been started yet");
        }

        return csv;
    }

    // Query elections (polling place)
    @Override
    public String getPollingPlace(int pollingPlaceNumber) {
        if(Election.getInstance().getDone() || Election.getInstance().getStarted()){
            for(Province p : Election.getInstance().getProvinces().values()){
                if(p.getPollingPlaces().containsKey(pollingPlaceNumber)){
                    String csv = Election.getInstance().computeFPTP(pollingPlaceNumber, p);
                    return csv;
                }
            }

            throw new IllegalStateException("No Votes");
        }
        else{
            throw new IllegalStateException("Elections has not been started yet or has been closed");
        }
    }

    // Vote action
    @Override
    public void vote(String line) {
        if(!Election.getInstance().getStarted() || Election.getInstance().getDone()){
            throw new IllegalStateException("Tried to vote when election wasn't opened");
        }
        else{
            Vote vote = VoteParser.parse(line);
            Election.getInstance().vote(vote.getPollingPlaceNumber(), vote.getProvince(), vote);
        }
    }



}
