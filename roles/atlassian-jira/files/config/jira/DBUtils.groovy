package jira

/**
 * Created by hein on 02/11/15.
 */
class DBUtils {

    def static boolean canConnect(groovy.sql.Sql sql, String dbValid) {
        try {
            return sql.rows(dbValid+";").size() == 1
        } catch (Exception e) {
            e.printStackTrace()
        }
        return false;
    }

    def static boolean existsTable(groovy.sql.Sql sql, String tablename) {
        try {
            return sql.rows("select count(*) from " + tablename + " ;").size() >= 1;
        } catch (Exception e) {
            e.printStackTrace()
        }
        return false;
    }

}
