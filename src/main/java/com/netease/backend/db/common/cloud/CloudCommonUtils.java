package com.netease.backend.db.common.cloud;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CloudCommonUtils {

	
	public static String generateMySQLUrl(String host, int port, String database) {
		StringBuilder urlBuilder = new StringBuilder("jdbc:mysql:
		urlBuilder.append(host);
		urlBuilder.append(":");
		urlBuilder.append(port);
		urlBuilder.append("/");
		urlBuilder.append(database);

		
		urlBuilder.append("?characterEncoding=utf8");
		return urlBuilder.toString();
	}

	
	public static String generateErrorMessage(String errorCode, String message) {
		StringBuilder builder = new StringBuilder("#");
		builder.append(Definition.ExceptionType.ERROR.name());
		builder.append("#");
		builder.append(errorCode);
		builder.append("#");
		builder.append(message);
		return builder.toString();
	}

	
	public static String generateWarningMessage(String errorCode, String message) {
		StringBuilder builder = new StringBuilder("#");
		builder.append(Definition.ExceptionType.WARNING.name());
		builder.append("#");
		builder.append(errorCode);
		builder.append("#");
		builder.append(message);
		return builder.toString();
	}

	private static Pattern errorPattern = null;
	static {
		StringBuilder strBuilder = new StringBuilder("#(");
		strBuilder.append(Definition.ExceptionType.ERROR.name());
		strBuilder.append("|");
		strBuilder.append(Definition.ExceptionType.WARNING.name());
		strBuilder.append(")#(\\S+)#(.*)");
		
		errorPattern = Pattern.compile(strBuilder.toString(), Pattern.DOTALL);
	}

	
	public static CloudDBAException parseException(String message) {
		Matcher matcher = errorPattern.matcher(message);
		if (matcher.matches()) {
			return new CloudDBAException(
					Definition.ExceptionType.valueOf(matcher.group(1)),
					matcher.group(2), matcher.group(3));
		}
		return null;
	}

	
	public static Set<String> getDBUserDefaultQsIps() {
		return new TreeSet<String>(Arrays.asList("%"));
	}


	
	public static boolean checkIdentifier1(String identifier, int maxLength) {
		if (identifier == null || identifier.length() == 0
				|| identifier.length() > maxLength) {
			return false;
		}
		
		if (!Character.isLetter(identifier.charAt(0)))
			return false;
		for (int i = 1; i < identifier.length(); i++) {
			if (!Character.isLetterOrDigit(identifier.charAt(0)))
				return false;
		}
		return true;
	}

	public static void main(String[] args) {
		String errorMsg = generateErrorMessage("ErrorCode",
				"test message \n test");
		parseException(errorMsg);
		errorMsg = "#WARNING#CO_ce#msg\n";
		parseException(errorMsg);
	}

}
