package com.jhh.hdb.sqlparser;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.ql.Context;
import org.apache.hadoop.hive.ql.ErrorMsg;
import org.apache.hadoop.hive.ql.QueryProperties;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;


/**
 * BaseSemanticAnalyzer.
 *
 */
public abstract class BaseSemanticAnalyzer {
  protected static final Log STATIC_LOG = LogFactory.getLog(BaseSemanticAnalyzer.class.getName());
  


  protected Context ctx;
  protected HashMap<String, String> idToTableNameMap;
  protected QueryProperties queryProperties;

  /**
   * A set of FileSinkOperators being written to in an ACID compliant way.  We need to remember
   * them here because when we build them we don't yet know the transaction id.  We need to go
   * back and set it once we actually start running the query.
   */

  public static int HIVE_COLUMN_ORDER_ASC = 1;
  public static int HIVE_COLUMN_ORDER_DESC = 0;


/*  protected TableAccessInfo tableAccessInfo;
  protected ColumnAccessInfo columnAccessInfo;
  *//**
   * Columns accessed by updates
   *//*
  protected ColumnAccessInfo updateColumnAccessInfo;*/


  public boolean skipAuthorization() {
    return false;
  }



  public BaseSemanticAnalyzer() throws SemanticException {
    try {

      idToTableNameMap = new HashMap<String, String>();

    } catch (Exception e) {
      throw new SemanticException(e);
    }
  }


  public HashMap<String, String> getIdToTableNameMap() {
    return idToTableNameMap;
  }

  public  void analyzeInternal(ASTNode ast) throws SemanticException{};
  public void init(boolean clearPartsCache) {
    //no-op
  }

  public void initCtx(Context ctx) {
    this.ctx = ctx;
  }

  public void analyze(ASTNode ast, Context ctx) throws SemanticException {
    initCtx(ctx);
    init(true);
    analyzeInternal(ast);
  }

  public void validate() throws SemanticException {
    // Implementations may choose to override this
  }

 


  public static String stripIdentifierQuotes(String val) {
    if ((val.charAt(0) == '`' && val.charAt(val.length() - 1) == '`')) {
      val = val.substring(1, val.length() - 1);
    }
    return val;
  }

  public static String charSetString(String charSetName, String charSetString)
      throws SemanticException {
    try {
      // The character set name starts with a _, so strip that
      charSetName = charSetName.substring(1);
      if (charSetString.charAt(0) == '\'') {
        return new String(unescapeSQLString(charSetString).getBytes(),
            charSetName);
      } else // hex input is also supported
      {
        assert charSetString.charAt(0) == '0';
        assert charSetString.charAt(1) == 'x';
        charSetString = charSetString.substring(2);

        byte[] bArray = new byte[charSetString.length() / 2];
        int j = 0;
        for (int i = 0; i < charSetString.length(); i += 2) {
          int val = Character.digit(charSetString.charAt(i), 16) * 16
              + Character.digit(charSetString.charAt(i + 1), 16);
          if (val > 127) {
            val = val - 256;
          }
          bArray[j++] = (byte)val;
        }

        String res = new String(bArray, charSetName);
        return res;
      }
    } catch (UnsupportedEncodingException e) {
      throw new SemanticException(e);
    }
  }

  /**
   * Get dequoted name from a table/column node.
   * @param tableOrColumnNode the table or column node
   * @return for table node, db.tab or tab. for column node column.
   */
  public static String getUnescapedName(ASTNode tableOrColumnNode) {
    return getUnescapedName(tableOrColumnNode, null);
  }

  public static Map.Entry<String,String> getDbTableNamePair(ASTNode tableNameNode) {
    assert(tableNameNode.getToken().getType() == HiveParser.TOK_TABNAME);
    if (tableNameNode.getChildCount() == 2) {
      String dbName = unescapeIdentifier(tableNameNode.getChild(0).getText());
      String tableName = unescapeIdentifier(tableNameNode.getChild(1).getText());
      return Pair.of(dbName, tableName);
    } else {
      String tableName = unescapeIdentifier(tableNameNode.getChild(0).getText());
      return Pair.of(null,tableName);
    }
  }

  public static String getUnescapedName(ASTNode tableOrColumnNode, String currentDatabase) {
    int tokenType = tableOrColumnNode.getToken().getType();
    if (tokenType == HiveParser.TOK_TABNAME) {
      // table node
      Map.Entry<String,String> dbTablePair = getDbTableNamePair(tableOrColumnNode);
      String dbName = dbTablePair.getKey();
      String tableName = dbTablePair.getValue();
      if (dbName != null){
        return dbName + "." + tableName;
      }
      if (currentDatabase != null) {
        return currentDatabase + "." + tableName;
      }
      return tableName;
    } else if (tokenType == HiveParser.StringLiteral) {
      return unescapeSQLString(tableOrColumnNode.getText());
    }
    // column node
    return unescapeIdentifier(tableOrColumnNode.getText());
  }


  public static String getDotName(String[] qname) throws SemanticException {
    String genericName = StringUtils.join(qname, ".");
    if (qname.length != 2) {
      throw new SemanticException(ErrorMsg.INVALID_TABLE_NAME, genericName);
    }
    return genericName;
  }

  /**
   * Get the unqualified name from a table node.
   *
   * This method works for table names qualified with their schema (e.g., "db.table")
   * and table names without schema qualification. In both cases, it returns
   * the table name without the schema.
   *
   * @param node the table node
   * @return the table name without schema qualification
   *         (i.e., if name is "db.table" or "table", returns "table")
   */
  public static String getUnescapedUnqualifiedTableName(ASTNode node) {
    assert node.getChildCount() <= 2;

    if (node.getChildCount() == 2) {
      node = (ASTNode) node.getChild(1);
    }

    return getUnescapedName(node);
  }


  /**
   * Remove the encapsulating "`" pair from the identifier. We allow users to
   * use "`" to escape identifier for table names, column names and aliases, in
   * case that coincide with Hive language keywords.
   */
  public static String unescapeIdentifier(String val) {
    if (val == null) {
      return null;
    }
    if (val.charAt(0) == '`' && val.charAt(val.length() - 1) == '`') {
      val = val.substring(1, val.length() - 1);
    }
    return val;
  }

  /**
   * Converts parsed key/value properties pairs into a map.
   *
   * @param prop ASTNode parent of the key/value pairs
   *
   * @param mapProp property map which receives the mappings
   */
  public static void readProps(
    ASTNode prop, Map<String, String> mapProp) {

    for (int propChild = 0; propChild < prop.getChildCount(); propChild++) {
      String key = unescapeSQLString(prop.getChild(propChild).getChild(0)
          .getText());
      String value = null;
      if (prop.getChild(propChild).getChild(1) != null) {
        value = unescapeSQLString(prop.getChild(propChild).getChild(1).getText());
      }
      mapProp.put(key, value);
    }
  }

  private static final int[] multiplier = new int[] {1000, 100, 10, 1};

  @SuppressWarnings("nls")
  public static String unescapeSQLString(String b) {
    Character enclosure = null;

    // Some of the strings can be passed in as unicode. For example, the
    // delimiter can be passed in as \002 - So, we first check if the
    // string is a unicode number, else go back to the old behavior
    StringBuilder sb = new StringBuilder(b.length());
    for (int i = 0; i < b.length(); i++) {

      char currentChar = b.charAt(i);
      if (enclosure == null) {
        if (currentChar == '\'' || b.charAt(i) == '\"') {
          enclosure = currentChar;
        }
        // ignore all other chars outside the enclosure
        continue;
      }

      if (enclosure.equals(currentChar)) {
        enclosure = null;
        continue;
      }

      if (currentChar == '\\' && (i + 6 < b.length()) && b.charAt(i + 1) == 'u') {
        int code = 0;
        int base = i + 2;
        for (int j = 0; j < 4; j++) {
          int digit = Character.digit(b.charAt(j + base), 16);
          code += digit * multiplier[j];
        }
        sb.append((char)code);
        i += 5;
        continue;
      }

      if (currentChar == '\\' && (i + 4 < b.length())) {
        char i1 = b.charAt(i + 1);
        char i2 = b.charAt(i + 2);
        char i3 = b.charAt(i + 3);
        if ((i1 >= '0' && i1 <= '1') && (i2 >= '0' && i2 <= '7')
            && (i3 >= '0' && i3 <= '7')) {
          byte bVal = (byte) ((i3 - '0') + ((i2 - '0') * 8) + ((i1 - '0') * 8 * 8));
          byte[] bValArr = new byte[1];
          bValArr[0] = bVal;
          String tmp = new String(bValArr);
          sb.append(tmp);
          i += 3;
          continue;
        }
      }

      if (currentChar == '\\' && (i + 2 < b.length())) {
        char n = b.charAt(i + 1);
        switch (n) {
        case '0':
          sb.append("\0");
          break;
        case '\'':
          sb.append("'");
          break;
        case '"':
          sb.append("\"");
          break;
        case 'b':
          sb.append("\b");
          break;
        case 'n':
          sb.append("\n");
          break;
        case 'r':
          sb.append("\r");
          break;
        case 't':
          sb.append("\t");
          break;
        case 'Z':
          sb.append("\u001A");
          break;
        case '\\':
          sb.append("\\");
          break;
        // The following 2 lines are exactly what MySQL does TODO: why do we do this?
        case '%':
          sb.append("\\%");
          break;
        case '_':
          sb.append("\\_");
          break;
        default:
          sb.append(n);
        }
        i++;
      } else {
        sb.append(currentChar);
      }
    }
    return sb.toString();
  }

  /**
   * Escapes the string for AST; doesn't enclose it in quotes, however.
   */
  public static String escapeSQLString(String b) {
    // There's usually nothing to escape so we will be optimistic.
    String result = b;
    for (int i = 0; i < result.length(); ++i) {
      char currentChar = result.charAt(i);
      if (currentChar == '\\' && ((i + 1) < result.length())) {
        // TODO: do we need to handle the "this is what MySQL does" here?
        char nextChar = result.charAt(i + 1);
        if (nextChar == '%' || nextChar == '_') {
          ++i;
          continue;
        }
      }
      switch (currentChar) {
      case '\0': result = spliceString(result, i, "\\0"); ++i; break;
      case '\'': result = spliceString(result, i, "\\'"); ++i; break;
      case '\"': result = spliceString(result, i, "\\\""); ++i; break;
      case '\b': result = spliceString(result, i, "\\b"); ++i; break;
      case '\n': result = spliceString(result, i, "\\n"); ++i; break;
      case '\r': result = spliceString(result, i, "\\r"); ++i; break;
      case '\t': result = spliceString(result, i, "\\t"); ++i; break;
      case '\\': result = spliceString(result, i, "\\\\"); ++i; break;
      case '\u001A': result = spliceString(result, i, "\\Z"); ++i; break;
      default: {
        if (currentChar < ' ') {
          String hex = Integer.toHexString(currentChar);
          String unicode = "\\u";
          for (int j = 4; j > hex.length(); --j) {
            unicode += '0';
          }
          unicode += hex;
          result = spliceString(result, i, unicode);
          i += (unicode.length() - 1);
        }
        break; // if not a control character, do nothing
      }
      }
    }
    return result;
  }

  private static String spliceString(String str, int i, String replacement) {
    return spliceString(str, i, 1, replacement);
  }

  private static String spliceString(String str, int i, int length, String replacement) {
    return str.substring(0, i) + replacement + str.substring(i + length);
  }




  public static List<String> getColumnNames(ASTNode ast) {
    List<String> colList = new ArrayList<String>();
    int numCh = ast.getChildCount();
    for (int i = 0; i < numCh; i++) {
      ASTNode child = (ASTNode) ast.getChild(i);
      colList.add(unescapeIdentifier(child.getText()).toLowerCase());
    }
    return colList;
  }


}
