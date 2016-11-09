
package com.netease.backend.db.common.sql;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;

import com.netease.backend.db.common.exceptions.DBSQLException;
import com.netease.backend.db.common.utils.Message;


public class SQLLexParser implements Cloneable {
    private static final int CHAR_END = -1, CHAR_VALUE = 2, CHAR_QUOTED = 3;

    private static final int CHAR_SPACE = 0, CHAR_NAME = 4, CHAR_SPECIAL_1 = 5,
            CHAR_SPECIAL_2 = 6;

    private static final int CHAR_STRING = 7, CHAR_DECIMAL = 8;
    
    private static final int CHAR_QUOTED_2 = 9;

    
    public static final int KEYWORD = 1, IDENTIFIER = 2, PARAMETER = 3,
            END = 4, VALUE = 5;

    public static final int EQUAL = 6, BIGGER_EQUAL = 7, BIGGER = 8;

    public static final int SMALLER = 9, SMALLER_EQUAL = 10, NOT_EQUAL = 11;

    public static final int MINUS = 17, PLUS = 18;

    public static final int STRINGCONCAT = 22;

    public static final int OPEN = 31, CLOSE = 32, NULL = 34, TRUE = 40, FALSE = 41;

    private int[] characterTypes; 

    private int currentTokenType;

    private String currentToken;

    private Object currentValue;

    private String sqlCommand; 

    private String originalSQL; 

    private char[] sqlCommandChars; 

    private int lastParseIndex; 

    private int parseIndex; 
    
    public static SQLLexParser getLexParser(String sql) throws SQLException {
        return new SQLLexParser(sql);
    }
    
	public Object clone() {
		try {
			SQLLexParser cloned = (SQLLexParser) super.clone();
			return cloned;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}    
    
    private SQLLexParser(String sql) throws SQLException {
        if (sql == null) {
            sql = "";
        }
        originalSQL = sql;
        sqlCommand = sql;
       	initialize();
    }
    
    private void initialize() throws SQLException {
        int len = originalSQL.length() + 1;
        char[] command = new char[len];
        int[] types = new int[len];
        len--;
        originalSQL.getChars(0, len, command, 0);
        command[len] = ' ';
        int startLoop = 0;

        for (int i = 0; i < len; i++) {
            char c = command[i];
            
            switch (c) {
            case '(':
            case ')':
            case '*':
            case '/':
            case ',':
            case ';':
            case '+':
            case '-':
            case '%':
            case '?':
            case '&':
            case '@':
            case '^':
            case '~':
                types[i] = CHAR_SPECIAL_1;
                break;
            case '!':
            case '<':
            case '>':
            case '|':
            case '=':
                types[i] = CHAR_SPECIAL_2;
                break;
            case '.':
                types[i] = CHAR_DECIMAL;
                break;
            case ' ':
            case '\t':
            case '\n':
            case '\r':
                types[i] = CHAR_SPACE;
                break;
            case '\'':
                types[i] = CHAR_STRING;
                startLoop = i;
                while (true) {
                    
                    
                    
                    
                    if (command[++i] == '\'') {
                        checkRunOver(i, len, startLoop);
                        if (command[i - 1] == '\\') {
                            
                            
                            boolean isRealSlash = true;
                            int j = i - 2;
                            while (j >= 0 && command[j] == '\\') {
                                isRealSlash = !isRealSlash;
                                j--;
                            }
                            if (isRealSlash) 
                                continue;
                        } 
                        if (command[i + 1] == '\'') {
                            ++i;
                            checkRunOver(i, len, startLoop);
                            continue;
                        } else 
                            break;
                    } else
                        checkRunOver(i, len, startLoop);
                    }
                break;
            case '\"':
                types[i] = CHAR_QUOTED;
                startLoop = i;
                while (command[++i] != '\"') {
                    checkRunOver(i, len, startLoop);
                }
                break;
            case '`':
                types[i] = CHAR_QUOTED_2;
                startLoop = i;
                while (command[++i] != '`') {
                    checkRunOver(i, len, startLoop);
                }
                break;
            case '_':
                types[i] = CHAR_NAME;
                break;
            default:
                if (c >= 'a' && c <= 'z') {
                    types[i] = CHAR_NAME;
                } else if (c >= 'A' && c <= 'Z') {
                    types[i] = CHAR_NAME;
                } else if (c >= '0' && c <= '9') {
                    types[i] = CHAR_VALUE;
                } else {
                    throw Message.getSQLException(Message.C_UNSUPPORTED_SQL_CHAR, i, c, originalSQL);
                }
            }
        }
        sqlCommandChars = command;
        types[len] = CHAR_END;
        characterTypes = types;
        
        parseIndex = 0;
    }
    
    private void checkRunOver(int i, int len, int startLoop)
            throws DBSQLException {
        if (i >= len) {
            parseIndex = startLoop;
            throw Message.getSQLException(Message.C_UNMATCH_QUOTE);
        }
    }

    public boolean readIf(String token) throws SQLException {
        if (token.equalsIgnoreCase(currentToken)) {
            read();
            return true;
        }
        return false;
    }

    public void read() throws SQLException {
        int[] types = characterTypes;
        lastParseIndex = parseIndex;
        int i = parseIndex;
        int type = types[i];
        while (type == CHAR_SPACE) {
            type = types[++i];
        }
        int start = i;
        char[] chars = sqlCommandChars;
        char c = chars[i++];
        currentToken = "";
        switch (type) {
        case CHAR_NAME:
            while (true) {
                type = types[i];
                if (type != CHAR_NAME && type != CHAR_VALUE) {
                    c = chars[i];
                    break;
                }
                i++;
            }
            currentToken = sqlCommand.substring(start, i);
            currentTokenType = getTokenType(currentToken);
            parseIndex = i;
            return;
        case CHAR_QUOTED: {
            String result = null;
            while (true) {
                for (int begin = i;; i++) {
                    if (chars[i] == '\"') {
                        if (result == null) {
                            result = sqlCommand.substring(begin, i);
                        } else {
                            result += sqlCommand.substring(begin - 1, i);
                        }
                        break;
                    }
                }
                if (chars[++i] != '\"') {
                    break;
                }
                i++;
            }
            
            currentToken = result;
            parseIndex = i;
            currentTokenType = IDENTIFIER;
            return;
        }
        case CHAR_QUOTED_2: {
        	int begin = i;
            while (chars[++i] != '`') {
            }
            currentToken = sqlCommand.substring(begin, i);
            parseIndex = i;
            currentTokenType = IDENTIFIER;
            return;
        }
        case CHAR_SPECIAL_2:
            if (types[i] == CHAR_SPECIAL_2) {
                i++;
            }
        
        case CHAR_SPECIAL_1:
            currentToken = sqlCommand.substring(start, i);
            currentTokenType = getSpecialType(currentToken);
            parseIndex = i;
            return;
        case CHAR_VALUE:
            if (c == '0' && chars[i] == 'X') {
                
                long number = 0;
                start += 2;
                i++;
                while (true) {
                    c = chars[i];
                    if ((c < '0' || c > '9') && (c < 'A' || c > 'F')) {
                        
                        currentTokenType = VALUE;

                        parseIndex = i;
                        currentToken = sqlCommand.substring(start, i); 
                        return;
                    }
                    number = (number << 4) + c
                            - (c >= 'A' ? ('A' - 0xa) : ('0'));
                    if (number > Integer.MAX_VALUE) {
                        i = readHexDecimal(start, i);
                        currentToken = sqlCommand.substring(start, i); 
                        return;
                    }
                    i++;
                }
            }
            long number = c - '0';
            while (true) {
                c = chars[i];
                if (c < '0' || c > '9') {
                    if (c == '.') {
                        i = readDecimal(start, i);
                        break;
                    }
                    if (c == 'E') {
                        i = readDecimal(start, i);
                        break;
                    }
                    currentValue = (int) number;
                    currentTokenType = VALUE;

                    parseIndex = i;
                    break;
                }
                number = number * 10 + (c - '0');
                if (number > Integer.MAX_VALUE) {
                    i = readDecimal(start, i);
                    break;
                }
                i++;
            }
            currentToken = sqlCommand.substring(start, i); 
            return;
        case CHAR_DECIMAL:
            if (types[i] != CHAR_VALUE) {
                currentTokenType = KEYWORD;
                currentToken = ".";
                parseIndex = i;
                return;
            }
            i = readDecimal(i - 1, i);
            return;
        case CHAR_STRING: {
            String result = null;
            
            
            for (int begin = i;; i++) {
                if (chars[i] == '\'') {
                    if (chars[i - 1] == '\\') {
                        
                        
                        boolean isRealSlash = true;
                        int j = i - 2;
                        while (j >= 0 && chars[j] == '\\') {
                            isRealSlash = !isRealSlash;
                            j--;
                        }
                        if (isRealSlash) 
                            continue;
                    }
                    if (chars[i + 1] == '\'') {
                        ++i;
                        continue;
                    } 
                    result = sqlCommand.substring(begin, i);
                    break;
                }
            }
            currentToken = "'";
            currentValue = result;
            parseIndex = i;
            currentTokenType = VALUE;
            return;
        }
        case CHAR_END:
            currentToken = "";
            currentTokenType = END;
            parseIndex = i;
            return;
        default:
            throw Message.getSQLException(Message.C_UNSUPPORTED_SQL_CHAR, i, chars[i], originalSQL);
        }
    }
    
    private int getTokenType(String s) throws SQLException {
        int len = s.length();
        if (len == 0) {
            throw Message.getSQLException(Message.C_EMPTY_TOKEN);
        }
        
        switch (s.charAt(0)) {
        case 'D':
        case 'd':
            if (s.equalsIgnoreCase("DISTINCT")) {
                return KEYWORD;
            }
        case 'E':
        case 'e':
            if (s.equalsIgnoreCase("EXCEPT")) {
                return KEYWORD;
            }
            return getKeywordOrIdentifier(s, "EXISTS", KEYWORD);
        case 'F':
        case 'f':
            if (s.equalsIgnoreCase("FROM")) {
                return KEYWORD;
            } else if (s.equals("FOR")) {
                return KEYWORD;
            }
            return getKeywordOrIdentifier(s, "FALSE", FALSE);
        case 'G':
        case 'g':
            return getKeywordOrIdentifier(s, "GROUP", KEYWORD);
        case 'H':
        case 'h':
            return getKeywordOrIdentifier(s, "HAVING", KEYWORD);
        case 'I':
        case 'i':
            if (s.equalsIgnoreCase("INNER")) {
                return KEYWORD;
            } else if (s.equalsIgnoreCase("INTERSECT")) {
                return KEYWORD;
            }
            return getKeywordOrIdentifier(s, "IS", KEYWORD);
        case 'L':
        case 'l':
            return getKeywordOrIdentifier(s, "LIKE", KEYWORD);
        case 'M':
        case 'm':
            if (s.equalsIgnoreCase("MINUS")) {
                return KEYWORD;
            }
            break;
        case 'N':
        case 'n':
            if (s.equalsIgnoreCase("NOT")) {
                return KEYWORD;
            }
            return getKeywordOrIdentifier(s, "NULL", NULL);
        case 'O':
        case 'o':
            if (s.equalsIgnoreCase("ON")) {
                return KEYWORD;
            }
            return getKeywordOrIdentifier(s, "ORDER", KEYWORD);
        case 'P':
        case 'p':
            return getKeywordOrIdentifier(s, "PRIMARY", KEYWORD);
        case 'T':
        case 't':       
            return getKeywordOrIdentifier(s, "TRUE", TRUE);
        case 'U':
        case 'u':
            return getKeywordOrIdentifier(s, "UNION", KEYWORD);
        case 'W':
        case 'w':
            return getKeywordOrIdentifier(s, "WHERE", KEYWORD);
        }
        return IDENTIFIER;
    }

    private int getKeywordOrIdentifier(String s1, String s2, int keywordType) {
        if (s1.equalsIgnoreCase(s2)) {
            return keywordType;
        }
        return IDENTIFIER;
    }
    
    private int getSpecialType(String s) throws SQLException {
        char c0 = s.charAt(0);
        if (s.length() == 1) {
            switch (c0) {
            case '?':
                return PARAMETER;
            case ';':
                return KEYWORD;
            case '+':
                return PLUS;
            case '-':
                return MINUS;
            case '*':
                return KEYWORD;
            case '/':
                return KEYWORD;
            case '(':
                return OPEN;
            case ')':
                return CLOSE;
            case '<':
                return SMALLER;
            case '>':
                return BIGGER;
            case '=':
                return EQUAL;
            case ',':
                return KEYWORD;
            case '@':
            	return KEYWORD;
            }
        } else if (s.length() == 2) {
            switch (c0) {
            case '>':
                if (s.equals(">=")) {
                    return BIGGER_EQUAL;
                }
                break;
            case '<':
                if (s.equals("<=")) {
                    return SMALLER_EQUAL;
                } else if (s.equals("<>")) {
                    return NOT_EQUAL;
                }
                break;
            case '!':
                if (s.equals("!=")) {
                    return NOT_EQUAL;
                }
                break;
            case '|':
                if (s.equals("||")) {
                    return STRINGCONCAT;
                }
            }
        }
        throw Message.getSQLException(Message.C_ILLEGAL_SPECIAL_CHAR, c0, originalSQL);
    }

    private int readHexDecimal(int start, int i) throws SQLException {
        char[] chars = sqlCommandChars;
        char c;
        do {
            c = chars[++i];
        } while ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F'));
        parseIndex = i;
        String sub = sqlCommand.substring(start, i);
        BigDecimal bd = new BigDecimal(new BigInteger(sub, 16));
        currentValue = bd;
        currentTokenType = VALUE;
        return i;
    }

    private int readDecimal(int start, int i) throws SQLException {
        char[] chars = sqlCommandChars;
        int[] types = characterTypes;
        
        int t;
        do {
            t = types[++i];
        } while (t == CHAR_DECIMAL || t == CHAR_VALUE);
        if (chars[i] == 'E') {
            i++;
            if (chars[i] == '+' || chars[i] == '-') {
                i++;
            }
            if (types[i] != CHAR_VALUE) {
                throw Message.getSQLException(Message.C_ILLEGAL_DECIMAL, i, originalSQL);
            }
            while (types[++i] == CHAR_VALUE) {
                
            }
        }
        parseIndex = i;
        String sub = sqlCommand.substring(start, i);
        BigDecimal bd;
        try {
            bd = new BigDecimal(sub);
        } catch (NumberFormatException e) {
            throw Message.getSQLException(Message.C_ILLEGAL_DECIMAL, start, originalSQL);
        }
        currentValue = bd;
        currentTokenType = VALUE;
        return i;
    }

    public void read(String expected) throws SQLException {
        if (!currentToken.equalsIgnoreCase(expected)) {
            throw Message.getSQLException(Message.C_SQL_PARSE_ERROR, currentToken, expected, originalSQL);
        }
        read();
    }

    public String readIdentifier() throws SQLException {
        if (currentTokenType != IDENTIFIER) {
            throw Message.getSQLException(Message.C_ILLEGAL_IDENTIFIER, currentToken, originalSQL);
        }
        String s = currentToken;
        read();
        return s;
    }
    
    public String getCurrentToken() {
        return currentToken;
    }
    
    public int getCurrentTokenType() {
        return currentTokenType;
    }
    
    public int getLastParseIndex() {
        return lastParseIndex;
    }
    
    public int getParseIndex() {
    	return parseIndex;
    }

    public Object getCurrentValue() {
        return currentValue;
    }
    
    public boolean parseOver() throws SQLException {
        if (currentToken.trim().length() == 0) {
            return true;
        } else if (currentToken.equals(";")) {
            while (currentToken.equals(";")) {
                read();
            }
            if (currentToken.trim().length() == 0) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
