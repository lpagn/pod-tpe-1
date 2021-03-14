package ar.edu.itba.pod.services;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ManagementService extends Remote {
    void open() throws RemoteException;
    String state() throws RemoteException;
    void close() throws RemoteException;
}
