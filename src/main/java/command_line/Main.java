package command_line;

import command_line.commands.Cd;
import command_line.commands.Dir;
import command_line.commands.Pwd;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static ArrayList<ClientHolder> clients;
    StringBuffer lastMessageBuffer = new StringBuffer("There were no messages yet");

    static {
        clients = new ArrayList<>();
    }

    public static void main(String[] args) {
        new Main().start();
    }

    void start() {
        ExecutorService service = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("net version? : yes|no");
            String line = reader.readLine();
            System.out.println();

            if (line.equals("yes")) {
                ServerSocket serverSocket = new ServerSocket(1234);
                while (true) {
                    Socket socket = serverSocket.accept();
                    ClientHolder clientHolder = new ClientHolder(socket);
                    clients.add(clientHolder);
                    service = Executors.newCachedThreadPool();
                    service.submit(clientHolder);
                    //new Thread(clientHolder).start();
                }
            } else {
                go(System.in, System.out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                service.shutdown();
                service.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS); //forever
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void who() {
        for (ClientHolder client : clients) {
            client.printName();
        }
    }

    private void write() {
        for (ClientHolder client : clients) {
            PrintWriter clientWriter = new PrintWriter(client.getOutputStream(), true);
            clientWriter.println(lastMessageBuffer);
        }
    }

    private void go(InputStream inStream, OutputStream outStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
        StringWriter strWriter = new StringWriter();
        String line;
        try {
            while (!(line = reader.readLine()).equals("exit")) {
                if (line.equals("write")) {
                    write();
                } else {
                    lastMessageBuffer.delete(0, lastMessageBuffer.length());
                    if (line.equals("who")) {
                        who();
                    } else if (line.equals("jobs"))
                        DaemonsHolder.printDaemons1(outStream);
                    else if (line.contains("&&") || line.contains("||")) {
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
                                startProgram(command1, commandArgs1, strWriter);
                                startProgram(command2, commandArgs2, strWriter);
                            } catch (FileNotFoundException e) {
                                System.out.println(e.getMessage());
                            }
                        else
                            try {
                                startProgram(command1, commandArgs1, strWriter);
                            } catch (FileNotFoundException e) {
                                System.out.println(e.getMessage());
                                try {
                                    startProgram(command2, commandArgs2, strWriter);
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
                            startProgram(command, commandArgs, strWriter);
                        } else {
                            if (line.charAt(line.length() - 1) == '&') {
                                words[0] = command.substring(1, command.length() - 1);
                                Process process = new ProcessBuilder(words).start();
                                DaemonsHolder.addDaemon(words[0], process);
                            } else {
                                words[0] = command.substring(1);
                                Process process = new ProcessBuilder(words).start();
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
                    }
                    lastMessageBuffer = strWriter.getBuffer();
                    PrintWriter writer = new PrintWriter(outStream, true);
                    writer.println(lastMessageBuffer);
                    //writer.println();
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            DaemonsHolder.destroyAllDaemons();
        }
    }

    private static class DaemonsHolder {
        private static ArrayList<DaemonInfo> daemons;

        static {
            daemons = new ArrayList<>();
        }

        static void addDaemon(String name, Process process) {
            DaemonInfo daemon = new DaemonInfo();
            daemon.setName(name);
            daemon.setProcess(process);
            daemons.add(daemon);
        }

        static void printDaemons1(OutputStream outStream) {
            PrintWriter writer = new PrintWriter(outStream, true);
            Iterator<DaemonInfo> iterator = daemons.iterator();
            while (iterator.hasNext()) {
                DaemonInfo daemon = iterator.next();
                if (daemon.getProcess().isAlive())
                    writer.println(daemon.getName());
                else {
                    daemon.getProcess().destroy();
                    try {
                        daemon.getProcess().waitFor();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    iterator.remove();
                }
            }
        }

        static void destroyAllDaemons() {
            for (DaemonInfo daemon : daemons) {
                daemon.getProcess().destroy();
                try {
                    daemon.getProcess().waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private static class DaemonInfo {
            private String name;
            private Process process;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Process getProcess() {
                return process;
            }

            public void setProcess(Process process) {
                this.process = process;
            }
        }
    }

    private class ClientHolder implements Runnable {
        private InetAddress ip;
        private Socket socket;
        private InputStream inStream;
        private OutputStream outStream;

        ClientHolder(Socket socket) {
            this.socket = socket;
            this.ip = socket.getInetAddress();
            try {
                this.inStream = socket.getInputStream();
                this.outStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        OutputStream getOutputStream() {
            return outStream;
        }

        InputStream getInputStream() {
            return inStream;
        }

        public InetAddress getIp() {
            return ip;
        }

        void printName() {
            PrintWriter writer = new PrintWriter(getOutputStream(), true);
            writer.println(ip.getHostAddress());
        }

        @Override
        public void run() {
            go(getInputStream(), getOutputStream());
        }
    }

    private void startProgram(String command, String[] commandArgs, StringWriter outStream) throws FileNotFoundException {
        Dir dir = new Dir();
        Pwd pwd = new Pwd();
        Cd cd = new Cd();

        String className = XMLConfigReader.find(command);
        switch (className) {
            case "command_line.commands.Dir": {
                dir.start(outStream);
                break;
            }
            case "command_line.commands.Pwd": {
                pwd.start(outStream);
                break;
            }
            case "command_line.commands.Cd": {
                cd.start(commandArgs[0]);
                break;
            }
            default: {
                PrintWriter writer = new PrintWriter(outStream, true);
                writer.println("Not such command");
            }
        }
    }

    private void readFromWriteTo(InputStream reader, OutputStream writer) {
        BufferedReader reader1 = new BufferedReader(new InputStreamReader(reader));
        PrintStream writer1 = new PrintStream(writer, true);
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