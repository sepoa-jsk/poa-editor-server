package com.poa.editor.domain.template;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TemplateRepository extends JpaRepository<Template, Long> {

    List<Template> findByFolderIdOrderByOrderIndexAscNameAsc(Long folderId);

    List<Template> findByFolderIdIsNullOrderByOrderIndexAscNameAsc();

    List<Template> findByIsPublicTrueOrderByOrderIndexAscNameAsc();

    List<Template> findAllByOrderByOrderIndexAscNameAsc();

    /** 공용 템플릿 전체 + 특정 사용자의 개인 템플릿 */
    @Query("SELECT t FROM Template t WHERE t.isPublic = true OR t.createdBy = :userId ORDER BY t.orderIndex ASC, t.name ASC")
    List<Template> findVisibleForUser(@Param("userId") String userId);

    @Modifying
    @Query("DELETE FROM Template t WHERE t.folderId = :folderId")
    void deleteByFolderId(Long folderId);

    @Modifying
    @Query("DELETE FROM Template t WHERE t.isTemp = true")
    void deleteAllTemp();
}
