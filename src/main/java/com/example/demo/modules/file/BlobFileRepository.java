package com.example.demo.modules.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlobFileRepository extends JpaRepository<BlobFile, String> {}
