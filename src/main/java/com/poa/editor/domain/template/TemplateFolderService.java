package com.poa.editor.domain.template;

import com.poa.editor.domain.template.dto.FolderCreateRequest;
import com.poa.editor.domain.template.dto.FolderUpdateRequest;
import com.poa.editor.domain.template.dto.TemplateFolderDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TemplateFolderService {

    private final TemplateFolderRepository folderRepository;
    private final TemplateRepository templateRepository;

    public List<TemplateFolderDto> getAllFolders(String userId) {
        return folderRepository.findVisibleForUser(userId)
                .stream()
                .map(TemplateFolderDto::from)
                .toList();
    }

    @Transactional
    public TemplateFolderDto createFolder(FolderCreateRequest req, String userId) {
        TemplateFolder folder = TemplateFolder.builder()
                .parentId(req.parentId())
                .name(req.name().trim())
                .isPublic(req.isPublic())
                .orderIndex(req.orderIndex())
                .createdBy(req.isPublic() ? null : userId)
                .build();
        return TemplateFolderDto.from(folderRepository.save(folder));
    }

    @Transactional
    public TemplateFolderDto updateFolder(Long id, FolderUpdateRequest req, String userId) {
        TemplateFolder folder = folderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("폴더를 찾을 수 없습니다. id=" + id));
        checkWritePermission(folder.isPublic(), folder.getCreatedBy(), userId);
        folder.setParentId(req.parentId());
        folder.setName(req.name().trim());
        folder.setPublic(req.isPublic());
        folder.setOrderIndex(req.orderIndex());
        return TemplateFolderDto.from(folderRepository.save(folder));
    }

    @Transactional
    public void deleteFolder(Long id, String userId) {
        TemplateFolder folder = folderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("폴더를 찾을 수 없습니다. id=" + id));
        checkWritePermission(folder.isPublic(), folder.getCreatedBy(), userId);
        deleteFolderRecursive(id, userId);
    }

    private void deleteFolderRecursive(Long folderId, String userId) {
        List<TemplateFolder> children = folderRepository.findByParentIdOrderByOrderIndexAscNameAsc(folderId);
        for (TemplateFolder child : children) {
            deleteFolderRecursive(child.getId(), userId);
        }
        templateRepository.deleteByFolderId(folderId);
        folderRepository.deleteById(folderId);
    }

    private void checkWritePermission(boolean isPublic, String createdBy, String userId) {
        if (isPublic) {
            if (!"admin".equals(userId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "공용 폴더는 관리자만 수정/삭제할 수 있습니다.");
            }
        } else {
            if (!userId.equals(createdBy)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 폴더만 수정/삭제할 수 있습니다.");
            }
        }
    }
}
