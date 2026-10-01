package com.library.search.repository;

import com.library.search.model.LibraryDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<LibraryDocument, Long> {
    Optional<LibraryDocument> findByFileName(String fileName);
    List<LibraryDocument> findByCategoryIgnoreCase(String category);
    List<LibraryDocument> findByDocumentTypeIgnoreCase(String documentType);
    long countByDocumentTypeIgnoreCase(String documentType);
}
