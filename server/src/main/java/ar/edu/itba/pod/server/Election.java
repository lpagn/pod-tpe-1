package ar.edu.itba.pod.server;

import ar.edu.itba.pod.server.models.PollingPlace;
import ar.edu.itba.pod.server.models.Province;
import ar.edu.itba.pod.server.models.Vote;
import ar.edu.itba.pod.services.NotificationCallBack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Election {
    private final Map<String,Province> provinces = new ConcurrentHashMap<>();
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final AtomicBoolean done = new AtomicBoolean(false);
    private final Map<Integer,PollingPlace> pollingPlacesWithoutProvince = new ConcurrentHashMap<>();
    private static Election instance = new Election();

    private Election(){
        provinces.put("JUNGLE",new Province());
        provinces.put("SAVANNAH",new Province());
        provinces.put("TUNDRA",new Province());
    }

    public static Election getInstance() {
        return instance;
    }

    public void startElection(){
        if(instance.started.get()){
            System.out.println("Election already started");
            return;
        }
        instance.started.getAndSet(true);
        System.out.println("Election Started");
    }

    public void closeElection(){
        if (!instance.started.get()){
            System.out.println("Election already closed");
            return;
        }
        instance.started.getAndSet(false);
        instance.done.getAndSet(true);
        System.out.println("Election Ended");
    }

    public void addFiscalToPollingPlace(int pollingPlaceNumber, String party, NotificationCallBack callback){
        synchronized (pollingPlacesWithoutProvince) {
            if (pollingPlacesWithoutProvince.containsKey(pollingPlaceNumber)) {
                pollingPlacesWithoutProvince.get(pollingPlaceNumber).addFiscal(party, callback);
            } else {
                PollingPlace pp = new PollingPlace();
                pp.addFiscal(party, callback);
                pollingPlacesWithoutProvince.put(pollingPlaceNumber, pp);
            }
        }
    }

    public Map<String, Province> getProvinces(){
        return provinces;
    }

    public boolean getStarted(){
        return started.get();
    }

    public boolean getDone(){
        return done.get();
    }

    public void vote(int pollingPlaceNumber, String province, Vote vote){
        /* Check if pollingPlace has been created before to add a fiscal */
        synchronized (pollingPlacesWithoutProvince) {
            if (pollingPlacesWithoutProvince.containsKey(vote.getPollingPlaceNumber())) {
                provinces.get(province).getPollingPlaces().put(pollingPlaceNumber, pollingPlacesWithoutProvince.remove(pollingPlaceNumber));
            }
            /* If not, create a new one and put it in the specified province */
            else if (!provinces.get(province).getPollingPlaces().containsKey(pollingPlaceNumber)) {
                provinces.get(province).getPollingPlaces().put(pollingPlaceNumber, new PollingPlace());
            }
        }

        provinces.get(province).vote(pollingPlaceNumber, vote);
    }

    public String computeSTAR(){
        Map<String, Integer> total = new HashMap<>();
        for (Province p : provinces.values()){
            Map<String,Integer> subtotal = p.STAR1();
            Set<String> candidates = subtotal.keySet();
            for(String candidate : candidates){
                if(total.containsKey(candidate)){
                    total.put(candidate, total.get(candidate) + subtotal.get(candidate));
                }
                else{
                    total.put(candidate, subtotal.get(candidate));
                }
            }
        }

        StringBuilder scoring = new StringBuilder("Score;Party\n");
//        List<Map.Entry<String,Integer>> l = total.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).peek(e -> scoring.append(e.getValue()).append(";").append(e.getKey()).append("\n")).collect(Collectors.toList());


        List<Map.Entry<String,Integer>> l = total.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.<String,Integer>comparingByValue()).thenComparing(Map.Entry.comparingByKey())).peek(e -> scoring.append(e.getValue()).append(";").append(e.getKey()).append("\n")).collect(Collectors.toList());


        String first = l.get(0).getKey();
        String second = l.get(1).getKey();


        Map<String,Integer> finalVote = new HashMap<>();
        finalVote.put(first,0);
        finalVote.put(second,0);

        for(Province p : provinces.values()){
            Map<String,Integer> subtotal = p.STAR2(first,second);
            finalVote.put(first,subtotal.get(first)+finalVote.get(first));
            finalVote.put(second,subtotal.get(second)+finalVote.get(second));
        }

        scoring.append("Percentage;Party\n");

        int tv = finalVote.values().stream().reduce(Integer::sum).get();
        List<Map.Entry<String,Integer>> fl = finalVote.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.<String,Integer>comparingByValue()).thenComparing(Map.Entry.comparingByKey())).peek(e -> scoring.append(String.format("%.2f%%;%s\n",e.getValue()*100.0/(tv),e.getKey()))).collect(Collectors.toList());

        scoring.append("Winner\n").append(fl.get(0).getKey()).append("\n");

        return scoring.toString();

    }

    public String computeSPAV(String province, int rounds){
        List<String> winners = new ArrayList<>();
        StringBuilder result = new StringBuilder();

        for(int i = 0; i < rounds; i++){
            Map<String,Double> results = new HashMap<>();
            result.append("Round ").append(i + 1).append("\n").append("Approval;Party\n");

            /* Cargamos el mapa 'results' con los grados de aceptación de cada candidato */
            for(PollingPlace pollingPlace : Election.getInstance().getProvinces().get(province).getPollingPlaces().values()){
                for(Vote vote : pollingPlace.getVotes()){
                    for(Map.Entry<String,Boolean> entry : vote.getSPAV().entrySet()){
                        if(!winners.contains(entry.getKey()) && !results.containsKey(entry.getKey())){
                            results.put(entry.getKey(), getAddition(vote.getSPAV(), winners));
                        }

                        else if(!winners.contains(entry.getKey())){
                            results.replace(entry.getKey(), results.get(entry.getKey()) + getAddition(vote.getSPAV(), winners));
                        }
                    }
                }
            }
            
            /* Buscamos el ganador y lo agregamos al mapa dependiendo de en que ronda gano */
            Map.Entry<String,Double> winner = results.entrySet().stream().sorted(Map.Entry.comparingByKey(String::compareTo)).max(Map.Entry.comparingByValue(Double::compare)).orElseThrow(IllegalStateException::new);
            results.forEach((k,v) -> result.append(String.format("%2.2f;%s\n", v, k)));
            winners.add(winner.getKey());
            result.append("Winners\n");

            //winners.forEach(System.out::println);
            winners.forEach(w -> result.append(w).append(","));
            result.deleteCharAt(result.length()-1);
            result.append("\n");
        }

        return result.toString();
    }

    public String computeFPTP(int pollingPlaceNumber, Province province){
        StringBuilder result = new StringBuilder("Percentage;Party\n");

        PollingPlace pp = province.getPollingPlaces().get(pollingPlaceNumber);
        int totalVotes = pp.getFPTP().values().stream().reduce(Integer::sum).orElse(1);

        pp.getFPTP().entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.<String,Integer>comparingByValue()).thenComparing(Map.Entry.comparingByKey())).forEach(entry -> result.append(String.format("%2.2f%%;%s\n", (double) 100*entry.getValue()/totalVotes, entry.getKey())));

        if(done.get()){
            result.append("Winner\n");
            pp.getFPTP().entrySet().stream().sorted(Map.Entry.comparingByKey(String::compareTo)).max(Map.Entry.comparingByValue(Integer::compare)).ifPresent(entry -> result.append(entry.getKey()).append("\n"));
        }

        return result.toString();
    }

    private Double getAddition(Map<String, Boolean> spav, List<String> winners) {
        int alreadyWon = (int) spav.entrySet().stream().filter(entry -> winners.contains(entry.getKey())).count();
        return 1.0/(1.0 + alreadyWon);
    }

    public Map<Integer, PollingPlace> getPollingPlacesWithoutProvince() {
        return pollingPlacesWithoutProvince;
    }

    public void restart(){
        instance = new Election();
    }
}
