package test;

import controllers.Game_Controls;
import graphics.MotusFrame;

public class test {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MotusFrame().setVisible(true);
            }
        });
    }
}