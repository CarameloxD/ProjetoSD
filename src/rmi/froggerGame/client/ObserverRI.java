package rmi.froggerGame.client;

import rmi.froggerGame.frogger.Main;
import rmi.froggerGame.server.State;
import rmi.froggerGame.server.SubjectRI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ObserverRI extends Remote {
    void update(State state) throws RemoteException;
    public void setSubjectRI(SubjectRI subjectRI) throws RemoteException;
    public void setMain(Main main) throws RemoteException;
    public int getId() throws RemoteException;
    public void setId(int id) throws RemoteException;
    public void setLastObserverState(State lastObserverState) throws RemoteException;
    public SubjectRI getSubjectRI() throws RemoteException;
    public Main getMain() throws RemoteException;
}
