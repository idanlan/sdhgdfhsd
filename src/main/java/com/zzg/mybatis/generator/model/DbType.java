package com.zzg.mybatis.generator.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Owen on 6/14/16.
 */
public enum DbType {

    MySQL("com.mysql.jdbc.Driver",
            "jdbc:mysql://%s:%s/%s?useUnicode=true&useSSL=false&characterEncoding=%s",
            "mysql-connector-java-5.1.38.jar",
            "com.zzg.mybatis.generator.plugins.PaginationPluginMysql"),
    Oracle("oracle.jdbc.driver.OracleDriver",
            "jdbc:oracle:thin:@%s:%s:%s",
            "ojdbc14.jar",
            "com.zzg.mybatis.generator.plugins.PaginationPluginOracle"),
    PostgreSQL("org.postgresql.Driver",
            "jdbc:postgresql://%s:%s/%s",
            "postgresql-9.4.1209.jar",
            "com.zzg.mybatis.generator.plugins.PaginationPluginMysql"),
	SQL_Server("com.microsoft.sqlserver.jdbc.SQLServerDriver",
            "jdbc:sqlserver://%s:%s;databaseName=%s",
            "sqljdbc4-4.0.jar",
            "org.mybatis.generator.plugins.SerializablePlugin"),
    DB2("com.ibm.db2.jcc.DB2Driver",
            "jdbc:db2://%s:%s/%s",
            "db2jcc-10.5.jar",
            "com.zzg.mybatis.generator.plugins.PaginationPluginDb2");

    private final String driverClass;
    private final String connectionUrlPattern;
    private final String connectorJarFile;
    private final String pagePlugin;

    private static Map<String,DbType> map = new ConcurrentHashMap<>();

    DbType(String driverClass, String connectionUrlPattern, String connectorJarFile,String pagePlugin) {
        this.driverClass = driverClass;
        this.connectionUrlPattern = connectionUrlPattern;
        this.connectorJarFile = connectorJarFile;
        this.pagePlugin = pagePlugin;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public String getConnectionUrlPattern() {
        return connectionUrlPattern;
    }

    public String getConnectorJarFile() {
        return connectorJarFile;
    }

    public String getPagePlugin(){return pagePlugin;}

    public static DbType getTypeByName(String name){
        if(null==name || name.equals(""))return DbType.Oracle;
        if(null==map || map.size()==0){
            DbType[] dbTypes = DbType.values();
            for(DbType d:dbTypes){
                map.put(d.name(),d);
            }
        }
        return map.get(name);
    }

}