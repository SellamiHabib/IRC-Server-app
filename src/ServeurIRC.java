import java.io.*;
import java.net.*;
import java.util.*;

class ServeurIRC {
    Vector<ThreadClient> V;
    int nbClient;

    public static void main(String[] args) {
        int port = 1973;
        if (args.length == 1)
            port = Integer.parseInt(args[0]);
        new ServeurIRC(port);

    }
    public ServeurIRC(int port) {
        V = new Vector<ThreadClient>();
        try {
            ServerSocket server = new ServerSocket(port);
            while (true) {
                try {
                    System.out.println("Listening to new clients");
                    Socket socket = server.accept();
                    BufferedReader In = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter Out = new PrintWriter(socket.getOutputStream(), true);
                    nbClient++;
                    ThreadClient c = new ThreadClient(socket, this, In, Out);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    synchronized protected void ajouterClient(ThreadClient c) {
        V.addElement(c);
    }

    synchronized public void EnvoyerATous(String s) {
        for (int i = 0; i < V.size(); i++) {
            ThreadClient c = (ThreadClient) V.elementAt(i);
            c.Envoyer(s);
        }
    }

    synchronized public void EnvoyerListeClients() {
        //...(envoyer dans out le nom de tous les clients du vecteur V)
        EnvoyerATous("!LIST");
        for (int i = 0; i < V.size(); i++) {
            ThreadClient x = (ThreadClient) V.elementAt(i);
            EnvoyerATous(x.getNom());
            if (i == V.size() - 1) {
                EnvoyerATous("EOF");
            }
        }
    }
    synchronized public void SupprimerClient(ThreadClient c, String nom) {
        // supprimer le client
        V.removeElement(c);

    }
}