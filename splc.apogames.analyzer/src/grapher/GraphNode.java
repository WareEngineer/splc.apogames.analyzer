package grapher;

import java.awt.Canvas;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GraphNode {
	private static final int initFontSize = 10;
	private int x;
	private int y;
	private int width;
	private int height;
	private double value = 0;
	private List<Object> contents = new ArrayList<Object>();
	
	public int getX() { return x; }
	public int getY() { return y; }
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	
	public String toString() {
		return "hello";
	}
	
	public GraphNode() {
		this.x = (int) (Math.random()*1250+50);
		this.y = (int) (Math.random()*900+50);
	}
	
	public void setValue(double value) {
		this.value = value;
	}
	
	public double getValue() {
		return this.value;
	}
	
	public void addText(String text) {
		this.addText(text, Font.PLAIN, initFontSize);
	}
	
	public void addText(String text, int size) {
		this.addText(text, Font.PLAIN, size);
	}
	
	public void addText(String text, int size, int style) {
		Txt txt = new Txt(text, "TimesRoman", style, size);
		this.contents.add(txt);
	}
	
	public void addLine() {
		Line line = new Line();
		this.contents.add(line);
	}
	
	public void draw(Graphics g) {
		width = 0;
		height = 0;
		
		for(Object content : contents) {
			if(content instanceof Txt) {
				Txt txt = (Txt) content;
				int rectWidth = (int) (txt.getWidth() * 1.2);
				if(width < rectWidth) {
					width = rectWidth;
				}
			}
		}
		
		for(Object content : contents) {
			if(content instanceof Txt) {
				Txt txt = (Txt) content;
				int textWidth = txt.getWidth();
				int textHeight = txt.getFont().getSize();
				
				int textX = x + (width-textWidth)/2;
				int textY = y + height + textHeight;

				height += (int) (txt.getFont().getSize()*1.4);
				
				g.setFont(txt.getFont());
				g.drawString(txt.getText(), textX, textY);
			} else if(content instanceof Line) {
				g.drawLine(x, y+height, x+width, y+height);
			}
		}
		
		g.drawRect(x, y, width, height);
	}
	
	public boolean isMouseOn(int mouseX, int mouseY) {
		if(x<mouseX && mouseX<x+width) {
			if (y<mouseY && mouseY<y+height) {
				return true;
			}
		}
		return false;
	}
	
	public void move(int dx, int dy) {
		this.x += dx;
		this.y += dy;
	}
	
	public void zoomIn() {
		for(Object content : contents) {
			if(content instanceof Txt) {
				((Txt)content).zoomIn();;
			}
		}
	}
	
	public void zoomOut() {
		for(Object content : contents) {
			if(content instanceof Txt) {
				((Txt)content).zoomOut();;
			}
		}
	}
}

class Txt {
	private static Canvas canvas = new Canvas();
	private static Map<String, Font> fontPool = new HashMap<String, Font>();
	private String txt;
	private Font font;
	
	public Font getFont() { return this.font; }
	public String getText() { return this.txt; }
	
	public Txt(String txt, String name, int style, int size) {
		String key = name + style + ":"+ size;
		if(fontPool.containsKey(key) == false) {
			fontPool.put(key, new Font(name, style, size));
		}
		this.font = fontPool.get(key);
		this.txt = txt;
	}
	
	public int getWidth() {
		return canvas.getFontMetrics(font).stringWidth(txt);
	}
	public int getHeight() {
		return font.getSize();
	}
	
	public void zoomIn() {
		font = new Font(font.getName(), font.getStyle(), font.getSize()+1);
	}
	
	public void zoomOut() {
		font = new Font(font.getName(), font.getStyle(), font.getSize()-1);
	}
}

class Line { }
