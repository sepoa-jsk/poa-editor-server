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

    private static final List<String> TEST_TEMPLATE_NAMES = List.of(
        "이름", "템플릿", "A", "B", "샘플2", "샘플3"
    );

    private static final List<String> TEST_FOLDER_NAMES = List.of(
        "이름", "A", "B", "손자", "자식", "자식1",
        "부모", "삭제 대상", "원래 이름", "개인",
        "내 폴더", "김종무 TEst", "하위 폴더", "개인폴더",
        "새 폴더", "테스트 폴더"
    );

    /**
     * 임시/테스트 데이터 일괄 정리:
     * 1단계: 템플릿 삭제 (FK 제약상 폴더보다 먼저)
     *   - isTemp = true
     *   - name IN (테스트 이름 목록)
     *   - name LIKE 'preview_%'
     *   - name LIKE '임시_%'
     *   - name LIKE '__%'
     * 2단계: 테스트 폴더 삭제 (이름 목록 직접 삭제)
     * 3단계: 기본 폴더(공용 템플릿/내 템플릿) 없으면 재생성
     */
    @Transactional
    public void cleanup() {
        // ── 1단계: 템플릿 삭제 ────────────────────────────────────────────────

        // isTemp 플래그
        templateRepository.deleteAllTemp();

        // 테스트 이름 목록
        templateRepository.deleteByNameIn(TEST_TEMPLATE_NAMES);

        // 접두어 패턴 (LIKE)
        templateRepository.deleteByNameLike("preview_%");
        templateRepository.deleteByNameLike("임시_%");
        templateRepository.deleteByNameLike("__%");

        // ── 2단계: 테스트 폴더 삭제 ──────────────────────────────────────────

        // 이름 목록으로 직접 삭제 (재귀 불필요 — 하위 폴더도 같은 이름 목록에 포함)
        // 단, 하위 항목이 남아있을 수 있으므로 재귀 삭제 방식도 병행
        List<TemplateFolder> testFolders = folderRepository.findByNameIn(TEST_FOLDER_NAMES);
        for (TemplateFolder folder : testFolders) {
            if (folderRepository.existsById(folder.getId())) {
                deleteFolderRecursive(folder.getId());
            }
        }
        // 재귀 삭제 후 남은 이름 항목 직접 삭제 (안전망)
        folderRepository.deleteByNameIn(TEST_FOLDER_NAMES);

        // ── 3단계: 기본 폴더 재생성 ──────────────────────────────────────────
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
