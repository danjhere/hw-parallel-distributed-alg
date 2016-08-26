package com.abc.draw.geometry;

import com.abc.draw.Drawable;

import java.awt.*;

/**
 * Created by marymosman on 8/25/16.
 */
public class Rectangle implements Drawable {
    private Point p1;
    private Point p2;
    private Point p3;
    private Point p4;
    private double width;
    private double height;

    public Rectangle(Point p1, double width, double height) {
        this.p1 = p1;
        this.p2 = new Point(p1.getX(), p1.getY() + width);
        this.p3 = new Point(p1.getX() + height, p1.getY());
        this.p4 = new Point(p1.getX() + height, p1.getY() + width);
    }

    public double getWidth(){
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
        this.p2 = new Point(p2.getX(), p1.getY() + width);
        this.p4 = new Point(p4.getX(), p1.getY() + width);
    }

    public double getHeight(){
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
        this.p3 = new Point(p3.getX() + height, p1.getY());
        this.p4 = new Point(p4.getX() + height, p4.getY());
    }

    public Point getUpperLeft() {
        return p1;
    }

    public void draw(Graphics2D g2) {
        int x1 = (int) Math.round(p1.getX());
        int y1 = (int) Math.round(p1.getY());
        int x2 = (int) Math.round(p2.getX());
        int y2 = (int) Math.round(p2.getY());
        int x3 = (int) Math.round(p3.getX());
        int y3 = (int) Math.round(p3.getY());
        int x4 = (int) Math.round(p4.getX());
        int y4 = (int) Math.round(p4.getY());

        g2.drawLine(x1, y1, x2, y2);
        g2.drawLine(x2, y2, x4, y4);
        g2.drawLine(x4, y4, x3, y3);
        g2.drawLine(x3, y3, x1, y1);
    }
}