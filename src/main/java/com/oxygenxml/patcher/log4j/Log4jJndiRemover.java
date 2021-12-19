/*
 * Copyright (c) 2021 Syncro Soft SRL - All Rights Reserved.
 *
 * This file contains proprietary and confidential source code.
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 */
package com.oxygenxml.patcher.log4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Removes potential vulnerable Jndi classes from Log4j.
 */
public class Log4jJndiRemover extends Log4jSearcher {

  /**
   * The folder to process.
   * 
   * @param folderToProcess The root folder to process, typically an Oxygen
   *                        installation.
   */
  public Log4jJndiRemover(File folderToProcess) {
    super(folderToProcess);
  }

  @Override
  protected boolean canContainLog4jReferences(String fileName) {
    // Preserves the same file names. Does not need to change references in files.
    return false;
  }

  @Override
  protected void processLog4jReferencesInContentOfFile(File file) throws IOException {
    // Preserves the same file names. Does not need to change references in files.
  }

  @Override
  protected int processLog4jFile(File file) throws IOException {
    int removed = 0;
    
    File tmp = File.createTempFile("log4Patcher", ".jar");

    try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(tmp));) {

      ZipEntry entry = zis.getNextEntry();
      byte[] buff = new byte[4096];
      while (entry != null) {
        if (entry.getName().endsWith("org/apache/logging/log4j/core/lookup/JndiLookup.class")) {
          System.out.println("Removing JNDI class from " + file);
          // Do not copy it.
          while (zis.read(buff) != -1)
            ;
          removed ++;
        } else {
          zos.putNextEntry(entry);
          int read;
          while ((read = zis.read(buff)) != -1) {
            zos.write(buff, 0, read);
          }
        }
        // Move to next.
        entry = zis.getNextEntry();
      }
    }

    Files.copy(tmp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
    Files.delete(tmp.toPath());

    return removed;
  }

}
