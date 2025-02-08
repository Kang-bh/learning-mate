package org.study.learning_mate.demandlecture;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemandLectureRepository extends JpaRepository<DemandLecture, Long> {

}
