package rmi.froggerGame.server;

import rmi.froggerGame.client.ObserverRI;

import java.awt.image.renderable.RenderableImage;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class SubjectImpl extends UnicastRemoteObject implements SubjectRI {
    private State subjectState;
    private ArrayList<ObserverRI> observers;

    protected SubjectImpl() throws RemoteException {
        this.subjectState = new State(null, "");
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
        synchronized(this) {
            this.subjectState = state;
            notifyAllObservers(state);
        }
    }

    public void notifyAllObservers(State state) throws RemoteException {
        for (ObserverRI observer : observers) {
             observer.update(state);
        }
    }

    public ArrayList<ObserverRI> getObservers() throws RemoteException{
        return observers;
    }

    public int findObserverArrayPosition(ObserverRI observer) throws RemoteException{
        for (int i = 0; i< observers.size(); i++){
            if(observer.equals(observers.get(i))){
                return i;
            }
        }
        return -1;
    }
}
