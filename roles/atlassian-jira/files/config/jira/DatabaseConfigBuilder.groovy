package jira

import groovy.sql.Sql

/**
 * Created by hein on 22/09/15.
 */


class DatabaseConfigBuilder {

    groovy.sql.Sql dbSql

    String dbName
    String dbType
    String dbUser
    String dbPassword
    String dbHost
    String schema
    String dbInstance
    String dbDialect

    String dbValidation
    String dbUrl
    String dbSchema
    String dbDriver
    String dbPort

    String dbValid

    void name(String name) {
        dbName = name
    }

    void type(String type) {
        dbType = type
    }

    void user(String user) {
        dbUser = user
    }

    void password(String password) {
        dbPassword = password
    }

    void host(String host) {
        dbHost = host
    }

    void port(String port) {
        dbPort = port
    }

    void instance(String instance) {
        dbInstance = instance
    }

    void schema(String s) {
        schema = s
    }

    void doIt() {
        if (schema){
            dbSchema="<schema-name>${schema}</schema-name>"
        } else {
            dbSchema="<schema-name>jiraschema</schema-name>"
        }


        switch (dbType) {

            case "mssql":
            case "ms-sql":
                dbPort = dbPort?:"1433"
                if (dbInstance!=null){
                  dbUrl="jdbc:jtds:sqlserver://${dbHost}/${dbName};instance=${dbInstance}"
                } else {
                  dbUrl="jdbc:jtds:sqlserver://${dbHost}:${dbPort}/${dbName}"
                }
                dbDriver="net.sourceforge.jtds.jdbc.Driver"
		            dbDialect="org.hibernate.dialect.SQLServerDialect"
                dbValid="SELECT @@VERSION"
            break;
            case "mysql":
                dbPort= dbPort?:"3306"
                dbUrl="jdbc:mysql://${dbHost}:${dbPort}/${dbName}?useUnicode=true&amp;characterEncoding=UTF8&amp;sessionVariables=storage_engine=InnoDB"
                dbDriver="com.mysql.jdbc.Driver"
                dbSchema = ""
		            dbDialect = "com.atlassian.hibernate.dialect.MySQLDialect"
                dbValid="select 1"
            break;
            case "postgres72":
            case "postgres":
                dbPort= dbPort?:"5432"
                dbUrl="jdbc:postgresql://${dbHost}:${dbPort}/${dbName}"
                dbDriver="org.postgresql.Driver"
		            dbDialect = "org.hibernate.dialect.PostgreSQLDialect"
                dbValid = "select version();"
            break;
            case "oracle10g":
            case "oracle":
                dbPort= dbPort?:"1521"
                dbUrl="jdbc:oracle:thin:@//${dbHost}:${dbPort}/${dbName}"
                dbDriver="oracle.jdbc.OracleDriver"
		            dbDialect= "org.hibernate.dialect.OracleDialect"
                /* org.hibernate.dialect.Oracle9Dialect */
                dbValid="select 1 from dual"
            break;

            default:
                dbType="hsql"
                dbSchema="<schema-name>PUBLIC</schema-name>"
                dbUrl="jdbc:hsqldb:/opt/jira/data/database/jiradb"
                dbUser="sa"
                dbPassword=""
                dbDriver="org.hsqldb.jdbcDriver"
                dbValid="select 1"
        }

        dbValidation="<validation-query>select 1</validation-query>"
    }

    void persist(filename) {
/**
 *       if (!conffile.exists()) {
 *           println "${conffile.absolutePath} does not exist, so not changing the file"
 *           return
 *       }
*/
        println "Writing databaseconfig to ${filename}"
        println "    Database name : ${dbName}"
        println "    Database type : ${dbType}"
        println "    Database user : ${dbUser}"
        println "    Database password : ${dbPassword}"
        println "    Database host : ${dbHost}"
        println "    Database port : ${dbPort}"
        println "    Database schema : ${dbSchema}"
        println "    Database dialect : ${dbDialect}"
        println "    Database instance : ${dbInstance}"
        println "    Database url : ${dbUrl}"
        new File(filename).text = xml()
    }

    Sql sql(int timeout=10000, int retries=6) {
        Exception lastException = null;
        if (dbSql == null) {
            for (int i = 0; i < retries; i++) {
                try {
                    dbSql = Sql.newInstance(dbUrl,dbUser,dbPassword,dbDriver)
                    return dbSql;

                } catch (Exception e) {
                    lastException = e
                }
                if (i < retries-1) {
                    println "Waiting for db : ${timeout} ms"
                    try {
                        sleep(timeout)
                    } catch (InterruptedException e) {

                    }

                }
            }
        }
        throw lastException;
    }

    String xml() {

        return """<?xml version="1.0" encoding="UTF-8"?>
<jira-database-config>
  <name>defaultDS</name>
  <delegator-name>default</delegator-name>
  <database-type>${dbType}</database-type>
  ${dbSchema}
  <jdbc-datasource>
    <url>${dbUrl}</url>
    <driver-class>${dbDriver}</driver-class>
    <username>${dbUser}</username>
    <password>${dbPassword}</password>
    <pool-min-size>20</pool-min-size>
    <pool-max-size>20</pool-max-size>
    <pool-max-wait>30000</pool-max-wait>
    ${dbValidation}
    <min-evictable-idle-time-millis>60000</min-evictable-idle-time-millis>
    <time-between-eviction-runs-millis>300000</time-between-eviction-runs-millis>
    <pool-max-idle>20</pool-max-idle>
    <pool-remove-abandoned>true</pool-remove-abandoned>
    <pool-remove-abandoned-timeout>300</pool-remove-abandoned-timeout>
    <pool-test-while-idle>true</pool-test-while-idle>
    <pool-test-on-borrow>false</pool-test-on-borrow>
  </jdbc-datasource>
</jira-database-config>"""

    }
}
