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

    /** 이름이 특정 접두어로 시작하는 템플릿 일괄 삭제 */
    @Modifying
    @Query("DELETE FROM Template t WHERE t.name LIKE CONCAT(:prefix, '%')")
    void deleteByNameStartingWith(@Param("prefix") String prefix);

    /** 지정된 폴더 ID 목록에 속하는 템플릿 일괄 삭제 */
    @Modifying
    @Query("DELETE FROM Template t WHERE t.folderId IN :folderIds")
    void deleteByFolderIdIn(@Param("folderIds") List<Long> folderIds);

    /** 폴더 내 실제(비임시) 템플릿 수 */
    int countByFolderIdAndIsTempFalse(Long folderId);
}
