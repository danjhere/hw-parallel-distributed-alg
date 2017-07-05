package com.abc.draw.geometry;

import com.abc.draw.Drawable;

import java.awt.*;

public class Triangle implements Drawable {
    private Point p1;
    private Point p2;
    private Point p3;

    public Triangle(Point p1, Point p2, Point p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    public Point getP1() {
        return p1;
    }

    public Point getP2() {
        return p2;
    }

    public Point getP3() {
        return p3;
    }

    public void draw(Graphics2D g2) {
        int x1 = (int) Math.round(p1.getX());
        int y1 = (int) Math.round(p1.getY());
        int x2 = (int) Math.round(p2.getX());
        int y2 = (int) Math.round(p2.getY());
        int x3 = (int) Math.round(p3.getX());
        int y3 = (int) Math.round(p3.getY());

        g2.drawLine(x1, y1, x2, y2);
        g2.drawLine(x2, y2, x3, y3);
        g2.drawLine(x3, y3, x1, y1);
    }
}