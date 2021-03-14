package ar.edu.itba.pod.client.utils;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

public class VoteCsvGenerator {

    /* Only accepts one candidate per vote */
    public static String generate(int lines){
        StringBuilder toReturn = new StringBuilder();
        int pollingPlace, score;

        for(int i = 0; i < lines; i++){
            pollingPlace = ThreadLocalRandom.current().nextInt(1000, 1010);
            score = ThreadLocalRandom.current().nextInt(0, 6);
            toReturn.append(String.format("%d;%s;%s|%d;%s\n", pollingPlace, getProvince(), getCandidate(), score, getCandidate()));
        }

        return toReturn.toString();
    }

    private static String getCandidate(){
        switch (ThreadLocalRandom.current().nextInt(0, 4)){
            case 1:
                return "OWL";
            case 2:
                return "BUFFALO";
            case 3:
                return "LYNX";
            default:
                return "TIGER";
        }
    }

    private static String getProvince(){
        switch (ThreadLocalRandom.current().nextInt(0, 3)){
            case 1:
                return "JUNGLE";
            case 2:
                return "TUNDRA";
            default:
                return "SAVANNAH";
        }
    }

}
