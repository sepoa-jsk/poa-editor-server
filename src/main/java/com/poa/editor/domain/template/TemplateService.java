package com.poa.editor.domain.template;

import com.poa.editor.domain.template.dto.TemplateCreateRequest;
import com.poa.editor.domain.template.dto.TemplateDto;
import com.poa.editor.domain.template.dto.TemplateUpdateRequest;
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
public class TemplateService {

    private final TemplateRepository templateRepository;

    public List<TemplateDto> getTemplates(Long folderId, String userId) {
        if (folderId != null) {
            return templateRepository.findByFolderIdOrderByOrderIndexAscNameAsc(folderId)
                    .stream().map(TemplateDto::summary).toList();
        }
        return templateRepository.findVisibleForUser(userId)
                .stream().map(TemplateDto::summary).toList();
    }

    public TemplateDto getTemplate(Long id) {
        return templateRepository.findById(id)
                .map(TemplateDto::from)
                .orElseThrow(() -> new EntityNotFoundException("템플릿을 찾을 수 없습니다. id=" + id));
    }

    @Transactional
    public TemplateDto createTemplate(TemplateCreateRequest req, String userId) {
        Template template = Template.builder()
                .folderId(req.folderId())
                .name(req.name().trim())
                .content(req.content())
                .isPublic(req.isPublic())
                .isTemp(req.isTemp())
                .orderIndex(req.orderIndex())
                .createdBy(req.isPublic() ? null : userId)
                .build();
        return TemplateDto.from(templateRepository.save(template));
    }

    @Transactional
    public TemplateDto updateTemplate(Long id, TemplateUpdateRequest req, String userId) {
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("템플릿을 찾을 수 없습니다. id=" + id));
        checkWritePermission(template.isPublic(), template.getCreatedBy(), userId);
        template.setFolderId(req.folderId());
        template.setName(req.name().trim());
        if (req.content() != null) template.setContent(req.content());
        template.setPublic(req.isPublic());
        template.setOrderIndex(req.orderIndex());
        return TemplateDto.from(templateRepository.save(template));
    }

    @Transactional
    public void deleteTemplate(Long id, String userId) {
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("템플릿을 찾을 수 없습니다. id=" + id));
        checkWritePermission(template.isPublic(), template.getCreatedBy(), userId);
        templateRepository.deleteById(id);
    }

    @Transactional
    public void deleteTempTemplates() {
        templateRepository.deleteAllTemp();
    }

    private void checkWritePermission(boolean isPublic, String createdBy, String userId) {
        if (isPublic) {
            if (!"admin".equals(userId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "공용 템플릿은 관리자만 수정/삭제할 수 있습니다.");
            }
        } else {
            if (!userId.equals(createdBy)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 템플릿만 수정/삭제할 수 있습니다.");
            }
        }
    }
}
