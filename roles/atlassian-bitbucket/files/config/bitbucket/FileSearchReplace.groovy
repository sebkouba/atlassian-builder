package bitbucket

/**
 * Created by hein on 22/09/15.
 */
class FileSearchReplace {
    static searchReplace(File file, String searchExpression,String replaceString, boolean backup=false) {
        def fileText = file.text;
        if (backup) {
            def backupFile = new File(file.path + ".bak");
            backupFile.write(fileText);
        }
        fileText = fileText.replaceAll(searchExpression, replaceString)
        file.write(fileText);
    }
}
