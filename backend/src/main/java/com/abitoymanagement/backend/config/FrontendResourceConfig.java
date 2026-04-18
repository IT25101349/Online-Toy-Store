package com.abitoymanagement.backend.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FrontendResourceConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(FrontendResourceConfig.class);
    private static final List<Path> FRONTEND_DIRECTORY_CANDIDATES = List.of(
            Path.of("frontend"),
            Path.of("../frontend"));

    private final String frontendResourceLocation;

    public FrontendResourceConfig() {
        this.frontendResourceLocation = resolveFrontendResourceLocation();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (frontendResourceLocation == null) {
            return;
        }

        registry.addResourceHandler("/**")
                .addResourceLocations(frontendResourceLocation)
                .setCachePeriod(0);
    }

    private String resolveFrontendResourceLocation() {
        for (Path candidate : FRONTEND_DIRECTORY_CANDIDATES) {
            Path normalizedCandidate = candidate.toAbsolutePath().normalize();
            if (Files.isDirectory(normalizedCandidate) && Files.exists(normalizedCandidate.resolve("index.html"))) {
                String location = normalizedCandidate.toUri().toString();
                return location.endsWith("/") ? location : location + "/";
            }
        }

        logger.warn("Frontend directory not found. Checked locations: {}", FRONTEND_DIRECTORY_CANDIDATES);
        return null;
    }
}
