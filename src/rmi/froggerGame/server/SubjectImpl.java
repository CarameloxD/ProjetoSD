package rmi.froggerGame.server;

import rmi.froggerGame.client.ObserverRI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class SubjectImpl extends UnicastRemoteObject implements SubjectRI {
    private State subjectState;
    private ArrayList<ObserverRI> observers;

    protected SubjectImpl() throws RemoteException {
        this.subjectState = new State("", "");
        this.observers = new ArrayList<ObserverRI>();
    }

    protected SubjectImpl(State subjectState) throws RemoteException {
        this.subjectState = subjectState;
        this.observers = new ArrayList<ObserverRI>();
    }


    @Override
    public void attach(ObserverRI obsRI) throws RemoteException{
        this.observers.add(obsRI);
    }

    @Override
    public void detach(ObserverRI obsRI) throws RemoteException{
        this.observers.remove(obsRI);
    }

    @Override
    public State getState() throws RemoteException{
        return this.subjectState;
    }

    @Override
    public void setState(State state) throws RemoteException{
        notifyAllObservers();
        this.subjectState = state;
    }

    public void notifyAllObservers() throws RemoteException {
        for (ObserverRI observer : observers) {
             observer.update();
        }
    }

    public ArrayList<ObserverRI> getObservers() throws RemoteException{
        return observers;
    }
}
