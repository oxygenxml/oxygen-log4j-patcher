/*
 * Copyright (c) 2021 Syncro Soft SRL - All Rights Reserved.
 *
 * This file contains proprietary and confidential source code.
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 */
package com.oxygenxml.patcher.log4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

public class PatcherTest {
  private Path rootPath = Paths.get("test");

  @Before
  public void setUp() throws IOException {
    deleteTestDir();
    fillTestDir();
  }

  private void deleteTestDir() throws IOException {
    try (Stream<Path> walk = Files.walk(rootPath)) {
      walk.sorted(Comparator.reverseOrder()).map(Path::toFile).peek(System.out::println).forEach(File::delete);
    }
  }

  private void fillTestDir() throws IOException {
    new File("test/lib1/lib2/lib3/lib4").mkdirs();

    try (Stream<Path> walk = Files.walk(rootPath)) {
      walk.sorted(Comparator.naturalOrder()).map(Path::toFile).peek(System.out::println).forEach((f) -> {
        try {
          System.out.println("Creating log4j target files in folder: " + f);
          touch(f, "log4j-core-2.14.0.jar");
          touch(f, "log4j-api-2.14.0.jar");
          touch(f, "log4j-1.2-api-2.14.0.jar");

          for (String ext : Patcher.FILES_WITH_REFS_EXTENSIONS) {
            touch(f, "some" + ext, "Library references log4j-core-2.14.0.jar;log4j-api-2.14.0.jar;log4j-1.2-api-2.14.0.jar");
          }

        } catch (IOException e) {
          fail(e.getMessage());
        }
      });
    }
  }

  private void checkTestDir() throws IOException {
    try (Stream<Path> walk = Files.walk(rootPath)) {
      walk.sorted(Comparator.naturalOrder()).map(Path::toFile).peek(System.out::println).forEach((f) -> {
        System.out.println("Checking: " + f);
        if (f.isDirectory()) {
          assertFalse(new File(f, "log4j-core-2.14.0.jar").exists());
          assertFalse(new File(f, "log4j-api-2.14.0.jar").exists());
          assertFalse(new File(f, "log4j-1.2-api-2.14.0.jar").exists());
          assertTrue(new File(f, "log4j-core-2.16.0.jar").exists());
          assertTrue(new File(f, "log4j-api-2.16.0.jar").exists());
          assertTrue(new File(f, "log4j-1.2-api-2.16.0.jar").exists());
        } else {
          if (Patcher.canContainLog4jReferences(f.getName())) {
            try {
              byte[] bytes;
              bytes = Files.readAllBytes(f.toPath());
              String content = new String(bytes, StandardCharsets.UTF_8);
              assertEquals("Library references log4j-core-2.16.0.jar;log4j-api-2.16.0.jar;log4j-1.2-api-2.16.0.jar", content);
            } catch (IOException e) {
              fail(e.getMessage());
            }
          }
        }
      });
    }
  }

  private void touch(File folder, String fileName) throws IOException {
    touch(folder, fileName, "a");
  }

  private void touch(File folder, String fileName, String content) throws IOException {
    FileOutputStream outputStream = new FileOutputStream(new File(folder, fileName));
    outputStream.write(content.getBytes(StandardCharsets.UTF_8));
    outputStream.close();
  }

  @Test
  public void testPatcher() throws IOException {
    Patcher patcher = new Patcher(rootPath.toFile());
    patcher.scanAndReplaceFiles();
    checkTestDir();

  }

}
