package com.netease.backend.db.common.validate;

import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;

import com.netease.backend.db.common.exceptions.DumpException;
import com.netease.cli.StringTable;


public interface SQLDumper {

	
	int dumpByQuery(String sql, String filePath) throws SQLException,
			IOException;

	
	int dumpByQuery(String sql, int offset, int limit, String filePath,
			String charset, boolean writeHeader, char delimiter, char quoter)
			throws SQLException, IOException;

	
	StringTable dumpByQuery(String sql, int offset, int limit)
			throws SQLException;

	
	int dumpByQuery(String sql, int offset, int limit, Writer writer,
			boolean writeHeader, char delimiter, char quoter)
			throws SQLException, IOException;

}
