package command_line;

import command_line.commands.Cd;
import command_line.commands.Dir;
import command_line.commands.Pwd;

import java.io.*;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        new Main().start();
    }

    void start() {
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
                if (command.charAt(0) != '!') {
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
                } else {
                    words[0] = command.substring(1);

                    Process process = new ProcessBuilder(words).start();

                    readFromWriteTo(process.getInputStream(), System.out);

                    while (process.isAlive()) {
                        readFromWriteTo(System.in, process.getOutputStream());
                        readFromWriteTo(process.getInputStream(), System.out);
                    }

                    process.destroy();
                    process.waitFor();
                }
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void readFromWriteTo(InputStream reader, OutputStream writer) {
        BufferedReader reader1 = new BufferedReader(new InputStreamReader(reader));
        PrintStream writer1 = new PrintStream(writer);
        String lineFromProcess;

        try {
            while ((lineFromProcess = reader1.readLine()) != null) {
                writer1.println(lineFromProcess);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
