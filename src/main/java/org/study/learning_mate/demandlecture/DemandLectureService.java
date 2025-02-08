package org.study.learning_mate.demandlecture;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.study.learning_mate.dto.DemandLectureDTO;
import org.study.learning_mate.post.Post;
import org.study.learning_mate.post.PostRepository;
import org.study.learning_mate.post.PostType;
import org.study.learning_mate.user.User;
import org.study.learning_mate.utils.DemandLectureMapper;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DemandLectureService {

    private final DemandLectureRepository demandLectureRepository;
    private final DemandLectureMapper demandLectureMapper;
    private final PostRepository postRepository;

    public DemandLectureService(
            DemandLectureRepository demandLectureRepository,
            DemandLectureMapper demandLectureMapper,
            PostRepository postRepository) {
        this.demandLectureRepository = demandLectureRepository;
        this.demandLectureMapper = demandLectureMapper;
        this.postRepository = postRepository;
    }

    public List<DemandLectureDTO.DemandLectureResponse> findAllDemandLectureList(Pageable pageable) {
        Page<DemandLecture> demandLectures = demandLectureRepository.findAll(pageable);

        return demandLectureMapper.toDemandLectureListDTO(demandLectures.stream().toList());
    }

    // 날강도 상세 조회
    public DemandLectureDTO.DemandLectureDetailResponse findDemandLectureById(Long demandLectureId) {
        DemandLecture demandLecture = demandLectureRepository.findByPostId(demandLectureId);

        return demandLectureMapper.toDemandLectureDetailDTO(demandLecture);
    }

    // 날강도 생성
    @Transactional
    public void createDemandLecture(DemandLectureDTO.createDemandLectureRequest request, User user) {

        System.out.println("title + " + request.getTitle());
        System.out.println("content + " + request.getContent());

        Post post = Post.builder()
                .postType(PostType.DEMAND_LECTURE)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        Post savedPost = postRepository.save(post);

        DemandLecturePK demandLecturePK = DemandLecturePK.builder()
                .post(savedPost)
                .user(user)
                .build();

        DemandLecture demandLecture = DemandLecture.builder()
                        .demandLecturePK(demandLecturePK)
                        .build();

        demandLectureRepository.save(demandLecture);

        return;
    }

    // 날강도 수정
    @Transactional
    public DemandLectureDTO.DemandLectureDetailResponse updateDemandLecture(
            DemandLectureDTO.updateDemandLectureRequest request,
            User user,
            Long demandLectureId
    ) {
        Post post = postRepository.findById(demandLectureId).orElseThrow(() -> {
            throw new NoSuchElementException("No such demandLecture");
        });

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        postRepository.save(post);

        DemandLecture demandLecture = demandLectureRepository.findByPostId(demandLectureId);

        return demandLectureMapper.toDemandLectureDetailDTO(demandLecture);
    }

    // 날강도 삭제
    @Transactional
    public void deleteDemandLectureById(Long demandLectureId, User user ) throws AccessDeniedException {
        // check 권한
        DemandLecture demandLecture = demandLectureRepository.findByPostId(demandLectureId);

        if (user.getId() != demandLecture.getDemandLecturePK().getUser().getId()) {
            throw new AccessDeniedException("Not your post");
        }

        demandLectureRepository.delete(demandLecture);
        postRepository.deleteById(demandLectureId);
    }
}
