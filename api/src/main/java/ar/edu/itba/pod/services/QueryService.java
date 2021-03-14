package ar.edu.itba.pod.services;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface QueryService extends Remote {
    String getNation() throws RemoteException;
    String getProvince(String province) throws RemoteException;
    String getPollingPlace(int pollingPlaceNumber) throws RemoteException;
}
