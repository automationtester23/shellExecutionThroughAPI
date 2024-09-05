package com.sumedh.linuxapi.domain;
//changes done +10
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.jcraft.jsch.*;
import org.springframework.stereotype.Component;

@Component
public class shellCommandExecutor {

    private Session session;

    public String executeCommand(String command, String hostname, int port, String username, String password) {
        String sessionStatus;
        StringBuilder output = new StringBuilder();
        try {
            establishSshConnection(hostname, port, username, password);

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.connect();

            InputStream inputStream = channel.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            channel.disconnect();

            session.disconnect();

            // Check if the session is closed
            if (!session.isConnected()) {
                sessionStatus = "Session is closed.";
                System.out.println("Session is closed.");
            } else {
                sessionStatus = "Session is still open.";
                System.out.println("Session is still open.");
            }

        } catch (JSchException | IOException e) {
            e.printStackTrace();
            return "command Execution failed ";
        } finally {
            if (session != null) {
                session.disconnect(); // Ensure the session is closed even if an exception occurs
                // Check if the session is closed
                if (!session.isConnected()) {
                    sessionStatus = "Session is closed in finally";
                    System.out.println("Session is closed.");
                } else {
                    sessionStatus = "Session is still open in finally";
                    System.out.println("Session is still open.");
                }
            }
        }

        return output.toString() + "\n" + sessionStatus;
    }

    public static void main(String args[]) {
        shellCommandExecutor executor = new shellCommandExecutor();
        executor.establishSshConnection("172.", 22, "sumedh", "");

        // String result = executor.executeCommand("ls");
        System.out.println("result");
    }

    public void establishSshConnection(String host, int port, String username, String password) {
        try {
            JSch jsch = new JSch();

            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            // Now you have an active SSH session to the remote server
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String executeCommand(String command) {
        StringBuilder output = new StringBuilder();
        int exitCode = 1;
        try {
            // Create the process builder
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("bash", "-c", command);

            // Start the process
            Process process = processBuilder.start();

            // Get the process output
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                output.append(line).append("\n");
            }

            // Wait for the process to finish
            exitCode = process.waitFor();
            System.out.println("Command executed with exit code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return output.toString();
    }
}
