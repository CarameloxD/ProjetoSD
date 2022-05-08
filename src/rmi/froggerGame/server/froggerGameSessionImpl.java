package rmi.froggerGame.server;
import rmi.froggerGame.client.ObserverRI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Observer;

public class froggerGameSessionImpl extends UnicastRemoteObject implements froggerGameSessionRI{

    froggerGameFactoryImpl froggerGamefactoryimpl;

    public froggerGameSessionImpl(froggerGameFactoryImpl froggerGamefactoryimpl) throws RemoteException {
        super();
        this.froggerGamefactoryimpl = froggerGamefactoryimpl;
    }

    @Override
    public Game createGame(String difficulty, ObserverRI observer) throws RemoteException {
        SubjectRI subjectRI = new SubjectImpl();
        Game game = froggerGamefactoryimpl.db.insert(difficulty, subjectRI);
        game.getSubjectRI().attach(observer);
        game.setNplayers(game.getNplayers() + 1);
        return game;
    }

    @Override
    public Game joinGame(int idGame, ObserverRI observer) throws RemoteException {
        Game game = froggerGamefactoryimpl.db.selectById(idGame);
        game.getSubjectRI().attach(observer);
        game.setNplayers(game.getNplayers() + 1);
        return game;
    }

    @Override
    public void exitGame(int idGame, ObserverRI observer) throws RemoteException {
        Game game = froggerGamefactoryimpl.db.selectById(idGame);
        game.getSubjectRI().detach(observer);
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
