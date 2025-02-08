package org.study.learning_mate.platform;

import jakarta.persistence.Enumerated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PlatformTypeManager {
    private final PlatformRepository platformRepository;
    private List<PlatformType> platformTypes;

    @Autowired
    public PlatformTypeManager(PlatformRepository platformRepository) {
        this.platformRepository = platformRepository;
        this.platformTypes = loadFromDatabase();
    }

    private List<PlatformType> loadFromDatabase() {
        return platformRepository.findAll().stream()
                .map(platform -> new PlatformType(platform.getTitle().toUpperCase(), platform.getTitle()))
                .collect(Collectors.toList());
    }

    public List<PlatformType> getPlatformTypes() {
        return new ArrayList<>(platformTypes);
    }

    public void refreshPlatformTypes() {
        this.platformTypes = loadFromDatabase();
    }

    public static class PlatformType {
        private final String code;
        private final String name;

        private PlatformType(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }    }

    public PlatformTypeManager.PlatformType getPlatformTypeByName(String name) {
        return platformTypes.stream()
                .filter(platformType -> platformType.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Platform not found: " + name));
    }
}
