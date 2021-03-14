package ar.edu.itba.pod.client;

import ar.edu.itba.pod.client.callBack.NotificationCallBackImpl;
import ar.edu.itba.pod.services.FiscalService;
import ar.edu.itba.pod.services.NotificationCallBack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class FiscalClient {
    public static void main(String[] args) throws RemoteException, NotBoundException {
        final String address = System.getProperty("serverAddress");
        final int pollingPlaceNumber = Integer.parseInt(System.getProperty("id"));
        final String party = System.getProperty("party");

        if(address == null || party == null || System.getProperty("id") == null){
            System.out.println("Address, party name and polling place number required");
            System.exit(-1);
        }

        final Registry registry = LocateRegistry.getRegistry(address, 1099);
        final FiscalService fiscalService  = (FiscalService) registry.lookup("fiscal");

        NotificationCallBack callBack = new NotificationCallBackImpl();
        Remote remote = UnicastRemoteObject.exportObject(callBack, 0);

        try{
            fiscalService.addFiscal(pollingPlaceNumber, party, callBack);
            System.out.printf("Fiscal of %s registered on polling place %d",party,pollingPlaceNumber);
        } catch (IllegalStateException e){
            System.out.println(e.getMessage());
        }
    }
}
