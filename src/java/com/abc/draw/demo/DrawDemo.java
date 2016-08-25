package com.abc.draw.demo;

import java.awt.event.*;

import javax.swing.*;

import com.abc.draw.*;
import com.abc.draw.geometry.*;

public class DrawDemo {
    private static Drawing createDrawing() {
        Drawing drawing = new Drawing();
        
        Point p1 = new Point(50.0, 5.0);
        Point p2 = new Point(300.0, 50.0);
        Line line = new Line(p1, p2);
        drawing.append(line);

        /* Comment out until the class Triangle has been written
        Triangle t1 = new Triangle(
            new Point(10.0, 20.0),
            new Point(10.0, 300.0), 
            new Point(50.0, 50.0));
        drawing.append(t1);

        Triangle t2 = new Triangle(
            new Point(100.0, 100.0), 
            new Point(380.0, 170.0), 
            new Point(150.0, 300.0));
        drawing.append(t2);
        */

        /* Comment out until the class Square has been written
        Square s = new Square(new Point(200.0, 200.0), 300.0);
        s.setWidth(200.0); // just to see that this method works
        drawing.append(s);
        */

        /* Comment out until the class Rectangle has been written
        Rectangle rect1 = new Rectangle(new Point(400.0, 50.0), 300.0, 1.0);
        rect1.setHeight(130.0); // just to see that this method works
        drawing.append(rect1);
        */

        return drawing;
    }

    public static void main(String[] args) {
        Drawing drawing = createDrawing();
        DrawingComponent drawingComponent = new DrawingComponent(drawing);

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame f = new JFrame("DrawDemo");
        f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        f.setContentPane(drawingComponent);
        f.setSize(800, 450);
        f.setVisible(true);
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}
