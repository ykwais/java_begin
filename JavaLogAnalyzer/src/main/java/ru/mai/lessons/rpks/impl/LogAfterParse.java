package ru.mai.lessons.rpks.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogAfterParse {
    long id;
    boolean isResult;
    LocalDateTime dateTime;

    public LogAfterParse(String line) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String[] parts = line.split(" – ");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid log format: " + line);
        }

        this.dateTime = LocalDateTime.parse(parts[0].trim(), formatter);

        this.isResult = parts[2].contains("RESULT QUERY");

        String idPart = parts[2].split("ID = ")[1];
        this.id = Long.parseLong(idPart.trim());
    }
}
