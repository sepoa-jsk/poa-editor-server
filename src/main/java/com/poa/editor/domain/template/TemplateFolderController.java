package com.poa.editor.domain.template;

import com.poa.editor.common.ApiResponse;
import com.poa.editor.config.AdminConfig;
import com.poa.editor.domain.template.dto.FolderCreateRequest;
import com.poa.editor.domain.template.dto.FolderUpdateRequest;
import com.poa.editor.domain.template.dto.TemplateFolderDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/folders")
@RequiredArgsConstructor
public class TemplateFolderController {

    private final TemplateFolderService folderService;
    private final AdminConfig adminConfig;

    @GetMapping
    public ApiResponse<List<TemplateFolderDto>> getFolders(
            @RequestHeader(value = "Poa-User-Id", defaultValue = "anonymous") String userId) {
        return ApiResponse.ok(folderService.getAllFolders(userId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TemplateFolderDto> createFolder(
            @Valid @RequestBody FolderCreateRequest req,
            @RequestHeader(value = "Poa-User-Id", defaultValue = "anonymous") String userId) {
        if (req.isPublic() && !adminConfig.isAdmin(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "공용 폴더는 관리자만 생성할 수 있습니다.");
        }
        return ApiResponse.ok(folderService.createFolder(req, userId));
    }

    @PutMapping("/{id}")
    public ApiResponse<TemplateFolderDto> updateFolder(
            @PathVariable Long id,
            @Valid @RequestBody FolderUpdateRequest req,
            @RequestHeader(value = "Poa-User-Id", defaultValue = "anonymous") String userId) {
        return ApiResponse.ok(folderService.updateFolder(id, req, userId));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFolder(
            @PathVariable Long id,
            @RequestHeader(value = "Poa-User-Id", defaultValue = "anonymous") String userId) {
        folderService.deleteFolder(id, userId);
    }
}
