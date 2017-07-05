package com.abc.draw;

import java.awt.*;
import java.util.ArrayList;


public class Drawing extends Object {

	private ArrayList<Drawable> drawables;
    
	public Drawing() {
        drawables = new ArrayList<Drawable>();
	}

	public void drawAll(Graphics2D g2) {
		for (Drawable drawable : drawables) {
			drawable.draw(g2);
		}
	}

	public void append(Drawable drawable) {
	    if (drawables != null) {
			drawables.add(drawable);
		}
	}
}
