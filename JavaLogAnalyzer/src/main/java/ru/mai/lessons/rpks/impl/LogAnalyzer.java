package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.ILogAnalyzer;
import ru.mai.lessons.rpks.exception.WrongFilenameException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

public class LogAnalyzer implements ILogAnalyzer {

  String prefixForPathForTest = "src/test/resources/";
  long readingFragmentSize = 1024 * 1024;

  @Override
  public List<Integer> analyze(String filename, String deviation) throws WrongFilenameException {

    Path pathToFile = getPath(filename);
    File file = new File(pathToFile.toString());
    long totalSizeFile = file.length();


    int threadsCount = Runtime.getRuntime().availableProcessors();
    ExecutorService executor = Executors.newFixedThreadPool(threadsCount);

    List<Future<List<LogAfterParse>>> futures = new ArrayList<>();

    long currentStartPosition = 0;

    while (currentStartPosition < totalSizeFile) {
      futures.add(executor.submit(new ReadFileFuture(pathToFile, currentStartPosition, currentStartPosition + readingFragmentSize)));
      currentStartPosition += readingFragmentSize;
    }

    executor.shutdown();

    List<LogAfterParse> logs = new ArrayList<>();

    boolean isInterrupted = false;

    for (Future<List<LogAfterParse>> future : futures) {
      try {
        logs.addAll(future.get());
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

    long deviationTime = parseDeviation(deviation);

    return findDeviant(logs, deviationTime);
  }

  private Path getPath(String filename) throws WrongFilenameException {
    if (filename == null || filename.isEmpty()) {
      throw new WrongFilenameException("File path cannot be null or empty!");
    }

    String pathToFile = prefixForPathForTest + filename;

    Path path = Path.of(pathToFile);

    if (!Files.exists(path) || !Files.isRegularFile(path)) {
      throw new WrongFilenameException("File path does not exist or is not a regular file!");
    }

    return path;
  }

  private List<Integer> findDeviant(List<LogAfterParse> logs, long deviationTime) {
    Map<Long, LocalDateTime> startTimes = new HashMap<>();
    Map<Long, LocalDateTime> endTimes = new HashMap<>();
    List<Long> executionTimes = new ArrayList<>();
    List<Integer> deviants = new ArrayList<>();

    for (LogAfterParse log : logs) {
      if (log.isResult) {
        if (endTimes.containsKey(log.id)) {
          throw new IllegalStateException("Query with ID " + log.id + " already has an end time.");
        }

        endTimes.put(log.id, log.dateTime);
      } else {
        if (startTimes.containsKey(log.id)) {
          throw new IllegalStateException("Query with ID " + log.id + " already has a start time.");
        }

        startTimes.put(log.id, log.dateTime);
      }
    }


    for (Map.Entry<Long, LocalDateTime> entry : startTimes.entrySet()) {
      long id = entry.getKey();
      LocalDateTime startTime = entry.getValue();
      LocalDateTime endTime = endTimes.get(id);
      if (endTime != null) {

        if (endTime.isBefore(startTime)) {
          throw new IllegalStateException("Query with ID " + id + " resulted before started!");
        }

        long executionTimeSeconds = startTime.until(endTime, java.time.temporal.ChronoUnit.SECONDS);
        executionTimes.add(executionTimeSeconds);
      } else {
        throw new IllegalStateException("Query with ID " + id + " has no resulted time!");
      }
    }

    if (deviationTime == -1) {
      deviationTime = calculateMeanDeviation(executionTimes);
    }



    long median = calculateMedian(executionTimes);

    for (Map.Entry<Long, LocalDateTime> entry : startTimes.entrySet()) {
      long id = entry.getKey();
      LocalDateTime startTime = entry.getValue();
      LocalDateTime endTime = endTimes.get(id);
      if (endTime != null) {
        long executionTime = startTime.until(endTime, java.time.temporal.ChronoUnit.SECONDS);

        if (Math.abs(executionTime - median) > deviationTime) {
          deviants.add((int) id);
        }
      } else {
        throw new IllegalStateException("Query with ID " + id + " has no resulted time!");
      }
    }

    Collections.sort(deviants);


    return deviants;
  }

  private long calculateMedian(List<Long> executionTimes) {
    Collections.sort(executionTimes);
    int size = executionTimes.size();
    if (size % 2 != 0) {
      return executionTimes.get(size / 2);
    } else {
      return (executionTimes.get(size / 2 - 1) + executionTimes.get(size / 2)) / 2;
    }
  }

  private long parseDeviation(String deviation) {
    long defaultDeviationInSeconds = -1;

    if (deviation == null || deviation.trim().isEmpty()) {
      return defaultDeviationInSeconds;
    }

    deviation = deviation.trim().toLowerCase();


    long deviationInSeconds;
    if (deviation.endsWith("sec")) {
      deviationInSeconds = Long.parseLong(deviation.replace("sec", "").trim());
    } else if (deviation.endsWith("min")) {
      deviationInSeconds = Long.parseLong(deviation.replace("min", "").trim()) * 60;
    } else {
      throw new IllegalArgumentException("Invalid deviation format. Expected 'sec' or 'min'.");
    }

    return deviationInSeconds;
  }

  private long calculateMeanDeviation(List<Long> executionTimes) {

    long sumOfDeviations = 0;

    for (long time : executionTimes) {
      sumOfDeviations += time;
    }

    return sumOfDeviations / executionTimes.size();
  }
}
