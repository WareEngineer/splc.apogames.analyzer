package grapher;

import java.awt.Canvas;
import java.awt.Font;

public class Text {
	private static Canvas canvas = new Canvas();
	private Font font;
	private String text;
	
	public Text(String text, String name, int style, int size) {
		font = new Font(name, style, size);
		this.text = text;
	}
	
	public Font getFont() {
		return font;
	}
	public String getText() {
		return text;
	}
	public int getWidth() {
		return canvas.getFontMetrics(font).stringWidth(text);
	}
	public int getHeight() {
		return font.getSize();
	}

	public void increaseFontSize() {
		this.font = new Font(font.getName(), font.getStyle(), font.getSize()+1);
	}
	public void decreaseFontSize() {
		this.font = new Font(font.getName(), font.getStyle(), font.getSize()-1);
	}
}