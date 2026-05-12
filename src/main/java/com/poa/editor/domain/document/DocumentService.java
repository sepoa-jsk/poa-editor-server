package com.poa.editor.domain.document;

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
public class DocumentService {

    private final DocumentRepository documentRepository;

    public List<DocumentDto> getDocuments(String userId) {
        return documentRepository.findByCreatedByAndIsDeletedFalseOrderByUpdatedAtDesc(userId)
                .stream().map(DocumentDto::summary).toList();
    }

    public DocumentDto getDocument(String docKey, String userId) {
        Document doc = documentRepository.findByDocKeyAndIsDeletedFalse(docKey)
                .orElseThrow(() -> new EntityNotFoundException("문서를 찾을 수 없습니다. docKey=" + docKey));
        if (!doc.getCreatedBy().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 문서만 조회할 수 있습니다.");
        }
        return DocumentDto.from(doc);
    }

    @Transactional
    public DocumentDto createDocument(String userId, String title, String content) {
        String safeTitle = (title == null || title.isBlank()) ? "새 문서" : title.trim();
        Document doc = Document.builder()
                .title(safeTitle)
                .content(content)
                .createdBy(userId)
                .build();
        return DocumentDto.from(documentRepository.save(doc));
    }

    @Transactional
    public DocumentDto updateDocument(String docKey, String userId, String title, String content) {
        Document doc = documentRepository.findByDocKeyAndIsDeletedFalse(docKey)
                .orElseThrow(() -> new EntityNotFoundException("문서를 찾을 수 없습니다. docKey=" + docKey));
        if (!doc.getCreatedBy().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 문서만 수정할 수 있습니다.");
        }
        String safeTitle = (title == null || title.isBlank()) ? "새 문서" : title.trim();
        doc.setTitle(safeTitle);
        doc.setContent(content);
        return DocumentDto.from(documentRepository.save(doc));
    }

    @Transactional
    public void deleteDocument(String docKey, String userId) {
        Document doc = documentRepository.findByDocKeyAndIsDeletedFalse(docKey)
                .orElseThrow(() -> new EntityNotFoundException("문서를 찾을 수 없습니다. docKey=" + docKey));
        if (!doc.getCreatedBy().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 문서만 삭제할 수 있습니다.");
        }
        doc.setDeleted(true);
        documentRepository.save(doc);
    }
}
