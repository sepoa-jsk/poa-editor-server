package com.poa.editor.domain.template.dto;

import com.poa.editor.domain.template.TemplateFolder;

import java.time.LocalDateTime;

public record TemplateFolderDto(
        Long id,
        Long parentId,
        String name,
        boolean isPublic,
        int orderIndex,
        String createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TemplateFolderDto from(TemplateFolder folder) {
        return new TemplateFolderDto(
                folder.getId(),
                folder.getParentId(),
                folder.getName(),
                folder.isPublic(),
                folder.getOrderIndex(),
                folder.getCreatedBy(),
                folder.getCreatedAt(),
                folder.getUpdatedAt()
        );
    }
}
