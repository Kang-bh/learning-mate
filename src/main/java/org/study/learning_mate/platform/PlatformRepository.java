package org.study.learning_mate.platform;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long> {
    Platform findByUrlPrefix(String urlPrefix);
    @Query("SELECT p FROM Platform p WHERE :url LIKE CONCAT('%', p.urlPrefix, '%')")
    Platform findByUrlContainingPrefix(@Param("url") String url);
}