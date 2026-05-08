package com.poa.editor.domain.template;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TemplateFolderRepository extends JpaRepository<TemplateFolder, Long> {

    List<TemplateFolder> findByParentIdIsNullOrderByOrderIndexAscNameAsc();

    List<TemplateFolder> findByParentIdOrderByOrderIndexAscNameAsc(Long parentId);

    List<TemplateFolder> findAllByOrderByOrderIndexAscNameAsc();

    /** 공용 폴더 전체 + 특정 사용자의 개인 폴더 */
    @Query("SELECT f FROM TemplateFolder f WHERE f.isPublic = true OR f.createdBy = :userId ORDER BY f.orderIndex ASC, f.name ASC")
    List<TemplateFolder> findVisibleForUser(@Param("userId") String userId);
}
