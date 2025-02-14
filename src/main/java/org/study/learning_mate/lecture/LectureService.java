package org.study.learning_mate.lecture;

import jakarta.transaction.Transactional;
import org.hibernate.persister.entity.UniqueKeyEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.study.learning_mate.dto.BookmarkDTO;
import org.study.learning_mate.dto.LectureDTO;
import org.study.learning_mate.platform.Platform;
import org.study.learning_mate.platform.PlatformRepository;
import org.study.learning_mate.platform.PlatformTypeManager.PlatformType;
import org.study.learning_mate.post.Post;
import org.study.learning_mate.post.PostRepository;
import org.study.learning_mate.service.CrawlService;
import org.study.learning_mate.utils.LectureMapper;

import javax.management.InstanceAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;

import static org.study.learning_mate.post.PostType.LECTURE;

@Service
public class LectureService {

    private final LectureRepository lectureRepository;
    private final LectureMapper lectureMapper;
    private final PostRepository postRepository;
    private final CrawlService crawlService;
    private final PlatformRepository platformRepository;

    public LectureService(
            LectureRepository lectureRepository,
            LectureMapper lectureMapper,
            PostRepository postRepository,
            CrawlService crawlService,
            PlatformRepository platformRepository
    ) {
        this.lectureRepository = lectureRepository;
        this.lectureMapper = lectureMapper;
        this.postRepository = postRepository;
        this.crawlService = crawlService;
        this.platformRepository = platformRepository;
    }

    // overloading
    public List<LectureDTO.LectureResponse> getLectures(
        Pageable pageable
    ) {
        Page<Lecture> lectures = lectureRepository.findAll(pageable);
        return lectureMapper.toLectureResponseDTOList(lectures.stream().toList());
    }

    public List<LectureDTO.LectureResponse> getLectures(
            String title,
            Pageable pageable
    ) {
        Page<Lecture> lectures = lectureRepository.findAllByPost_TitleContaining(title, pageable);
        return lectureMapper.toLectureResponseDTOList(lectures.stream().toList());
    }

    public List<LectureDTO.LectureResponse> getLectures(
            PlatformType platform, // enum처리 필수
            Pageable pageable
    ) {
        Page<Lecture> lectures = lectureRepository.findAllByPlatform(platform, pageable);
        return lectureMapper.toLectureResponseDTOList(lectures.stream().toList());
    }

    public List<LectureDTO.LectureResponse> getLectures(
            String title,
            PlatformType platform,
            Pageable pageable
    ) {
        Page<Lecture> lectures = lectureRepository.findAllByPost_TitleContainingAndPlatform(title, platform, pageable);
        return lectureMapper.toLectureResponseDTOList(lectures.stream().toList());
    }

    // todo :
    @Transactional
    public void createLecture(String url) throws InstanceAlreadyExistsException {

        // 이미 존재하는 강의인지 확인 by title
        // Platform check
        //
        Platform platform = platformRepository.findByUrlContainingPrefix(url);

        System.out.println("platform = " + platform);

        String title = crawlService.crawlLectureTitle(url);
        Boolean isExist = postRepository.existsByTitle(title);


        // TODO : ALREADY EXIST ERROR
//        if (isExist) {
//            throw new Conflict("already exist lecture");
//        }


        // TODO :: url prefix extract
//        Platform platform = platformRepository.findByUrlPrefix("urlPrefix");
        // TODO :: 없으면 추가
        // TODO :: ENUM 추가도

        Post post = Post.builder()
                .title(title)
                .content("temp") // todo : summarize with ai
                .postType(LECTURE)
                .build();

        postRepository.save(post);

        Lecture lecture = Lecture.builder()
                .url(url)
                .post(post)
                .platform(platform)
//                .platform("")
                .build();

        lectureRepository.save(lecture);

        return;
    }

    public List<LectureDTO.LectureResponse> findByLectureIds (List<Long> bookmarkIds) {
        List<Lecture> result = lectureRepository.findAllById(bookmarkIds);;
        return lectureMapper.toLectureResponseDTOList(result);
    }
}
