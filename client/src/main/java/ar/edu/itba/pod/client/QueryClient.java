package ar.edu.itba.pod.client;

import ar.edu.itba.pod.services.QueryService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class QueryClient {

    public static void main(String[] args) throws RemoteException, NotBoundException {
        final String address = System.getProperty("serverAddress");
        final String state = System.getProperty("state");
        final String pollingPlaceNumber = System.getProperty("id");
        final String outPath = System.getProperty("outPath");

        final Registry registry = LocateRegistry.getRegistry(address, 1099);
        final QueryService queryService  = (QueryService) registry.lookup("query");
        String csv = "";
        try{
            if(state == null && pollingPlaceNumber == null){
                csv = queryService.getNation();
            }
            else if(state != null && pollingPlaceNumber == null){
                csv = queryService.getProvince(state);
            }
            else if(state == null){
                csv = queryService.getPollingPlace(Integer.parseInt(pollingPlaceNumber));
            }
            else{
                System.out.println("Wrong arguments passed to QueryClient");
                System.exit(-1);
            }
        } catch (IllegalStateException e){
            System.out.println(e.getMessage());
        }

        writeCSV(outPath, csv);
    }

    private static void writeCSV(String fileName, String csv){
        File file = new File(fileName);
        try {
            FileWriter myWriter = new FileWriter(file);
            myWriter.write(csv);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("Can't write query file");
        }

    }
}
