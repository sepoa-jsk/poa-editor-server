package com.poa.editor.domain.document;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByCreatedByAndIsDeletedFalseOrderByUpdatedAtDesc(String createdBy);

    Optional<Document> findByDocKeyAndIsDeletedFalse(String docKey);
}
