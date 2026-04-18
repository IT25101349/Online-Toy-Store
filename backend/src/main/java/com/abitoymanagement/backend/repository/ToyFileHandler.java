package com.abitoymanagement.backend.repository;

import com.abitoymanagement.backend.config.FileStorageProperties;
import com.abitoymanagement.backend.exception.StorageException;
import com.abitoymanagement.backend.model.Toy;
import com.abitoymanagement.backend.util.DelimitedTextUtil;
import com.abitoymanagement.backend.util.ToyFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class ToyFileHandler {

    private static final int EXPECTED_FIELD_COUNT = 10;
    private final Path dataFilePath;

    public ToyFileHandler(FileStorageProperties properties) {
        this.dataFilePath = Path.of(properties.getToyDataFile()).normalize().toAbsolutePath();
        initializeStorage();
    }

    public List<Toy> readAllToys() {
        List<Toy> toys = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(dataFilePath, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                toys.add(mapLineToToy(line, lineNumber));
            }
            return toys;
        } catch (IOException exception) {
            throw new StorageException("Failed to read toy data file.", exception);
        }
    }

    public void appendToy(Toy toy) {
        try (BufferedWriter writer = Files.newBufferedWriter(
                dataFilePath,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            writer.write(mapToyToLine(toy));
            writer.newLine();
        } catch (IOException exception) {
            throw new StorageException("Failed to save the toy record.", exception);
        }
    }

    public void overwriteToys(List<Toy> toys) {
        Path tempFilePath = dataFilePath.resolveSibling(dataFilePath.getFileName() + ".tmp");
        try (BufferedWriter writer = Files.newBufferedWriter(
                tempFilePath,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            for (Toy toy : toys) {
                writer.write(mapToyToLine(toy));
                writer.newLine();
            }
        } catch (IOException exception) {
            throw new StorageException("Failed to rewrite the toy data file.", exception);
        }

        try {
            Files.move(tempFilePath, dataFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new StorageException("Failed to replace the toy data file.", exception);
        }
    }

    private void initializeStorage() {
        try {
            Path parentDirectory = dataFilePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            if (Files.notExists(dataFilePath)) {
                Files.createFile(dataFilePath);
            }
        } catch (IOException exception) {
            throw new StorageException("Failed to initialize toy data storage.", exception);
        }
    }

    private Toy mapLineToToy(String line, int lineNumber) {
        List<String> fields = DelimitedTextUtil.split(line);
        if (fields.size() != EXPECTED_FIELD_COUNT) {
            throw new StorageException("Invalid toy record found at line " + lineNumber + ".");
        }

        try {
            return ToyFactory.createToy(
                    fields.get(0),
                    fields.get(1),
                    fields.get(2),
                    fields.get(3),
                    new BigDecimal(fields.get(4)),
                    Integer.parseInt(fields.get(5)),
                    fields.get(6),
                    fields.get(7),
                    fields.get(8),
                    fields.get(9));
        } catch (Exception exception) {
            throw new StorageException("Failed to parse toy record at line " + lineNumber + ".", exception);
        }
    }

    private String mapToyToLine(Toy toy) {
        return DelimitedTextUtil.join(Arrays.asList(
                toy.getToyId(),
                toy.getName(),
                toy.getCategory(),
                toy.getBrand(),
                toy.getPrice().toPlainString(),
                String.valueOf(toy.getQuantity()),
                toy.getAgeGroup(),
                toy.getDescription(),
                toy.getImageFileName(),
                toy.getToyType()
        ));
    }
}
