package org.study.learning_mate.utils;

import org.springframework.stereotype.Component;
import org.study.learning_mate.dto.LectureDTO;
import org.study.learning_mate.lecture.Lecture;
import org.study.learning_mate.post.Post;

import java.util.ArrayList;
import java.util.List;

@Component
public class LectureMapper {

    private final PlatformMapper platformMapper;

    public LectureMapper(PlatformMapper platformMapper) {
        this.platformMapper = platformMapper;
    }

    public LectureDTO.LectureResponse toLectureResponseDTO(Lecture lecture) {
        return LectureDTO.LectureResponse.builder()
                .id(lecture.getId())
                .title(lecture.getPost().getTitle())
                .url(lecture.getUrl())
                .description(lecture.getPost().getContent())
                .likes(lecture.getPost().getLikeCounts())
                .comments(lecture.getPost().getCommentCount())
                .dislikes(lecture.getDislikeCounts())
                .views(lecture.getPost().getViewCounts())
                .platform(platformMapper.toPlatformDTO(lecture.getPlatform()))
                .build();
    }

    public List<LectureDTO.LectureResponse> toLectureResponseDTOList(List<Lecture> lectures) {
        List<LectureDTO.LectureResponse> responses = new ArrayList<>();

        for (Lecture lecture : lectures) {
            responses.add(toLectureResponseDTO(lecture));
        }

        return responses;
    }
}
