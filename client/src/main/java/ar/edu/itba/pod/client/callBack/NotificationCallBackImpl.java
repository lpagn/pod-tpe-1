package ar.edu.itba.pod.client.callBack;

import ar.edu.itba.pod.services.NotificationCallBack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

public class NotificationCallBackImpl implements NotificationCallBack {

    private static final Logger logger = LoggerFactory.getLogger(NotificationCallBackImpl.class);

    @Override
    public void notify(String party,Integer pollingPlaceNumber) throws RemoteException {
        System.out.printf("New vote for %s on polling place %d%n",party,pollingPlaceNumber);
    }
}
