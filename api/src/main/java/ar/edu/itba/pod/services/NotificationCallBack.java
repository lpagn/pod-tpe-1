package ar.edu.itba.pod.services;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NotificationCallBack extends Remote {
    void notify(String party,Integer pollingPlaceNumber) throws RemoteException;
}
