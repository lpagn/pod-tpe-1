package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.utils.VoteCsvGenerator;
import ar.edu.itba.pod.services.VoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class VoteClient {
    private static final Logger logger = LoggerFactory.getLogger(VoteClient.class);
    private static final ExecutorService pool = Executors.newFixedThreadPool(16);

    public static void main(String[] args) throws IOException, NotBoundException, InterruptedException {
        final String address = System.getProperty("serverAddress");
        final String votes = System.getProperty("votesPath");

        if(address == null || votes == null){
            System.out.println("Address and votes path required");
            System.exit(-1);
        }

        final Registry registry = LocateRegistry.getRegistry(address, 1099);
        final VoteService voteService  = (VoteService) registry.lookup("vote");

        /* Expected file reading */
        File votesFile = new File(votes);
        BufferedReader scanner = new BufferedReader(new FileReader(votesFile));

        /* Just for testing - Method that generates random votes */
        //BufferedReader scanner = new BufferedReader(new StringReader(VoteCsvGenerator.generate(1000000)));
        String line;
        int voteCounter = 0;

        while((line = scanner.readLine()) != null){
            String finalLine = line;
            pool.submit(() -> {
                try {
                    voteService.vote(finalLine);
                } catch (RemoteException e) {
                    logger.error("VoteService is down");
                } catch (IllegalStateException e){
                    System.out.println(e.getMessage());
                }
            });

            voteCounter++;
        }

        pool.shutdown();
        pool.awaitTermination(120, TimeUnit.SECONDS);
        System.out.println(voteCounter + " votes registered");
    }
}
