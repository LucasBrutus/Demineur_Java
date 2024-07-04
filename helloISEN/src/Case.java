import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Case extends JPanel implements MouseListener {
    private static final int width = 50;
    private static final int height = 50;
    private static final int border = 4;
    public boolean clicked,flag;
    private final Gui gui;
    private final int x;
    private final int y;
    static int cpt;

    Case(Gui gui, int x, int y) {
        this.gui = gui;
        this.x = x;
        this.y = y;
        setPreferredSize(new Dimension(width, height));
        addMouseListener(this);
        cpt=0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!clicked) {
            g.setColor(Color.orange);
            g.fillRect(border / 2, border / 2, width - border, height - border);
            if(flag){
                ImageIcon bombe = new ImageIcon("C:\\Users\\lucas\\OneDrive\\Documents\\ISEN\\Java\\flag3.png");
                g.drawImage(bombe.getImage(),border/2,border/2,width-border,height-border,this);
            }
        }
        else if (!Gui.online && !flag) {
            if(gui.getField().isMine(x,y)) {
                ImageIcon flag = new ImageIcon("C:\\Users\\lucas\\OneDrive\\Documents\\ISEN\\Java\\bombe.png");
                g.drawImage(flag.getImage(),border/2,border/2,width-border,height-border,this);
            }
            else {
                g.setFont(new Font("Arial", Font.BOLD, 20));
                g.setColor(Color.black);
                g.drawString(String.valueOf(gui.getField().nbMinesAround(x,y)), width / 2 - getFont().getSize() / 2, height / 2 + getFont().getSize() / 2);
            }
        }
        else {
            if(gui.getRes_net(x,y)==9) {
                ImageIcon bombe = new ImageIcon("C:\\Users\\lucas\\OneDrive\\Documents\\ISEN\\Java\\bombe.png");
                g.drawImage(bombe.getImage(),border/2,border/2,width-border,height-border,this);
            }
            else {
                g.setFont(new Font("Arial", Font.BOLD, 20));
                g.setColor(Color.black);
                g.drawString(String.valueOf(gui.getRes_net(x,y)), width / 2 - getFont().getSize() / 2, height / 2 + getFont().getSize() / 2);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        int MouseClick = e.getButton();
        if (MouseClick == MouseEvent.BUTTON1) {
            if(gui.time ==0) {
                gui.stop_chrono = false;
                gui.chrono();
            }
            System.out.println("Clicked");
            if (Gui.online && !clicked && !flag)
                gui.send_position(x, y);
            else if (!clicked && !flag) {
                cpt++;
                clicked = true;
                repaint();
                if (!Gui.online && gui.getField().isMine(x, y) && !flag) {
                    gui.stop_chrono = true;
                    gui.popUp("<HTML><BODY><FONT COLOR=\"RED\"> <FONT SIZE=\"+3\"> <B> " + "Game over");
                }
                else if (!Gui.online && cpt == gui.getField().getWidth() * gui.getField().getHeight() - gui.getField().getNbMines()) {
                    gui.stop_chrono = true;
                    gui.popUp("<HTML><BODY><FONT COLOR=\"GREEN\"> <FONT SIZE=\"+3\"> <B> " + "Victory");
                }
            }
        }
        else if (MouseClick == MouseEvent.BUTTON3 && !clicked) {
            System.out.println("Flag");
            flag = !flag;
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}