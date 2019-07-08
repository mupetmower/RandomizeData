package code;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class DBConnector {
	
	private Connection con;
	private PreparedStatement updateBatch;
	private Map<String, PreparedStatement> updateBatches = new HashMap<String, PreparedStatement>();

	private final String DRIVER_MYSQL = "com.mysql.jdbc.Driver";
	private final String DRIVER_SQLSERVER = "net.sourceforge.jtds.jdbc.Driver";
	private final String CON_STR = "jdbc:mysql://localhost/h20_live_backup";
	private final String USER = "demo_cis";
	private final String PASS = "demo_cis";
	
	private String conString;
	private DBType dbType;
	private String dbHost;
	private String dbName;
	private String dbUser;
	private String dbPass;
	//private String idCol;
	
	private final String SELECT_CP_IDS = "select commonparty_id from commonparty;";
	private final String UPDATE_CP_NAME = "update commonparty set name = ? where commonparty_id = ?;";
	
	private final String GENERIC_UPDATE = "update %s set %s = ? where %s = ?;";
	private final String GENERIC_UPDATE_MULTICOL = "update %s set %s = ? where %s = ? and %s is not null;";
	private final String GENERIC_UPDATE_MULTICOL_TEXT = "update %s set %s = ? where %s = ? and %s not like '';";
	private final String SELECT_IDS_NONNULL = "select %s from %s where %s is not null and %s != '';";
	private final String SELECT_IDS_NONNULL_TEXT = "select %s from %s where %s is not null and %s not like '';";
	private final String SELECT_IDS_MULTICOL = "select %s from %s where ( %s );";
	private final String MULTICOL_NONNULL = "%s is not null";
	private final String MULTICOL_NONNULL_TEXT = "%s not like ''";
	private final String OR = " or ";
	
	private final String UPDATE = " update %s ";
	private final String SET = " set %s = ? ";
	private final String WHERE_EQUALS = " where %s = ? ";
	private final String WHERE_IN = " where %s in ";
	private final String AND_EQUALS = " and %s = ? ";
	private final String AND_IN = " and %s in ";
	private final String OR_EQUALS = " or %s = ? ";
	private final String OR_IN = " or %s in ";
	
	public final static int MAX_RECORD_COUNT = 9999999;
	public final static int MAX_VARCHAR_LENGTH = 150;
	
	public DBConnector() throws Exception {
		init();
	}
	
	public DBConnector(DBType dbType, String dbHost, String dbName, String dbUser, String dbPass) throws Exception {
		this.dbType = dbType;
		this.dbHost = dbHost;
		this.dbName = dbName;
		this.dbUser = dbUser;
		this.dbPass = dbPass;
		//this.idCol = idCol;
		init();
	}
	
	public DBConnector(String conString) throws Exception {
		this.conString = conString;
		init();
	}

	private void init() throws Exception {
		DriverManager.registerDriver((Driver) Class.forName(DRIVER_SQLSERVER).newInstance());
		if (dbType == null || isEmpty(dbHost) || isEmpty(dbName) || isEmpty(dbUser) || isEmpty(dbPass)) {
			con = DriverManager.getConnection(CON_STR, USER, PASS);
		} else {
			if (isEmpty(conString)) {
				//jdbc:mysql://localhost/h20_live_backup
				StringBuilder sb = new StringBuilder("jdbc:");
				sb.append(dbType.toString());
				sb.append("://");
				sb.append(dbHost);
				sb.append("/");
				sb.append(dbName);
				conString = sb.toString();
			}
			con = DriverManager.getConnection(conString, dbUser, dbPass);
		}
		con.setAutoCommit(false);
		System.out.println("Connected to database");
		//con.prepareStatement("select 1 from commonparty;").executeQuery();
		//System.out.println("Selected record from commonparty. Connection is good.");
		//updateBatch = con.prepareStatement(GENERIC_UPDATE);
		//prepUpdateBatch();
	}
	
	
	public void addToUpdateBatch(String tableName, String colToUpdate, String newValue, String idCol, String idValue) throws SQLException {
		updateBatch.setString(1, newValue);
		updateBatch.setString(2, idValue);
		updateBatch.addBatch();
	}

	public void addToUpdateBatchForCol(String tableName, String colToUpdate, String newValue, String idCol, String idValue) throws SQLException {
		updateBatches.get(colToUpdate).setString(1, newValue);
		updateBatches.get(colToUpdate).setString(2, idValue);
		updateBatches.get(colToUpdate).addBatch();
	}
	
	public int[] executeUpdateBatch() throws SQLException {
		return updateBatch.executeBatch();
	}

	public Map<String, int[]> executeUpdateBatches() throws SQLException {
		Map<String, int[]> results = new HashMap<>();
		for (Map.Entry<String, PreparedStatement> item : updateBatches.entrySet()) {
			results.put(item.getKey(), item.getValue().executeBatch());
		}
		return results;
	}
	
	public List<Object> retrieveIds(String tableName, String idCol, String colToChange, boolean isTextType) throws SQLException {
		List<Object> ids = new ArrayList<Object>();
		String sql = SELECT_IDS_NONNULL;
		if (isTextType) {
			sql = SELECT_IDS_NONNULL_TEXT;
		}
		PreparedStatement stmt = con.prepareStatement(String.format(sql, idCol, tableName, colToChange, colToChange));
		System.out.println("RetrieveIds SQL: " + String.format(sql, idCol, tableName, colToChange, colToChange));
		
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			ids.add(rs.getObject(1));
		}
		rs.close();
		return ids;
	}

	public List<Object> retrieveIds(String tableName, String idCol, Map<String, String> colsToChange) throws SQLException {
		List<Object> ids = new ArrayList<Object>();
		String sql = SELECT_IDS_MULTICOL;

		StringBuilder sqlWhere = new StringBuilder();
		int i = 0;
		for (Map.Entry<String, String> item : colsToChange.entrySet()) {
			if (i != 0)
				sqlWhere.append(OR);


			String col = MULTICOL_NONNULL;
			if (item.getValue().trim().toLowerCase().equals("sentence"))
				col = MULTICOL_NONNULL_TEXT;

			sqlWhere.append(String.format(col, item.getKey()));
			i++;
		}


		PreparedStatement stmt = con.prepareStatement(String.format(sql, idCol, tableName, sqlWhere));
		System.out.println("RetrieveIds SQL: " + String.format(sql, idCol, tableName, sqlWhere));

		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			ids.add(rs.getObject(1));
		}
		rs.close();
		return ids;
	}
	
	public void prepUpdateBatch(String tableName, String idCol, String colToChange) throws Exception {
		if (dbName == null) throw new Exception("No id column.");
		updateBatch = con.prepareStatement(String.format(GENERIC_UPDATE, tableName, colToChange, idCol));
	}

	public void prepUpdateBatches(String tableName, String idCol, Map<String, String> colsToChange) throws Exception {
		if (dbName == null) throw new Exception("No id column.");
		for (Map.Entry<String, String> item : colsToChange.entrySet()) {
			String col = item.getKey();
			if (item.getValue().trim().toLowerCase().equals("sentence")) {
				updateBatches.put(col, con.prepareStatement(String.format(GENERIC_UPDATE_MULTICOL_TEXT, tableName, col, idCol, col)));
			} else {
				updateBatches.put(col, con.prepareStatement(String.format(GENERIC_UPDATE_MULTICOL, tableName, col, idCol, col)));
			}
		}
	}
	
	public void commitBatch() throws SQLException {
		con.commit();
	}

	public void close() throws SQLException {
		updateBatch.close();
		for (PreparedStatement batch : updateBatches.values())
			batch.close();
		con.close();
	}
	
	
	private boolean isEmpty(String text) {
		if (text == null)
			return true;
		text = text.trim();
		if (text.isEmpty() || text.length() <= 0 || text.equals(""))
			return true;
		return false;
	}
	
	public static DBType getDBTypeFromString(String type) {
		switch(type.trim().toLowerCase()) {
			case "mysql":
				return DBType.MYSQL;
			case "sqlserver":
				return DBType.SQLSERVER;
			default:
				return DBType.MYSQL;
		}
	}
	
	public enum DBType {
		MYSQL("mysql"), 
		SQLSERVER("jtds:sqlserver");
		private String name;
		private DBType(String name) {
			this.name = name;
		}
		@Override
		public String toString() {
			return name;
		}
	}
}


