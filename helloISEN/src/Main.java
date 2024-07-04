/**
 * My first prog Java
 * @author lucas
 * @version 0.0
 */

import javax.swing.*;

public class Main extends JFrame {
    Main() {
        Field field = new Field();
        field.display();

        setTitle("Démineur");
        setVisible(true);
        Gui gui = new Gui(field, this);
        setContentPane(gui);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        pack();

    }

    public static void main(String[] args) {
        System.out.println("Hello ISEN!");
        new Main() ;
    }
}