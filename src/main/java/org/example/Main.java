package org.example;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    static final String INPUT_DIRECTORY = "src/logs";
    static final String OUTPUT_DIRECTORY_NAME = "transactions_by_users";

    public static void main(String[] args) {
        File inputDir = new File(INPUT_DIRECTORY);
        if (!inputDir.exists() || !inputDir.isDirectory()) {
            System.out.println("Input directory doesn't exist or is not a directory.");
            return;
        }

        Map<String, List<LogEntry>> userLogs = new HashMap<>();
        Map<String, Balance> balances = new HashMap<>();

        Pattern balancePattern = Pattern.compile("\\[(.*?)\\] (\\w+) balance inquiry ([\\d.]+)");
        Pattern transferPattern = Pattern.compile("\\[(.*?)\\] (\\w+) transferred ([\\d.]+) to (\\w+)");
        Pattern withdrawPattern = Pattern.compile("\\[(.*?)\\] (\\w+) withdrew ([\\d.]+)");

        File[] logFiles = inputDir.listFiles((dir, name) -> name.endsWith(".log"));
        if (logFiles == null) return;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (File logFile : logFiles) {
            try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;

                    Matcher m;

                    m = balancePattern.matcher(line);
                    if (m.matches()) {
                        String dateStr = m.group(1);
                        String user = m.group(2);
                        double value = Double.parseDouble(m.group(3));
                        Date date = sdf.parse(dateStr);

                        balances.putIfAbsent(user, new Balance());
                        balances.get(user).setValue(value);
                        userLogs.computeIfAbsent(user, k -> new ArrayList<>()).add(new LogEntry(line, date));
                        continue;
                    }

                    m = transferPattern.matcher(line);
                    if (m.matches()) {
                        String dateStr = m.group(1);
                        String sender = m.group(2);
                        double amount = Double.parseDouble(m.group(3));
                        String receiver = m.group(4);
                        Date date = sdf.parse(dateStr);

                        balances.putIfAbsent(sender, new Balance());
                        balances.putIfAbsent(receiver, new Balance());

                        balances.get(sender).decrease(amount);
                        balances.get(receiver).increase(amount);

                        userLogs.computeIfAbsent(sender, k -> new ArrayList<>()).add(new LogEntry(line, date));

                        String receivedLine = String.format("[%s] %s recived %.2f from %s", dateStr, receiver, amount, sender);
                        userLogs.computeIfAbsent(receiver, k -> new ArrayList<>()).add(new LogEntry(receivedLine, date));
                        continue;
                    }

                    m = withdrawPattern.matcher(line);
                    if (m.matches()) {
                        String dateStr = m.group(1);
                        String user = m.group(2);
                        double amount = Double.parseDouble(m.group(3));
                        Date date = sdf.parse(dateStr);

                        balances.putIfAbsent(user, new Balance());
                        balances.get(user).decrease(amount);
                        userLogs.computeIfAbsent(user, k -> new ArrayList<>()).add(new LogEntry(line, date));
                        continue;
                    }

                    System.err.println("Skipped unrecognized line: " + line);
                }
            } catch (IOException | ParseException e) {
                System.err.println("Error processing file " + logFile.getName() + ": " + e.getMessage());
            }
        }

        File outDir = new File(inputDir.getParentFile(), OUTPUT_DIRECTORY_NAME);
        if (!outDir.exists()) outDir.mkdir();

        for (String user : userLogs.keySet()) {
            List<LogEntry> logs = userLogs.get(user);
            logs.sort(Comparator.comparing(LogEntry::getTimestamp));

            File outFile = new File(outDir, user + ".log");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))) {
                for (LogEntry entry : logs) {
                    writer.write(entry.getLine());
                    writer.newLine();
                }

                String finalLine = String.format("[%s] %s final balance %s",
                        sdf.format(new Date()), user, balances.get(user));
                writer.write(finalLine);
                writer.newLine();
            } catch (IOException e) {
                System.err.println("Error writing file for user " + user + ": " + e.getMessage());
            }
        }
    }
}
