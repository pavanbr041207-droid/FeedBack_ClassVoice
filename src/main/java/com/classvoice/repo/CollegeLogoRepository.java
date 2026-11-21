package com.classvoice.repo;

import com.classvoice.domain.CollegeLogo;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CollegeLogoRepository extends JpaRepository<CollegeLogo, Long> {

    Optional<CollegeLogo> findFirstByOrderByIdDesc();

    /** Light query: does not load the image bytes. */
    @Query("select l.updatedAt from CollegeLogo l order by l.id desc")
    List<Instant> findAllUpdatedAt();
}
