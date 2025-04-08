package com.tss;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

@Slf4j
public class OutputCleaner {

    private static final String OUTPUT_FOLDER = "output";

    public void cleanOutputFolder() {
        Path outputPath = Paths.get(OUTPUT_FOLDER);

        if (!Files.exists(outputPath)) {
            System.out.println("Output folder doesn't exist.");
            return;
        }

        try {
            Files.walkFileTree(outputPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
            log.info("Output folder cleaned.");
        } catch (IOException e) {
            log.info("Failed to clean output folder: {}", e.getMessage());
        }
    }
}
