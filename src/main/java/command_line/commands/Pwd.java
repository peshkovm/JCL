package command_line.commands;

import command_line.CurrentDirectory;

import java.io.File;
import java.io.IOException;

public class Pwd {
    public void start() {
        String currentDir = CurrentDirectory.getCurrentDir();
        System.out.println(currentDir);
    }
}