import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class Gui extends JPanel implements ActionListener, Runnable {
    private final JLabel labelNbMines;
    private final JLabel labelTime;
    private final JLabel labelConnect;
    private final JButton butQuit = new JButton("Exit") ;
    private final JButton butNew = new JButton("New Game") ;
    private final JPanel panelCenter = new JPanel();
    private final JMenuItem menuEasy = new JMenuItem("Easy");
    private final JMenuItem menuMedium = new JMenuItem("Medium");
    private final JMenuItem menuHard = new JMenuItem("Hard");
    private final JMenuItem menuConnexion = new JMenuItem("Connexion");
    private final JTextField fieldName = new JTextField("Name");
    private Field field;
    private final Main main;
    private Case [][] tabLabelMines;
    private TimerTask task;
    private final Timer timer = new Timer();
    public boolean stop_chrono = true;
    public int time = 0;
    private DataOutputStream dOut;
    private DataInputStream dIn;
    private int [][] res_net;
    static boolean online = false;
    Gui(Field field, Main main) {
        this.main = main;
        this.field = field;
        setLayout(new BorderLayout());

        labelConnect = new JLabel("Disconnected"+"          ");
        labelConnect.setForeground(Color.RED);
        JPanel panelNorth = new JPanel();
        panelNorth.add(labelConnect);
        labelNbMines = new JLabel("Mines : "+ field.getNbMines()+"          ");
        labelNbMines.setForeground(Color.WHITE);
        panelNorth.add(labelNbMines);
        labelTime = new JLabel("Time : "+ time);
        labelTime.setForeground(Color.WHITE);
        panelNorth.add(labelTime);
        menu();
        panelNorth.setBackground(Color.darkGray);
        add(panelNorth, BorderLayout.NORTH);

        tabLabelMines = new Case[field.getWidth()][field.getHeight()];
        panelCenter.setLayout(new GridLayout(field.getWidth(),field.getHeight()));
        panelCenter.setBackground(Color.white);
        for(int i=0; i< field.getWidth(); i++)
            for(int j=0; j< field.getHeight(); j++) {
                tabLabelMines[i][j] = new Case(this,i,j);
                panelCenter.add(tabLabelMines[i][j]);
            }
        add(panelCenter,BorderLayout.CENTER);

        butQuit.setBackground(Color.lightGray);
        butNew.setBackground(Color.lightGray);
        butQuit.setForeground(Color.RED);
        butQuit.addActionListener(this);
        butNew.addActionListener(this);
        JPanel panelSouth = new JPanel();
        panelSouth.add(butNew);
        panelSouth.add(butQuit);
        panelSouth.setBackground(Color.darkGray);
        add(panelSouth,BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == butQuit) {
            System.out.println("Exit");
            System.exit(0);
        }
        else if (e.getSource()==butNew)   {
            System.out.println("Play Again");
            if (!online)
                popUp("<HTML><BODY><FONT COLOR=\"BLACK\"> <FONT SIZE=\"+3\"> <B> " + "New game");
            else
                popUpOnline("<HTML><BODY><FONT COLOR=\"BLACK\"> <FONT SIZE=\"+3\"> <B> " + "New game");
        }
        else if (e.getSource()==menuEasy) {
            online = false;
            System.out.println("Start easy game");
            field = new Field(5, 5, 5);
            newGame();

        }
        else if (e.getSource()==menuMedium) {
            online = false;
            System.out.println("Start medium game");
            field = new Field(10, 7, 7);
            newGame();
        }
        else if (e.getSource()==menuHard){
            online = false;
            System.out.println("Start hard game");
            field = new Field(20, 10, 10);
            newGame();
        }
        else if (e.getSource()==menuConnexion){
            connect2Netwotk();
        }
    }

    private void connect2Netwotk(){
        System.out.println("Try to connect");
        labelConnect.setForeground(Color.WHITE);
        labelConnect.setText("Try to connect"+"          ");
        try {
            Socket socket = new Socket("localhost",Serveur.PORT);
            labelConnect.setForeground(Color.GREEN);
            labelConnect.setText("Connected"+"          ");
            System.out.println("Connected");
            online = true;

            dOut = new DataOutputStream(socket.getOutputStream());
            dIn = new DataInputStream(socket.getInputStream());
            dOut.writeInt(Serveur.CMD_NAME);
            dOut.writeUTF(fieldName.getText());

            System.out.println("Start online game");
            onlineGame(Serveur.WIDTH_NETWORK,Serveur.HEIGHT_NETWORK,Serveur.NBMINES_NETWORK);
            time = 0;

            //process for listening server
            Thread th = new Thread(this);
            th.start();

        } catch (IOException e) {
            e.printStackTrace();
            labelConnect.setForeground(Color.RED);
            labelConnect.setText("Can't connected"+"          ");
        }

    }

    private void newGame() {
        stop_chrono = true;
        panelCenter.removeAll();
        field.placeMines();
        field.display();
        tabLabelMines = new Case[field.getWidth()][field.getHeight()];
        panelCenter.setLayout(new GridLayout(field.getWidth(),field.getHeight()));
        for(int i=0; i< field.getWidth(); i++)
            for(int j=0; j< field.getHeight(); j++) {
                tabLabelMines[i][j] = new Case(this, i, j);
                panelCenter.add(tabLabelMines[i][j]);
            }
        labelNbMines.setText("Mines : "+ field.getNbMines()+"          ");
        add(panelCenter, BorderLayout.CENTER);
        time =0;
        labelTime.setText("Time : "+ time);
        main.pack();
        repaint();
    }
    public void chrono(){
        timer.schedule(task = new TimerTask() {
            @Override
            public void run() {
                if(!stop_chrono) {
                    labelTime.setText("Time : " + time);
                    repaint();
                    main.pack();
                    time++;
                }
                else {
                    task.cancel();
                }
            }
        },1000,1000);
    }

    public void popUp(String text){
        UIManager.put("OptionPane.yesButtonText", "Easy");
        UIManager.put("OptionPane.noButtonText", "Medium");
        UIManager.put("OptionPane.cancelButtonText", "Hard");
        int click = JOptionPane.showConfirmDialog(main,text + "\nSelect level","Démineur",JOptionPane.YES_NO_CANCEL_OPTION);
        if (click == 0) {
            System.out.println("Start easy game");
            field = new Field(3, 5, 5);
            newGame();
        }
        else if (click == 1) {
            System.out.println("Start medium game");
            field = new Field(10, 10, 10);
            newGame();
        }
        else if (click == 2){
            System.out.println("Start hard game");
            field = new Field(20, 15, 15);
            newGame();
        }
        else {
            System.out.println("Exit");
            System.exit(0);
        }
    }

    public void popUpOnline(String text){
        UIManager.put("OptionPane.yesButtonText", "Play again");
        UIManager.put("OptionPane.noButtonText", "Disconnect");
        UIManager.put("OptionPane.cancelButtonText", "Quit");
        int click = JOptionPane.showConfirmDialog(main,text + "\nSelect level","Démineur",JOptionPane.YES_NO_CANCEL_OPTION);
        if (click == 0) {
            System.out.println("Start new game");
            onlineGame(Serveur.WIDTH_NETWORK,Serveur.HEIGHT_NETWORK,Serveur.NBMINES_NETWORK);
            try {
                dOut.writeInt(Serveur.CMD_START);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else if (click == 1) {
            System.out.println("Disconnect");
            labelConnect.setForeground(Color.RED);
            labelConnect.setText("Disconnected"+"          ");
            online = false;
            popUp("<HTML><BODY><FONT COLOR=\"BLACK\"> <FONT SIZE=\"+3\"> <B> " + "New game");
        }
        else if (click == 2){
            System.out.println("Quit");
            System.exit(0);
        }
        else {
            System.out.println("Exit");
            System.exit(0);
        }
    }

    private void menu(){
        JMenuBar menuBar =new JMenuBar();
        JMenu menuLevel= new JMenu("Level");
        JMenu menuConnect= new JMenu("Connect");
        menuBar.add(menuLevel);
        menuBar.add(menuConnect);
        menuBar.add(fieldName);

        menuEasy.addActionListener(this);
        menuMedium.addActionListener(this);
        menuHard.addActionListener(this);
        menuLevel.add(menuEasy);
        menuLevel.add(menuMedium);
        menuLevel.add(menuHard);

        menuConnexion.addActionListener(this);
        menuConnect.add(menuConnexion);

        main.setJMenuBar(menuBar);
    }
    public Field getField() {
        return field;
    }

    @Override
    public void run(){
        System.out.println("Listen server");
        while(online){
            try {
                int cmd = dIn.readInt();
                if(cmd == Serveur.CMD_POS) {
                    int x_net = dIn.readInt();
                    int y_net = dIn.readInt();
                    res_net[x_net][y_net] = dIn.readInt();
                    System.out.println( "case "+ x_net +","+ y_net +" = "+ res_net[x_net][y_net]);
                    tabLabelMines[x_net][y_net].clicked = true;
                    repaint();
                }
                if (cmd == Serveur.CMD_END){
                    String text = dIn.readUTF();
                    stop_chrono = true;
                    popUpOnline(text);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void send_position(int x, int y){
        try {
            dOut.writeInt(Serveur.CMD_POS);
            dOut.writeInt(x);
            dOut.writeInt(y);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void onlineGame(int Width,int Height, int Mines) {
        panelCenter.removeAll();
        tabLabelMines = new Case[Width][Height];
        res_net = new int [Width][Height];
        panelCenter.setLayout(new GridLayout(Width,Height));
        for(int i=0; i< Width; i++)
            for(int j=0; j< Height; j++) {
                tabLabelMines[i][j] = new Case(this, i, j);
                panelCenter.add(tabLabelMines[i][j]);
            }
        labelNbMines.setText("Mines : "+ Mines+"          ");
        add(panelCenter, BorderLayout.CENTER);
        time =0;
        labelTime.setText("Time : "+ time);
        main.pack();
        repaint();
    }
    public int getRes_net(int x,int y) {
        return res_net[x][y];
    }
}