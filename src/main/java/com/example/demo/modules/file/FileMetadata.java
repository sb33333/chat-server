package com.example.demo.modules.file;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="FILE")
public class FileMetadata {
    @Id
    private String fileId;
    @Column(name="FILENAME")
    private String filename;
    @Column(name="path")
    private String path;
    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadDate;
}
