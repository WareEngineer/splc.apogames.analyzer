package parser;

interface TOKEN_TYPE {
	int EOF = -1;
	int KEYWORD = 0;
	int CONST = 1;
	int IDENTIFIER = 2;
	int SYMBOL = 3;
}

public class Token {
	private final int type;
	private final String id;
	
	public Token(int type, String id) {
		this.type = type;
		this.id = id;
	}
	
	public int getType() {
		return this.type;
	}
	
	public String getId() {
		return this.id;
	}
	
	public boolean isKeyword() {
		if (this.type == TOKEN_TYPE.KEYWORD) {
			return true;
		}
		return false;
	}
	
	public boolean isIdentifier() {
		if (this.type == TOKEN_TYPE.IDENTIFIER) {
			return true;
		}
		return false;
	}
	
	public String toString() {
		String str = "";
		switch (this.type) {
		case TOKEN_TYPE.KEYWORD :
			str = String.format("[%10s : %-30s]", "KEYWORD", this.id);
			break;
		case TOKEN_TYPE.CONST :
			str = String.format("[%10s : %-30s]", "CONST", this.id);
			break;
		case TOKEN_TYPE.IDENTIFIER :
			str = String.format("[%10s : %-30s]", "IDENTIFIER", this.id);
			break;
		case TOKEN_TYPE.SYMBOL :
			str = String.format("[%10s : %-30s]", "SYMBOL", this.id);
			break;
		default :
			str = String.format("[%10s : %-30s]", "ERROR", this.id);
		}
		
		return str;
	}
}
