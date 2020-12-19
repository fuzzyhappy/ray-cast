import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Server implements Runnable, Constants {
    private BufferedImage frontImage;
    private Graphics2D front;
    private BufferedImage backImage;
    private Graphics2D back;
    private Player player;

    private byte[][] map;
    private boolean w, a, s, d;

    public Server() {
        try (BufferedReader in = new BufferedReader(new FileReader("map.txt"))) {
            int rows = Integer.parseInt(in.readLine());
            map = new byte[rows][];
            for (int i = 0; i < rows; i++) {
                char[] row = in.readLine().toCharArray();
                map[i] = new byte[row.length];
                for (int j = 0; j < row.length; j++) {
                    map[i][j] = (byte)(row[j] - '0');
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        frontImage = new BufferedImage(map[0].length * side, map.length * side, BufferedImage.TYPE_INT_ARGB);
        front = frontImage.createGraphics();
        backImage = new BufferedImage(map[0].length * side, map.length * side, BufferedImage.TYPE_INT_ARGB);
        back = backImage.createGraphics();


        player = new Player(side + side / 2,  side /2, map);
    }

    private double validateAngle(double angle) {
        if (angle >= 2 * Math.PI) return angle % (2 * Math.PI);
        if (angle < 0) return 2 * Math.PI + angle;
        return angle;
    }

    @Override
    public void run() {
        JFrame frame = new JFrame();
        ImageIcon icon = new ImageIcon(frontImage);
        JLabel canvas = new JLabel(icon);
        frame.add(canvas);

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                switch (e.getKeyCode()) {
                    case (KeyEvent.VK_W) -> w = true;
                    case (KeyEvent.VK_A) -> a = true;
                    case (KeyEvent.VK_S) -> s = true;
                    case (KeyEvent.VK_D) -> d = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                switch (e.getKeyCode()) {
                    case (KeyEvent.VK_W) -> w = false;
                    case (KeyEvent.VK_A) -> a = false;
                    case (KeyEvent.VK_S) -> s = false;
                    case (KeyEvent.VK_D) -> d = false;
                }
            }
        });

        frame.addMouseMotionListener(new MouseMotionAdapter() {
            double oldX = 0;
            double curX = map[0].length * side / 2;

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                oldX = curX;
                curX = e.getX();

                player.angle -= (oldX - curX) / sens;
            }
        });

        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });

        Timer timer = new Timer(1, new ActionListener() {
            double oldTime = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                double fps = -1000.0 / (oldTime - (oldTime = System.currentTimeMillis()));

                System.out.printf("%.2f%n", fps);

                if (w) player.vy -= v;
                if (a) player.vx -= v;
                if (s) player.vy += v;
                if (d) player.vx += v;
                player.update();

                back.setColor(new Color(0, 0, 0));
                for (int i = 0; i < map.length; i++) {
                    for (int j = 0; j < map[0].length; j++) {
                        if (map[i][j] == 1) {
                            back.fill(new Rectangle2D.Double(j * side, i * side, side, side));
                        }
                    }
                }
                back.setColor(new Color(0, 100, 200));
                back.fill(new Ellipse2D.Double(player.x - 15.0 / 2, player.y - 15.0 / 2, 15, 15));

                double x = player.x;
                double y = player.y;
                double angle = player.angle;
                double rayAngle;

                for (double i = 0; i <= Math.PI / 2; i += Math.PI / 2048) {
                    rayAngle = validateAngle(angle + i);
                    Raytrace ray = new Raytrace(x, y, rayAngle, map);
                    double mag = ray.raytrace();
                    back.draw(new Line2D.Double(x, y,
                            x + mag * Math.cos(rayAngle),
                            y + mag * Math.sin(rayAngle)));
                }

                front.drawImage(backImage, 0, 0, null);
                back.setColor(new Color(255, 255, 255));
                back.fill(new Rectangle2D.Double(0, 0, map[0].length * side, map.length * side));
                frame.repaint();
            }
        });
        timer.setRepeats(true);
        timer.start();

        back.setColor(new Color(0, 0, 0));
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == '1') {
                    back.fill(new Rectangle2D.Double(j * side, i * side, side, side));
                }
            }
        }
        front.drawImage(backImage, 0, 0, null);

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                frame.dispose();
                timer.stop();
            }
        });
        frame.setLocationRelativeTo(null);

        frame.pack();
        frame.validate();
        frame.repaint();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Server());
    }
}
