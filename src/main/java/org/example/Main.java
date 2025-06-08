package org.example;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Main {
    static final String INPUT_DIRECTORY = "src/logs";
    static final String OUTPUT_DIRECTORY = "src/output";

    public static void main(String[] args) {
        File file = new File(INPUT_DIRECTORY);
        if (!file.exists()) {
            System.out.println("Input directory doesn't exist");
            return;
        }
        Map<String, List<LogEntry>> logEntries = new HashMap<>();
        Map<String, Balance> balances = new HashMap<>();
        
    }
}