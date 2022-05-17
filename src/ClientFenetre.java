import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;


class ClientFenetre extends JFrame implements Runnable, ActionListener {
    TextArea ServerOutput;
    TextArea UserList;
    JTextField Input;
    JButton send_button;
    JButton clear_button;

    Socket socketClient = null;
    BufferedReader In = null;
    PrintWriter Out = null;
    JLabel welcome_label;
    String nom = "";

    public static void main(String[] args) {
        InetAddress hote = null;
        int port = 1973;
        try {
            if (args.length >= 1)
                hote = InetAddress.getByName(args[0]);
            else
                hote = InetAddress.getLocalHost();

            if (args.length == 2)
                port = Integer.parseInt(args[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ClientFenetre IRCClient = new ClientFenetre(hote, port);
    }

    ClientFenetre(InetAddress hote, int port) {
        // PARTIE FRONT END
        super("IRC Chat");
        setSize(300, 300);
        setLayout(new BorderLayout());
        Container container = getContentPane();

        addWindowListener(new WindowAdapter() {
                              public void windowClosing(WindowEvent e) {
                                  try{
                                      Out.println("exit");
                                      Out.close();
                                      In.close();
                                      socketClient.close();
                                  }
                                  catch (Exception e1) {
                                      e1.printStackTrace();
                                  }
                                  setVisible(false);
                                  dispose();
                                  System.exit(0);
                              }
                          }
        );

        JPanel P_north = new JPanel(new FlowLayout(FlowLayout.CENTER));
        welcome_label = new JLabel("Serveur IRC");
        P_north.add(welcome_label);
        P_north.setSize(300, 100);
        container.add(P_north, BorderLayout.NORTH);

        //South Panel
        JPanel P_south = new JPanel();
        Input = new JTextField(40);

        Input.addActionListener(this);
        Input.setEnabled(true);

        ImageIcon send_img = new ImageIcon("assets/send.png");
        send_button = new JButton(send_img);
        send_button.addActionListener(this);

        ImageIcon clear_img = new ImageIcon("assets/clear.png");
        clear_button = new JButton(clear_img);
        clear_button.addActionListener(this);

        P_south.add(Input);
        P_south.add(clear_button);
        P_south.add(send_button);
        container.add(P_south, BorderLayout.SOUTH);

        //Center Panel
        ServerOutput = new TextArea(30, 50);
        container.add(ServerOutput, BorderLayout.CENTER);
        ServerOutput.setEditable(false);

        UserList = new TextArea(20, 20);
        container.add(UserList, BorderLayout.EAST);
        UserList.setEditable(false);
        UserList.setFocusable(false);

        pack();

        setVisible(true);
        Input.requestFocus();

        try {
            socketClient = new Socket(hote, port);
            In = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
            Out = new PrintWriter(socketClient.getOutputStream(), true);
            ServerOutput.append("Connexion avec le serveur établi\n");
            ServerOutput.append("Donner votre nom :\n");
            String message = "";

            while (this.nom.equals("")) {
                this.nom = In.readLine();
            }

            while (true) {
                message = In.readLine();
                if (message.equals("!LIST")) {
                    try {
                        UserList.setText("");
                        while (true) {
                            message = In.readLine();
                            if (message.equals("EOF")) {
                                break;
                            }
                            UserList.append(message + "\n");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else
                    ServerOutput.append(message + "\n");


            }
        } catch (Exception e) {
            ServerOutput.append("Error: Connexion avec le serveur échoué\n");
            Input.setEnabled(false);
            System.out.println(e.getMessage());
        }

        // Lancement du Thread
        Thread t = new Thread(this);
        t.start();
    }

    public void run() {
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == Input || e.getSource() == send_button) {
            String message = Input.getText();

            if (!message.equals("")) {
                Out.println(message);
            }
            Input.setText("");
        }
        if (e.getSource() == clear_button) {
            ServerOutput.setText("");
        }
    }

    protected void finalize() {

    }
}
