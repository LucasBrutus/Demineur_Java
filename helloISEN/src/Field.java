import java.util.Random;

/**
 * contains data for mines
 */
public class Field {
    private final boolean[][] arrayMines;
    private static final int DIM = 5;
    private static final int NBMINES = 3;
    private final int nbMines;
    Random random = new Random();

    Field() {
        this(NBMINES, DIM, DIM);
    }

    /**
     * Constructeur nbMines
     */
    Field(int nbMines, int col, int row) {
        this.nbMines = nbMines;
        arrayMines = new boolean[col][row];
        placeMines();
    }

    /**
     * display the field
     */
    public void display() {
        for (int i = 0; i < arrayMines.length; i++) {
            for (int j = 0; j < arrayMines[0].length; j++) {
                if (arrayMines[i][j])
                    System.out.print("X ");
                else
                    System.out.print(nbMinesAround(i, j) + " ");
            }
            System.out.println(" ");
        }
    }

    /**
     * Place Mines ramdomly
     */
    public void placeMines() {
        int nb = 0;
        for(int i=0; i<arrayMines.length; i++)
            for(int j=0; j< arrayMines[0].length; j++)
                arrayMines[i][j] = false;
        while (nb < nbMines) {
            int x = random.nextInt(arrayMines.length);
            int y = random.nextInt(arrayMines[0].length);
            if (!arrayMines[x][y]) {
                arrayMines[x][y] = true;
                nb++;
            }
        }
    }

    /**
     * find number of mines around
     */
    public int nbMinesAround(int x, int y) {
        int nb = 0;
        int borneInfX = x == 0 ? 0 : x - 1;
        int borneInfY = y == 0 ? 0 : y - 1;
        int borneSupX = x == arrayMines.length - 1 ? x + 1 : x + 2; //opérateur ternaire
        int borneSupY = y == arrayMines[0].length - 1 ? y + 1 : y + 2;

        for (int i = borneInfX; i < borneSupX; i++)
            for (int j = borneInfY; j < borneSupY; j++)
                if (arrayMines[i][j])
                    nb++;
        return nb;
    }

    public int getNbMines() {
        return nbMines;
    }

    public int getWidth() {
        return arrayMines.length;
    }
    public int getHeight() {
        return arrayMines[0].length;
    }
    public boolean isMine(int x, int y){ return arrayMines[x][y]; }
}