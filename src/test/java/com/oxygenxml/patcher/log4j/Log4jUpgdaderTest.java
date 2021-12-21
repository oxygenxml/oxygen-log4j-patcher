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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

public class Log4jUpgdaderTest {
  private static final String SOME_FILE_WITH_NO_REFERENCES_XML = "some-file-with-no-references.xml";
  private static final String THIRD_PARTY_COMPONENTS_XML = "third-party-components.xml";
  private Path                rootPath                   = Paths.get("test");

  @Before
  public void setUp() throws IOException {
    deleteTestDir();
    fillTestDir();
  }

  private void deleteTestDir() throws IOException {
    if (rootPath.toFile().exists()) {
      try (Stream<Path> walk = Files.walk(rootPath)) {
        walk.sorted(new Comparator<Path>() {
          @Override
          public int compare(Path o1, Path o2) {
            return -o1.compareTo(o2);
          }
        }).map(new Function<Path, File>() {
          public File apply(Path t) {
            return t.toFile();
          };
        }).forEach(new Consumer<File>() {
          public void accept(File f) {
            f.delete();
          }
        });
      }
    }
  }

  private void fillTestDir() throws IOException {
    new File("test/lib1/lib2/lib3/lib4").mkdirs();

    try (Stream<Path> walk = Files.walk(rootPath)) {
      walk.sorted(new Comparator<Path>() {
        @Override
        public int compare(Path o1, Path o2) {
          return o1.compareTo(o2);
        }
      }).map(new Function<Path, File>() {
        public File apply(Path t) {
          return t.toFile();
        };
      }).forEach(new Consumer<File>() {
        public void accept(File f) {
          try {
            System.out.println("Creating log4j target files in folder: " + f);
            touch(f, "log4j-core-2.14.0.jar");
            touch(f, "log4j-api-2.14.0.jar");
            touch(f, "log4j-1.2-api-2.14.0.jar");

            for (String ext : Log4jUpgrader.EXTENSIONS_OF_FILES_WITH_REFERENCES) {
              touch(f, "some" + ext, "Library references log4j-core-2.14.0.jar;log4j-api-2.14.0.jar;log4j-1.2-api-2.14.0.jar");
            }
            touch(f, SOME_FILE_WITH_NO_REFERENCES_XML, "No references to log jars.");

            touch(f, THIRD_PARTY_COMPONENTS_XML, ""
                + "  <version>x.y.z</version>\n\n"
                + "  <project-info>\n\n"
                + "    <about>Something else...\n"
                + "  <version>2.13.0</version>\n\n"
                + "  <project-info>\n\n"
                + "    <about>Apache Log4j");

          } catch (IOException e) {
            fail(e.getMessage());
          }
        }
      });
    }
  }

  private void checkTestDir() throws IOException {
    try (Stream<Path> walk = Files.walk(rootPath)) {
      walk.sorted().map(new Function<Path, File>() {
        public File apply(Path t) {
          return t.toFile();
        };
      }).forEach(new Consumer<File>() {
        public void accept(File f) {

          System.out.println("Checking: " + f);
          if (f.isDirectory()) {
            assertFalse(new File(f, "log4j-core-2.14.0.jar").exists());
            assertFalse(new File(f, "log4j-api-2.14.0.jar").exists());
            assertFalse(new File(f, "log4j-1.2-api-2.14.0.jar").exists());
            assertTrue(new File(f, "log4j-core-" + Log4jUpgrader.NEW_LOG4J_VERSION + ".jar").exists());
            assertTrue(new File(f, "log4j-api-" + Log4jUpgrader.NEW_LOG4J_VERSION + ".jar").exists());
            assertTrue(new File(f, "log4j-1.2-api-" + Log4jUpgrader.NEW_LOG4J_VERSION + ".jar").exists());
          } else {
            try {
              if (Log4jUpgrader.canContainLog4jReferencesInternal(f.getName()) 
                  && !f.getName().equals(THIRD_PARTY_COMPONENTS_XML)
                  && !f.getName().equals(SOME_FILE_WITH_NO_REFERENCES_XML)
                  ) {
                String content = new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
                assertEquals("Library references log4j-core-" + Log4jUpgrader.NEW_LOG4J_VERSION + ".jar;" +
                "log4j-api-" + Log4jUpgrader.NEW_LOG4J_VERSION + ".jar;" +
                "log4j-1.2-api-" + Log4jUpgrader.NEW_LOG4J_VERSION + ".jar", content);
              }
              if (f.getName().equals(THIRD_PARTY_COMPONENTS_XML)) {
                String content = new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
                assertEquals(
                    "  <version>x.y.z</version>\n" + 
                    "\n" + 
                    "  <project-info>\n" + 
                    "\n" + 
                    "    <about>Something else...\n" + 
                    "  <version>" + Log4jUpgrader.NEW_LOG4J_VERSION + "</version>\n" + 
                    "<project-info>\n" + 
                    "<about>Apache log4j", content);
              }
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
  public void testUpgrader() throws IOException {
    Patcher.main(new String[] {rootPath.toFile().getAbsolutePath(), "u"});
    assertEquals(65, Patcher.changes);
    Patcher.main(new String[] {rootPath.toFile().getAbsolutePath(), "u"});
    assertEquals(0, Patcher.changes);
    Patcher.main(new String[] {rootPath.toFile().getAbsolutePath(), "u"});
    assertEquals(0, Patcher.changes);
    checkTestDir();
  }

}
