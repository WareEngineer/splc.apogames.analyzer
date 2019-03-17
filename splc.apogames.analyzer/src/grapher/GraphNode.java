package grapher;

import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GraphNode {
	private static final int TOP = 1;
	private static final int MIDDLE = 2;
	private static final int BOTTOM = 3;
	private static final int initFontSize = 10;
	private static int drawX = 50;
	private static int drawY = 50;
	private int fontSize = initFontSize;
	private int x;
	private int y;
	private int width;
	private int height;
	private Map<Integer, List<Text>> texts = new HashMap<Integer, List<Text>>();
	
	public int getX() { return x; }
	public int getY() { return y; }
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	
	public GraphNode(String text, double tcci, String stereotype) {
		texts.put(TOP, new ArrayList<Text>());
		texts.put(MIDDLE, new ArrayList<Text>());
		texts.put(BOTTOM, new ArrayList<Text>());
		
		if(stereotype != null) { 
			String st = "¡ì" + stereotype + "¡í";
			texts.get(TOP).add(new Text(st, "TimesRoman", Font.ITALIC, fontSize));
		}
		texts.get(TOP).add(new Text(text, "TimesRoman", Font.PLAIN, fontSize+2));
		texts.get(MIDDLE).add(new Text(String.format("%.2f", tcci), "TimesRoman", Font.BOLD, fontSize+1));
		
		drawX += 150 + fontSize*5;
		if(drawX > 1500) {
			drawX = 50;
			drawY += 80 + fontSize*5;
		}
		
		this.x = drawX;
		this.y = drawY;
//		this.x = (int) (Math.random()*1150+50);
//		this.y = (int) (Math.random()*900+50);
	}
	
	public GraphNode(String text, double tcci) {
		this(text, tcci, null);
	}
	
	public void draw(Graphics g) {
		width = 0;
		height = 0;
		
		for(List<Text> list : texts.values()) {
			for(Text text : list) {
				int rectWidth = (int) (text.getWidth() * 1.2);
				if(width < rectWidth) {
					width = rectWidth;
				}
			}
		}
		
		for(int level=TOP; level<BOTTOM; level++) {
			for(Text text : texts.get(level)) {
				int textWidth = text.getWidth();
				int textHeight = text.getFont().getSize();
				
				int textX = x + (width-textWidth)/2;
				int textY = y + height + textHeight;
				
				g.setFont(text.getFont());
				g.drawString(text.getText(), textX, textY);
				
				height += (int) (text.getFont().getSize()*1.4);
			}
			g.drawLine(x, y+height, x+width, y+height);
		}
		
		g.drawRect(x, y, width, height);
	}
	
	public void zoomIn() {
		fontSize++;
		this.x += (fontSize)-initFontSize;
		this.y += (fontSize)-initFontSize;
		for(List<Text> list : texts.values()) {
			for(Text text : list) {
				text.increaseFontSize();
			}
		}
	}
	
	public void zoomOut() {
		if(fontSize > 1) {
			this.x -= (fontSize)-initFontSize;
			this.y -= (fontSize)-initFontSize;
			for(List<Text> list : texts.values()) {
				for(Text text : list) {
					text.decreaseFontSize();
				}
			}
//			System.out.println(fontSize);
			fontSize--;
		}
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
}
