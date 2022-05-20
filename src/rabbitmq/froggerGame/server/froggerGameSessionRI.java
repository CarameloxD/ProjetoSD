package rabbitmq.froggerGame.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface froggerGameSessionRI extends Remote {
    public Game createGame(String difficulty) throws RemoteException;

    public Game joinGame(int idGame) throws RemoteException;

    public void exitGame(int idGame) throws RemoteException;

    public Game[]listGamesByDifficulty(String difficulty) throws RemoteException;

    public Game[] listAllGames() throws RemoteException;

    public void logout() throws RemoteException;
}
