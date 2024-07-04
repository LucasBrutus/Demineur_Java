import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Serveur {
    final static int PORT =10000;
    final static int CMD_NAME = 1;
    final static int CMD_POS = 2;
    final static int CMD_START = 3;
    final static int CMD_END = 4;
    private String user;
    public static int WIDTH_NETWORK = 7;
    public static int HEIGHT_NETWORK = 7;
    public static int NBMINES_NETWORK = 5;
    private int cpt = 0;
    Field field = new Field(NBMINES_NETWORK,WIDTH_NETWORK,WIDTH_NETWORK);
    Serveur(){
            System.out.println("Démarrage serveur");
            try {
                ServerSocket server = new ServerSocket(PORT);
                System.out.println("Attente de client");
                Socket socket = server.accept();

                DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
                DataInputStream dIn = new DataInputStream(socket.getInputStream());

                while (!Gui.online) {
                    int cmd = dIn.readInt();
                    if (cmd == CMD_NAME) {
                        user = dIn.readUTF();
                        System.out.println(user + " connected");
                    } else if (cmd == CMD_POS) {
                        int x = dIn.readInt();
                        int y = dIn.readInt();
                        if (field.isMine(x, y)) {
                            System.out.println(user + " clicked " + x + "," + y + " = X");
                            dOut.writeInt(Serveur.CMD_POS);
                            dOut.writeInt(x);
                            dOut.writeInt(y);
                            dOut.writeInt(9);
                            dOut.writeInt(Serveur.CMD_END);
                            dOut.writeUTF("<HTML><BODY><FONT COLOR=\"RED\"> <FONT SIZE=\"+3\"> <B> " + user + " Loose the game");
                        } else {
                            System.out.println(user + " clicked " + x + "," + y + " = " + field.nbMinesAround(x, y));
                            dOut.writeInt(Serveur.CMD_POS);
                            dOut.writeInt(x);
                            dOut.writeInt(y);
                            dOut.writeInt(field.nbMinesAround(x, y));
                            cpt++;
                            if (cpt == field.getWidth() * field.getHeight() - field.getNbMines()) {
                                dOut.writeInt(Serveur.CMD_END);
                                dOut.writeUTF("<HTML><BODY><FONT COLOR=\"GREEN\"> <FONT SIZE=\"+3\"> <B> " + user + " Win the game");
                            }
                        }
                    } else if (cmd == CMD_START) {
                        field = new Field(NBMINES_NETWORK, WIDTH_NETWORK, WIDTH_NETWORK);
                        cpt = 0;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    public static void main(String[] args){
        new Serveur();
    }
}
