package com.abitoymanagement.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.storage")
public class FileStorageProperties {

    private String uploadDir = "uploads/toys";
    private String toyDataFile = "data/toys.txt";

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String getToyDataFile() {
        return toyDataFile;
    }

    public void setToyDataFile(String toyDataFile) {
        this.toyDataFile = toyDataFile;
    }
}
