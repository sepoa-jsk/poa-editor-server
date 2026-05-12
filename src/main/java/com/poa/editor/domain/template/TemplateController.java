package com.poa.editor.domain.template;

import com.poa.editor.common.ApiResponse;
import com.poa.editor.config.AdminConfig;
import com.poa.editor.domain.template.dto.TemplateCreateRequest;
import com.poa.editor.domain.template.dto.TemplateDto;
import com.poa.editor.domain.template.dto.TemplateUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;
    private final AdminConfig adminConfig;

    @GetMapping
    public ApiResponse<List<TemplateDto>> getTemplates(
            @RequestParam(required = false) Long folderId,
            @RequestHeader(value = "Poa-User-Id", defaultValue = "anonymous") String userId) {
        return ApiResponse.ok(templateService.getTemplates(folderId, userId));
    }

    @GetMapping("/{id}")
    public ApiResponse<TemplateDto> getTemplate(@PathVariable Long id) {
        return ApiResponse.ok(templateService.getTemplate(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TemplateDto> createTemplate(
            @Valid @RequestBody TemplateCreateRequest req,
            @RequestHeader(value = "Poa-User-Id", defaultValue = "anonymous") String userId) {
        if (req.isPublic() && !adminConfig.isAdmin(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "공용 템플릿은 관리자만 생성할 수 있습니다.");
        }
        return ApiResponse.ok(templateService.createTemplate(req, userId));
    }

    @PutMapping("/{id}")
    public ApiResponse<TemplateDto> updateTemplate(
            @PathVariable Long id,
            @Valid @RequestBody TemplateUpdateRequest req,
            @RequestHeader(value = "Poa-User-Id", defaultValue = "anonymous") String userId) {
        return ApiResponse.ok(templateService.updateTemplate(id, req, userId));
    }

    @DeleteMapping("/temp")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTempTemplates() {
        templateService.deleteTempTemplates();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTemplate(
            @PathVariable Long id,
            @RequestHeader(value = "Poa-User-Id", defaultValue = "anonymous") String userId) {
        templateService.deleteTemplate(id, userId);
    }
}
