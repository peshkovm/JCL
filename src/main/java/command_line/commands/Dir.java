package command_line.commands;

import command_line.CurrentDirectory;

import java.io.File;

public class Dir {
    public void start() {
        String currentDir = CurrentDirectory.getCurrentDir();
        File dir = new File(currentDir);
        File[] files = dir.listFiles();

        if (files != null)
            for (File file : files) {
                if (!file.isHidden())
                    System.out.println(file.getName());
            }
    }
}
