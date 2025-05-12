package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.ILineFinder;
import ru.mai.lessons.rpks.exception.LineCountShouldBePositiveException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class LineFinder implements ILineFinder {

  private static final long readingFragmentSize = 2048;

  @Override
  public void find(String inputFilename, String outputFilename, String keyWord, int lineCount) throws LineCountShouldBePositiveException {

    if (lineCount < 0) {
      throw new LineCountShouldBePositiveException("Line count should be positive!");
    }

    if (keyWord == null || keyWord.isEmpty()) {
      return;
    }

    Path pathToInputFile = getPath(inputFilename);
    Path pathToOutputFile = getPath(outputFilename);

    int threadsCount = Runtime.getRuntime().availableProcessors();
    ExecutorService executor = Executors.newFixedThreadPool(threadsCount);

    List<Future<List<String>>> futures = new ArrayList<>();

    long currentStartPosition = 0;
    File file = new File(pathToInputFile.toString());
    long totalSizeFile = file.length();
    Logger logger = new Logger(totalSizeFile);

    while (currentStartPosition < totalSizeFile) {
      futures.add(executor.submit(new ReadFileFuture(pathToInputFile, currentStartPosition, currentStartPosition + readingFragmentSize, lineCount, keyWord, logger)));
      currentStartPosition += readingFragmentSize;
    }

    executor.shutdown();

    List<String> result = new ArrayList<>();

    boolean isInterrupted = false;

    for (Future<List<String>> future : futures) {
      try {
        result.addAll(future.get());
      } catch (InterruptedException e) {
        System.err.println("Thread was interrupted!");
        isInterrupted = true;
      } catch (ExecutionException e) {
        System.err.println("Error occurred during task execution: " + e.getCause());
        e.printStackTrace(System.err);
      } finally {
        executor.shutdown();
        try {
          if (!isInterrupted && !executor.awaitTermination(60, TimeUnit.SECONDS)) {
            executor.shutdownNow();
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
              System.err.println("Executor did not terminate in time");
            }
          }
        } catch (InterruptedException e) {
          executor.shutdownNow();
        }
      }
    }

    try (BufferedWriter writer = Files.newBufferedWriter(pathToOutputFile)) {
      for (String res : result) {
        writer.write(res);
        writer.newLine();
      }
    } catch (IOException e) {
      System.err.println("Error writing to the result file: " + e.getMessage());
    }
  }

  private Path getPath(String filename) {
    if (filename == null || filename.isEmpty()) {
      throw new IllegalArgumentException("File path cannot be null or empty!");
    }
      return Path.of(filename);
  }
}
