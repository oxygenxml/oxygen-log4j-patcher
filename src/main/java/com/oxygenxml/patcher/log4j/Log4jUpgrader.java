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
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Patches an Oxygen installation, replacing the log4j library.
 */
public class Log4jUpgrader extends Log4jSearcher {

  /**
   * A change is a pair of the new file name and the file that replaces an
   * existing resource.
   */
  private class Replacement {
    private String replacementName;
    private File file;

    private Replacement(String replacementName, File pathToReplacement) {
      this.replacementName = replacementName;
      this.file = pathToReplacement;
    }

    /**
     * Gets the new name of the resource. The replacement resource will be copied
     * under this name. This name will be used also in the content references
     * replacements. (like in the contents of scripts or configuration files)
     * 
     * @return the new name.
     */
    private String getReplacementName() {
      return replacementName;
    }

    /**
     * The path to the new resource
     * 
     * @return The path.
     */
    private File getFile() {
      return file;
    }
  }

  @Override
  public int scanFiles() throws IOException {
    int noOfChanges = super.scanFiles();
    if (noOfChanges > 1) {
      System.out.println("Successfully updated log4j library to version: " + newLog4jVersion);
    } else {
      System.out.println("No changes were made. ");
    }
    return noOfChanges;
  }

  /**
   * The log4j version.
   */
  protected static final String NEW_LOG4J_VERSION = "2.17.0";

  /**
   * Maps between a jar file name and its the new name, and the source that will
   * be copied under the new name. The old jar is removed.
   * 
   * The pattern is applied also in the content of the files identified by
   * {@link #EXTENSIONS_OF_FILES_WITH_REFERENCES}
   */
  private LinkedHashMap<Pattern, Replacement> fileAndContentReplacementMap = new LinkedHashMap<>();

  /**
   * The new version of log4j.
   */
  private String newLog4jVersion;

  /**
   * Text replacements that apply only to the third-party-components.xml files.
   */
  private LinkedHashMap<Pattern, Replacement> thirdPartyReplacementMap = new LinkedHashMap<>();

  /**
   * Constructor.
   * 
   * @param folderToProcess The root folder to process, typically an Oxygen
   *                        installation.
   * @param newLog4jVersion
   */
  public Log4jUpgrader(File folderToProcess) {
    super(folderToProcess);
    this.newLog4jVersion = NEW_LOG4J_VERSION;

    // Match the log4j files, there are three jars.
    fileAndContentReplacementMap.put(Pattern.compile("log4j-core-?(.*?).jar"), new Replacement(
        "log4j-core-" + newLog4jVersion + ".jar",
        new File("lib/log4j-core-" + newLog4jVersion + ".jar")));

    fileAndContentReplacementMap.put(Pattern.compile("log4j-api-?(.*?).jar"), new Replacement(
        "log4j-api-" + newLog4jVersion + ".jar",
        new File("lib/log4j-api-" + newLog4jVersion + ".jar")));

    fileAndContentReplacementMap.put(Pattern.compile("log4j-1.2-api-?(.*?).jar"), new Replacement(
        "log4j-1.2-api-" + newLog4jVersion + ".jar",
        new File("lib/log4j-1.2-api-" + newLog4jVersion + ".jar")));

    fileAndContentReplacementMap.put(Pattern.compile("log4j-slf4j-impl-?(.*?).jar"), new Replacement(
        "log4j-slf4j-impl-" + newLog4jVersion + ".jar",
        new File("lib/log4j-slf4j-impl-" + newLog4jVersion + ".jar")));

    fileAndContentReplacementMap.put(Pattern.compile("log4j-web-?(.*?).jar"), new Replacement(
        "log4j-web-" + newLog4jVersion + ".jar",
        new File("lib/log4j-web-" + newLog4jVersion + ".jar")));

    // Matches the log4j files for the XProc Calabash engine.
    fileAndContentReplacementMap.put(Pattern.compile("calabash-log4j-core-(.*?).jar"), new Replacement(
        "calabash-log4j-core-" + newLog4jVersion + ".jar",
        new File("lib/log4j-core-" + newLog4jVersion + ".jar")));

    fileAndContentReplacementMap.put(Pattern.compile("calabash-log4j-api-(.*?).jar"), new Replacement(
        "calabash-log4j-api-" + newLog4jVersion + ".jar",
        new File("lib/log4j-api-" + newLog4jVersion + ".jar")));

    fileAndContentReplacementMap.put(Pattern.compile("calabash-log4j-1.2-api-(.*?).jar"), new Replacement(
        "calabash-log4j-1.2-api-" + newLog4jVersion + ".jar",
        new File("lib/log4j-1.2-api-" + newLog4jVersion + ".jar")));

    thirdPartyReplacementMap.put(
        Pattern.compile("<version>(.*?)</version>([\\r\\n\\s]*)<project-info>([\\r\\n\\s]*)<about>Apache (l|L)og4j"),
        new Replacement(
            "<version>" + newLog4jVersion + "</version>\n"
                + "<project-info>\n"
                + "<about>Apache log4j",
            null));
  }

  protected static final String[] EXTENSIONS_OF_FILES_WITH_REFERENCES = new String[] {
      ".properties",
      ".conf",
      ".framework",
      ".txt",
      ".list",
      ".bat",
      ".cmd",
      ".sh",
      ".xml" };

  protected boolean canContainLog4jReferences(String fileName) {
    return canContainLog4jReferencesInternal(fileName);
  }

  static boolean canContainLog4jReferencesInternal(String fileName) {
    boolean found = false;
    for (String ext : EXTENSIONS_OF_FILES_WITH_REFERENCES) {
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
  protected int processLog4jReferencesInContentOfFile(File file) throws IOException {
    int changed = 0;
    // Read the content.
    byte[] bytes = Files.readAllBytes(file.toPath());
    String content = new String(bytes, StandardCharsets.UTF_8);

    // Replace all
    String newContent = content;
    newContent = applyPatterns(fileAndContentReplacementMap, newContent);

    if (file.getName().endsWith("third-party-components.xml")) {
      newContent = applyPatterns(thirdPartyReplacementMap, newContent);
    }

    if (!newContent.equals(content)) {
      // Write the content.
      System.out.print("Updating references in: " + file + " .. ");
      Files.write(file.toPath(), newContent.getBytes(StandardCharsets.UTF_8));
      System.out.println("ok.");
      changed = 1;
    }
    
    return changed;
  }

  private String applyPatterns(HashMap<Pattern, Replacement> patterns, String newContent) {
    Set<Pattern> keySet = patterns.keySet();
    for (Pattern pattern : keySet) {
      newContent = pattern.matcher(newContent).replaceAll(patterns.get(pattern).getReplacementName());
    }
    return newContent;
  }

  /**
   * Replaces a jar file with the newer version.
   * 
   * @param toReplace The file to be replaced by the a new jar file.
   * @throws IOException When the file cannot be accessed.
   */
  protected int processLog4jFile(File toReplace) throws IOException {
    int processed = 0;
    Set<Pattern> keySet = fileAndContentReplacementMap.keySet();
    for (Pattern pattern : keySet) {
      if (pattern.matcher(toReplace.getName()).matches()) {
        Replacement replacement = fileAndContentReplacementMap.get(pattern);
        File copySource = replacement.getFile();
        File copyTarget = new File(toReplace.getParentFile(), replacement.getReplacementName());

        if (copyTarget.equals(toReplace)) {
          System.out.println("Already updated: " + copyTarget);
        } else {
          if (toReplace.exists()) {
            Files.delete(toReplace.toPath());
          }

          if (!copyTarget.exists()) {
            System.out.print("Updating: " + toReplace + " with " + copySource + " .. ");
            Files.copy(copySource.toPath(), copyTarget.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("ok.");
            processed ++;
          }
        }
      }
    }
    return processed;
  }

}
