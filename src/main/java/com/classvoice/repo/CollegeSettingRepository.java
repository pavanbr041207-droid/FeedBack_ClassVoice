package com.classvoice.repo;

import com.classvoice.domain.CollegeSetting;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollegeSettingRepository extends JpaRepository<CollegeSetting, Long> {
    Optional<CollegeSetting> findBySettingKey(String settingKey);
}
