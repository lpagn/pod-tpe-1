package ar.edu.itba.pod.services;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VoteService extends Remote {
    void vote(String line) throws RemoteException;
}
