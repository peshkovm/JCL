package command_line.commands;

import command_line.CurrentDirectory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class Pwd {
    public void start(OutputStream outStream) {
        PrintStream writer = new PrintStream(outStream, true);
        String currentDir = CurrentDirectory.getCurrentDir();
        writer.println(currentDir);
    }
}