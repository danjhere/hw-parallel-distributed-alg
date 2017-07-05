package com.abc.draw.geometry;

import com.abc.draw.Drawable;

import java.awt.*;

public class Square extends Rectangle implements Drawable {

    public Square(Point p1, double width) {
        super(p1, width, width);
    }

}