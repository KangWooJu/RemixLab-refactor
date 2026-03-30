package org.woojukang.remixlab.global.infra;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface CloudStorageUploader {

    String upload(MultipartFile multipartFile) throws IOException;

    String upload(byte[] bytes, String contentType);

    String videoUpload(InputStream inputStream);
}
