package ar.edu.itba.pod.client;

import ar.edu.itba.pod.services.ManagementService;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ManagementClient {
    public static void main(String[] args) throws RemoteException, NotBoundException {
        final String address = System.getProperty("serverAddress");
        final String action = System.getProperty("action");

        if(address == null || action == null){
            System.out.println("Address and action needed");
            System.exit(-1);
        }

        final Registry registry = LocateRegistry.getRegistry(address, 1099);
        final ManagementService managementService = (ManagementService) registry.lookup("management");

        try{
            switch (action) {
                case "open":
                    managementService.open();
                    break;
                case "close":
                    managementService.close();
                    break;
                case "state":
                    System.out.println(managementService.state());
                    break;
                default:
                    System.out.println("Invalid action passed to ManagementClient");
            }
        } catch (IllegalStateException e){
            System.out.println(e.getMessage());
        }
    }
}
