/*
 * Copyright (c) 2021 Syncro Soft SRL - All Rights Reserved.
 *
 * This file contains proprietary and confidential source code.
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 */
package com.oxygenxml.patcher.log4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.List;

public class Patcher {

  static final String STRATEGY_REMOVE_JNDI = "removeJndi";
  static final String STRATEGY_UPGRADE = "upgrade";
  static final String STRATEGY_BOTH = "both";

  static int changes = 0;
  
  /**
   * Entry point.
   * 
   * @param args The first argument is the Oxygen installation folder.
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {

    changes = 0;
    System.out.println("Java version is " + System.getProperty("java.version"));
    
    String folderToProcess = getFolderToProcess(args);
    String strategy = getStrategy(args);

    try {
      if (STRATEGY_UPGRADE.equals(strategy)) {
        changes = new Log4jUpgrader(new File(folderToProcess)).scanFiles();
      } else if (STRATEGY_REMOVE_JNDI.equals(strategy)) {
        changes = new Log4jJndiRemover(new File(folderToProcess)).scanFiles();
      } else if (STRATEGY_BOTH.equals(strategy)) {
        System.out.println("Applying both upgrade, and then removal of Jndi class from the upgraded jars.");
        changes = new Log4jUpgrader(new File(folderToProcess)).scanFiles();
        changes += new Log4jJndiRemover(new File(folderToProcess)).scanFiles();
      } else {
        System.out.println(
            "Unknown patching strategy: '" + strategy + "'. ");
      }

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

  private static String getStrategy(String[] args) {
    String strategy = null;
    List<String> argsList = Arrays.asList(args);
    if (argsList.contains("r")) {
      strategy = STRATEGY_REMOVE_JNDI;
    } else if (argsList.contains("u")) {
      strategy = STRATEGY_UPGRADE;
    } else if (argsList.contains("b")) {
      strategy = STRATEGY_BOTH;
    }
    return strategy;
  }

  private static String getFolderToProcess(String[] args) {
    String installFolder = null;
    if (!isWindows()) {
      // Only on Linux and Mac we pass this environment var.
     System.getenv("OXYGEN_HOME");
    }
    if (installFolder == null) {
      if (args.length > 0) {
        installFolder = args[0];
      } else {
        System.out.println("No OXYGEN_HOME specified.");
        System.exit(-1);
      }
    }
    installFolder = installFolder.trim();
    return installFolder;
  }

  private static boolean isWindows() {
    return String.valueOf(System.getProperties().get("os.name")).toLowerCase().contains("win");
  }

  private static boolean isMac() {
    return String.valueOf(System.getProperties().get("os.name")).toLowerCase().contains("mac");
  }

}
