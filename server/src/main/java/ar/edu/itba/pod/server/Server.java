package ar.edu.itba.pod.server;

import ar.edu.itba.pod.server.servant.ElectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    public static void main(final String[] args) throws RemoteException {
        System.out.println("Election Server Starting ...");

        /* Service initialization */
        final ElectionService electionService = new ElectionService();

        /* Export services */
        final Remote remote = UnicastRemoteObject.exportObject(electionService, 0);

        /* Registry binding */
        final Registry registry = LocateRegistry.getRegistry();
        registry.rebind("management", remote);
        registry.rebind("vote", remote);
        registry.rebind("fiscal", remote);
        registry.rebind("query", remote);

        System.out.println("Service bound");
    }
}
