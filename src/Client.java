import javax.swing.*;
import java.awt.event.*;

public class Client implements Runnable {

    @Override
    public void run() {
        JFrame frame = new JFrame();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                frame.dispose();
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                super.windowLostFocus(e);
            }
        });

        frame.addKeyListener(new KeyboardInputListener());

        frame.addMouseListener(new MouseInputListener());

        Timer timer = new Timer(1000 / 60, new Updater());
    }

    class KeyboardInputListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
        }
    }

    class MouseInputListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            super.mouseWheelMoved(e);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            super.mouseDragged(e);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            super.mouseMoved(e);
        }
    }

    class Updater implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

    class CommunicatorThread extends Thread {
        public CommunicatorThread() {

        }

        @Override
        public void run() {

        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Client());
    }
}
