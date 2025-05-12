package ru.mai.lessons.rpks.impl;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;


public class ReadFileFuture implements Callable<List<LogAfterParse>> {
    Path pathToFile;
    long startPosition;
    long endPosition;

    public ReadFileFuture(Path pathToFile, long startPosition, long endPosition) {
        this.pathToFile = pathToFile;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    @Override
    public List<LogAfterParse> call() {
        List<LogAfterParse> logs = new ArrayList<>();
        try (RandomAccessFile file = new RandomAccessFile(pathToFile.toFile(), "r")) {
            file.seek(startPosition);

            if (startPosition != 0) {
                goToNextLine(file);
            }

            long currentPosition = file.getFilePointer();
            String line;

            while (currentPosition <= endPosition && (line = file.readLine()) != null) {
                logs.add(new LogAfterParse(new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)));
                currentPosition = file.getFilePointer();
            }

        } catch (IOException e) {
            System.err.println("I/O error while reading file: " + pathToFile.toString() + e.getMessage());
            e.printStackTrace(System.err);
        } catch (Exception e) {
            System.err.println("Unexpected error occurred: " + e.getMessage());
            e.printStackTrace(System.err);
        }
        return logs;
    }

    private void goToNextLine(RandomAccessFile file) throws IOException {
        int currentCharacter = file.read();

        while (currentCharacter != -1 && currentCharacter != '\n' && currentCharacter != '\r') {
            currentCharacter = file.read();
        }

        if (currentCharacter == '\r') {
            long position = file.getFilePointer();
            if ( file.read() != '\n') {
                file.seek(position);
            }
        }
    }

}
