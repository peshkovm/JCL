package command_line.commands;

import command_line.CurrentDirectory;

public class Cd {
    public void start(String dirPath) {
        CurrentDirectory.setCurrentDir(dirPath);
    }
}
