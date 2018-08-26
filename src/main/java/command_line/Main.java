package command_line;

import command_line.commands.Cd;
import command_line.commands.Dir;
import command_line.commands.Pwd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Dir dir = new Dir();
        Pwd pwd = new Pwd();
        Cd cd = new Cd();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String line;
            while (!(line = reader.readLine()).equals("exit")) {
                String[] words = line.split("\\s+");
                String command = words[0];
                String[] commandArgs = {""};
                if (line.contains(" "))
                    commandArgs = Arrays.copyOfRange(words, 1, words.length);

                String className = XMLConfigReader.find(command);
                switch (className) {
                    case "command_line.commands.Dir": {
                        dir.start();
                        break;
                    }
                    case "command_line.commands.Pwd": {
                        pwd.start();
                        break;
                    }
                    case "command_line.commands.Cd": {
                        cd.start(commandArgs[0]);
                        break;
                    }
                    default:
                        System.out.println("Not such command");
                }
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
