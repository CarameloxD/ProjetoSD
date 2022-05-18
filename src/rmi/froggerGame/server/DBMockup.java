package rmi.froggerGame.server;

import rmi.froggerGame.client.ObserverRI;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * This class simulates a DBMockup for managing users and books.
 *
 * @author rmoreira
 */
public class DBMockup {

    private final ArrayList<User> users;// = new ArrayList();
    private final ArrayList<Game> games;

    /**
     * This constructor creates and inits the database with some books and users.
     */
    public DBMockup() {
        games = new ArrayList();
        users = new ArrayList();
        //Add 3 books
       /* books.add(new Book("Distributed Systems: principles and paradigms", "Tanenbaum"));
        books.add(new Book("Distributed Systems: concepts and design", "Colouris"));
        books.add(new Book("Distributed Computing Networks", "Tanenbaum"));*/
        //Add one user
        users.add(new User("guest", "ufp"));
        users.add(new User("joao", "123"));
    }

    /**
     * Registers a new user.
     *
     * @param u username
     * @param p passwd
     */
    public void register(String u, String p) {
        if (!exists(u, p)) {
            users.add(new User(u, p));
        }
    }

    /**
     * Checks the credentials of an user.
     *
     * @param u username
     * @param p passwd
     * @return
     */
    public boolean exists(String u, String p) {
        for (User usr : this.users) {
            if (usr.getUname().compareTo(u) == 0 && usr.getPword().compareTo(p) == 0) {
                return true;
            }
        }
        return false;
        //return ((u.equalsIgnoreCase("guest") && p.equalsIgnoreCase("ufp")) ? true : false);
    }

    /**
     * Inserts a new book into the DigLib.
     *
     * @param d difficulty
     */
    public Game insert(String d, SubjectRI s) {
        Game game = new Game(d, s);
        games.add(game);
        return game;
    }

    /**
     * Looks up for books with given title and author keywords.
     *
     * @param d difficulty keyword
     * @return
     */
    public Game[] select(String d) {
        Game[] agames = null;
        ArrayList<Game> vgames = new ArrayList();
        // Find games that match
        for (int i = 0; i < games.size(); i++) {
            Game game = (Game) games.get(i);
            System.out.println("DB - select(): game[" + i + "] = " + game.getNplayers() + ", " + game.getDifficulty());
            if (game.getDifficulty().toLowerCase().contains(d.toLowerCase())) {
                System.out.println("DB - select(): add game[" + i + "] = " + game.getNplayers() + ", " + game.getDifficulty());
                vgames.add(game);
            }
        }
        // Copy Vector->Array
        agames = new Game[vgames.size()];
        for (int i = 0; i < vgames.size(); i++) {
            agames[i] = (Game) vgames.get(i);
        }
        return agames;
    }

    public Game selectById(int idGame) throws RemoteException {
        // Find games that match
        for (int i = 0; i < games.size(); i++) {
            Game game = (Game) games.get(i);
            System.out.println("DB - select(): game[" + i + "] = " + game.getNplayers() + ", " + game.getDifficulty());
            if (game.getId() == idGame) {
                System.out.println("DB - select(): add game[" + i + "] = " + game.getNplayers() + ", " + game.getDifficulty());
                return game;
            }
        }
        System.out.println("Game with id = " + idGame + " not found.");
        return null;
    }

    public Game[] listAllGames() {
        Game[] agames = null;
        ArrayList<Game> vgames = new ArrayList();
        // Find games that match
        for (int i = 0; i < games.size(); i++) {
            Game game = (Game) games.get(i);
            System.out.println("DB - select(): game[" + i + "] = " + game.getNplayers() + ", " + game.getDifficulty());
            vgames.add(game);
        }
        // Copy Vector->Array
        agames = new Game[vgames.size()];
        for (int i = 0; i < vgames.size(); i++) {
            agames[i] = (Game) vgames.get(i);
        }
        return agames;
    }
}
