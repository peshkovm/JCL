package command_line.commands;

import command_line.CurrentDirectory;

import java.io.*;

public class Dir {
    public void start(StringWriter outStream) {
        PrintWriter writer = new PrintWriter(outStream, true);
        String currentDir = CurrentDirectory.getCurrentDir();
        File dir = new File(currentDir);
        File[] files = dir.listFiles();

        if (files != null)
            for (File file : files) {
                if (!file.isHidden())
                    writer.println(file.getName());
            }
    }
}
