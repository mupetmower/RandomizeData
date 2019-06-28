package code;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBConnector {
	
	private Connection con;
	private PreparedStatement updateBatch;

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
	private final String SELECT_IDS_NONNULL = "select %s from %s where %s is not null and %s != '';";
	
	private final String UPDATE = " update %s ";
	private final String SET = " set %s = ? ";
	private final String WHERE_EQUALS = " where %s = ? ";
	private final String WHERE_IN = " where %s in ";
	private final String AND_EQUALS = " and %s = ? ";
	private final String AND_IN = " and %s in ";
	private final String OR_EQUALS = " or %s = ? ";
	private final String OR_IN = " or %s in ";
	
	public final static int MAX_RECORD_COUNT = 200000;
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
	
	public int[] executeUpdateBatch() throws SQLException {
		return updateBatch.executeBatch();
	}
	
	public List<Object> retrieveIds(String tableName, String idCol, String colToChange) throws SQLException {
		List<Object> ids = new ArrayList<Object>();
		PreparedStatement stmt = con.prepareStatement(String.format(SELECT_IDS_NONNULL, idCol, tableName, colToChange, colToChange));
		System.out.println("RetrieveIds SQL: " + String.format(SELECT_IDS_NONNULL, idCol, tableName, colToChange, colToChange));
		
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			ids.add(rs.getInt(1));
		}
		rs.close();
		return ids;
	}
	
	public void prepUpdateBatch(String tableName, String idCol, String colToChange) throws Exception {
		if (dbName == null) throw new Exception("No id column.");
		updateBatch = con.prepareStatement(String.format(GENERIC_UPDATE, tableName, colToChange, idCol));
	}
	
	public void commitBatch() throws SQLException {
		con.commit();
	}

	public void close() throws SQLException {
		updateBatch.close();
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


