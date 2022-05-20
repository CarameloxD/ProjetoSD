package rabbitmq.froggerGame.server;

import rabbitmq.froggerGame.client.Observer;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * @author rmoreira
 */
public class Game implements Serializable {

    private int id = 0;
    private int nplayers = 0;
    private String difficulty = "";

    private ArrayList<Observer> observers;

    public Game(String d) {
        id++;
        this.difficulty = d;
        this.nplayers++;
    }

    @Override
    public String toString() {
        return "Game{ Id = " + getId() + ", nplayers = " + getNplayers() + ", difficulty = " + getDifficulty() + '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the nplayers
     */
    public int getNplayers() {
        return nplayers;
    }

    /**
     * @param nplayers the nplayers to set
     */
    public void setNplayers(int nplayers) {
        this.nplayers = nplayers;
    }

    /**
     * @return the difficulty
     */
    public String getDifficulty() {
        return difficulty;
    }

    /**
     * @param difficulty the Difficulty to set
     */

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void attach(Observer obs) throws RemoteException {
        this.observers.add(obs);
    }

    public void detach(Observer obs) throws RemoteException{
        this.observers.remove(obs);
    }

    public int findObserverArrayPosition(Observer observer) throws RemoteException{
        for (int i = 0; i< observers.size(); i++){
            if(observer.equals(observers.get(i))){
                return i;
            }
        }
        return -1;
    }
}