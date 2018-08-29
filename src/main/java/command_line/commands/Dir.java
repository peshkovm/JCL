package command_line.commands;

import command_line.CurrentDirectory;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

public class Dir {
    public void start(OutputStream outStream) {
        PrintStream writer = new PrintStream(outStream, true);
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
