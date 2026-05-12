package com.poa.editor.domain.template;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final TemplateFolderRepository folderRepository;
    private final TemplateRepository templateRepository;

    private static final List<String> PROTECTED_FOLDERS = List.of("공용 템플릿", "내 템플릿");

    /**
     * 임시/테스트 데이터 일괄 정리:
     * 1단계: 기본 폴더(공용 템플릿/내 템플릿) 외 모든 폴더에 속한 템플릿 삭제
     * 2단계: isTemp/preview_/임시_/__ 패턴 템플릿 삭제 (기본 폴더 포함)
     * 3단계: 기본 폴더 외 모든 폴더 삭제
     * 4단계: 기본 폴더 없으면 재생성
     */
    @Transactional
    public void cleanup() {
        // ── 1단계: 비기본 폴더 소속 템플릿 삭제 ─────────────────────────────
        List<Long> nonDefaultFolderIds = folderRepository.findAll().stream()
                .filter(f -> !PROTECTED_FOLDERS.contains(f.getName()))
                .map(TemplateFolder::getId)
                .toList();
        if (!nonDefaultFolderIds.isEmpty()) {
            templateRepository.deleteByFolderIdIn(nonDefaultFolderIds);
        }

        // ── 2단계: isTemp/패턴 템플릿 삭제 (기본 폴더 포함) ─────────────────
        templateRepository.deleteAllTemp();
        templateRepository.deleteByNameLike("preview_%");
        templateRepository.deleteByNameLike("임시_%");
        templateRepository.deleteByNameLike("__%");

        // ── 3단계: 비기본 폴더 전체 삭제 ─────────────────────────────────────
        if (!nonDefaultFolderIds.isEmpty()) {
            folderRepository.deleteAllById(nonDefaultFolderIds);
        }

        // ── 4단계: 기본 폴더 재생성 ──────────────────────────────────────────
        ensureDefaultFolders();
    }

    private void ensureDefaultFolders() {
        if (!folderRepository.existsByName("공용 템플릿")) {
            folderRepository.save(TemplateFolder.builder()
                .name("공용 템플릿").isPublic(true).orderIndex(0).build());
        }
        if (!folderRepository.existsByName("내 템플릿")) {
            folderRepository.save(TemplateFolder.builder()
                .name("내 템플릿").isPublic(false).orderIndex(1).build());
        }
    }
}
