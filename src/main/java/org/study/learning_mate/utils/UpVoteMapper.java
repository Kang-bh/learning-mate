package org.study.learning_mate.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import org.study.learning_mate.dto.UpVoteDTO;
import org.study.learning_mate.upvote.UpVote;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UpVoteMapper {

    private final UserMapper userMapper;

    public UpVoteMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public UpVoteDTO.UpVoteResponse toUpVoteResponse(UpVote upVote) {
        return UpVoteDTO.UpVoteResponse.builder()
                .id(upVote.getId())
                .user(userMapper.toUserProfile(upVote.getUser()))
                .likeCount(upVote.getLikeCount())
                .title(upVote.getTitle())
                .reason(upVote.getReason())
                .createTime(upVote.getCreatedAt())
                .updateTime(upVote.getUpdatedAt())
                .build();
    }

    public List<UpVoteDTO.UpVoteResponse> toUpVoteListResponse(List<UpVote> upVoteList) {
        List<UpVoteDTO.UpVoteResponse> upVoteResponseList = new ArrayList<>();
        for (UpVote upVote : upVoteList) {
            upVoteResponseList.add(toUpVoteResponse(upVote));
        }

        return upVoteResponseList;
    }

    public Page<UpVoteDTO.UpVoteResponse> toUpVotePageResponse(Page<UpVote> upVotePage) {
        List<UpVoteDTO.UpVoteResponse> responses = upVotePage.getContent().stream()
                .map(this::toUpVoteResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, upVotePage.getPageable(), upVotePage.getTotalElements());
    }
}
