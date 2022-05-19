package rmi.froggerGame.client;

import rmi.froggerGame.frogger.Main;
import rmi.froggerGame.server.State;
import rmi.froggerGame.server.SubjectRI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ObserverImpl extends UnicastRemoteObject implements ObserverRI {
    private int id;
    private State lastObserverState;
    private SubjectRI subjectRI;
    private Main main;

    protected ObserverImpl() throws RemoteException {

    }

    public State getLastObserverState() {
        return lastObserverState;
    }

    @Override
    public void update(State state) throws RemoteException {
        synchronized (this) {
            this.lastObserverState = state;
            if (state.getInfo().contains("upPressed") || state.getInfo().contains("downPressed") || state.getInfo().contains("rightPressed") || state.getInfo().contains("leftPressed")) {
                this.main.froggerHandler(state);
            } else if (state.getId().equals("cycleTraffic")) {
                this.main.cycleTraffic(5, state);
            }
        }
    }

    public void setSubjectRI(SubjectRI subjectRI) throws RemoteException {
        this.subjectRI = subjectRI;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public int getId() throws RemoteException {
        return id;
    }

    public void setId(int id) throws RemoteException {
        this.id = id;
    }

    public void setLastObserverState(State lastObserverState) throws RemoteException {
        this.lastObserverState = lastObserverState;
    }

    public SubjectRI getSubjectRI() throws RemoteException {
        return subjectRI;
    }

    public Main getMain() throws RemoteException {
        return main;
    }
}
