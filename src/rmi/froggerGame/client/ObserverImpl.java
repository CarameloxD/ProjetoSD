package rmi.froggerGame.client;

import rmi.froggerGame.frogger.Main;
import rmi.froggerGame.server.State;
import rmi.froggerGame.server.SubjectRI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ObserverImpl extends UnicastRemoteObject implements ObserverRI {
    private String id;
    private State lastObserverState;
    private SubjectRI subjectRI;
    private Main main;

    protected ObserverImpl(String id) throws RemoteException {
        this.id = id;
    }

    public State getLastObserverState() {
        return lastObserverState;
    }

    @Override
    public void update() throws RemoteException{
        this.lastObserverState = subjectRI.getState();
        this.main.froggerHandler();
    }

    public void setSubjectRI(SubjectRI subjectRI) throws RemoteException {
        this.subjectRI = subjectRI;
        this.subjectRI.attach(this);
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public String getId() throws RemoteException{
        return id;
    }

    public void setId(String id) throws RemoteException{
        this.id = id;
    }

    public void setLastObserverState(State lastObserverState) throws RemoteException{
        this.lastObserverState = lastObserverState;
    }

    public SubjectRI getSubjectRI() throws RemoteException{
        return subjectRI;
    }

    public Main getMain() throws RemoteException{
        return main;
    }
}