package rabbitmq.froggerGame.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class froggerGameFactoryImpl extends UnicastRemoteObject implements froggerGameFactoryRI {

    public froggerGameFactoryImpl() throws RemoteException {
        super();
    }

    DBMockup db = new DBMockup();
    HashMap<froggerGameSessionImpl, String> sessions = new HashMap<froggerGameSessionImpl, String>();

    public void register(String email, String password) throws RemoteException{
        db.register(email, password);
    }

    public froggerGameSessionRI login(String username, String password) throws RemoteException{
        if (db.exists(username, password)) {
            froggerGameSessionImpl session = new froggerGameSessionImpl(this);
            sessions.put(session, username);
            return session;
        }
        return null;
    }

}
