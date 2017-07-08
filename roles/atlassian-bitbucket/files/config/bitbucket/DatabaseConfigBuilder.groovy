package bitbucket

import groovy.util.slurpersupport.GPathResult
import groovy.util.slurpersupport.NoChildren

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
    String dbInstance
    String schema

    String dbValidation
    String dbUrl
    String dbSchema
    String dbDriver
    String dbPort
    String dbDialect

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

    void instance(String instance) {
        dbInstance = instance
    }

    void port(String port) {
        dbPort = port
    }

    void schema(String s) {
        schema = s
    }

    void doIt() {
    //    if (schema){
    //        dbSchema="<schema-name>${schema}</schema-name>"
    //    } else {
    //        dbSchema="<schema-name>jiraschema</schema-name>"
    //    } 
     
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
                dbDialect="net.sf.hibernate.dialect.SQLServerIntlDialect"
            break;
            case "mysql":
                dbPort= dbPort?:"3306"
                dbUrl="jdbc:mysql://${dbHost}:${dbPort}/${dbName}?useUnicode=true&amp;characterEncoding=UTF8&amp;sessionVariables=storage_engine=InnoDB"
                dbDriver="com.mysql.jdbc.Driver"
                dbSchema = ""
                dbDialect = "com.atlassian.hibernate.dialect.MySQLDialect"
            break;
            case "postgres72":
            case "postgres":
                dbPort= dbPort?:"5432"
                dbUrl="jdbc:postgresql://${dbHost}:${dbPort}/${dbName}"
                dbDriver="org.postgresql.Driver"
                dbDialect="org.hibernate.dialect.PostgreSQLDialect"
            break;
            case "oracle10g":
            case "oracle":
                dbPort= dbPort?:"1521"
                dbUrl="jdbc:oracle:thin:@//${dbHost}:${dbPort}/${dbName}"
                dbDriver="oracle.jdbc.OracleDriver"
                dbDialect="org.hibernate.dialect.OracleDialect"
                /* org.hibernate.dialect.Oracle9Dialect */
            break;

            default:
                dbType="hsql"
                dbSchema="<schema-name>PUBLIC</schema-name>"
                dbUrl="jdbc:hsqldb:/opt/jira/data/database/jiradb"
                dbUser="sa"
                dbPassword=""
                dbDriver="org.hsqldb.jdbcDriver"
        }
    }

    void dbType(String type) {
        println "Setting dbtype to ${dbType}"
        dbType = type;
    }

    void persist( configFileName) {
	def conffile = new File(configFileName)

        if (!conffile.exists()) {
            println "${conffile.absolutePath} does not exist, so not changing the file"
            return
        }
        println "Writing databaseconfig to ${conffile.absolutePath}"
        println "    Database name : ${dbName}"
        println "    Database type : ${dbType}"
        println "    Database user : ${dbUser}"
        println "    Database password : ${dbPassword}"
        println "    Database host : ${dbHost}"
        println "    Database instance : ${dbInstance}"
        println "    Database port : ${dbPort}"
        println "    Database schema : ${dbSchema}"
        println "    Database dialect : ${dbDialect}"
        println "    Database url : ${dbUrl}"
        new File(configFileName).text = text()

    }
    String text() {
      return """
jdbc.driver=${dbDriver}
jdbc.url=${dbUrl}
jdbc.user=${dbUser}
jdbc.password=${dbPassword}
"""

    }
}
