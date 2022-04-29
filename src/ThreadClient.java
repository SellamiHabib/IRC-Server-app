
import java.io.*;
import java.net.*;

class ThreadClient extends Thread {
    BufferedReader In;
    PrintWriter Out;
    ServeurIRC serveur;
    String nom = "";
    Socket socket;

    public ThreadClient(Socket socket, ServeurIRC s, BufferedReader In, PrintWriter Out) {
        //...(initialisation des proprietes)
        serveur = s;
        this.socket = socket;
        this.In = In;
        this.Out = Out;
        start();
    }

    public void run() {
        try {
            while (this.nom.equals("")) {
                this.nom = In.readLine();
                Out.println(this.nom);
            }
            serveur.ajouterClient(this);
            serveur.EnvoyerATous("Connexion : " + this.getNom() + " a entré le chat");
            serveur.EnvoyerListeClients();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String message = "";
        try {
            while (true) {
                message = In.readLine();
                if (message.equals("exit")) {
                    serveur.SupprimerClient(this, nom);
                    serveur.EnvoyerListeClients();
                    serveur.EnvoyerATous("Deconnexion : " + getNom() + " a quitté le chat");
                    break;
                }
                else
                    serveur.EnvoyerATous(getNom() + "> " + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Envoyer(String message) {
        try {
            Out.println(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getNom() {
        return nom;
    }

    @Override
    protected void finalize() throws Throwable {
        In.close();
        Out.close();
        socket.close();
        super.finalize();
    }
}
