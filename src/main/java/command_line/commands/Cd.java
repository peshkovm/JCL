package command_line.commands;

import command_line.CurrentDirectory;

import java.io.FileNotFoundException;

public class Cd {
    public void start(String dirPath) throws FileNotFoundException {
        CurrentDirectory.setCurrentDir(dirPath);
    }
}
