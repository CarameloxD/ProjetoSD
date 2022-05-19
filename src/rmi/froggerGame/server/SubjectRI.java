package rmi.froggerGame.server;

import rmi.froggerGame.client.ObserverRI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface SubjectRI  extends Remote {
    void attach(ObserverRI obsRI) throws RemoteException;
    void detach(ObserverRI obsRI) throws RemoteException;
    State getState() throws RemoteException;
    void setState(State state) throws RemoteException;
    public void notifyAllObservers(State state) throws RemoteException;
    public int findObserverArrayPosition(ObserverRI observer) throws RemoteException;
    public ArrayList<ObserverRI> getObservers() throws RemoteException;
}
