package com.example.demo.modules.file;

import org.springframework.util.StringUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="BLOB_FILE")
@Getter @Setter @NoArgsConstructor
public class BlobFile {
	private static final String EXT_SEPARATOR =".";
	
	@Id private String fileId;
	private String filename;
	private String contentType;
	@Lob @Column(columnDefinition="BLOB") private byte[] data;
	
	public String getFileIdWithExtension() {
		var ext = StringUtils.getFilenameExtension(filename);
		return (ext == null) ? fileId : fileId + EXT_SEPARATOR + ext;
	}
}