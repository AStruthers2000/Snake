import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Random;

public class Snake extends JPanel {

    private int[] allXDots = new int[2000];
    private int[] allYDots = new int[2000];

    private int coinX = 0;
    private int coinY = 0;

    private static int dots = 6;
    private static int size = 10;
    private static String direction = "left";
    private static String endMessage = "";
    private static boolean inGame = false;
    private static boolean paused = false;

    Snake() {
        initGame();
        addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    direction = "left";
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    direction = "right";
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    direction = "up";
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    direction = "down";
                }
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    paused = !paused;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        setFocusable(true);
    }

    private void initGame() {
        System.out.println("Welcome to snake");

        genCoinSpot();

        for (int a = 0; a < 3; a++) {
            allXDots[a] = 250 + (a * 15);
            allYDots[a] = 100;
        }
    }

    private void genCoinSpot() {
        Random rand = new Random();
        while(true) {
            boolean isTaken = false;
            coinX = (rand.nextInt(48) + 1) * 10;
            coinY = (rand.nextInt(38) + 1) * 10;

            for(int spot = 0; spot < dots; spot++){
                if(allXDots[spot] == coinX && allYDots[spot] == coinY){
                    isTaken = true;
                }
            }
            if(!isTaken){
                break;
            }
        }
        System.out.println("(" + coinX + ", " + coinY + ")");
    }

    private void move() {
        for (int spot = dots; spot > 0; spot--) {
            allXDots[spot] = allXDots[(spot - 1)];
            allYDots[spot] = allYDots[(spot - 1)];
        }

        if (direction.equalsIgnoreCase("left")) {
            allXDots[0] -= size;
        }
        if (direction.equalsIgnoreCase("right")) {
            allXDots[0] += size;
        }
        if (direction.equalsIgnoreCase("up")) {
            allYDots[0] -= size;
        }
        if (direction.equalsIgnoreCase("down")) {
            allYDots[0] += size;
        }

        if (new Rectangle(allXDots[0], allYDots[0], size, size).intersects(new Rectangle(coinX, coinY, size, size))) {
            dots += 6;
            allXDots[dots - 1] = allXDots[dots - 2];
            allYDots[dots - 1] = allYDots[dots - 2];
            genCoinSpot();
        }
    }

    private void checkCollision() {
        Rectangle head = new Rectangle(allXDots[0], allYDots[0], size, size);
        Rectangle playArea = new Rectangle(10, 10, 480, 380);

        if (!playArea.contains(head)) {
            gameover(0);
        }

        for (int a = 1; a < dots; a++) {
            Rectangle body = new Rectangle(allXDots[a], allYDots[a], size, size);
            if (head.intersects(body)) {
                if (dots >= 1824) {
                    gameover(2);
                } else {
                    gameover(1);
                }
            }
        }
    }

    private void gameover(int code) {
        inGame = false;
        if (code == 0) {
            endMessage = "You hit a wall!";
        } else if (code == 1) {
            endMessage = "You hit yourself!";
        } else if (code == 2) {
            endMessage = "Congratulations!!! You won!!!";
        }
    }

    private void reset() {
        allXDots = new int[2000];
        allYDots = new int[2000];

        coinX = 0;
        coinY = 0;

        dots = 5;
        size = 10;
        direction = "left";
        endMessage = "";
        inGame = false;
    }

    private void drawBorder(Graphics2D graphics) {
        Color prevColor = graphics.getColor();
        graphics.setColor(Color.RED);
        graphics.fillRect(0, 0, 10, 400);
        graphics.fillRect(0, 0, 500, 10);
        graphics.fillRect(490, 0, 10, 400);
        graphics.fillRect(0, 390, 500, 10);
        graphics.setColor(prevColor);
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D graphics = (Graphics2D) g;

        this.setBackground(Color.BLACK);
        graphics.setColor(Color.GREEN);

        drawBorder(graphics);

        for (int spot = 0; spot < dots; spot++) {
            if (spot == 0) {
                graphics.setColor(Color.BLUE);
            } else {
                graphics.setColor(Color.GREEN);
            }
            graphics.fillOval(allXDots[spot], allYDots[spot], size, size);
        }

        graphics.setColor(Color.YELLOW);
        graphics.fillOval(coinX, coinY, size, size);

        if(!paused) {
            move();
            checkCollision();
        }else{
            graphics.setFont(new Font("verdana", Font.BOLD, 50));
            graphics.drawString("Paused",10,50);
        }
    }

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Snake!");
        Snake game = new Snake();
        frame.add(game);
        frame.setSize(506, 435);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        JOptionPane.showMessageDialog(
                null,
                "Welcome to Snake!\n" +
                        "The objective of the game is to fill the entire screen with the snake.\n" +
                        "Every coin you gather increases the length of the snake.\n\n" +
                        "Controls:\n" +
                        "The arrow keys move the snake (up/down/left/right)\n" +
                        "Press <ESC> to pause the game, <ESC> to resume"
        );

        inGame = true;

        while (true) {
            while (inGame) {
                frame.repaint();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            frame.repaint();
            JOptionPane.showMessageDialog(null, endMessage + "\nGame over!");

            int again = JOptionPane.showConfirmDialog(null, "Play again?");
            if (again == 0) {
                game.reset();
                game.initGame();
                inGame = true;
            } else {
                JOptionPane.showMessageDialog(null, "Thanks for playing! - Andrew");
                System.exit(0);
            }
        }
    }
}
