package com.zzg.mybatis.generator.util;

import com.zzg.mybatis.generator.model.DatabaseConfig;
import com.zzg.mybatis.generator.model.DbType;
import com.zzg.mybatis.generator.model.UITableColumnVO;
import org.mybatis.generator.internal.util.ClassloaderUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * Created by Owen on 6/12/16.
 */
public class DbUtil {

    private static final Logger _LOG = LoggerFactory.getLogger(DbUtil.class);
    private static final int DB_CONNECTION_TIMEOUTS_SECONDS = 30;

    private static Map<DbType, Driver> drivers;

    /**
     * 保持数据库连接,避免每步操作都需要重新连接
     */
    public static Map<String,Connection> connectionMap = new HashMap<>();

	static {
		drivers = new HashMap<>();
		List<String> driverJars = ConfigHelper.getAllJDBCDriverJarPaths();
		ClassLoader classloader = ClassloaderUtility.getCustomClassloader(driverJars);
		DbType[] dbTypes = DbType.values();
		for (DbType dbType : dbTypes) {
			try {
				Class clazz = Class.forName(dbType.getDriverClass(), true, classloader);
				Driver driver = (Driver) clazz.newInstance();
				_LOG.info("load driver class: {}", driver);
				drivers.put(dbType, driver);
			} catch (Exception e) {
				_LOG.error("load driver error");
			}
		}
	}

    public static Connection getConnection(DatabaseConfig config) throws ClassNotFoundException, SQLException {
        String url = getConnectionUrlWithSchema(config);
	    Properties props = new Properties();
	    props.setProperty("user", config.getUsername());
	    props.setProperty("password", config.getPassword());
        props.setProperty("remarksReporting","true");

		DriverManager.setLoginTimeout(DB_CONNECTION_TIMEOUTS_SECONDS);
	    Connection connection = drivers.get(DbType.valueOf(config.getDbType())).connect(url, props);
        _LOG.info("getConnection, connection url: {}", connection);
        return connection;
    }

    public static List<String> getTableNames(DatabaseConfig config) throws Exception {
        Connection connection = null;
        if(!isEmpty(config.getName())){
            connection = connectionMap.get(config.getName());
        }
        if(null==connection){
            connection = getConnection(config);
            connectionMap.put(config.getName(),connection);
        }
//	    try {
		    List<String> tables = new ArrayList<>();
		    DatabaseMetaData md = connection.getMetaData();
		    ResultSet rs;
		    if (DbType.valueOf(config.getDbType()) == DbType.SQL_Server) {
			    String sql = "select name from sysobjects  where xtype='u' or xtype='v' ";
			    rs = connection.createStatement().executeQuery(sql);
			    while (rs.next()) {
				    tables.add(rs.getString("name"));
			    }
		    } else if (DbType.valueOf(config.getDbType()) == DbType.Oracle){
			    rs = md.getTables(null, config.getUsername().toUpperCase(), null, new String[] {"TABLE", "VIEW"});
		    } else if(DbType.valueOf(config.getDbType()).equals(DbType.DB2)){
		        rs = md.getTables(null,config.getSchema().split(":")[1],null,new String[] {"TABLE", "VIEW"});
            }else {
			    rs = md.getTables(null, config.getUsername().toUpperCase(), null, null);
		    }
		    while (rs.next()) {
			    tables.add(rs.getString(3));
		    }
		    return tables;
//	    } finally {
//	    	connection.close();//保持连接以便下次使用
//	    }
	}

    public static List<UITableColumnVO> getTableColumns(DatabaseConfig dbConfig, String tableName) throws Exception {
        Connection conn = null;
        if(!isEmpty(dbConfig.getName())){
            conn = connectionMap.get(dbConfig.getName());
        }
        if(null==conn){
            conn = getConnection(dbConfig);
            connectionMap.put(dbConfig.getName(),conn);
        }
//		try {
        DatabaseMetaData md = conn.getMetaData();

        String schemaName = null;
        if(dbConfig.getDbType().equals("DB2"))schemaName = dbConfig.getSchema().split(":")[1];

        ResultSet rs = md.getColumns(null, schemaName, tableName, null);
        ResultSet rs2 = md.getPrimaryKeys(null,schemaName,tableName);


        Set<String> primaryColSet = new HashSet<>();
        while (rs2.next()){
            primaryColSet.add(rs2.getString("COLUMN_NAME"));
        }

        List<UITableColumnVO> columns = new ArrayList<>();
        while (rs.next()) {
            UITableColumnVO columnVO = new UITableColumnVO();
            String columnName = rs.getString("COLUMN_NAME");
            columnVO.setColumnName(columnName);
            if(primaryColSet.contains(columnName)){
                columnVO.setColumnIsPrimaryKey("Y");
            }else{
                columnVO.setColumnIsPrimaryKey("N");
            }
            columnVO.setJdbcType(rs.getString("TYPE_NAME"));
            columnVO.setColumnRemark(rs.getString("REMARKS"));
            columns.add(columnVO);
        }
        return columns;
//		} finally {
//			conn.close();
//		}
	}

	public static List<UITableColumnVO> getSequence(DatabaseConfig dbConfig) throws Exception{
	    if(dbConfig.getDbType().equals(DbType.MySQL.name()))return new ArrayList<>();

        Connection conn = null;
        if(!isEmpty(dbConfig.getName())){
            conn = connectionMap.get(dbConfig.getName());
        }
        if(null==conn){
            conn = getConnection(dbConfig);
            connectionMap.put(dbConfig.getName(),conn);
        }

        String sql="";
        if(dbConfig.getDbType().equals(DbType.Oracle.name()))sql="select sequence_name from user_sequences";
        if(dbConfig.getDbType().equals(DbType.DB2.name()))sql="SELECT * FROM SYSCAT.SEQUENCES WHERE SEQSCHEMA='"+dbConfig.getSchema().split(":")[1]+"'";

        ResultSet rs = conn.createStatement().executeQuery(sql);
        List<UITableColumnVO> columns = new ArrayList<>();
        while (rs.next()){
            UITableColumnVO columnVO = new UITableColumnVO();
            if(dbConfig.getDbType().equals(DbType.Oracle.name()))columnVO.setColumnName(rs.getString("SEQUENCE_NAME"));
            if(dbConfig.getDbType().equals(DbType.DB2.name()))columnVO.setColumnName(rs.getString("SEQNAME"));
            columns.add(columnVO);
        }
        return columns;
    }

    public static String getConnectionUrlWithSchema(DatabaseConfig dbConfig) throws ClassNotFoundException {
		DbType dbType = DbType.valueOf(dbConfig.getDbType());
		String schemaName = dbConfig.getSchema();
		if(dbType.name().equals("DB2")){
            schemaName = schemaName.split(":")[0];
        }
		String connectionUrl = String.format(dbType.getConnectionUrlPattern(), dbConfig.getHost(), dbConfig.getPort(), schemaName, dbConfig.getEncoding());
        _LOG.info("getConnectionUrlWithSchema, connection url: {}", connectionUrl);
        return connectionUrl;
    }
    public static String getDb2SchemaName(DatabaseConfig dbConfig)throws Exception{
        String schemaName = dbConfig.getSchema();
        schemaName = schemaName.split(":")[1];
        return null != schemaName && !"".equals(schemaName)?schemaName:"";
    }

    public static boolean isEmpty(Object o){
	    if(null==o)return true;
	    if(o instanceof String){
	        return ((String) o).length()==0;
        }
        return false;
    }
}
