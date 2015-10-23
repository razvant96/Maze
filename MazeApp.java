
import java.util.Scanner;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Random;

class Maze extends JPanel implements ActionListener {

    int width;
    int height;
    Cell[][] cells;
    int currentX;  // not used, only there to stifle the compiler
    int currentY;
    int[][] vis;
    Timer timer2 = new Timer(500, this);
    Timer timer1 = new Timer(500, this);

    // maze reader
    void readFromFile(String filename) {
        final String WALL = "#";
        final String FLOOR = " ";
        final String START = "S";
        final String END = "E";
        File initfile;

        try {
            initfile = new File(filename);
            Scanner scanner = new Scanner(initfile);

            width = scanner.nextInt();
            height = scanner.nextInt();
            scanner.nextLine(); // go to the next line, where the grid starts
            cells = new Cell[width][height];

            for (int y = 0; y < height; y++) {
                String line = scanner.nextLine();
                for (int x = 0; x < width; x++) {
                    switch (line.substring(x, x + 1)) {
                        case WALL:
                            cells[x][y] = new Wall();
                            break;
                        case START:
                            cells[x][y] = new Start();
                            currentX = x;
                            currentY = y;
                            break;
                        case END:
                            cells[x][y] = new End();
                            break;
                        case FLOOR:
                        default:
                            cells[x][y] = new Floor();
                            break;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Could not open file due to");
            System.out.println(e);
        }
    }

    public void paintComponent(Graphics g) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                cells[i][j].draw(g, 10 * i, 10 * j);//drawing every cell
            }
        }
    }

    public void actionPerformed(ActionEvent e) {

    }

    //random step algorithm
    /*void randomStep() {
     int dx[] = {1, 0, -1, 0};
     int dy[] = {0, 1, 0, -1};
     int x;
     int y;
     Random random = new Random();

     do {
     int i = random.nextInt(dx.length);
     x = currentX + dx[i];
     y = currentY + dy[i];
     } while (cells[x][y].isWall());
     cells[currentX][currentY].leave();
     cells[x][y].visit();
     currentX = x;
     currentY = y;
     if (cells[x][y].isEnd()) {
     timer2.stop();
     }
     }
     */
    
    void stepLeastVisitedNeighbor() {
        int i = currentX;
        int j = currentY;
        int type = 5;
        vis[i][j]++;
        int min;
        if (width < height) {
            min = height;
        } else {
            min = width;
        }
        int px = 0;
        int py = 0;
        int dx[] = {1, 0, -1, 0};
        int dy[] = {0, 1, 0, -1};
        int vx = i + dx[0];
        int vy = j + dy[0];
        if (vx >= 0 && vx < width && vy >= 0 && vy < height) {
            min = vis[i + dx[0]][j + dy[0]];
            px = i + dx[0];
            py = j + dy[0];
        }
        for (int k = 1; k < 3; k++) {
            vx = i + dx[k];
            vy = j + dy[k];
            if (vx >= 0 && vx < width && vy >= 0 && vy < height) {
                if (vis[i + dx[k]][j + dy[k]] < min) {
                    min = vis[i + dx[k]][j + dy[k]];
                    px = i + dx[k];
                    py = j + dy[k];
                }
            }
        }
        currentX = px;
        currentY = py;
        if (cells[currentX][currentY].isWall()) {
            type = 0;
        }
        if (cells[currentX][currentY].isEnd()) {
            type = 3;
        }
        if (cells[currentX][currentY].isFloor()) {
            type = 1;
        }
        if (cells[currentX][currentY].isStart()) {
            type = 2;
        }
        cells[currentX][currentY] = new CurrentPos();
        repaint();
        if (type == 0) {
            cells[currentX][currentY] = new Wall();
        }
        if (type == 1) {
            cells[currentX][currentY] = new Floor();
        }
        if (type == 2) {
            cells[currentX][currentY] = new Start();
        }
        if (type == 3) {
            cells[currentX][currentY] = new End();
        }
    }

    void leastVisitedNeighbor() {
        vis = new int[width][height];
        while (!cells[currentX][currentY].isEnd()) {
            stepLeastVisitedNeighbor();
        }
        timer1.stop();
    }
}

//cell class
abstract class Cell {

    void draw(Graphics g, int px, int py) {
    }

    abstract boolean isWall();

    abstract boolean isEnd();

    abstract boolean isFloor();

    abstract boolean isStart();

    void leave() {

    }

    void visit() {
    }
}
//current position

class CurrentPos extends Cell {

    private int px;
    private int py;

    void draw(Graphics g, int px, int py) {
        Color color = new Color(255, 165, 0);
        g.setColor(color);
        g.fillRect(px, py, 10, 10);
    }

    boolean isEnd() {
        return false;
    }

    boolean isWall() {
        return false;
    }

    boolean isFloor() {
        return false;
    }

    boolean isStart() {
        return false;
    }

    void leave() {
    }

    void visit() {

    }
}

class Wall extends Cell {

    private int px;
    private int py;

    void draw(Graphics g, int px, int py) {
        Color color = new Color(0, 0, 0);
        g.setColor(color);
        g.fillRect(px, py, 10, 10);
    }

    boolean isEnd() {
        return false;
    }

    boolean isWall() {
        return true;
    }

    boolean isFloor() {
        return false;
    }

    boolean isStart() {
        return false;
    }
}

class Floor extends Cell {

    private int px;
    private int py;

    void draw(Graphics g, int px, int py) {
        Color color = new Color(0, 255, 0);
        g.setColor(color);
        g.fillRect(px, py, 10, 10);
    }

    boolean isEnd() {
        return false;
    }

    boolean isWall() {
        return false;
    }

    boolean isFloor() {
        return true;
    }

    boolean isStart() {
        return false;
    }
}

class Start extends Cell {

    private int px;
    private int py;

    void draw(Graphics g, int px, int py) {
        Color color = new Color(255, 0, 0);
        g.setColor(color);
        g.fillRect(px, py, 10, 10);
    }

    boolean isEnd() {
        return false;
    }

    boolean isWall() {
        return false;
    }

    boolean isFloor() {
        return false;
    }

    boolean isStart() {
        return true;
    }
}

class End extends Cell {

    private int px;
    private int py;

    void draw(Graphics g, int px, int py) {
        Color color = new Color(255, 0, 0);
        g.setColor(color);
        g.fillRect(px, py, 10, 10);
    }

    boolean isEnd() {
        return true;
    }

    boolean isWall() {
        return false;
    }

    boolean isFloor() {
        return false;
    }

    boolean isStart() {
        return false;
    }

}

public class MazeApp implements ActionListener {

    JFrame frame;
    JPanel buttonPanel;
    JButton start1Button;
    JButton resetButton;
    JButton start2Button;
    JButton start3Button;
    Maze maze = new Maze();

    void buildGUI() {
        this.frame = new JFrame("Maze");
        this.frame.add(maze, BorderLayout.CENTER);

        this.buttonPanel = new JPanel();
        this.frame.add(buttonPanel, BorderLayout.NORTH);
        this.start1Button = new JButton("Start1");
        this.start1Button.addActionListener(this);
        this.buttonPanel.add(start1Button);
        this.resetButton = new JButton("Reset");
        this.resetButton.addActionListener(this);
        this.buttonPanel.add(resetButton);
        this.start2Button = new JButton("Start2");
        this.start2Button.addActionListener(this);
        this.buttonPanel.add(start2Button);
        this.start3Button = new JButton("Start3");
        this.start3Button.addActionListener(this);
        this.buttonPanel.add(start3Button);

        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(600, 500);
        this.frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == start1Button) {
            /*maze.timer2.start();
             maze.randomStep();*/
        } else {
            if (e.getSource() == start2Button);
            {
                maze.timer1.start();
                maze.leastVisitedNeighbor();
            }
        }
    }
// comment

    void run() {
        this.maze.readFromFile("maze.txt");
        this.buildGUI();
        //this.maze.repaint();
    }

    public static void main(String[] args) {
        new MazeApp().run();
    }
}
