package com.jhh.hdb.proxyserver.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.netease.backend.db.common.utils.Pair;

/**
 * 定义查询服务器对非普通sql(select、update、delete、insert等)的处理行为
 * 
 *
 */
public class SQLSupport {

	// 运行sql语句
	public static final int BEHAVIOR_EXECUTE = 0;

	// 拒绝执行
	public static final int BEHAVIOR_REFUSE = 1;

	// 直接忽略
	public static final int BEHAVIOR_IGNORE = 2;

	// 查询并返回结果
	public static final int BEHAVIOR_QUERY = 3;

	// 随机获取一个底层数据库节点进行查询，并缓存查询结果以备以后使用
	public static final int BEHAVIOR_QUERY_DBN_CACHE = 4;

	// 插入数据并返回可能的唯一ID值
	public static final int BEHAVIOR_INSERT = 5;
	
	// 执行Isql命令
	public static final int BEHAVIOR_ISQL = 6;

	// 普通字符串类型
	public static final int KEYWORD_TYPE_STRING = 0;

	// 正则表达式类型
	public static final int KEYWORD_TYPE_REGEX = 1;

	private Map<String, Integer> stringKeywords;

	private List<Pair<RegexMatcher, Integer>> regexKeywords;

	public SQLSupport() {
		stringKeywords = new HashMap<String, Integer>();
		regexKeywords = new ArrayList<Pair<RegexMatcher, Integer>>();
	}

	/**
	 * 增加一个命令
	 * @param behavior
	 * @param cmd
	 */
	public void addCmd(int behavior, int type, String cmd) {
		if (behavior != BEHAVIOR_EXECUTE && behavior != BEHAVIOR_REFUSE
				&& behavior != BEHAVIOR_IGNORE && behavior != BEHAVIOR_QUERY
				&& behavior != BEHAVIOR_QUERY_DBN_CACHE	&& behavior != BEHAVIOR_INSERT
				&& behavior != BEHAVIOR_ISQL) {
			throw new IllegalArgumentException("unknown behavior:" + behavior);
		}

		if (type == KEYWORD_TYPE_STRING) {
			cmd = cmd.trim().toUpperCase();
			stringKeywords.put(cmd, behavior);
		} else if (type == KEYWORD_TYPE_REGEX) {
			regexKeywords.add(new Pair<RegexMatcher, Integer>(new RegexMatcher(cmd), behavior));
		} else {
			throw new IllegalArgumentException("unknown type:" + type);
		}
	}

	/**
	 * 获取该命令的行为
	 * @param cmd
	 * @return
	 */
	public int getBehavior(String cmd) {
		cmd = cmd.trim().toUpperCase();

		Integer behavior = stringKeywords.get(cmd);
		if (behavior != null) {
			return behavior;
		}
		for (Pair<RegexMatcher, Integer> regexPair : regexKeywords) {
			if (regexPair.getFirst().match(cmd)) {
				return regexPair.getSecond();
			}
		}

		throw new IllegalArgumentException("unknown cmd:" + cmd);
	}

	/**
	 * 判断是SQLSupport.KEYWORD_TYPE_STRING或SQLSupport.KEYWORD_TYPE_REGEX
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static int getKeyWordTypeFromStr(String str) throws Exception {
		if (str == null || (str = str.trim()).length() < 1) {
			throw new Exception("sql support type incorrect.");
		}
		if ("string".equalsIgnoreCase(str))
			return KEYWORD_TYPE_STRING;
		else if ("regex".equalsIgnoreCase(str))
			return KEYWORD_TYPE_REGEX;
		else
			throw new Exception("sql support type incorrect, "
					+ "should be 'string' or 'regex': " + str);
	}

	/**
	 * 获取相应的行为编号
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static int getBehaviorFromStr(String str) throws Exception {
		if (str == null || (str = str.trim()).length() < 1) {
			throw new Exception("behavior type incorrect.");
		}
		if ("execute".equalsIgnoreCase(str))
			return BEHAVIOR_EXECUTE;
		else if ("query".equalsIgnoreCase(str))
			return BEHAVIOR_QUERY;
		else if ("refuse".equalsIgnoreCase(str))
			return BEHAVIOR_REFUSE;
		else if ("ignore".equalsIgnoreCase(str))
			return BEHAVIOR_IGNORE;
		else if ("query_dbn_cache".equalsIgnoreCase(str))
			return BEHAVIOR_QUERY_DBN_CACHE;
		else if ("insert".equalsIgnoreCase(str))
			return BEHAVIOR_INSERT;
		else if ("isql".equalsIgnoreCase(str))
			return BEHAVIOR_ISQL;
		else
			throw new Exception("behavior type incorrect: " + str);
	}
}

/**
 * 正则匹配器，避免每次都需要创建Pattern
 * 
 *
 */
class RegexMatcher {
	private final Pattern pattern;
	
	RegexMatcher(String regex) {
		this.pattern = Pattern.compile(regex, Pattern.DOTALL);
	}
	
	boolean match(String input) {
		Matcher match = pattern.matcher(input);
		return match.matches();
	}
}
