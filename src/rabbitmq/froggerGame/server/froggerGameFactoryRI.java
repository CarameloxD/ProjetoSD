package rabbitmq.froggerGame.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface froggerGameFactoryRI extends Remote {
     public void register(String email, String password) throws RemoteException;

     public froggerGameSessionRI login(String email, String password) throws RemoteException;

}
