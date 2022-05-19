/**
 * Copyright (c) 2009 Vitaliy Pavlenko
 * <p>
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * <p>
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package rmi.froggerGame.frogger;

import java.awt.event.KeyEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;

import jig.engine.ImageResource;
import jig.engine.PaintableCanvas;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.PaintableCanvas.JIGSHAPE;
import jig.engine.hli.ImageBackgroundLayer;
import jig.engine.hli.StaticScreenGame;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.util.Vector2D;
import rmi.froggerGame.client.ObserverRI;
import rmi.froggerGame.server.State;

public class Main extends StaticScreenGame {
    static final int WORLD_WIDTH = (13 * 32);
    static final int WORLD_HEIGHT = (14 * 32);
    static final Vector2D FROGGER_START = new Vector2D(6 * 32, WORLD_HEIGHT - 32);
    static final Vector2D FROGGER_START_P2 = new Vector2D(8 * 32, WORLD_HEIGHT - 32);

    static final String RSC_PATH = "rmi/froggerGame/resources/";
    static final String SPRITE_SHEET = RSC_PATH + "frogger_sprites.png";

    static final int FROGGER_LIVES = 5;
    static final int STARTING_LEVEL = 1;
    static final int DEFAULT_LEVEL_TIME = 60;

    private volatile FroggerCollisionDetection frogCol;
    private volatile FroggerCollisionDetection frogColP2;
    private volatile Frogger frog;
    private volatile Frogger frogP2;
    private AudioEfx audiofx;
    private AudioEfx audiofxP2;
    private volatile FroggerUI ui;
    private volatile WindGust wind;
    private volatile HeatWave hwave;
    private GoalManager goalmanager;

    private volatile AbstractBodyLayer<MovingEntity> movingObjectsLayer;
    private volatile AbstractBodyLayer<MovingEntity> particleLayer;

    private volatile MovingEntityFactory roadLine1;
    private volatile MovingEntityFactory roadLine2;
    private volatile MovingEntityFactory roadLine3;
    private volatile MovingEntityFactory roadLine4;
    private volatile MovingEntityFactory roadLine5;

    private volatile MovingEntityFactory riverLine1;
    private volatile MovingEntityFactory riverLine2;
    private volatile MovingEntityFactory riverLine3;
    private volatile MovingEntityFactory riverLine4;
    private volatile MovingEntityFactory riverLine5;

    private ImageBackgroundLayer backgroundLayer;

    static final int GAME_INTRO = 0;
    static final int GAME_PLAY = 1;
    static final int GAME_FINISH_LEVEL = 2;
    static final int GAME_INSTRUCTIONS = 3;
    static final int GAME_OVER = 4;

    protected int GameState = GAME_INTRO;
    protected int GameLevel = STARTING_LEVEL;

    public int GameLives = FROGGER_LIVES;
    public int GameScore = 0;

    public int levelTimer = DEFAULT_LEVEL_TIME;

    private boolean space_has_been_released = false;
    private boolean keyPressed = false;
    private boolean listenInput = true;

    private ObserverRI observerRI;
    private int dificuldade = 0;
    private MovingEntity c;

    /**
     * Initialize game objects
     */
    public Main(int difficulty, ObserverRI observer) throws RemoteException {

        super(WORLD_WIDTH, WORLD_HEIGHT, false);

        observer.setMain(this);
        observerRI = observer;

        gameframe.setTitle("Frogger | " + observer.getId());

        ResourceFactory.getFactory().loadResources(RSC_PATH, "resources.xml");

        ImageResource bkg = ResourceFactory.getFactory().getFrames(
                SPRITE_SHEET + "#background").get(0);
        backgroundLayer = new ImageBackgroundLayer(bkg, WORLD_WIDTH,
                WORLD_HEIGHT, ImageBackgroundLayer.TILE_IMAGE);

        // Used in CollisionObject, basically 2 different collision spheres
        // 30x30 is a large sphere (sphere that fits inside a 30x30 pixel rectangle)
        //  4x4 is a tiny sphere
        PaintableCanvas.loadDefaultFrames("col", 30, 30, 2, JIGSHAPE.RECTANGLE, null);
        PaintableCanvas.loadDefaultFrames("colSmall", 4, 4, 2, JIGSHAPE.RECTANGLE, null);

        frog = new Frogger(this, "#frog", FROGGER_START);
        frogP2 = new Frogger(this, "#frogP2", FROGGER_START_P2);
        frogColP2 = new FroggerCollisionDetection(frogP2);
        frogCol = new FroggerCollisionDetection(frog);
        audiofx = new AudioEfx(frogCol, frog);
        audiofxP2 = new AudioEfx(frogColP2, frog);
        ui = new FroggerUI(this);
        wind = new WindGust();
        hwave = new HeatWave();
        goalmanager = new GoalManager();

        movingObjectsLayer = new AbstractBodyLayer.IterativeUpdate<MovingEntity>();
        particleLayer = new AbstractBodyLayer.IterativeUpdate<MovingEntity>();

        dificuldade = difficulty;
        if (!observerRI.getId().equals("joao")) {
            while (!observerRI.getSubjectRI().getState().getInfo().equals("initialize")) {
                observerRI.getSubjectRI().getState();
                System.out.println("OBS = " + observerRI.getId() + " Entrou aqui");
            }
        } else {
            System.out.println("obs = " + observerRI.getId() + "Passou aqui");
        }
        synchronized (this) {initializeLevel(1);}
    }


    public synchronized void initializeLevel(int level) throws RemoteException {
        /* dV is the velocity multiplier for all moving objects at the current game level */
        double dV = level * 0.05 + 0.5; //+ dificuldade;

        movingObjectsLayer.clear();

        /* River Traffic */
        riverLine1 = new MovingEntityFactory(new Vector2D(-(32 * 3), 2 * 32),
                new Vector2D(0.06 * dV, 0));

        riverLine2 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH, 3 * 32),
                new Vector2D(-0.04 * dV, 0));

        riverLine3 = new MovingEntityFactory(new Vector2D(-(32 * 3), 4 * 32),
                new Vector2D(0.09 * dV, 0));

        riverLine4 = new MovingEntityFactory(new Vector2D(-(32 * 4), 5 * 32),
                new Vector2D(0.045 * dV, 0));

        riverLine5 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH, 6 * 32),
                new Vector2D(-0.045 * dV, 0));

        /* Road Traffic */
        roadLine1 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH, 8 * 32),
                new Vector2D(-0.1 * dV, 0));

        roadLine2 = new MovingEntityFactory(new Vector2D(-(32 * 4), 9 * 32),
                new Vector2D(0.08 * dV, 0));

        roadLine3 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH, 10 * 32),
                new Vector2D(-0.12 * dV, 0));

        roadLine4 = new MovingEntityFactory(new Vector2D(-(32 * 4), 11 * 32),
                new Vector2D(0.075 * dV, 0));

        roadLine5 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH, 12 * 32),
                new Vector2D(-0.05 * dV, 0));

        goalmanager.init(level);
        for (Goal g : goalmanager.get()) {
            movingObjectsLayer.add(g);
        }

        if (observerRI.getId().equals("joao")) {
            State s = new State(observerRI.getId(), "initialize");
            observerRI.getSubjectRI().setState(s);
        }

        /* Build some traffic before game starts buy running MovingEntityFactories for fews cycles */

        /*for (int i = 0; i < 500; i++)
            cycleTrafficState();*/

    }

    public void cycleTrafficState() throws RemoteException {
        Random r = new Random(System.currentTimeMillis());
        int buildvehicle = r.nextInt(100);
        int buildbasic = r.nextInt(100);
        int car = r.nextInt(Car.TYPES);
        int croc = r.nextInt(100);
        int turtle = r.nextInt(100);
        int turtleif = r.nextInt(100);
        int genP1 = r.nextInt(100);
        int genP2 = r.nextInt(13 * 32);
        double genP3 = r.nextDouble();
        double genP4 = r.nextDouble();
        int genP5 = r.nextInt(100);
        double genP6 = r.nextDouble();
        double genP7 = r.nextDouble();
        State state = new State("cycleTraffic", buildvehicle + " " + buildbasic + " " + car + " " + croc + " " + turtle + " " + turtleif + " " + genP1 + " " + genP2 + " " + genP3 + " " + genP4 + " " + genP5 + " " + genP6 + " " + genP7);
        observerRI.getSubjectRI().setState(state);
    }

    /**
     * Populate movingObjectLayer with a cycle of cars/trucks, moving tree logs, etc
     *
     * @param deltaMs
     */
    public void cycleTraffic(long deltaMs, State state) throws RemoteException {
        MovingEntity m;
        String[] st = state.getInfo().split(" ");
        ArrayList<Integer> args = new ArrayList<>();
        ArrayList<Double> argsDouble = new ArrayList<>();
        if (state.getId().equals("cycleTraffic")) {
            for (int i = 0; i < st.length; i++) {
                if (i == 8 || i == 9 || i == 11 || i == 12) {
                    argsDouble.add(Double.parseDouble(st[i]));
                } else {
                    args.add(Integer.parseInt(st[i]));
                }
            }
        } else return;
        System.out.println(roadLine1);

        /* Road traffic updates */
        roadLine1.update(deltaMs);
        if ((m = roadLine1.buildVehicle(args.get(0), args.get(1), args.get(2))) != null)
            synchronized (this) {
                movingObjectsLayer.add(m);
            }

        roadLine2.update(deltaMs);
        if ((m = roadLine2.buildVehicle(args.get(0), args.get(1), args.get(2))) != null)
            synchronized (this) {
                movingObjectsLayer.add(m);
            }

        roadLine3.update(deltaMs);
        if ((m = roadLine3.buildVehicle(args.get(0), args.get(1), args.get(2))) != null)
            synchronized (this) {
                movingObjectsLayer.add(m);
            }

        roadLine4.update(deltaMs);
        if ((m = roadLine4.buildVehicle(args.get(0), args.get(1), args.get(2))) != null)
            synchronized (this) {
                movingObjectsLayer.add(m);
            }

        roadLine5.update(deltaMs);
        if ((m = roadLine5.buildVehicle(args.get(0), args.get(1), args.get(2))) != null)
            synchronized (this) {
                movingObjectsLayer.add(m);
            }

        /* River traffic updates */
        riverLine1.update(deltaMs);
        if ((m = riverLine1.buildShortLogWithTurtles(40, args.get(1), args.get(4), args.get(5))) != null)
            synchronized (this) {
                movingObjectsLayer.add(m);
            }

        riverLine2.update(deltaMs);
        if ((m = riverLine2.buildLongLogWithCrocodile(30, args.get(1), args.get(3))) != null)
            synchronized (this) {
                movingObjectsLayer.add(m);
            }

        riverLine3.update(deltaMs);
        if ((m = riverLine3.buildShortLogWithTurtles(50, args.get(1), args.get(4), args.get(5))) != null)
            synchronized (this) {
                movingObjectsLayer.add(m);
            }

        riverLine4.update(deltaMs);
        if ((m = riverLine4.buildLongLogWithCrocodile(20, args.get(1), args.get(3))) != null)
            synchronized (this) {
                movingObjectsLayer.add(m);
            }

        riverLine5.update(deltaMs);
        if ((m = riverLine5.buildShortLogWithTurtles(10, args.get(1), args.get(4), args.get(5))) != null)
            synchronized (this) {
                movingObjectsLayer.add(m);
            }

        // Do Wind
        if ((m = wind.genParticles(GameLevel, args.get(6), args.get(7), argsDouble.get(0), argsDouble.get(1))) != null)
            synchronized (this) {
                particleLayer.add(m);
            }

        // HeatWave
        if ((m = hwave.genParticles(frog.getCenterPosition(), args.get(8), argsDouble.get(2), argsDouble.get(3))) != null)
            synchronized (this) {
                particleLayer.add(m);
            }

        synchronized (this) {
            movingObjectsLayer.update(deltaMs);
            particleLayer.update(deltaMs);
        }
    }


    /**
     * Handling Frogger movement from keyboard input
     */
    public void froggerKeyboardHandler() throws RemoteException {
        keyboard.poll();

        boolean keyReleased = false;
        boolean downPressed = keyboard.isPressed(KeyEvent.VK_DOWN);
        boolean upPressed = keyboard.isPressed(KeyEvent.VK_UP);
        boolean leftPressed = keyboard.isPressed(KeyEvent.VK_LEFT);
        boolean rightPressed = keyboard.isPressed(KeyEvent.VK_RIGHT);

        // Enable/Disable cheating
        if (keyboard.isPressed(KeyEvent.VK_C))
            frog.cheating = true;
        if (keyboard.isPressed(KeyEvent.VK_V))
            frog.cheating = false;
        if (keyboard.isPressed(KeyEvent.VK_0)) {
            GameLevel = 10;
            initializeLevel(GameLevel);
        }


        /*
         * This logic checks for key strokes.
         * It registers a key press, and ignores all other key strokes
         * until the first key has been released
         */
        if (downPressed || upPressed || leftPressed || rightPressed)
            keyPressed = true;
        else if (keyPressed)
            keyReleased = true;

        if (listenInput) {
            State state;
            if (downPressed) {
                state = new State(String.valueOf(this.observerRI.getId()), "downPressed");
                this.observerRI.getSubjectRI().setState(state);
            }
            if (upPressed) {
                state = new State(String.valueOf(this.observerRI.getId()), "upPressed");
                this.observerRI.getSubjectRI().setState(state);
            }
            if (leftPressed) {
                state = new State(String.valueOf(this.observerRI.getId()), "leftPressed");
                this.observerRI.getSubjectRI().setState(state);
            }
            if (rightPressed) {
                state = new State(String.valueOf(this.observerRI.getId()), "rightPressed");
                this.observerRI.getSubjectRI().setState(state);
            }
            if (keyPressed)
                listenInput = false;
        }

        if (keyReleased) {
            listenInput = true;
            keyPressed = false;
        }

        if (keyboard.isPressed(KeyEvent.VK_ESCAPE))
            GameState = GAME_INTRO;
    }

    public synchronized void froggerHandler(State state) throws RemoteException {
        switch (state.getInfo()) {
            case "upPressed":
                if (state.getId().equals("joao"))
                    frog.moveUp();
                else if (state.getId().equals("guest"))
                    frogP2.moveUp();
                break;
            case "downPressed":
                if (state.getId().equals("joao"))
                    frog.moveDown();
                else if (state.getId().equals("guest"))
                    frogP2.moveDown();
                break;
            case "rightPressed":
                if (state.getId().equals("joao"))
                    frog.moveRight();
                else if (state.getId().equals("guest"))
                    frogP2.moveRight();
                break;
            case "leftPressed":
                if (state.getId().equals("joao"))
                    frog.moveLeft();
                else if (state.getId().equals("guest"))
                    frogP2.moveLeft();
                break;
        }
    }

    /**
     * Handle keyboard events while at the game intro menu
     */
    public void menuKeyboardHandler() throws RemoteException {
        keyboard.poll();
        // Following 2 if statements allow capture space bar key strokes
        if (!keyboard.isPressed(KeyEvent.VK_SPACE)) {
            space_has_been_released = true;
        }

        if (!space_has_been_released)
            return;

        if (keyboard.isPressed(KeyEvent.VK_SPACE) || space_has_been_released) {
            switch (GameState) {
                case GAME_INSTRUCTIONS:
                case GAME_OVER:
                    GameState = GAME_INTRO;
                    space_has_been_released = false;
                    break;
                default:
                    GameLives = FROGGER_LIVES;
                    GameScore = 0;
                    GameLevel = STARTING_LEVEL;
                    levelTimer = DEFAULT_LEVEL_TIME;
                    frog.setPosition(FROGGER_START);
                    frogP2.setPosition(FROGGER_START_P2);
                    GameState = GAME_PLAY;
                    audiofx.playGameMusic();
                    initializeLevel(GameLevel);
            }
        }
        if (keyboard.isPressed(KeyEvent.VK_H))
            GameState = GAME_INSTRUCTIONS;
    }

    /**
     * Handle keyboard when finished a level
     */
    public void finishLevelKeyboardHandler() throws RemoteException {
        keyboard.poll();
        if (keyboard.isPressed(KeyEvent.VK_SPACE)) {
            GameState = GAME_PLAY;
            audiofx.playGameMusic();
            audiofxP2.playGameMusic();
            initializeLevel(++GameLevel);
        }
    }


    /**
     * w00t
     */
    public void update(long deltaMs) {
        switch (GameState) {
            case GAME_PLAY:
                try {
                    froggerKeyboardHandler();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                wind.update(deltaMs);
                hwave.update(deltaMs);
                frog.update(deltaMs);
                frogP2.update(deltaMs);
                audiofx.update(deltaMs);
                ui.update(deltaMs);

                try {
                    if (observerRI.getId() == 0) cycleTrafficState();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                synchronized (this) {
                    frogCol.testCollision(movingObjectsLayer);
                    frogColP2.testCollision(movingObjectsLayer);
                }

                // Wind gusts work only when Frogger is on the river
                if (frogCol.isInRiver())
                    wind.start(GameLevel);
                wind.perform(frog, GameLevel, deltaMs);

                // Do the heat wave only when Frogger is on hot pavement
                if (frogCol.isOnRoad())
                    hwave.start(frog, GameLevel);
                hwave.perform(frog, deltaMs, GameLevel);

                // Wind gusts work only when Frogger is on the river
                if (frogColP2.isInRiver())
                    wind.start(GameLevel);
                wind.perform(frogP2, GameLevel, deltaMs);

                // Do the heat wave only when Frogger is on hot pavement
                if (frogColP2.isOnRoad())
                    hwave.start(frogP2, GameLevel);
                hwave.perform(frogP2, deltaMs, GameLevel);

            synchronized (this) {
                if (!frog.isAlive)
                    synchronized (this){particleLayer.clear();}

                if (!frogP2.isAlive)
                    synchronized (this){particleLayer.clear();}
            }
                goalmanager.update(deltaMs);

                if (goalmanager.getUnreached().size() == 0) {
                    GameState = GAME_FINISH_LEVEL;
                    audiofx.playCompleteLevel();
                    synchronized (this){particleLayer.clear();}
                }

                if (GameLives < 1) {
                    GameState = GAME_OVER;
                }

                break;

            case GAME_OVER:
            case GAME_INSTRUCTIONS:
            case GAME_INTRO:
                goalmanager.update(deltaMs);
                try {
                    menuKeyboardHandler();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                try {
                    if (observerRI.getId() == 0) {
                        cycleTrafficState();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;

            case GAME_FINISH_LEVEL:
                try {
                    finishLevelKeyboardHandler();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    /**
     * Rendering game objects
     */
    public void render(RenderingContext rc) {
        switch (GameState) {
            case GAME_FINISH_LEVEL:
            case GAME_PLAY:
                backgroundLayer.render(rc);

                if (frog.isAlive) {
                    synchronized (this) {
                        movingObjectsLayer.render(rc);
                    }
                    //frog.collisionObjects.get(0).render(rc);
                    frog.render(rc);
                    frogP2.render(rc);
                } else {
                    frog.render(rc);
                    frogP2.render(rc);
                    synchronized (this) {
                        movingObjectsLayer.render(rc);
                    }
                }

                synchronized (this){particleLayer.render(rc);}
                try {
                    if (this.observerRI.getId() == 0){
                        ui.render(rc, frog.getFROGGER_LIVES());
                    } else if (this.observerRI.getId() == 1){
                        ui.render(rc, frogP2.getFROGGER_LIVES());
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                break;

            case GAME_OVER:
            case GAME_INSTRUCTIONS:
            case GAME_INTRO:
                backgroundLayer.render(rc);
                synchronized (this) {
                    movingObjectsLayer.render(rc);
                }
                try {
                    if (this.observerRI.getId() == 0){
                        ui.render(rc, frog.getFROGGER_LIVES());
                    } else if (this.observerRI.getId() == 1){
                        ui.render(rc, frogP2.getFROGGER_LIVES());
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    /*public static void main (String[] args) {
		Main f = new Main();
		f.run();
	}*/
}