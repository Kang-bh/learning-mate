package org.study.learning_mate.platform;

import org.springframework.stereotype.Service;
import org.study.learning_mate.dto.PlatformDTO;
import org.study.learning_mate.utils.PlatformMapper;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlatformService {

    private final PlatformRepository platformRepository;
    private final PlatformMapper platformMapper;

    public PlatformService(
            PlatformRepository platformRepository,
            PlatformMapper platformMapper
    ) {
        this.platformRepository = platformRepository;
        this.platformMapper = platformMapper;
    }

    public List<PlatformDTO.PlatformResponse> getPlatforms() {
        List<Platform> platforms = platformRepository.findAll();
        return platformMapper.toPlatformDTOList(platforms); // TODO: 공통으로 사용하는 지 확인 후 private으로 빼기
    }
}
