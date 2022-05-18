package rmi.froggerGame.server;

import java.io.Serializable;

/**
 * @author rmoreira
 */
public class Game implements Serializable {

    private int id = 0;
    private int nplayers = 0;
    private String difficulty = "";
    private SubjectRI subjectRI;

    public Game(String d, SubjectRI subjectRI) {
        id++;
        this.difficulty = d;
        this.nplayers = 0;
        this.subjectRI = subjectRI;
    }

    @Override
    public String toString() {
        return "Game{ Id = " + getId() + ", nplayers = " + getNplayers() + ", difficulty = " + getDifficulty() + ", SubjectRI = " + getSubjectRI() +'}';
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

    public SubjectRI getSubjectRI() {
        return subjectRI;
    }

    public void setSubjectRI(SubjectRI subjectRI) {
        this.subjectRI = subjectRI;
    }
}