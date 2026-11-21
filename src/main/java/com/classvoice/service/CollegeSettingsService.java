package com.classvoice.service;

import com.classvoice.domain.CollegeSetting;
import com.classvoice.repo.CollegeLogoRepository;
import com.classvoice.repo.CollegeSettingRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CollegeSettingsService {

    private final CollegeSettingRepository repository;
    private final CollegeLogoRepository logos;
    private final AuditService audit;

    public CollegeSettingsService(CollegeSettingRepository repository,
                                  CollegeLogoRepository logos,
                                  AuditService audit) {
        this.repository = repository;
        this.logos = logos;
        this.audit = audit;
    }

    @Transactional(readOnly = true)
    public String displayName() {
        return repository.findBySettingKey(CollegeSetting.DISPLAY_NAME)
                .map(CollegeSetting::getSettingValue)
                .filter(v -> !v.isBlank())
                .orElse("ClassVoice");
    }

    /** Relative URL with a version so browsers refetch after a change; null when no logo exists. */
    @Transactional(readOnly = true)
    public String logoUrl() {
        List<Instant> stamps = logos.findAllUpdatedAt();
        return stamps.isEmpty() ? null : "/logo?v=" + stamps.get(0).toEpochMilli();
    }

    @Transactional
    public void updateDisplayName(Long actorUserId, String newName) {
        CollegeSetting setting = repository.findBySettingKey(CollegeSetting.DISPLAY_NAME)
                .orElseThrow(() -> new IllegalStateException("Missing setting " + CollegeSetting.DISPLAY_NAME));
        String old = setting.getSettingValue();
        String cleaned = newName.trim();
        setting.update(cleaned);
        audit.log(actorUserId, "SETTINGS_DISPLAY_NAME_CHANGED", "SETTING", CollegeSetting.DISPLAY_NAME,
                "old=" + old + ";new=" + cleaned);
    }
}
