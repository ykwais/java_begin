package ru.mai.lessons.rpks.impl;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Callable;

public final class ReadFileFuture implements Callable<List<String>> {
    private final Path pathToFile;
    private final long startPosition;
    private final long endPosition;
    private final int lineCountBeforeAfter;
    private final String keyWord;
    private final Logger logger;

    public ReadFileFuture(Path pathToFile, long startPosition, long endPosition, int LineCountBeforeAfter, String key, Logger log) {
        this.pathToFile = pathToFile;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.lineCountBeforeAfter = LineCountBeforeAfter;
        this.keyWord = key;
        this.logger = log;
    }

    @Override
    public List<String> call() throws Exception {
        List<String> lines = new ArrayList<>();

        try (RandomAccessFile file = new RandomAccessFile(pathToFile.toFile(), "r")) {

            if (startPosition != 0) {
                long curetka = moveUpLines(file, startPosition, lineCountBeforeAfter);
                file.seek(curetka);
            }

            long currentBytePosition = file.getFilePointer();
            long amountStringInFragment = 0;
            long readedBytes = 0;
            Deque<String> previousLines = new ArrayDeque<>();
            Deque<String> postLines = new ArrayDeque<>();
            String currentLine;

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file.getFD()), StandardCharsets.UTF_8))) {

                while (currentBytePosition < startPosition && (currentLine = reader.readLine()) != null ) {
                    currentBytePosition += currentLine.getBytes(StandardCharsets.UTF_8).length + System.lineSeparator().getBytes(StandardCharsets.UTF_8).length;
                    previousLines.add(currentLine);
                }


                while (currentBytePosition <= endPosition && (currentLine = reader.readLine()) != null) {
                    long tmp = currentLine.getBytes(StandardCharsets.UTF_8).length + System.lineSeparator().getBytes(StandardCharsets.UTF_8).length;
                    currentBytePosition += tmp;
                    readedBytes += tmp;
                    postLines.add(currentLine);
                    amountStringInFragment++;

                }

                logger.addBytes(readedBytes);
                logger.log();

                for (int i = 0; i < lineCountBeforeAfter && (currentLine = reader.readLine()) != null; i++) {
                    postLines.add(currentLine);
                }
            }


            for (int k = 0; k < amountStringInFragment && !postLines.isEmpty(); k++) {
                String line = postLines.getFirst().toLowerCase();
                if (line.contains(keyWord.toLowerCase())) {

                    Iterator<String> it = previousLines.descendingIterator();

                    List<String> tmp = new ArrayList<>();
                    for (int i = 0; i < lineCountBeforeAfter && it.hasNext(); i++) {
                        tmp.add(it.next());
                    }

                    Collections.reverse(tmp);

                    lines.addAll(tmp);

                    Iterator<String> it2 = postLines.iterator();

                    for (int i = 0; i < lineCountBeforeAfter + 1 && it2.hasNext(); i++) {
                        lines.add(it2.next());
                    }

                }
                previousLines.add(postLines.removeFirst());
            }
        }

        return lines;
    }


    private long moveUpLines(RandomAccessFile file, long currentPosition, int nLines) throws IOException {

        file.seek(currentPosition);
        int linesRead = 0;

        if (nLines == 0) {
            long position;
            int currentCharacter = file.read();

            while (currentCharacter != -1 && currentCharacter != '\n' && currentCharacter != '\r') {
                currentCharacter = file.read();
            }

            position = file.getFilePointer();

            if (currentCharacter == '\r') {
                if ( file.read() != '\n') {
                    return position;
                } else {
                    return file.getFilePointer();
                }
            }

            return position;
        }

        while (linesRead <= nLines + 1 && currentPosition > 0) {
            currentPosition--;
            file.seek(currentPosition);

            int character = file.read();
            if (character == '\n' || character == '\r') {
                linesRead++;
            }

            if (character == '\r') {
                int nextCharacter = file.read();
                if (nextCharacter != '\n') {

                    file.seek(currentPosition);
                }
            }
        }
        return currentPosition;
    }
}
