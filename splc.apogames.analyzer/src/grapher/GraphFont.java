package grapher;

import java.awt.Canvas;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

public class GraphFont {
	private static Canvas canvas = new Canvas();
	private static Map<String, Font> fontPool = new HashMap<String, Font>();
	private String txt;
	private Font font;
	
	public Font getFont() { return this.font; }
	public String getText() { return this.txt; }
	
	public GraphFont(String txt, String name, int style, int size) {
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
