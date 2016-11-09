package com.netease.backend.db.common.sql;

import java.sql.SQLException;
import java.util.StringTokenizer;

import com.netease.backend.db.common.schema.OnlineAlterTaskInfo;


public class DDLParserUtils {
	
	public static void match(SQLLexParser parser, String expected) throws SQLException {
		if (!expected.equalsIgnoreCase(parser.getCurrentToken()))
			throw new SQLException("Syntax error, expect '" + expected + "', but was '"
					+ parser.getCurrentToken() + "'.");
		parser.read();
	}

	public static void readAndMatch(SQLLexParser parser, String expected) throws SQLException {
		parser.read();
		match(parser, expected);
	}
	
	public static String readQuoteValue(SQLLexParser parser, String errMsg) throws SQLException {
		String result = null;
		try {
			result = readTokenOrValue(parser, false);
		} catch (SQLException e) {
			throw new SQLException("Syntax error, you should specify " + errMsg + ".");
		}
		if (";".equals(result) && parser.getCurrentToken().length() == 0)
			throw new SQLException("Syntax error, you should specify " + errMsg + ".");
		return result;
	}
	
	
	public static String readIdentifier(SQLLexParser parser, String errMsg) throws SQLException {
		if (parser.parseOver())
			throw new SQLException("Syntax error, you should specify " + errMsg + ".");
		String result = null;
		try {
			result = parser.readIdentifier();
		} catch (SQLException e) {
			throw new SQLException("Syntax error, you should specify " + errMsg + ".");
		}
		if (";".equals(result) && parser.getCurrentToken().length() == 0)
			throw new SQLException("Syntax error, you should specify " + errMsg + ".");
		return result;
	}
	
	
	public static int readInt(SQLLexParser parser, String errMsg) throws SQLException {
		if (parser.getCurrentTokenType() != SQLLexParser.VALUE)
			throw new SQLException("Syntax error, " + errMsg + " should be an integer, but was '"
					+ parser.getCurrentToken() + "'");
		if (!(parser.getCurrentValue() instanceof Integer))
			throw new SQLException("Syntax error, " + errMsg + " should be an integer, but was '"
					+ parser.getCurrentValue() + "'");
		Integer r = (Integer) parser.getCurrentValue();
		parser.read();
		return r;
	}
	
	
	public static String readString(SQLLexParser parser, String delimiter, String[] stopWords) throws SQLException {
		String s = "";
		while (true) {
			String t = readTokenOrValue(parser, true);
			if (s.length() == 0)
				s = t;
			else
				s += delimiter + t;

			int i;
			for (i = 0; i < stopWords.length; i++)
				if (stopWords[i].equalsIgnoreCase(parser.getCurrentToken()))
					break;
			if (i < stopWords.length)
				break;
		}
		return s;
	}
	
	
	public static String readTokenOrValue(SQLLexParser parser, boolean quoteString) throws SQLException {
		String value = getTokenOrValue(parser, quoteString);
		parser.read();
		return value;
	}

	
	public static String getTokenOrValue(SQLLexParser parser, boolean quoteString) {
		String value;
		if (parser.getCurrentTokenType() == SQLLexParser.VALUE) {
			if (quoteString && parser.getCurrentValue() instanceof String)
				value = "'" + parser.getCurrentValue().toString() + "'";
			else
				value = parser.getCurrentValue().toString();
		} else
			value = parser.getCurrentToken();
		return value;
	}
	
	
	public static boolean readBoolean(SQLLexParser parser) throws SQLException {
		String token = parser.getCurrentToken();
		boolean result;
		if (token.equalsIgnoreCase("TRUE"))
			result = true;
		else if(token.equalsIgnoreCase("FALSE"))
			result = false;
		else throw new SQLException("Syntax err, you should specify true|false");
		
		parser.read();
		return result;
	}
	
	public static String getNextToken(StringTokenizer tokenizer) throws SQLException {
		boolean isAnnotation = false;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (!isAnnotation) {
				if (token.startsWith(""))
					continue;
				if (token.startsWith("*/")) {
					isAnnotation = true;
					continue;
				} else
					return token;
			} else if (token.endsWith("*/")) {
				isAnnotation = false;
				continue;
			}
		}
		if (isAnnotation)
			throw new SQLException("Syntax error,  not matched.");
		return null;
	}
	
	public static String getNextToken(StringTokenizer tokenizer, String expected) throws SQLException {
		String str = getNextToken(tokenizer);
		if (null == str)
			throw new SQLException("Syntax error, expect '" + expected + "', but sql end.");
		return str;
	}

	public static void matchNextToken(StringTokenizer tokenizer, String expected) throws SQLException {
		String str = getNextToken(tokenizer);
		if (null == str)
			throw new SQLException("Syntax error, expect '" + expected + "', but sql end.");
		if (!str.equalsIgnoreCase(expected))
			throw new SQLException("Syntax error, expect '" + expected + "', but was '" + str + "'.");
	}
	
	
	
	public static OnlineAlterTaskInfo parseAlterTableOptions(SQLLexParser parser, 
			SAlterTable alterTable) throws SQLException {
		DDLParserUtils.readAndMatch(parser, "*");
		OnlineAlterTaskInfo oat = null;

		while (true) {
			if (parser.getCurrentToken().equalsIgnoreCase("DBN")) {
				DDLParserUtils.readAndMatch(parser, "FIRST");
				alterTable.setDbnFirst(true);
			} else if (parser.getCurrentToken().equalsIgnoreCase("WITH")) {
				parser.read();
				if (parser.getCurrentToken().equalsIgnoreCase("RENAME")) {
					parser.read();
					alterTable.setWithRename(true);
				} else if (parser.getCurrentToken().equalsIgnoreCase("LOCK")) {
					parser.read();
					alterTable.setWithLock(true);
				} else if (parser.getCurrentToken().equalsIgnoreCase("MYISAM")) {
					DDLParserUtils.readAndMatch(parser, "TEMP");
					alterTable.setWithMyISAMTemp(true);
				} else
					throw new SQLException("Invalid alter table option: " + parser.getCurrentToken());
			} else if (parser.getCurrentToken().equalsIgnoreCase("ONLINE")) {
				oat = new OnlineAlterTaskInfo();
				oat.convertFromSAT(alterTable);
				parser.read();
				if (parser.getCurrentToken().equalsIgnoreCase("TRUNKSIZE")) {
					parser.read();
					if (parser.getCurrentToken().equalsIgnoreCase("=")) {
						parser.read();
					} else {
						throw new SQLException(
								"Invalid alter table option: input string must be TRUNKSIZE=");
					}
					String trunksizeStr = parser.getCurrentToken();
					int trunksize = -1;
					try {
						trunksize = Integer.parseInt(trunksizeStr);
					} catch (NumberFormatException e) {
						throw new SQLException(
								"Invalid alter table option, trunksize must be integer, input:"
										+ trunksizeStr);
					}
					oat.setTrunksize(trunksize);
					parser.read();
				}

				if (parser.getCurrentToken().equalsIgnoreCase("SLEEPTIME")) {
					parser.read();
					if (parser.getCurrentToken().equalsIgnoreCase("=")) {
						parser.read();
					} else {
						throw new SQLException(
								"Invalid alter table option: input string must be SLEEPTIME=");
					}
					String sleepStr = parser.getCurrentToken();
					int sleeptime = -1;
					try {
						sleeptime = Integer.parseInt(sleepStr);
					} catch (NumberFormatException e) {
						throw new SQLException(
								"Invalid alter table option, sleeptime must be integer, input:"
										+ sleepStr);
					}
					oat.setSleeptime(sleeptime);
					parser.read();
				}

			}
			if (parser.getCurrentToken().equals(",")) {
				parser.read();
				continue;
			}
			if (parser.getCurrentToken().equals("*")) {
				DDLParserUtils.readAndMatch(parser, "/");
				break;
			} else
				throw new SQLException("Invalid alter table option: " + parser.getCurrentToken());
		}

		return oat;
	}
}
