import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Project2 extends JPanel {
    private JFrame frame;
    private JButton start;
    private JLabel livesLabel;
    private JLabel scoreLabel;
    private JProgressBar lifeBar;
    private PlayerCircle player;
    private ArrayList<FallingShape> fallingShapes;
    private Timer timer;
    private Random random;
    private int lives = 3;
    private int shapeSpeed = 2;
    private int score = 0;
    private boolean gameRunning = false;

    /*
     * The constructor and setting up the frame/screen when the code is ran
     */
    public Project2() {
        frame = new JFrame("Falling Shapes"); // Creating JFrame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ensures that program stops when closed

        this.setBackground(Color.BLACK); // Setting background colour
        this.setPreferredSize(new Dimension(640, 480)); // Setting up size of JPanel

        start = new JButton("START"); // Setting up JButton
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { // When button is pressed, call startGame function (starts the game)
                startGame();
            }
        });

        this.add(start); // Adding the start button to JPanel

        // Set up the JLabel for Score
        scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setForeground(Color.WHITE);
        this.add(scoreLabel);

        // Set up the JLabel for Lives
        livesLabel = new JLabel("Lives: " + lives);
        livesLabel.setForeground(Color.WHITE);
        this.add(livesLabel);

        // Set up JProgressBar to show lives in a different way
        lifeBar = new JProgressBar(0, 3);
        lifeBar.setValue(lives);
        this.add(lifeBar);

        // Create player circle starting in the middle of the screen
        player = new PlayerCircle((getPreferredSize().width / 2) - 15, getPreferredSize().height - 80);

        fallingShapes = new ArrayList<>();
        random = new Random();

        // Set up key listener
        this.setFocusable(true);
        this.requestFocus();
        this.addKeyListener(new KeyTracker());

        // Timer for game loop (updates every 20ms)
        timer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateGame();
                repaint();
            }
        });

        // Adding JPanel to frame
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
    }

    /*
     * Setting up the game when the user presses the start button. Sets everything to default setting/position
     */
    private void startGame() {
        gameRunning = true;
        lives = 3;
        score = 0;
        shapeSpeed = 2;
        // Reset player to center horizontally and near bottom vertically
        player.setX((getWidth() / 2) - 15); 
        player.setY(getHeight() - 80); 
        fallingShapes.clear(); // Clear the ArrayList that contained all the shapes
        updateLabels();
        timer.start();
        start.setEnabled(false);
    }

    /*
     * When the game is running, this method continuously updates it.
     * A random shape is generated at the top and falls down.
     * If the shape goes past the bottom, score increases.
     * Every 10 points, shapes fall faster.
     * If the player collides with a shape, they lose a life.
     * If lives reach 0, the game ends and a score popup is shown.
     */
    private void updateGame() {
        if (random.nextInt(12) == 0) { // 1 in 12 chance per tick of spawning a shape
            FallingShape shape = null;

            int xPos = random.nextInt(Math.max(1, getWidth() - 30)); // Clamp spawn to panel width
            int shapeType = random.nextInt(3); // Randomly select shape type
            if (shapeType == 0) { 
                shape = new RectangleShape(xPos, 0); 
            } else if (shapeType == 1) {
                shape = new CircleShape(xPos, 0); 
            } else if (shapeType == 2) {
                shape = new TriangleShape(xPos, 0); 
            }

            if (shape != null) {
                fallingShapes.add(shape); // Add shape to ArrayList
            }
        }

        for (int i = 0; i < fallingShapes.size(); i++) { 
            FallingShape shape = fallingShapes.get(i); 
            shape.y += shapeSpeed; // Shape falls down by shapeSpeed

            if (shape.y > getHeight()) { // If it passes the bottom of the screen
                fallingShapes.remove(i); 
                score++; // Increase score
                if (score % 10 == 0 && shapeSpeed < 11) { 
                    shapeSpeed++; // Speed up after multiples of 10
                }
            } else if (shape.intersects(player)) { // Collision check
                fallingShapes.remove(i); 
                lives--; 
                if (lives <= 0) { // If no lives left
                    gameRunning = false; 
                    timer.stop(); 
                    JOptionPane.showMessageDialog(this, "Game Over! Final Score: " + score); 
                    start.setEnabled(true); 
                }
            }
        }
        updateLabels(); // Update HUD (labels and progress bar)
    }

    /*
     * This method simply updates the score and lives labels and also updates the progress bar 
     */
    private void updateLabels() {
        scoreLabel.setText("Score: " + score); 
        livesLabel.setText("Lives: " + lives); 
        lifeBar.setValue(lives); 
    }

    /*
     * Paints the player and the shapes onto the panel
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        player.draw(g); // Draw player circle

        // Draw each falling shape
        for (FallingShape fs : fallingShapes) {
            fs.draw(g);
        }
    }

    // Class for the blue circle that the player controls
    public class PlayerCircle {
        private int x;
        private int y;

        // Constructor for PlayerCircle
        public PlayerCircle(int x, int y) {
            this.x = x;
            this.y = y;
        }

        // Getter for x
        public int getX() {
            return x; 
        }

        // Getter for y
        public int getY() {
            return y; 
        }

        // Setter for x
        public void setX(int x) {
            this.x = x; 
        }

        // Setter for y
        public void setY(int y) {
            this.y = y;
        }

        // Draw method for drawing the user controlled circle
        public void draw(Graphics g) {
            g.setColor(Color.BLUE); // Set color to blue for player
            g.fillOval(x, y, 30, 30); // Draw player as a blue circle
        }
    }

    // Abstract class for shapes
    public abstract class FallingShape {
        protected int x; 
        protected int y;

        // Constructor for FallingShape
        public FallingShape(int x, int y) {
            this.x = x;
            this.y = y;
        }

        // Getter for x
        public int getX() {
            return x;
        }

        // Getter for y
        public int getY() {
            return y;
        }

        // Abstract method for drawing shapes
        public abstract void draw(Graphics g);

        // Method to detect a collision between the player and a shape
        public boolean intersects(PlayerCircle player) {
            return new Rectangle(x, y, 30, 30).intersects(new Rectangle(player.getX(), player.getY(), 30, 30));
        }
    }

    // Rectangle shape class
    public class RectangleShape extends FallingShape {
        // Constructor of RectangleShape
        public RectangleShape(int x, int y) {
            super(x, y);
        }

        // Implementing draw method
        @Override
        public void draw(Graphics g) {
            g.setColor(Color.RED);
            g.fillRect(getX(), getY(), 30, 30);
        }
    }

    // Circle shape class
    public class CircleShape extends FallingShape {
        // Constructor of CircleShape
        public CircleShape(int x, int y) {
            super(x, y);
        }

        // Implementing draw method
        @Override
        public void draw(Graphics g) {
            g.setColor(Color.GREEN);
            g.fillOval(getX(), getY(), 30, 30);
        }
    }

    // Triangle shape class
    public class TriangleShape extends FallingShape {
        // Constructor of TriangleShape
        public TriangleShape(int x, int y) {
            super(x, y);
        }

        // Implementing draw method
        @Override
        public void draw(Graphics g) {
            g.setColor(Color.YELLOW);
            int[] xPoints = {getX(), getX() + 15, getX() + 30}; // x coordinates
            int[] yPoints = {getY() + 30, getY(), getY() + 30}; // y coordinates
            g.fillPolygon(xPoints, yPoints, 3);
        }
    }

    // Implementing KeyListener for player movement
    public class KeyTracker implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {
            if (!gameRunning) return; // If game is not running, ignore key presses

            int code = e.getKeyCode(); // Get pressed key code

            // Check if left arrow key or 'A' key is pressed
            if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
                player.setX(player.getX() - 20); 
            // Check if right arrow key or 'D' key is pressed
            } else if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
                player.setX(player.getX() + 20); 
            }

            // Clamp player inside screen width
            if (player.getX() < 0) {
                player.setX(0);
            } else if (player.getX() > getWidth() - 30) {
                player.setX(getWidth() - 30);
            }

            repaint(); // Repaint to reflect new position
        }

        @Override
        public void keyReleased(KeyEvent e) {}
    }
}