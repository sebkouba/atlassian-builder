https://bitbucket.anarcon.org/projects/AZDAWS/repos/azd-aws-jicohc/browse/ami/scripts/init.d/atl-init-jira.sh

### jira dbconfig.xml
<jira-database-config>
  <name>defaultDS</name>
  <delegator-name>default</delegator-name>
  <database-type>postgres72</database-type>
  <schema-name>public</schema-name>
  <jdbc-datasource>
    <url>$2</url>
    <driver-class>$1</driver-class>
    <username>$3</username>
    <password>$4</password>
    <pool-min-size>20</pool-min-size>
    <pool-max-size>20</pool-max-size>
    <pool-max-wait>30000</pool-max-wait>
    <validation-query>select 1</validation-query>
    <min-evictable-idle-time-millis>60000</min-evictable-idle-time-millis>
    <time-between-eviction-runs-millis>300000</time-between-eviction-runs-millis>
    <pool-max-idle>20</pool-max-idle>
    <pool-remove-abandoned>true</pool-remove-abandoned>
    <pool-remove-abandoned-timeout>300</pool-remove-abandoned-timeout>
    <pool-test-on-borrow>false</pool-test-on-borrow>
    <pool-test-while-idle>true</pool-test-while-idle>
  </jdbc-datasource>
</jira-database-config>


function atl_createRemoteDb {
    local PRODUCT_SHORT_DISPLAY_NAME=$1
    local DB_NAME=$2
    local DB_USER=$3
    local DB_HOST=${4:-"localhost"}
    local DB_PORT=${5:-"5432"}
    local DB_COLLATE=${6:-"en_US"}
    local DB_CTYPE=${7:-"en_US"}
    local DB_TEMPLATE=${8:-"template1"}

    atl_log "Creating ${PRODUCT_SHORT_DISPLAY_NAME} DB ${DB_NAME}"
    su postgres -c "psql -w -h ${DB_HOST} -p ${DB_PORT} -U postgres --command \"CREATE DATABASE ${DB_NAME} WITH OWNER=${DB_USER} ENCODING='UTF8' LC_COLLATE '${DB_COLLATE}' LC_CTYPE '${DB_CTYPE}' TEMPLATE ${DB_TEMPLATE} CONNECTION LIMIT=-1;\"" >> "${ATL_LOG}" 2>&1
    atl_log "Done creating ${PRODUCT_SHORT_DISPLAY_NAME} DB ${DB_NAME}"
}

function configureRemoteDb {
    atl_log "Configuring remote DB for use with ${ATL_JIRA_SHORT_DISPLAY_NAME}"

    if [[ -n "${ATL_DB_PASSWORD}" ]]; then
        atl_configureDbPassword "${ATL_DB_PASSWORD}" "*" "${ATL_DB_HOST}" "${ATL_DB_PORT}"
        
        if atl_roleExists ${ATL_JDBC_USER} "postgres" ${ATL_DB_HOST} ${ATL_DB_PORT}; then
            atl_log "${ATL_JDBC_USER} role already exists. Skipping role creation."
        else
            atl_createRole "${ATL_JIRA_SHORT_DISPLAY_NAME}" "${ATL_JDBC_USER}" "${ATL_JDBC_PASSWORD}" "${ATL_DB_HOST}" "${ATL_DB_PORT}"
            atl_createRemoteDb "${ATL_JIRA_SHORT_DISPLAY_NAME}" "${ATL_DB_NAME}" "${ATL_JDBC_USER}" "${ATL_DB_HOST}" "${ATL_DB_PORT}" "C" "C" "template0"
        fi

        configureDbProperties "${ATL_JDBC_DRIVER}" "${ATL_JDBC_URL}" "${ATL_JDBC_USER}" "${ATL_JDBC_PASSWORD}"
    fi
}

function updateHostName {
    atl_configureTomcatConnector "${1}" "8080" "8081" "${ATL_JIRA_USER}" \
        "${ATL_JIRA_INSTALL_DIR}/conf" \
        "${ATL_JIRA_INSTALL_DIR}/atlassian-jira/WEB-INF"

    STATUS="$(service "${ATL_JIRA_SERVICE_NAME}" status || true)"
    if [[ "${STATUS}" =~ .*\ is\ running ]]; then
        atl_log "Restarting ${ATL_JIRA_SHORT_DISPLAY_NAME} to pick up host name change"
        noJIRA
        goJIRA
    fi
}