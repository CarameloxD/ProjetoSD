package rmi.froggerGame.server;

import rmi.froggerGame.client.ObserverRI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface froggerGameSessionRI extends Remote {
    public Game createGame(String difficulty, ObserverRI observer) throws RemoteException;

    public Game joinGame(int idGame, ObserverRI observer) throws RemoteException;

    public void exitGame(int idGame, ObserverRI observer) throws RemoteException;

    public Game[]listGamesByDifficulty(String difficulty) throws RemoteException;

    public Game[] listAllGames() throws RemoteException;

    public void logout() throws RemoteException;
}
