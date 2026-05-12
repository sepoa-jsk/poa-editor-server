package com.poa.editor.domain.document;

import com.poa.editor.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping
    public ApiResponse<List<DocumentDto>> getDocuments(
            @RequestHeader(value = "Poa-User-Id", defaultValue = "anonymous") String userId) {
        return ApiResponse.ok(documentService.getDocuments(userId));
    }

    @GetMapping("/{docKey}")
    public ApiResponse<DocumentDto> getDocument(
            @PathVariable String docKey,
            @RequestHeader(value = "Poa-User-Id", defaultValue = "anonymous") String userId) {
        return ApiResponse.ok(documentService.getDocument(docKey, userId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<DocumentDto> createDocument(
            @Valid @RequestBody DocumentCreateRequest req,
            @RequestHeader(value = "Poa-User-Id", defaultValue = "anonymous") String userId) {
        return ApiResponse.ok(documentService.createDocument(userId, req.title(), req.content()));
    }

    @PutMapping("/{docKey}")
    public ApiResponse<DocumentDto> updateDocument(
            @PathVariable String docKey,
            @Valid @RequestBody DocumentUpdateRequest req,
            @RequestHeader(value = "Poa-User-Id", defaultValue = "anonymous") String userId) {
        return ApiResponse.ok(documentService.updateDocument(docKey, userId, req.title(), req.content()));
    }

    @DeleteMapping("/{docKey}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDocument(
            @PathVariable String docKey,
            @RequestHeader(value = "Poa-User-Id", defaultValue = "anonymous") String userId) {
        documentService.deleteDocument(docKey, userId);
    }
}
