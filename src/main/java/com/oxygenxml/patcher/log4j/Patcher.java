package com.oxygenxml.patcher.log4j;
/*
 * Copyright (c) 2021 Syncro Soft SRL - All Rights Reserved.
 *
 * This file contains proprietary and confidential source code.
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 */

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Patches an Oxygen installation, replacing the log4j library.
 */
public class Patcher {

  /**
   * Installation folder. 
   */
  private File                   installFolder;

  /**
   * Maps between a log4j jar file name and the new name.
   */
  private HashMap<Pattern, String> replacementMap = new HashMap<>();

  /**
   * Constructor.
   * 
   * @param installFolder Installation folder.
   */
  public Patcher(File installFolder) {
    this.installFolder = installFolder;
    replacementMap.put(Pattern.compile("log4j-core-(.*?).jar"), "log4j-core-2.16.0.jar");
    replacementMap.put(Pattern.compile("log4j-api-(.*?).jar"), "log4j-api-2.16.0.jar");
    replacementMap.put(Pattern.compile("log4j-1.2-api-(.*?).jar"), "log4j-1.2-api-2.16.0.jar");
  }

  /**
   * Entry point.
   * 
   * @param args
   *          The first argument is the Oxygen installation folder.
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    String installFolder = args[0];
    new Patcher(new File(installFolder)).scanAndReplaceFiles();
  }

  /**
   * Scans and replaces log4j jar files with the newer version 
   * and also changes the references to the jar files.
   * 
   * @throws IOException When the replacement failed.
   */
  public void scanAndReplaceFiles() throws IOException {
    System.out.println("Start scanning.");
    scanAndReplaceFiles(installFolder);
    System.out.println("End scan.");
  }

  private void scanAndReplaceFiles(File folder) throws IOException {
    System.out.println(folder);
    File[] files = folder.listFiles();

    for (File file : files) {
      if (file.isDirectory()) {
        scanAndReplaceFiles(file);
      } else {
        String fileName = file.getName();
        if (fileName.contains("log4j")) {
          replaceLog4jFile(file);
        } else if (canContainLog4jReferences(fileName)) {
          replaceLog4jReferencesInContent(file);
        }
      }
    }
  }

  public static final String[] FILES_WITH_REFS_EXTENSIONS = new String[] {
          ".properties", 
          ".conf",
          ".framework",
          ".txt",
          ".list",
          ".bat",
          ".cmd",
          ".sh",
          ".xml"};
      
  
  static boolean canContainLog4jReferences(String fileName) {
    boolean found = false;
    for (String ext : FILES_WITH_REFS_EXTENSIONS) {
      if (fileName.endsWith(ext)) {
        found = true;
        break;
      }
    }
    return found;
    
  }

  /**
   * Replaces the references to the log4j libraries.
   * 
   * @param file The file that can contain references.
   * @throws IOException When the file cannot be accessed. 
   */
  private void replaceLog4jReferencesInContent(File file) throws IOException {

    // Read the content.
    byte[] bytes = Files.readAllBytes(file.toPath());
    String content = new String(bytes, StandardCharsets.UTF_8);
    
    
    // Replace all 
    Set<Pattern> keySet = replacementMap.keySet();
    String newContent = content;
    for (Pattern pattern : keySet) {
      newContent = pattern.matcher(newContent).replaceAll(replacementMap.get(pattern));      
    }
    if (!newContent.equals(content)) {
      // Write the content.
      System.out.println("Updating references in: " + file + " .. ");
      Files.write(file.toPath(), newContent.getBytes(StandardCharsets.UTF_8));
      System.out.print("ok.");
    } 
    
  }

  /**
   * Replaces a jar file with the newer version.
   * 
   * @param toReplace The file to be replaced by the a new jar file.
   * @throws IOException When the file cannot be accessed. 
   */
  private void replaceLog4jFile(File toReplace) throws IOException {
    Set<Pattern> keySet = replacementMap.keySet();
    for (Pattern pattern : keySet) {
      if (pattern.matcher(toReplace.getName()).matches()) {
        File replacement = new File("lib", replacementMap.get(pattern));
        System.out.print("Updating: " + toReplace + " with " + replacement + " .. ");
        Files.copy(replacement.toPath(), new File(toReplace.getParentFile(), replacement.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
        Files.delete(toReplace.toPath());
        System.out.print("ok.");
      }
    }
  }

}
