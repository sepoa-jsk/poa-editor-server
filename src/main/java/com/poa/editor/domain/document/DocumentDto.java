package com.poa.editor.domain.document;

import java.time.LocalDateTime;

public record DocumentDto(
        Long id,
        String docKey,
        String title,
        String content,
        String createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    /** 전체 필드 (단건 조회용) */
    public static DocumentDto from(Document doc) {
        return new DocumentDto(
                doc.getId(), doc.getDocKey(), doc.getTitle(), doc.getContent(),
                doc.getCreatedBy(), doc.getCreatedAt(), doc.getUpdatedAt()
        );
    }

    /** content 제외 (목록 조회용) */
    public static DocumentDto summary(Document doc) {
        return new DocumentDto(
                doc.getId(), doc.getDocKey(), doc.getTitle(), null,
                doc.getCreatedBy(), doc.getCreatedAt(), doc.getUpdatedAt()
        );
    }
}
