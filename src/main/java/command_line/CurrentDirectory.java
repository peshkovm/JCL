package command_line;

import java.io.File;

public class CurrentDirectory {
    private static File currentDir;

    static {
        String currDir = System.getProperty("user.dir");
        currentDir = new File(currDir);
    }

    public static String getCurrentDir() {
        return currentDir.getPath();
    }

    public static void setCurrentDir(String currDir) {
        File newCurrDir = new File(currDir);

        if (newCurrDir.exists())
            currentDir = newCurrDir;
        else if ((newCurrDir = new File(currentDir.getPath().concat("\\" + currDir))).exists())
            currentDir = newCurrDir;
        else
            System.out.println("Not such dir");
    }
}
