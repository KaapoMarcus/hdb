package com.netease.backend.db.common.utils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtils {

	
	private static final String[] regularExpKeys = {"(", ")", "[", "]", "{", "}", ".", "+", "?", "|", "*", "-", "^"};

	
    public static boolean equals(String a, String b) {
        if (a == null) {
            return b == null;
        }
        return a.equals(b);
    }

    public static boolean equalsIgnoreCase(String a, String b) {
        if (a == null) {
            return b == null;
        }
        return a.equalsIgnoreCase(b);
    }

    public static String[] convertToStringArray(String s, char splitChar) {
        if (s == null) {
            return null;
        }
        if (s.length() == 0) {
            return new String[0];
        }
        ArrayList<String> list = new ArrayList<String>();
        StringBuilder buff = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == splitChar) {
                list.add(buff.toString().trim());
                buff.setLength(0);
            } else {
                buff.append(c);
            }
        }
        list.add(buff.toString().trim());
        String[] array = new String[list.size()];
        list.toArray(array);
        return array;
    }

    public static ArrayList<String> convertToStringList(String s,
            String splitChar) {
        if (s == null) {
            return null;
        }
        if (s.length() == 0) {
            return new ArrayList<String>();
        }

        String[] array = s.split(splitChar);
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < array.length; i++)
            list.add(array[i]);

        return list;
    }

    public static ArrayList<Integer> convertToIntegerList(String s,
            String splitChar) {
        if (s == null) {
            return null;
        }
        if (s.length() == 0) {
            return new ArrayList<Integer>();
        }

        String[] array = s.split(splitChar);
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < array.length; i++)
            list.add(Integer.parseInt(array[i]));

        return list;
    }

    public static String enclose(String s) {
        if (s.startsWith("(")) {
            return s;
        } else {
            return "(" + s + ")";
        }
    }

    public static String unEnclose(String s) {
        if (s.startsWith("(") && s.endsWith(")")) {
            return s.substring(1, s.length() - 1);
        } else {
            return s;
        }
    }

    public static String convertArraytoString(String[] array, char splitChar) {
        if (array == null || array.length <= 0) {
            return "";
        }

        if (array.length == 1) {
            return array[0];
        } else {
            StringBuilder buff = new StringBuilder();
            for (int i = 0; i < array.length - 1; i++) {
                buff.append(array[i]);
                buff.append(splitChar);
            }
            buff.append(array[array.length - 1]);

            return buff.toString();
        }
    }

    public static String convertArraytoString(String[] array, String splitStr) {
        if (array == null || array.length <= 0) {
            return "";
        }

        if (array.length == 1) {
            return array[0];
        } else {
            StringBuilder buff = new StringBuilder();
            for (int i = 0; i < array.length - 1; i++) {
                buff.append(array[i]);
                buff.append(splitStr);
            }
            buff.append(array[array.length - 1]);

            return buff.toString();
        }
    }

    public static String convertArraytoString(int[] array, String splitStr) {
        if (array == null) {
            return "";
        }
        StringBuilder buff = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buff.append(splitStr);
            }
            buff.append(array[i]);
        }
        return buff.toString();
    }

    
    public static String multiplyString(String str, int times, String delimiter) {
        if (times <= 0) {
            return "";
        } else if (times == 1) {
            return str;
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < times - 1; i++) {
                sb.append(str);
                sb.append(delimiter);
            }
            sb.append(str);

            return sb.toString();
        }
    };

    public static String convertArraytoString(long[] array, String splitStr) {
        if (array == null) {
            return "";
        }
        StringBuilder buff = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buff.append(splitStr);
            }
            buff.append(array[i]);
        }
        return buff.toString();
    }

    public static String convertToString(String[] array, char splitChar) {
        return convertToString(array, String.valueOf(splitChar));
    }

    public static String convertToString(String[] array, String splitStr) {
        if (array == null || array.length <= 0) {
            return "";
        }

        if (array.length == 1) {
            return array[0];
        } else {
            StringBuilder buff = new StringBuilder();
            for (int i = 0; i < array.length - 1; i++) {
                buff.append(array[i]);
                buff.append(splitStr);
            }
            buff.append(array[array.length - 1]);

            return buff.toString();
        }
    }

    public static String convertToString(List<Integer> array, String splitStr) {
        if (array == null || array.size() <= 0) {
            return "";
        }

        if (array.size() == 1) {
            return Integer.toString(array.get(0));
        } else {
            StringBuilder buff = new StringBuilder();
            for (int i = 0; i < array.size() - 1; i++) {
                buff.append(array.get(i));
                buff.append(splitStr);
            }
            buff.append(array.get(array.size() - 1));

            return buff.toString();
        }
    }

    public static String convertToString(String[] array, char splitChar,
            char quoteChar) {
        return convertToString(array, String.valueOf(splitChar), String
                .valueOf(quoteChar));
    }

    public static String convertToString(String[] array, String splitStr,
            String quoteStr) {
        if (array == null || array.length <= 0) {
            return "";
        }

        if (array.length == 1) {
            return quoteStr + array[0] + quoteStr;
        } else {
            StringBuilder buff = new StringBuilder();
            for (int i = 0; i < array.length - 1; i++) {
                buff.append(quoteStr);
                buff.append(array[i]);
                buff.append(quoteStr);
                buff.append(splitStr);
            }
            buff.append(quoteStr);
            buff.append(array[array.length - 1]);
            buff.append(quoteStr);

            return buff.toString();
        }
    }

    public static String convertToString(Collection<?> list, String splitStr) {
        if (list == null || list.size() <= 0) {
            return "";
        }

        Iterator<?> it = list.iterator();
        if (list.size() == 1) {
            return it.next().toString();
        } else {
            StringBuilder buff = new StringBuilder();

            buff.append(it.next().toString());
            while (it.hasNext()) {
                buff.append(splitStr);
                buff.append(it.next().toString());
            }

            return buff.toString();
        }
    }

    public static String convertToString(Collection<?> list, String splitStr,
            String quoteStr) {
        if (list == null || list.size() <= 0) {
            return "";
        }

        Iterator<?> it = list.iterator();
        if (list.size() == 1) {
            return quoteStr + it.next().toString() + quoteStr;
        } else {
            StringBuilder buff = new StringBuilder();

            buff.append(quoteStr);
            buff.append(it.next().toString());
            buff.append(quoteStr);
            while (it.hasNext()) {
                buff.append(splitStr);
                buff.append(quoteStr);
                buff.append(it.next().toString());
                buff.append(quoteStr);
            }

            return buff.toString();
        }
    }

    public static String convertToString(Collection<?> list, char splitChar) {
        return convertToString(list, String.valueOf(splitChar));
    }

    public static String convertToString(Collection<?> list, char splitChar,
    		char quoteChar) {
    	return convertToString(list, String.valueOf(splitChar), String
    			.valueOf(quoteChar));
    }

    
    public static List<String> appendWithPrefix(Collection<?> list, String prefixStr) {
    	if (list == null || list.size() <= 0) {
    		return new ArrayList<String>();
    	}

    	List<String> result = new ArrayList<String>();
    	Iterator<?> it = list.iterator();
    	while (it.hasNext()) {
    		result.add(prefixStr + it.next().toString());
    	}
    	return result;
    }

    
    public static String convertToStringWithPrefix(Collection<?> list, String splitStr, String prefixStr) {
    	if (list == null || list.size() <= 0) {
    		return "";
    	}

    	Iterator<?> it = list.iterator();
    	if (list.size() == 1) {
    		return prefixStr + it.next().toString();
    	} else {
    		StringBuilder buff = new StringBuilder();

    		buff.append(prefixStr);
    		buff.append(it.next().toString());
    		while (it.hasNext()) {
    			buff.append(splitStr);
    			buff.append(prefixStr);
    			buff.append(it.next().toString());
    		}

    		return buff.toString();
    	}
    }

    
    public static String convertToString(Collection<?> list1, Collection<?> list2, String opStr, String splitStr) {
    	if (list1 == null || list1.size() <= 0 || list2 == null || list2.size() == 0) {
    		return "";
    	}
    	if (list1.size() != list2.size())
    		throw new IllegalArgumentException("Two lists must have same size.");

    	Iterator<?> it1 = list1.iterator();
    	Iterator<?> it2 = list2.iterator();
    	if (list1.size() == 1) {
    		return "(" + it1.next().toString() + opStr + it2.next().toString() + ")";
    	} else {
    		StringBuilder buff = new StringBuilder();

    		buff.append("(").append(it1.next().toString()).append(opStr).append(it2.next().toString()).append(")");
    		while (it1.hasNext() && it2.hasNext()) {
    			buff.append(splitStr);
    			buff.append("(").append(it1.next().toString()).append(opStr).append(it2.next().toString()).append(")");
    		}

    		return buff.toString();
    	}
    }

    
    public static int[] convertStrtoIntArray(String str, String splitter)
    throws NumberFormatException {
        List<String> strList = convertToStringList(str, splitter);
        if (strList == null)
            return null;
        if (strList.size() == 0)
            return null;
        int[] array = new int[strList.size()];
        int i = 0;
        for (String s : strList)
            array[i++] = Integer.valueOf(s).intValue();
        return array;
    }

    
    public static boolean addressContains(Collection<String> addList, String ip) {
        if (addList == null || addList.size() == 0)
            return false;
        if (addList.contains("%"))
            return true;
        for (String str : addList) {
            if (str.equals(ip))
                return true;
            if (str.contains("*"))
                continue;
            if (str.endsWith(".%")) {
                int offset = str.lastIndexOf(".%") + 1;
                if (offset < 0)
                    continue;
                String prefix = str.substring(0, offset).trim();
                if (ip.trim().startsWith(prefix))
                    return true;
            }
        }
        return addList.contains(ip);
    }

    
    public static String replaceGrantPassword(String sql, String password) {
        if (sql == null)
            return null;
        String sqlStr = sql.trim();
        if (!sqlStr.startsWith("grant"))
            return sqlStr;

        String lowCase = sqlStr.toLowerCase();

        int startIndex = lowCase.indexOf(" identified ");

        if (startIndex < 0) 
            return sqlStr;

        int midIndex = lowCase.indexOf("'", startIndex);
        int endIndex = lowCase.indexOf("'", midIndex + 1);

        String prefix = sqlStr.substring(0, startIndex
                + " identified ".length());
        String postfix = sqlStr.substring(endIndex);

        return prefix + "BY PASSWORD '" + password + postfix;
    }

    
    public static List<String> replaceGrantDb(List<String> sqlList,
            String target, String dbName) {
        List<String> result = new LinkedList<String>();
        if (sqlList == null)
            return null;
        for (int i = 0; i < sqlList.size(); i++)
            result.add(replaceDbName(sqlList.get(i), target, dbName));

        return result;
    }

    
    public static String replaceSqlHostByTag(String sql, String tag,
            String replacement) {
        String sqlStr = sql.trim();
        String lowCase = sqlStr.toLowerCase();
        int index = lowCase.indexOf(" " + tag.trim() + " ");
        if (index < 0)
            return sqlStr;
        
        boolean start = true;
        int startIndex = -1;
        int endIndex = -1;
        for (int i = index + tag.trim().length() + 2; i < lowCase.length(); i++) {
            if (lowCase.charAt(i) == ' ' && start)
                continue;
            else if (start && lowCase.charAt(i) != ' ') {
                start = false;
                startIndex = i;
            } else if (start == false && lowCase.charAt(i) == ' ') {
                endIndex = i - 1;
                break;
            }
        }

        if (start == true)
            return sqlStr;
        if (start == false && endIndex < 0)
            endIndex = lowCase.length() - 1;
        if (startIndex < 0)
            return sqlStr;
        String username = sqlStr.substring(startIndex, endIndex + 1);
        int atIndex = username.indexOf('@');
        if (atIndex < 0)
            username += (replacement.equals("") ? "" : "@") + replacement;
        else
            username = username.substring(0, atIndex)
                    + (replacement.equals("") ? "" : "@") + replacement;

        return sqlStr.substring(0, startIndex) + username
                + sqlStr.substring(endIndex + 1);
    }

    
    public static String replaceSqlHost(String sql, String replacement) {
        String sqlStr = sql.trim();
        String lowCase = sqlStr.toLowerCase();
        if (lowCase.startsWith("grant"))
            return replaceSqlHostByTag(sqlStr, "to", replacement);
        else if (lowCase.startsWith("revoke"))
            return replaceSqlHostByTag(sqlStr, "from", replacement);
        else
            return sqlStr;
    }

    
    public static String replaceDbName(String sql, String target,
            String replacement) {
        String sqlStr = sql.trim();
        if (!sqlStr.toLowerCase().startsWith("grant")
                && !sqlStr.toLowerCase().startsWith("revoke"))
            return sqlStr;

        String lowCase = sqlStr.toLowerCase();
        int index = lowCase.indexOf(" on ");
        if (index < 0)
            return sqlStr;
        
        boolean start = true;
        int startIndex = -1;
        int endIndex = -1;
        for (int i = index + 4; i < lowCase.length(); i++) {
            if (lowCase.charAt(i) == ' ' && start)
                continue;
            else if (start && lowCase.charAt(i) != ' ') {
                start = false;
                startIndex = i;
            } else if (start == false && lowCase.charAt(i) == ' ') {
                endIndex = i - 1;
                break;
            }
        }

        if (start == false && endIndex < 0)
            endIndex = lowCase.length();
        if (startIndex < 0)
            return sqlStr;
        String scope = sqlStr.substring(startIndex, endIndex + 1);
        int pointIndex = scope.indexOf('.');
        if (pointIndex < 0)
            scope = replacement + (replacement.equals("") ? "" : ".") + scope;
        else if (scope.substring(0, pointIndex).equals(target))
            scope = replacement + (replacement.equals("") ? "" : ".")
                    + scope.substring(pointIndex + 1);
        else
            return sqlStr;

        return sqlStr.substring(0, startIndex) + scope
                + sqlStr.substring(endIndex + 1);
    }

    
    public static String getDbNameFromGrant(String sql) {
        String sqlStr = sql.trim();
        if (!sqlStr.toLowerCase().startsWith("grant")
                && !sqlStr.toLowerCase().startsWith("revoke"))
            return null;

        String lowCase = sqlStr.toLowerCase();
        int index = lowCase.indexOf(" on ");
        if (index < 0)
            return null;
        
        boolean start = true;
        int startIndex = -1;
        int endIndex = -1;
        for (int i = index + 4; i < lowCase.length(); i++) {
            if (lowCase.charAt(i) == ' ' && start)
                continue;
            else if (start && lowCase.charAt(i) != ' ') {
                start = false;
                startIndex = i;
            } else if (start == false && lowCase.charAt(i) == ' ') {
                endIndex = i - 1;
                break;
            }
        }

        if (start == false && endIndex < 0)
            endIndex = lowCase.length();
        if (startIndex < 0)
            return "";
        String scope = sqlStr.substring(startIndex, endIndex + 1);
        int pointIndex = scope.indexOf('.');
        if (pointIndex < 0)
            return scope;
        else
            return scope.substring(0, pointIndex);
    }

    public static boolean startsWithIgnoreCase(String s, String prefix) {
        return s.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    
    public static Object convertNull(Object obj) {
        return null == obj ? "" : obj;
    }

    
    public static String fitString2Show(String str) {
        if (null == str) {
            return null;
        }
        int lineLength = 60;
        String line = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (0 != i && i % lineLength == 0) {
                sb.append(line);
            }
            sb.append(str.charAt(i));
        }
        return sb.toString();
    }

    
    public static String trimPathWithoutSlash(String path) {
        String result = "";
        if (path == null || (!path.endsWith("\\") && !path.endsWith("/")))
            return path;
        else
            result = path.substring(0, path.length() - 1);
        return trimPathWithoutSlash(result);
    }

    public static String deEscapeString(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '\\') {
                if (i == str.length() - 1)
                    throw new IllegalArgumentException(
                            "String ends in incomplete escape sequence");
                c = str.charAt(i + 1);
                switch (c) {
                case '\\':
                    sb.append('\\');
                    break;
                case '\'':
                    sb.append('\'');
                    break;
                case '"':
                    sb.append('"');
                    break;
                case 't':
                    sb.append('\t');
                    break;
                case 'n':
                    sb.append('\n');
                    break;
                case 'r':
                    sb.append('\r');
                    break;
                case '%':
                case '_':
                    sb.append('\\');
                    sb.append(c);
                    break;
                default:
                    sb.append(c);
                    break;
                }
                i++;
            } else if (c == '\'') {
                if (i == str.length() - 1)
                    throw new IllegalArgumentException(
                            "String ends in incomplete escape sequence");
                c = str.charAt(i + 1);
                if (c == '\'')
                    sb.append('\'');
                else
                    throw new IllegalArgumentException(
                            "Illegal escape sequence: '" + c);
                i++;
            } else
                sb.append(c);
        }
        return sb.toString();
    }

    
    public static String escapeString(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
            case '\\':
                sb.append("\\\\");
                continue;
            case '\'':
                sb.append("\\'");
                continue;
            case '"':
                sb.append("\"");
                continue;
            case '\t':
            case '\n':
            case '\r':
            default:
                sb.append(c);
                continue;
            }
        }

        return sb.toString();
    }

    
    public static boolean isPattern(String str, char[] patternSymbols,
            char escape) {
        StringBuilder sb = new StringBuilder(str);
        for (int i = 0; i < sb.length(); i++) {
            char ch = sb.charAt(i);
            if (ch == escape)
                i += 2;
            for (int j = 0; j < patternSymbols.length; j++)
                if (ch == patternSymbols[j])
                    return true;
        }
        return false;
    }

    
    public static int getClientId(byte[] gid) {
        try {
            if (gid == null)
                return 0;
            else {
                String sgid = new String(gid);
                int index = sgid.indexOf('_');
                if (index < 0)
                    return 0;
                else
                    return Integer.valueOf(sgid.substring(0, index)).intValue();
            }
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    
    public static boolean containIgnoreCase(Collection<String> col, String str) {
        if (col == null)
            return false;
        for (String item : col)
            if (item.equalsIgnoreCase(str))
                return true;
        return false;
    }
    
    
    public static int indexOfIgnoreCase(List<String> list, String str) {
    	if (list == null || list.size() == 0)
    		return -1;
    	if (str != null) {
    		for (int i = 0; i < list.size(); i++) {
    			if (list.get(i).equalsIgnoreCase(str))
    				return i;
    		}
    	} else {
    		for (int i = 0; i < list.size(); i++) {
    			if (list.get(i) == null)
    				return i;
    		}
    	}
    	return -1;
    }

    
    public static boolean containAllIgnoreCase(Collection<String> c1,
            Collection<String> c2) {
        if (c2 == null)
            return true;
        if (c1 == null)
            return false;

        for (String item : c2)
            if (!containIgnoreCase(c1, item))
                return false;
        return true;
    }

    
    public static String subString(String str, int lenLimit) {
        if (str == null || str.length() <= lenLimit || lenLimit < 0)
            return str;
        else
            return str.substring(lenLimit);
    }

    
    public static boolean hasSimilarAnnotation(String sql) {
        int start = sql.indexOf("", start);
        if (-1 == end)
            return false;
        return true;
    }

    
    public static String getMySQLDbNameByUrl(String dburl) {
        if (dburl == null || dburl.equals(""))
            return "";
        int posSlash = dburl.lastIndexOf('/');
        if (posSlash < 0)
        	return "";
        int posQuestion = dburl.indexOf('?', posSlash);
        if (posQuestion < 0)
			return dburl.substring(dburl.lastIndexOf('/') + 1, dburl.length());
        else
        	return dburl.substring(posSlash + 1, posQuestion);
    }

    
    public static String getHostNameByUrl(String dburl) {
        if (dburl == null || dburl.equals(""))
            return "";

        
        String host = dburl.substring(dburl.indexOf("
        if (host.contains(":"))
            host = host.substring(0, host.indexOf(':'));
        else
            host = host.substring(0, host.indexOf('/'));

        return host;
    }

    
    public static int getPortByUrl(String dburl) {
        if (dburl == null || dburl.equals(""))
            return 3306;

        
        int port;
        String portString = dburl.substring(dburl.indexOf("
                .length());
        if (!portString.contains(":"))
            port = 3306;
        else {
            portString = portString.substring(portString.indexOf(':') + 1,
                    portString.length());
            portString = portString.substring(0, portString.indexOf('/'));
            port = Integer.valueOf(portString);
        }

        return port;
    }

    
    public static String getIntervalDesc(long interval) {
        String timeStr = interval + "����";
        if (interval > 60000) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(1);
            timeStr = nf.format((double) interval / 60000) + "����";
        } else if (interval > 1000) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(2);
            timeStr = nf.format((double) interval / 1000) + "��";
        }
        return timeStr;
    }

    
    public static boolean isContainChinese(String str) {
        Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find())
            return true;
        else
            return false;
    }

    
    public static String getRegularExpression(String str) {
    	String str2 = str;
    	for (String k : regularExpKeys) {
    		str2 = str2.replaceAll("\\" + k, "\\\\" + k);
    	}
    	str2 = str2.replaceAll("(\\d)+", "(.)*");
    	return str2;
    }

    static public void main(String[] args) {
        System.out.println(replaceElememtIgnoreCase(Arrays.asList("a","bb"), "A", "cc"));

    }
    
    public static boolean startWithIgnoreCase(String str, String prefix) {
		if (str.length() < prefix.length())
			return false;
		String strPrefix = str.substring(0, prefix.length());
		return strPrefix.equalsIgnoreCase(prefix);
	}

    public static boolean isStartTransaction(String sql){
		if (startWithIgnoreCase(sql, "START ")) {
			String subSql = sql.substring("START ".length()).trim();
			if(subSql.equalsIgnoreCase("TRANSACTION")){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}

    
	public static List<String> replaceElememtIgnoreCase(List<String> list,
			String oldElement, String newElement) {
		if (list == null || list.size() == 0)
			return list;
		List<String> copy = new ArrayList<String>(list);
		for (int i = 0; i < copy.size(); i++) {
			if (copy.get(i).equalsIgnoreCase(oldElement)) {
				copy.set(i, newElement);
				return copy;
			}
		}
		return list;
	}
}
