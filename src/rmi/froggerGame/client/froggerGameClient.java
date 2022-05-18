package rmi.froggerGame.client;

import rmi.froggerGame.frogger.Main;
import rmi.froggerGame.server.Game;
import rmi.froggerGame.server.State;
import rmi.froggerGame.server.froggerGameFactoryRI;
import rmi.froggerGame.server.froggerGameSessionRI;
import rmi.util.rmisetup.SetupContextRMI;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class froggerGameClient {
    /**
     * Context for connecting a RMI client MAIL_TO_ADDR a RMI Servant
     */
    private SetupContextRMI contextRMI;
    /**
     * Remote interface that will hold the Servant proxy
     */
    private froggerGameFactoryRI froggerGameFactoryRI;
    private String e, p;

    public static void main(String[] args) {
        if (args != null && args.length < 2) {
            System.err.println("usage: java [options] froggerGameClient <rmi_registry_ip> <rmi_registry_port> <service_name>");
            System.exit(-1);
        } else {
            //1. ============ Setup client RMI context ============
            froggerGameClient hwc = new froggerGameClient(args);
            //2. ============ Lookup service ============
            hwc.lookupService();
            //3. ============ Play with service ============
            hwc.playService();
        }
    }

    public froggerGameClient(String args[]) {
        try {
            //List ans set args
            SetupContextRMI.printArgs(this.getClass().getName(), args);
            String registryIP = args[0];
            String registryPort = args[1];
            String serviceName = args[2];
            //Create a context for RMI setup
            contextRMI = new SetupContextRMI(this.getClass(), registryIP, registryPort, new String[]{serviceName});
        } catch (RemoteException e) {
            Logger.getLogger(froggerGameClient.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private Remote lookupService() {
        try {
            //Get proxy MAIL_TO_ADDR rmiregistry
            Registry registry = contextRMI.getRegistry();
            //Lookup service on rmiregistry and wait for calls
            if (registry != null) {
                //Get service url (including servicename)
                String serviceUrl = contextRMI.getServicesUrl(0);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going MAIL_TO_ADDR lookup service @ {0}", serviceUrl);

                //============ Get proxy MAIL_TO_ADDR HelloWorld service ============
                froggerGameFactoryRI = (froggerGameFactoryRI) registry.lookup(serviceUrl);
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "registry not bound (check IPs). :(");
                //registry = LocateRegistry.createRegistry(1099);
            }
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return froggerGameFactoryRI;
    }

    private void playService() {
        try {
            //============ Call HelloWorld remote service ============
            froggerGameSessionRI froggerGameSessionRI = null;
            while (froggerGameSessionRI == null) {
                froggerGameSessionRI = menuLogin();
            }
            menuGame(froggerGameSessionRI);

            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going MAIL_TO_ADDR finish, bye. ;)");
        } catch (RemoteException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public froggerGameSessionRI menuLogin() throws RemoteException {
        System.out.print("1 - Register\n2 - Login\nChoice: ");
        Scanner op = new Scanner(System.in);
        Scanner email = new Scanner(System.in);
        Scanner password = new Scanner(System.in);
        switch (op.nextInt()) {
            case 1:
                System.out.print("\nEmail: ");
                e = email.next();
                System.out.print("\nPassword: ");
                p = password.next();
                froggerGameFactoryRI.register(e, p);
                return menuLogin();
            case 2:
                System.out.print("\nEmail: ");
                e = email.next();
                System.out.print("\nPassword: ");
                p = password.next();
                return froggerGameFactoryRI.login(e, p);
            default:
                System.out.println("Choose a number between 1 and 2");
                return menuLogin();
        }
    }

    public void menuGame(froggerGameSessionRI froggerGameSessionRI) throws RemoteException {
        System.out.print("1 - Create Game\n2 - Join Game\n3 - Logout\nChoice: ");
        Scanner op = new Scanner(System.in);
        Scanner difficulty = new Scanner(System.in);
        Scanner game = new Scanner(System.in);
        Game game1;
        Main f;
        String lowerCaseDifficulty;
        ObserverRI observerRI;
        switch (op.nextInt()) {
            case 1:
                System.out.print("\nDifficulty (Easy/Normal/Hard): ");
                observerRI = new ObserverImpl(e);
                lowerCaseDifficulty = difficulty.next().toLowerCase();
                int dificuldade = transformDifficulty(lowerCaseDifficulty);
                if (dificuldade == 0) return;
                game1 = froggerGameSessionRI.createGame(lowerCaseDifficulty, observerRI);
                observerRI.setSubjectRI(game1.getSubjectRI());
                while(game1.getNplayers() < 2){
                    State state = observerRI.getSubjectRI().getState();
                    if (!state.getInfo().equals("")) game1.setNplayers(Integer.parseInt(state.getInfo()));
                }
                f = new Main(dificuldade, observerRI);
                f.run();
                froggerGameSessionRI.exitGame(game1.getId(), observerRI);
                menuGame(froggerGameSessionRI);
                break;
            case 2:
                System.out.print("\nDifficulty: (Easy/Normal/Hard/All)");
                Game[] games;
                lowerCaseDifficulty = difficulty.next().toLowerCase();
                switch (lowerCaseDifficulty) {
                    case "easy":
                        games = froggerGameSessionRI.listGamesByDifficulty("easy");
                        break;
                    case "normal":
                        games = froggerGameSessionRI.listGamesByDifficulty("normal");
                        break;
                    case "hard":
                        games = froggerGameSessionRI.listGamesByDifficulty("hard");
                        break;
                    default:
                        games = froggerGameSessionRI.listAllGames();
                }
                for (Game g : games) {
                    System.out.println(g);
                }
                System.out.print("\nChoose one game by id: ");
                observerRI = new ObserverImpl(e);
                game1 = froggerGameSessionRI.joinGame(game.nextInt(), observerRI);
                int gameDifficulty = transformDifficulty(game1.getDifficulty());
                if (gameDifficulty == 0) return;
                observerRI.setSubjectRI(game1.getSubjectRI());
                State state = new State(observerRI.getId(), String.valueOf(game1.getNplayers()));
                observerRI.getSubjectRI().setState(state);
                f = new Main(gameDifficulty, observerRI);
                f.run();
                froggerGameSessionRI.exitGame(game1.getId(), observerRI);
                menuGame(froggerGameSessionRI);
                break;
            case 3:
                System.out.println("Logging out...");
                froggerGameSessionRI.logout();
                System.out.println("Logged out.");
                break;
        }
    }

    public int transformDifficulty(String d) {
        int num;
        switch (d) {
            case "easy":
                num = 1;
                break;
            case "normal":
                num = 2;
                break;
            case "hard":
                num = 3;
                break;
            default:
                System.out.println("Invalid Option");
                num = 0;
        }
        return num;
    }
}
