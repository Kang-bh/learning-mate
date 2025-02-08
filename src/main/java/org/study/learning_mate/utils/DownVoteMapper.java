package org.study.learning_mate.utils;

import org.springframework.stereotype.Component;
import org.study.learning_mate.downvote.DownVote;
import org.study.learning_mate.dto.DownVoteDTO;

import java.util.ArrayList;
import java.util.List;

@Component
public class DownVoteMapper {

    private final UserMapper userMapper;

    public DownVoteMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public DownVoteDTO.DownVoteResponse toDownVoteDTO(DownVote downVote) {
        return DownVoteDTO.DownVoteResponse.builder()
                .id(downVote.getId())
                .user(userMapper.toUserProfile(downVote.getUser()))
                .title(downVote.getTitle())
                .reason(downVote.getReason())
                .likeCount(downVote.getLikeCount())
                .createAt(downVote.getCreatedAt())
                .updateAt(downVote.getUpdatedAt())
                .build();
    }

    public List<DownVoteDTO.DownVoteResponse> toDownVoteListDTO(List<DownVote> downVotes) {
        List<DownVoteDTO.DownVoteResponse> downVoteResponses = new ArrayList<>();

        for (DownVote downVote : downVotes) {
            downVoteResponses.add(toDownVoteDTO(downVote));
        }

        return downVoteResponses;
    }
}
