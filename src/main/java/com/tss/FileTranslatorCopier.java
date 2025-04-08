package com.tss;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class FileTranslatorCopier {

    private static final String SOURCE_FOLDER = "input";
    private static final String DEST_FOLDER = "output";
    private static final String NEW_EXTENSION = "txt";
    private static final String TIMESTAMP = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

    public void run() {

        OpenAIManager openAi = new OpenAIManager();

        try {
            Path sourcePath = Paths.get(SOURCE_FOLDER);
            Path outputRoot = Paths.get(DEST_FOLDER, TIMESTAMP);
            Files.createDirectories(outputRoot);

            Map<Path, String> fileMap = collectAllFiles(sourcePath);

            List<String> baseNames = extractUniqueBaseNames(fileMap);

            List<String> translatedNames = openAi.translateNamesWithChatGPT(baseNames);
            if (translatedNames.size() != baseNames.size()) {
                log.error("Translation mismatch: {} originals vs {} translated", baseNames.size(), translatedNames.size());
                return;
            }

            Map<String, String> nameTranslationMap = mapOriginalToTranslated(baseNames, translatedNames);

            copyTranslatedFiles(fileMap, nameTranslationMap, sourcePath, outputRoot);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<Path, String> collectAllFiles(Path sourceRoot) {
        Map<Path, String> map = new HashMap<>();
        try {
            Files.walk(sourceRoot)
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        String fileName = path.getFileName().toString().replace(" ", "_");
                        map.put(sourceRoot.relativize(path), fileName);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    private List<String> extractUniqueBaseNames(Map<Path, String> fileMap) {
        Set<String> names = new HashSet<>();
        for (String file : fileMap.values()) {
            int dot = file.lastIndexOf('.');
            if (dot != -1) {
                names.add(file.substring(0, dot));
            }
        }
        return new ArrayList<>(names);
    }

    private Map<String, String> mapOriginalToTranslated(List<String> originals, List<String> translated) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < originals.size(); i++) {
            map.put(originals.get(i), translated.get(i));
        }
        return map;
    }

    private void copyTranslatedFiles(Map<Path, String> fileMap,
                                     Map<String, String> translationMap,
                                     Path sourceRoot,
                                     Path outputRoot) throws IOException {

        for (Map.Entry<Path, String> entry : fileMap.entrySet()) {

            Path relativePath = entry.getKey();
            String originalFileName = entry.getValue();
            int dot = originalFileName.lastIndexOf('.');
            String baseName = originalFileName.substring(0, dot);
            String translatedName = translationMap.getOrDefault(baseName, baseName);

            Path sourceFile = sourceRoot.resolve(relativePath);
            Path destinationPath = outputRoot.resolve(relativePath.getParent() == null ? Paths.get("") : relativePath.getParent());
            Files.createDirectories(destinationPath);

            Path outputFile = destinationPath.resolve(translatedName + "." + NEW_EXTENSION);
            Files.copy(sourceFile, outputFile, StandardCopyOption.REPLACE_EXISTING);

            log.info("Copied: {} -> {}", sourceFile.getFileName(), outputFile);
        }
    }

}
