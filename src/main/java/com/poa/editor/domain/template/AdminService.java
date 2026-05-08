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

    private static final List<String> TEST_FOLDER_NAMES = List.of(
        "손자", "자식", "자식1", "A", "개인",
        "부모", "삭제 대상", "원래 이름", "이름", "B",
        "새 폴더", "하위 폴더", "테스트 폴더"
    );

    /**
     * 임시/테스트 데이터 일괄 정리:
     * 1. isTemp 템플릿 삭제
     * 2. 임시_/preview_ 접두어 템플릿 삭제
     * 3. 테스트 폴더 및 하위 항목 재귀 삭제
     * 4. 기본 폴더(공용 템플릿/내 템플릿) 없으면 재생성
     */
    @Transactional
    public void cleanup() {
        // 1단계: isTemp 플래그 템플릿 삭제
        templateRepository.deleteAllTemp();

        // 2단계: 임시_/preview_ 접두어 템플릿 삭제
        templateRepository.deleteByNameStartingWith("임시_");
        templateRepository.deleteByNameStartingWith("preview_");

        // 3단계: 테스트 폴더 및 하위 항목 재귀 삭제
        List<TemplateFolder> testFolders = folderRepository.findByNameIn(TEST_FOLDER_NAMES);
        for (TemplateFolder folder : testFolders) {
            if (folderRepository.existsById(folder.getId())) {
                deleteFolderRecursive(folder.getId());
            }
        }

        // 4단계: 기본 폴더 없으면 재생성
        ensureDefaultFolders();
    }

    private void deleteFolderRecursive(Long folderId) {
        List<TemplateFolder> children =
            folderRepository.findByParentIdOrderByOrderIndexAscNameAsc(folderId);
        for (TemplateFolder child : children) {
            deleteFolderRecursive(child.getId());
        }
        templateRepository.deleteByFolderId(folderId);
        folderRepository.deleteById(folderId);
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
