package jira
/**
 * Created by hein on 22/09/15.
 */
class JIRAConfigBuilder {

    DatabaseConfigBuilder databaseConfigBuilder

    def setenvFile = new File('/opt/jira/install/bin/setenv.sh')

    def serverXmlFile = new File('/opt/jira/install/conf/server.xml')

    def proxyPort = 80
    def proxyName = "localhost"
    def scheme = "http"
    def baseUrl = "http://localhost"
    def contextPath = ""

    JIRAConfigBuilder() {
        //println "constructor"
    }

    void disarm(Boolean disarmed) {
        if (disarmed) {
            println "Disable mail notifications"
            FileSearchReplace.searchReplace(setenvFile,"\n#DISABLE_NOTIFICATIONS","\nDISABLE_NOTIFICATIONS")
        } else {
            println "Not disabling mail notifications"
        }

    }

    void minimumMemory(String minimumMemory) {
        println "Setting minimum memory to ${minimumMemory}"
        FileSearchReplace.searchReplace(setenvFile,"\nJVM_MINIMUM_MEMORY.*\n","\nJVM_MINIMUM_MEMORY=${minimumMemory}\n")
    }

    void maximumMemory(String maximumMemory) {
        println "Setting maximum memory to ${maximumMemory}"
        FileSearchReplace.searchReplace(setenvFile,"\nJVM_MAXIMUM_MEMORY.*\n","\nJVM_MAXIMUM_MEMORY=${maximumMemory}\n")
    }

    void jmx(Boolean enableJmx) {
        if (enableJmx) {
            println "Enabling JMX"
            FileSearchReplace.searchReplace(setenvFile, "(JVM_REQUIRED_ARGS='.*)'", "\$1 -Dcom.sun.management.jmxremote.port=9999 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false'")
        } else {
            println "Not enabling JMX"
        }
    }

    void baseUrl(String aBaseUrl) {
        baseUrl = aBaseUrl
        URL url = new URL(baseUrl)
        scheme = url.protocol
        proxyPort = url.port
        if (proxyPort == -1 && scheme == "http") {
            proxyPort = 80;
        } else if (proxyPort == -1 && scheme == "https") {
            proxyPort = 443;
        }
        contextPath = url.path
        proxyName = url.host
    }

    def adaptServerXml() {

        println """Adapting server.xml with
    scheme= ${scheme}
    proxyName= ${proxyName}
    proxyPort= ${proxyPort}
    contextPath= ${contextPath}"""

        //Reading the XML
        def server = new XmlSlurper().parse(serverXmlFile)

        //Setting the scheme/proxy
        def connector = server.Service[0].Connector[0]
        connector.@scheme = scheme
        connector.@proxyName = proxyName
        connector.@proxyPort = proxyPort.toString()

        //Setting the contextPath
        server.Service[0].Engine[0].Host[0].Context[0].@path = contextPath

        //Writing back the XML
        serverXmlFile.text = groovy.xml.XmlUtil.serialize(server)

    }


    def adaptBaseUrl(groovy.sql.Sql sql) {
        if (!DBUtils.existsTable(sql,"propertyentry")) {
            print "No entries in table propertyentry"
            return;
        }
        if (!DBUtils.existsTable(sql,"propertystring")) {
            print "No entries in table propertystring"
            return;
        }
        println """Updating baseUrl: ${baseUrl}"""
        def updateCount = sql.executeUpdate("""
update propertystring
set propertyvalue = ?
where propertystring.id in (select id from propertyentry where property_key = 'jira.baseurl');
        """,[baseUrl])

        println "${updateCount} row(s) updated !!"
    }

    void doIt() {
        databaseConfigBuilder.doIt()
        databaseConfigBuilder.persist('/opt/jira/data/dbconfig.xml')

        //println "Executing the files"
        adaptServerXml()
	    def databaseExists = true;
	    if (databaseExists) {
             def sql = databaseConfigBuilder.sql()
             if (DBUtils.canConnect(sql,databaseConfigBuilder.dbValid)) {
                 adaptBaseUrl(sql)

             }
             sql.close()
	    }

    }

    void database(Closure c) {
        databaseConfigBuilder = new DatabaseConfigBuilder()
        c.delegate = databaseConfigBuilder
        c()
    }

    /**
     * This method accepts a closure which is essentially the DSL. Delegate the
     * closure methods to
     * the DSL class so the calls can be processed
     */
    def static build(Closure closure) {
        JIRAConfigBuilder jiraConfigBuilder = new JIRAConfigBuilder()
        // any method called in closure will be delegated to the JIRAConfigBuilder class
        closure.delegate = jiraConfigBuilder
        closure()
        jiraConfigBuilder.doIt()
    }
}
