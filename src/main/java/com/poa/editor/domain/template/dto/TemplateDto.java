package com.poa.editor.domain.template.dto;

import com.poa.editor.domain.template.Template;

import java.time.LocalDateTime;

public record TemplateDto(
        Long id,
        Long folderId,
        String name,
        String content,
        boolean isPublic,
        boolean isTemp,
        int orderIndex,
        String createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TemplateDto from(Template template) {
        return new TemplateDto(
                template.getId(),
                template.getFolderId(),
                template.getName(),
                template.getContent(),
                template.isPublic(),
                template.isTemp(),
                template.getOrderIndex(),
                template.getCreatedBy(),
                template.getCreatedAt(),
                template.getUpdatedAt()
        );
    }

    /** content 제외 요약 DTO (목록 조회용) */
    public static TemplateDto summary(Template template) {
        return new TemplateDto(
                template.getId(),
                template.getFolderId(),
                template.getName(),
                null,
                template.isPublic(),
                template.isTemp(),
                template.getOrderIndex(),
                template.getCreatedBy(),
                template.getCreatedAt(),
                template.getUpdatedAt()
        );
    }
}
