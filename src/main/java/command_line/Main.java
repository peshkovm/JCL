package command_line;

import command_line.commands.Cd;
import command_line.commands.Dir;
import command_line.commands.Pwd;
import javafx.scene.web.HTMLEditorSkin;

import java.io.*;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        new Main().start();
    }

    void start() {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        try {
            while (!(line = reader.readLine()).equals("exit")) {
                if (line.contains("&&") || line.contains("||")) {
                    int controlSymbolIndex;
                    if (line.contains("&&"))
                        controlSymbolIndex = line.indexOf("&&");
                    else
                        controlSymbolIndex = line.indexOf("||");
                    String str1 = line.substring(0, controlSymbolIndex - 1);
                    String str2 = line.substring(controlSymbolIndex + "&& ".length(), line.length());

                    String[] words1 = str1.split("\\s+");
                    String[] words2 = str2.split("\\s+");

                    String command1 = words1[0];
                    String command2 = words2[0];

                    String[] commandArgs1 = Arrays.copyOfRange(words1, 1, words1.length);
                    String[] commandArgs2 = Arrays.copyOfRange(words2, 1, words2.length);

                    //printTest(command1, commandArgs1, command2, commandArgs2);
                    if (line.contains("&&"))
                        try {
                            startProgramm(command1, commandArgs1);
                            startProgramm(command2, commandArgs2);
                        } catch (FileNotFoundException e) {
                            System.out.println(e.getMessage());
                        }
                    else
                        try {
                            startProgramm(command1, commandArgs1);
                        } catch (FileNotFoundException e) {
                            System.out.println(e.getMessage());
                            try {
                                startProgramm(command2, commandArgs2);
                            } catch (FileNotFoundException e1) {
                                e1.getMessage();
                            }
                        }
                } else {
                    String[] words = line.split("\\s+");
                    String command = words[0];
                    String[] commandArgs = {""};

                    if (line.contains(" "))
                        commandArgs = Arrays.copyOfRange(words, 1, words.length);
                    if (command.charAt(0) != '!') {
                        startProgramm(command, commandArgs);
                    } else {
                        words[0] = command.substring(1);
                        Process process = null;
                        process = new ProcessBuilder(words).start();
                        readFromWriteTo(process.getInputStream(), System.out);

                        while (process.isAlive()) {
                            readFromWriteTo(System.in, process.getOutputStream());
                            readFromWriteTo(process.getInputStream(), System.out);
                        }
                        process.destroy();
                        try {
                            process.waitFor();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                System.out.println();
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startProgramm(String command, String[] commandArgs) throws FileNotFoundException {
        Dir dir = new Dir();
        Pwd pwd = new Pwd();
        Cd cd = new Cd();

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
    }

    private void readFromWriteTo(InputStream reader, OutputStream writer) {
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

    private void printTest(String command1, String[] commandArgs1, String command2, String[] commandArgs2) {
        System.out.println("command1 = " + command1);
        System.out.print("command1 args = ");
        for (int i = 0; i < commandArgs1.length; i++) {
            System.out.print(commandArgs1[i] + " ");
        }

        System.out.println();
        System.out.println("command2 = " + command2);
        System.out.print("command2 args = ");
        for (int i = 0; i < commandArgs2.length; i++) {
            System.out.print(commandArgs2[i] + " ");
        }
    }
}
