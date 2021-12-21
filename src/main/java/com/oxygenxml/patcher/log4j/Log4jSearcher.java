/*
 * Copyright (c) 2021 Syncro Soft SRL - All Rights Reserved.
 *
 * This file contains proprietary and confidential source code.
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 */
package com.oxygenxml.patcher.log4j;

import java.io.File;
import java.io.IOException;

public abstract class Log4jSearcher {
  
  /**
   * The root folder to process, typically an Oxygen installation.
   */
  private File folderToProcess;

  /**
   * The folder to process.
   * 
   * @param folderToProcess The root folder to process, typically an Oxygen installation.
   */
  public Log4jSearcher(File folderToProcess) {
    this.folderToProcess = folderToProcess;    
  }

  /**
   * Scans and replaces log4j jar files with the newer version and also changes
   * the references to the jar files.
   * 
   * @throws IOException When the replacement failed.
   */
  public int scanFiles() throws IOException {
    System.out.println("Start scanning: " + folderToProcess.getAbsolutePath());
    int noOfChanges = scanFiles(folderToProcess);
    System.out.println("Performed " + noOfChanges + " changes.");
    return noOfChanges;
  }

  private int scanFiles(File folder) throws IOException {
    int noOfChanges = 0;
    if(folder.isDirectory()) {
      File[] files = folder.listFiles();
      if(files != null) {
        for (File file : files) {
          if (file.isDirectory()) {
            noOfChanges += scanFiles(file);
          } else {
            noOfChanges = processFile(noOfChanges, file);
          }
        }
      } else {
        System.out.println("Cannot list files from " + folder);
      }
    }
    return noOfChanges;
  }

  private int processFile(int noOfChanges, File file) throws IOException {
    String fileName = file.getName();
    if (fileName.contains("log4j") && fileName.endsWith(".jar")) {
      noOfChanges += processLog4jFile(file);
    } else if (canContainLog4jReferences(fileName)) {
      noOfChanges += processLog4jReferencesInContentOfFile(file);
    }
    return noOfChanges;
  }

  protected abstract boolean canContainLog4jReferences(String fileName);

  protected abstract int processLog4jReferencesInContentOfFile(File file) throws IOException;

  protected abstract int processLog4jFile(File file) throws IOException;

}
