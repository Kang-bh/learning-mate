package org.study.learning_mate.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import org.study.learning_mate.dto.LectureDTO;
import org.study.learning_mate.lecture.Lecture;
import org.study.learning_mate.post.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public Page<LectureDTO.LectureResponse> toLectureResponseDTOPage(Page<Lecture> lecturePage) {
        List<LectureDTO.LectureResponse> responses = lecturePage.getContent().stream()
                .map(this::toLectureResponseDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, lecturePage.getPageable(), lecturePage.getTotalElements());
    }

    public List<LectureDTO.LectureResponse> toLectureResponseDTOList(List<Lecture> lectureList) {
        List<LectureDTO.LectureResponse> result = new ArrayList<>();
        for (Lecture lecture : lectureList) {
            result.add(toLectureResponseDTO(lecture));
        }

        return result;
    }

}
