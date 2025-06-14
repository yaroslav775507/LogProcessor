package org.example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogEntry {
    private Date timestamp;
    private String line;

    public LogEntry(String line) throws ParseException {
        this.line = line;
        String dateForm = line.substring(1, 20);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.timestamp = sdf.parse(dateForm);
    }

    public LogEntry(String line, Date timestamp) {
        this.line = line;
        this.timestamp = timestamp;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getLine() {
        return line;
    }
}
