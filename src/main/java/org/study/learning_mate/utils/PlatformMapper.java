package org.study.learning_mate.utils;

import org.springframework.stereotype.Component;
import org.study.learning_mate.dto.PlatformDTO;
import org.study.learning_mate.platform.Platform;

import java.util.ArrayList;
import java.util.List;

@Component
public class PlatformMapper {

    public PlatformDTO.PlatformResponse toPlatformDTO(Platform platform) {
        return PlatformDTO.PlatformResponse.builder()
                .id(platform.getId())
                .url(platform.getUrl())
                .logoUrl(platform.getLogoUrl())
                .title(platform.getTitle())
                .build();
    }

    public List<PlatformDTO.PlatformResponse> toPlatformDTOList(List<Platform> platforms) {
        ArrayList<PlatformDTO.PlatformResponse> platformDTOS = new ArrayList<>();
        for (Platform platform : platforms) {
            platformDTOS.add(toPlatformDTO(platform));
        }

        return platformDTOS;
    }
}
