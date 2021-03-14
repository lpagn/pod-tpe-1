package ar.edu.itba.pod.services;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FiscalService extends Remote {
    void addFiscal(int pollingPlaceNumber, String party,NotificationCallBack callBack) throws RemoteException;
}
