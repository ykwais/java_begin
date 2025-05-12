package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IDirectorySizeChecker;
import ru.mai.lessons.rpks.exception.DirectoryAccessException;

import java.io.File;

public class DirectorySizeChecker implements IDirectorySizeChecker {
  @Override
  public String checkSize(String directoryName) throws DirectoryAccessException {
    if (directoryName.isEmpty() ) {
      throw new DirectoryAccessException("Directory name is empty");
    }

    File directoryCurrent = new File("src/test/resources/" + directoryName);

    if (!directoryCurrent.exists()) {
      throw new DirectoryAccessException("Directory does not exist");
    }

    if (!directoryCurrent.isDirectory()) {
      throw new DirectoryAccessException("Your path is not a directory");
    }

    File[] filesCurrentDirectory = directoryCurrent.listFiles();

    long summarySizeOfDirectory = getSizeDirectory(filesCurrentDirectory);

    if (summarySizeOfDirectory == 0) {
      return "0 bytes";
    }

    long sizeInMega = summarySizeOfDirectory / (1024 * 1024);

    long sizeInGiga = sizeInMega / 1024;

    StringBuilder resultString = new StringBuilder(directoryName);

    resultString.append(" ---- ").append(summarySizeOfDirectory).append(" bytes / ").append(sizeInMega).append(" MB / ").append(sizeInGiga).append(" GB");

    return resultString.toString(); // реализовать проверку
  }

  private long getSizeDirectory(File[] currentFilesInDirectory) {
    long summarySizeOfDirectory = 0;

    if (currentFilesInDirectory == null) {
      return 0;
    }

    for (File file : currentFilesInDirectory) {
      if (file.isDirectory()) {

        File[] filesOfInnerDirectory = file.listFiles();

        summarySizeOfDirectory += getSizeDirectory(filesOfInnerDirectory);

      } else {

        summarySizeOfDirectory += file.length();

      }
    }

    return summarySizeOfDirectory;
  }
}
