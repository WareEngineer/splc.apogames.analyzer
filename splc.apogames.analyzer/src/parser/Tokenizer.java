
package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class Tokenizer {
	private FileReader fileReader;
	private List<Token> tokenList;
	private int lineNumber;
	
	public Tokenizer(String path) {
		setFileReader(path);
	}

	private void setFileReader(String path) {
		File file = new File(path);
		try {
			fileReader = new FileReader(file);
			lineNumber = 1;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public List<Token> getTokens() {
		tokenList = new ArrayList<Token>();
		StringBuffer buffer;
		
		try {
			int ch = fileReader.read();
			
			while( ch != -1 ) {
				buffer = new StringBuffer();
				ch = skipWhiteSpace(ch);
				
				buffer.append((char)ch);
				if ( isNumber(ch) ) { 					// 정수 실수 
					while( (ch=fileReader.read()) != -1 ) {
						if (isNumber(ch) || ch=='x' || ch=='X' || ch=='.' || ch=='f' || ch=='F' || ch=='l' || ch=='L') {
							buffer.append( (char) ch);
						} else {
							tokenList.add(new Token(TOKEN_TYPE.CONST, buffer.toString(), lineNumber));
							break;
						}
					}
				} else if ( ch=='\'' || ch=='\"') {		// 문자와 문자열 상수 식별 :: 이스케이프 문자 고려
					int endPoint = ch;
					while( (ch=fileReader.read()) != -1 ) {
						buffer.append( (char)ch );
						if ( ch == endPoint ) {
							int count = 0;  			// '\'이 홀수 개 있다면 탈출문자로 사용되었음을 의미한다.
							for (int i=buffer.length()-2; (i>0 && buffer.charAt(i)=='\\') ; i--) {
								count++;
							}
							if ( count%2 == 0 ) {
								ch = fileReader.read();
								tokenList.add(new Token(TOKEN_TYPE.CONST, buffer.toString(), lineNumber));
								break;
							}
						}
					}
				} else if ( isAlphabet(ch) || ch=='_' ) {		// 식별자(변수명, 키워드) 식별
					while( (ch=fileReader.read()) != -1 ) {
						if ( isAlphabet(ch) || isNumber(ch) || ch=='_' ) {
							buffer.append( (char) ch);
						} else {
							if ( isLiterals(buffer.toString()) ) {
								tokenList.add(new Token(TOKEN_TYPE.CONST, buffer.toString(), lineNumber));
							} else if ( isKeyword(buffer.toString()) ) {
								tokenList.add(new Token(TOKEN_TYPE.KEYWORD, buffer.toString(), lineNumber));
							} else {
								tokenList.add(new Token(TOKEN_TYPE.IDENTIFIER, buffer.toString(), lineNumber));
							}
							break;
						}
					}
				} else if ( isSymbol(ch) ) {					// 기호 식별 :: 주석, 괄호, 연산자
					while( true ) {
						ch = fileReader.read();
						if (ch == -1) {
							tokenList.add(new Token(TOKEN_TYPE.SYMBOL, buffer.toString(), lineNumber));
							break;
						} else {
							String op = buffer.toString() + (char)ch;
							if ( isComment(op) ) {
								ch = skipComment(op);
								break;
							} else if ( isOperator(op) ) {
								buffer.append( (char)ch );
							} else {
								tokenList.add(new Token(TOKEN_TYPE.SYMBOL, buffer.toString(), lineNumber));
								break;
							}
						}
					}
				} else {
					System.out.print("ERROR");
					ch=fileReader.read();
				}
				ch = skipWhiteSpace(ch);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return tokenList;
	}

	
	private int skipWhiteSpace(int ch) {
		int c = ch;
		if (isWhiteSpace(c)) {
			try {
				while( (c=fileReader.read()) != -1) {
					if(!isWhiteSpace(c)) break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return c;
	}

	private int skipComment(String op) {
		int c = ' ';
		try {
			if ("/*".equals(op)) {
				int prech = ' ';
				while( (c=fileReader.read()) != -1 ) {
					if (c=='\n') {
						lineNumber++;
					}
					if (prech=='*' && c=='/') {
						return fileReader.read();
//						break;
					}
					prech = c;
				}
			} else if ("//".equals(op)) {
				while( (c=fileReader.read()) != -1 ) {
					if (c=='\n') {
						lineNumber++;
						return fileReader.read();
//						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return c;
	}

	private boolean isKeyword(String str) {
		switch (str) {
		case "abstract": case "assert": 
		case "boolean": case "break": case "byte":
//		case "const":
		case "case": case "catch": case "char": case "class": case "continue": 
		case "default": case "do": case "double":
		case "else": case "enum": case "extends":
		case "final": case "finally": case "float": case "for":
//		case "goto":
		case "if": case "implements": case "import": case "instanceof": case "int": case "interface":
		case "long":
		case "native": case "new":
		case "package": case "private": case "protected": case "public":
		case "return":
		case "short": case "static": case "strictfp": case "super": case "switch": case "synchronized":
		case "this": case "throw": case "throws": case "transient": case "try":
		case "void": case "volatile":
		case "while":
			return true;
		}
		return false;
	}
	
	
	private boolean isLiterals(String str) {
		switch (str) {
		case "null" :
		case "true" :
		case "false" :
			return true;
		}
		return false;
	}

	private boolean isNumber(int ch) {
		if ('0'<=ch && ch<='9') {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isAlphabet(int ch) {
		if ( ('a'<=ch && ch<='z') || ('A'<=ch && ch<='Z')) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isWhiteSpace(int ch) {
		if( ch == '\n') {
			lineNumber++;
		}
		if( ch==' ' || ch=='\t' || ch=='\n' ) {
			return true;
		}
		return false;
	}
	
	private boolean isSymbol(int ch) {
		switch (ch) {
		case '+':
		case '-':
		case '*':
		case '/':
		case '%':
		case '&':
		case '|':
		case '^':
		case '>':
		case '<':
		case '=':
		case '!':
		case '?':
		case '~':
		case '.':
		case ',':
		case ':':
		case ';':
		case '@':
		case '(':
		case ')':
		case '{':
		case '}':
		case '[':
		case ']':
			return true;
		}
		
		return false;
	}
	
	private boolean isComment(String str) {
		switch (str) {
		case "//": case "/*":
			return true;
		}
		return false;
	}
	
	private boolean isOperator(String str) {
		switch (str) {
		case "+": case "+=": case "++":
		case "-": case "-=":
		case "*": case "*=":
		case "/": case "/=": case "//": case "/*":
		case "%": case "%=":
		case "&": case "&=": case "&&": 
		case "|": case "|=": case "||": 
		case "^": case "^=":
		case ">": case ">=": case ">>": case ">>>":
		case "<": case "<=": case "<<":
		case "=": case "==": 
		case "!": case "!=": 
		case "~": case ".":  case "?":  case ":":  case ";":  case "@":
		case "(": case ")":  case "[":  case "]":  case "{":  case "}":
			return true;
		}
		return false;
	}
}
