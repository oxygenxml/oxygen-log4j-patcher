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
import java.nio.file.AccessDeniedException;
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
   * A change is a new name and a path to a replacement file. 
   */
  private class Change {    
    private Change(String newName, String pathToReplacement) {
      this.newName = newName;
      this.pathToReplacement = pathToReplacement;
    }
    private String newName;
    private String pathToReplacement;

    /**
     * Gets the new name of the resource.
     * The replacement resource will be copied under this name.
     * This name will be used also in the content references replacements. (like scripts)
     * @return the new name.
     */
    private String getNewName() {
      return newName;
    }
    
    /**
     * The path to the new resource
     * 
     * @return The path.
     */
    private String getPathToReplacement() {
      return pathToReplacement;
    }
  }
  
  /**
   * The log4j version.
   */
  protected static final String NEW_LOG4J_VERSION = "2.16.0";

  /**
   * Installation folder.
   */
  private File                     installFolder;

  /**
   * Maps between a jar file name and its the new name, and the source that will
   * be copied under the new name. The old jar is removed.
   * 
   * The pattern is applied also in the content of the files identified by 
   * {@link #EXTENSIONS_OF_FILES_WITH_REFERENCES}
   */
  private HashMap<Pattern, Change> fileAndContentReplacementMap = new HashMap<>();

  /**
   * The new version of log4j.
   */
  private String newLog4jVersion;

  /**
   * Text replacements that apply only to the third-party-components.xml files.
   */
  private HashMap<Pattern, Change> thirdPartyReplacementMap = new HashMap<>();

  /**
   * Constructor.
   * 
   * @param installFolder
   *          Installation folder.
   * @param newLog4jVersion
   */
  public Patcher(File installFolder, String newLog4jVersion) {
    this.installFolder = installFolder;
    this.newLog4jVersion = newLog4jVersion;
    
    
    fileAndContentReplacementMap.put(Pattern.compile("log4j-core-(.*?).jar"), new Change(
        "log4j-core-" + newLog4jVersion + ".jar",   // For the new name and in content replacements. 
        "lib/log4j-core-" + newLog4jVersion + ".jar" // Bytes source.
    ));
    
    fileAndContentReplacementMap.put(Pattern.compile("log4j-api-(.*?).jar"), new Change(
        "log4j-api-" + newLog4jVersion + ".jar",
        "lib/log4j-api-" + newLog4jVersion + ".jar"
        ));
    
    fileAndContentReplacementMap.put(Pattern.compile("log4j-1.2-api-(.*?).jar"), new Change(
        "log4j-1.2-api-" + newLog4jVersion + ".jar",
        "lib/log4j-1.2-api-" + newLog4jVersion + ".jar"
        ));
    
    fileAndContentReplacementMap.put(Pattern.compile("log4j-slf4j-impl-(.*?).jar"), new Change(
        "log4j-slf4j-impl-" + newLog4jVersion + ".jar",
        "lib/log4j-slf4j-impl-" + newLog4jVersion + ".jar"
        ));

    fileAndContentReplacementMap.put(Pattern.compile("calabash-log4j-core-(.*?).jar"), new Change(
        "calabash-log4j-core-" + newLog4jVersion + ".jar",  
        "lib/log4j-core-" + newLog4jVersion + ".jar"
    ));
    
    fileAndContentReplacementMap.put(Pattern.compile("calabash-log4j-api-(.*?).jar"), new Change(
        "calabash-log4j-api-" + newLog4jVersion + ".jar",
        "lib/log4j-api-" + newLog4jVersion + ".jar"
        ));
    
    fileAndContentReplacementMap.put(Pattern.compile("calabash-log4j-1.2-api-(.*?).jar"), new Change(
        "calabash-log4j-1.2-api-" + newLog4jVersion + ".jar",
        "lib/log4j-1.2-api-" + newLog4jVersion + ".jar"
        ));
    
    
    thirdPartyReplacementMap.put(Pattern.compile("<version>(.*?)</version>([\\r\\n\\s]*)<project-info>([\\r\\n\\s]*)<about>Apache (l|L)og4j"), new Change(
        "<version>" + newLog4jVersion + "</version>\n"
            + "<project-info>\n"
            + "<about>Apache log4j",
        null
    ));
  }

  /**
   * Entry point.
   * 
   * @param args
   *          The first argument is the Oxygen installation folder.
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    String installFolder = System.getenv("OXYGEN_HOME"); 
    if (installFolder == null) {		
    	if (args.length > 0) {
    		installFolder = args[0];
    	} else {
    		System.out.println("No OXYGEN_HOME specified.");
    		System.exit(-1);
    	}    	
    }
    installFolder = installFolder.trim();
    
    String newLog4jVersion = NEW_LOG4J_VERSION;
    if (args.length > 1) {
      newLog4jVersion = args[1];
    }

    try {
      new Patcher(new File(installFolder), newLog4jVersion).scanAndReplaceFiles();
    } catch (AccessDeniedException e) {
      System.out.println();
      System.out.println();
      System.out.println("=============================================================");
      System.out.println("ERROR!");
      System.out.println("You do not have permissions to change the file: " + e.getFile());
      System.out.println("Please run the script with adiministrator priviledges.");
      System.out.println("To do it:");
      if (isWindows()) {
        System.out.println("  1. Press the 'Windows' start button");
        System.out.println("  2. Type 'cmd' ");
        System.out.println("  3. From the right side of the menu choose 'Run as administrator'. ");
        System.out.println("  4. Type cd \"" + new File(".").getCanonicalPath() + "\"  ");
        System.out.println("  5. Run again this script");
      } else if (isMac()) {
        System.out.println("  1. Log in as an user with administrator privileges");
        System.out.println("  2. Start a terminal");
        System.out.println("  4. Type cd \"" + new File(".").getCanonicalPath() + "\"  ");
        System.out.println("  5. Run again this script");        
      } else {
        System.out.println("  1. Start a terminal");
        System.out.println("  2. Type 'sudo -s' and press ENTER.");
        System.out.println("  4. Type cd \"" + new File(".").getCanonicalPath() + "\"  ");
        System.out.println("  5. Run again this script");                
      }

    } catch (IOException e) {
      System.out.println();
      System.out.println();
      System.out.println("=============================================================");
      System.out.println("ERROR!");
      e.printStackTrace(System.out);
    }
  }

  private static boolean isWindows() {
    return String.valueOf(System.getProperties().get("os.name")).toLowerCase().contains("win");
  }
  private static boolean isMac() {
    return String.valueOf(System.getProperties().get("os.name")).toLowerCase().contains("mac");
  }

  /**
   * Scans and replaces log4j jar files with the newer version and also changes
   * the references to the jar files.
   * 
   * @throws IOException
   *           When the replacement failed.
   */
  public void scanAndReplaceFiles() throws IOException {
    System.out.println("Start scanning.");
    scanAndReplaceFiles(installFolder);
    System.out.println("Successfully updated log4j library to version: " + newLog4jVersion);
  }

  private void scanAndReplaceFiles(File folder) throws IOException {
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

  // @formatter:off
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
//@formatter:on

  static boolean canContainLog4jReferences(String fileName) {
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
   * @param file
   *          The file that can contain references.
   * @throws IOException
   *           When the file cannot be accessed.
   */
  private void replaceLog4jReferencesInContent(File file) throws IOException {

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
    }

  }

  private String applyPatterns(HashMap<Pattern, Change> patterns, String newContent) {
    Set<Pattern> keySet = patterns.keySet();
    for (Pattern pattern : keySet) {
      newContent = pattern.matcher(newContent).replaceAll(patterns.get(pattern).getNewName());
    }
    return newContent;
  }

  /**
   * Replaces a jar file with the newer version.
   * 
   * @param toReplace
   *          The file to be replaced by the a new jar file.
   * @throws IOException
   *           When the file cannot be accessed.
   */
  private void replaceLog4jFile(File toReplace) throws IOException {
    Set<Pattern> keySet = fileAndContentReplacementMap.keySet();
    for (Pattern pattern : keySet) {
      if (pattern.matcher(toReplace.getName()).matches()) {
        File copySource = new File(fileAndContentReplacementMap.get(pattern).getPathToReplacement());
        File copyTarget = new File(toReplace.getParentFile(), copySource.getName());

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
          }
        }

      }
    }
  }

}
