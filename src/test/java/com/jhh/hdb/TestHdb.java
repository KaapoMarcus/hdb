package com.jhh.hdb;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcUtils;
import com.jhh.hdb.meta.StringTemplateUtils;

@SuppressWarnings({ "rawtypes", "unchecked" })

public class TestHdb {

	static String sql = "";

	public static void test_expr() {
		sql = StringTemplateUtils.read_stat("test_expr_1");
		SQLExpr expr_1 = SQLUtils.toMySqlExpr(sql);
		System.out.println(expr_1);
	}

	public static void test_select() {
		sql = StringTemplateUtils.read_stat("test_select_1");

		List<SQLStatement> stmtList = null;

		stmtList = SQLUtils.parseStatements(sql, JdbcUtils.MYSQL);

		for (Iterator iterator = stmtList.iterator(); iterator.hasNext();) {
			SQLSelectStatement stmt = (SQLSelectStatement) iterator.next();

			StringBuilder strbuilder = new StringBuilder();
			SQLASTOutputVisitor visitor = SQLUtils.createFormatOutputVisitor(strbuilder, null, JdbcUtils.MYSQL);
			stmt.accept(visitor);
			System.out.println(strbuilder.toString());
			
			// StringBuffer strbuffer = new StringBuffer();
			// stmt.output(strbuffer);
			// System.out.println(strbuffer.toString());

		}

	}

	public static void test_enum() {

		Token[] all_token = Token.values();

		for (Token token : all_token) {

			System.out.println("name：" + token.name() + ",ordinal：" + token.ordinal());

		}
	}

	public static void getFileNameFromDir(String infile, List<String> fileList) {
		File baseFile = new File(infile);
		if (baseFile.isDirectory()) {
			File[] files = baseFile.listFiles();
			for (File tmpFile : files) {
				getFileNameFromDir(tmpFile.getAbsolutePath(), fileList);
			}
		} else {

			String path = baseFile.getPath();
			if (path.endsWith(".java")) {
				String name1 = path.substring(path.indexOf("src") + 4, path.length());
				String name2 = name1.replaceAll("\\\\", ".");
				String name3 = name2.substring(0, name2.lastIndexOf(".java"));
				fileList.add(name3);
			}
		}
	}

	private static void getClassNameFromJar(String jardir_name, List<String> fileList) throws Exception {
		File jardir = new File(jardir_name);

		if (jardir.isDirectory()) {
			File[] files = jardir.listFiles();
			for (File tmpFile : files) {
				getClassNameFromJar(tmpFile.getAbsolutePath(), fileList);
			}
		} else {

			JarFile jarFile = new JarFile(jardir);
			Enumeration<JarEntry> en = jarFile.entries(); // 枚举获得JAR文件内的实体,即相对路径
			while (en.hasMoreElements()) {
				String name1 = en.nextElement().getName();
				if (!name1.endsWith(".class")) {// 不是class文件
					continue;
				}
				String name2 = name1.substring(0, name1.lastIndexOf(".class"));
				String name3 = name2.replaceAll("/", ".");
				fileList.add(name3);
			}
			jarFile.close();
		}

	}

	public static void get_supper_class(Class clazz) {

		if (clazz != null) {
			Class parent_clazz = clazz.getSuperclass();
			Class[] interface_list = clazz.getInterfaces();

			if (parent_clazz != null) {
				System.err.println(parent_clazz.getName());
				get_supper_class(parent_clazz);
			}

			for (int i = 0; i < interface_list.length; i++) {
				System.err.println(interface_list[i].getName());
				get_supper_class(interface_list[i]);
			}
		}

	}

	public static boolean isChildClass(String childClass, Class parentClazz) {
		if (childClass == null) {
			return false;
		}
		try {
			Class childClazz = Class.forName(childClass);
			return parentClazz.isAssignableFrom(childClazz);
		} catch (Throwable e) {
			// e.printStackTrace();
			return false;
		}

	}

	public static void main(String[] args) throws Exception {

		test_select();
	}

	private static void test_class_parent_child() throws ClassNotFoundException, Exception {
		String class_name = "com.alibaba.druid.sql.ast.SQLObject";
		Class clazz = Class.forName(class_name);

		String[] srcdir_arr = { "D:/jhh/other_download/javaweb/WuLiu/src" };
		String[] jardir_arr = new String[] { "C:/Users/jhh/.m2/repository/com/alibaba/druid/1.0.18/druid-1.0.18.jar" };

		List<String> fileList = new ArrayList<String>();

		// for (int i = 0; i < srcdir_arr.length; i++) {
		// getFileNameFromDir(srcdir_arr[i], fileList);
		// }

		for (int i = 0; i < jardir_arr.length; i++) {
			getClassNameFromJar(jardir_arr[i], fileList);
		}

		// 获取父类
		get_supper_class(clazz);
		// 获取子类
		for (String name : fileList) {
			if (isChildClass(name, clazz)) {

				int lastindex = name.lastIndexOf(".");
				String pack = name.substring(0, lastindex).toLowerCase();
				String clsname = name.substring(lastindex + 1).toLowerCase();

				if (clsname.contains("sqlserver") || clsname.contains("postgresql") || clsname.contains("pg")
						|| clsname.contains("transactsql") || clsname.contains("oracle") || clsname.contains("odps")
						|| clsname.contains("db2")) {

				} else if (pack.contains("sqlserver") || pack.contains("postgresql") || pack.contains("pg")
						|| pack.contains("transactsql") || pack.contains("oracle") || pack.contains("odps")
						|| pack.contains("db2")) {

				} else {

					System.err.println(name);
				}
			}
		}
	}
}
