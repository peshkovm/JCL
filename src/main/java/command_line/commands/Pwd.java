package command_line.commands;

import command_line.CurrentDirectory;

import java.io.*;

public class Pwd {
    public void start(StringWriter outStream) {
        PrintWriter writer = new PrintWriter(outStream, true);
        String currentDir = CurrentDirectory.getCurrentDir();
        writer.println(currentDir);
    }
}