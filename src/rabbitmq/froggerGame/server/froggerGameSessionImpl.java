package rabbitmq.froggerGame.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class froggerGameSessionImpl extends UnicastRemoteObject implements froggerGameSessionRI{

    froggerGameFactoryImpl froggerGamefactoryimpl;

    public froggerGameSessionImpl(froggerGameFactoryImpl froggerGamefactoryimpl) throws RemoteException {
        super();
        this.froggerGamefactoryimpl = froggerGamefactoryimpl;
    }

    @Override
    public Game createGame(String difficulty) throws RemoteException {
        return froggerGamefactoryimpl.db.insert(difficulty);
    }

    @Override
    public Game joinGame(int idGame) throws RemoteException {
        return froggerGamefactoryimpl.db.selectById(idGame);
    }

    @Override
    public void exitGame(int idGame) throws RemoteException {
        Game game = froggerGamefactoryimpl.db.selectById(idGame);
        game.setNplayers(game.getNplayers() - 1);
    }

    @Override
    public Game[] listGamesByDifficulty(String difficulty) throws RemoteException {
        return froggerGamefactoryimpl.db.select(difficulty);
    }

    @Override
    public Game[] listAllGames() throws RemoteException {
        return froggerGamefactoryimpl.db.listAllGames();
    }

    public void logout() throws RemoteException {
        froggerGamefactoryimpl.sessions.remove(this);
    }
}
