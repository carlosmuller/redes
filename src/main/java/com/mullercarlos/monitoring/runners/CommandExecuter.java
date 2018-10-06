package com.mullercarlos.monitoring.runners;


import lombok.RequiredArgsConstructor;

import java.io.*;

@RequiredArgsConstructor
public class CommandExecuter {
    private final String command;

    public String execute() throws IOException {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("sh", "-c", command);
        builder.directory(new File(System.getProperty("user.home")));
        Process process = builder.start();
        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        do{
            line = input.readLine();
            response.append(line).append('\n');
        }while(input.ready());
        assert process.exitValue() == 0;
        return response.toString();
    }

    public static void main(String[] args) throws IOException {
        System.out.println(new CommandExecuter("top -bn1 | grep load | awk '{printf \"CPU Load: %.2f\\n\", $(NF-2)}' && free -m | awk 'NR==2{printf \"Memory Usage: %s/%sMB (%.2f%%)\\n\", $3,$2,$3*100/$2 }' && " +
                "df -h | awk '$NF==\"/\"{printf \"Disk Usage: %d/%dGB (%s)\\n\", $3,$2,$5}' ").execute());
    }
}
