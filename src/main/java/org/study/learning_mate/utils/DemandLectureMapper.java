package org.study.learning_mate.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import org.study.learning_mate.demandlecture.DemandLecture;
import org.study.learning_mate.dto.DemandLectureDTO;
import org.study.learning_mate.post.Post;
import org.study.learning_mate.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DemandLectureMapper {
    private final UserMapper userMapper;

    public DemandLectureMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public DemandLectureDTO.DemandLectureResponse toDemandLectureDTO(DemandLecture demandLecture) {
        Post post = demandLecture.getDemandLecturePK().getPost();

        return DemandLectureDTO.DemandLectureResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .likes(post.getLikeCounts())
                .comments(post.getCommentCounts())
                .views(post.getViewCounts())
                .createTime(demandLecture.getCreatedAt())
                .updateTime(demandLecture.getUpdatedAt())
                .build();
    }

    public List<DemandLectureDTO.DemandLectureResponse> toDemandLectureListDTO(List<DemandLecture> demandLectureList) {
        List<DemandLectureDTO.DemandLectureResponse> responseList = new ArrayList<>();
        for (DemandLecture demandLecture : demandLectureList) {
            responseList.add(toDemandLectureDTO(demandLecture));
        }

        return responseList;
    }

    public DemandLectureDTO.DemandLectureDetailResponse toDemandLectureDetailDTO(DemandLecture demandLecture) {
        Post post = demandLecture.getDemandLecturePK().getPost();
        User user = demandLecture.getDemandLecturePK().getUser();

        return DemandLectureDTO.DemandLectureDetailResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .likes(post.getLikeCounts())
                .comments(post.getCommentCounts())
                .views(post.getViewCounts())
                .createTime(demandLecture.getCreatedAt())
                .updateTime(demandLecture.getUpdatedAt())
                .user(userMapper.toUserProfile(user))
                .build();
    }

    public Page<DemandLectureDTO.DemandLectureDetailResponse> toDemandLectureDetailPageDTO(Page<DemandLecture> demandLecturePage) {
        List<DemandLectureDTO.DemandLectureDetailResponse> responses = demandLecturePage.getContent().stream()
                .map(this::toDemandLectureDetailDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, demandLecturePage.getPageable(), demandLecturePage.getTotalElements());
    }
}
