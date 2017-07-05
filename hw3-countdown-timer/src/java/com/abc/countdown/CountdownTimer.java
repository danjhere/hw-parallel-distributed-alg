package com.abc.countdown;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class CountdownTimer extends JPanel {
    private MinuteSecondDisplay display;

    public CountdownTimer() {
        display = new MinuteSecondDisplay();

        JButton startB = new JButton("Start");
        startB.addActionListener(new ActionListener() {
            @SuppressWarnings("unused")
            @Override
            public void actionPerformed(ActionEvent e) {
                new CountdownWorker(10, display);
            }
        });

        setLayout(new FlowLayout());
        add(startB);
        add(display);
    }

    public static void main(String[] args) {
        JPanel p = new JPanel(new GridLayout(0, 1));
        p.add(new CountdownTimer());
        p.add(new CountdownTimer());
        p.add(new CountdownTimer());

        JFrame f = new JFrame("CountdownTimer Demo");
        f.setContentPane(p);
        f.pack();
        f.setVisible(true);
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}
