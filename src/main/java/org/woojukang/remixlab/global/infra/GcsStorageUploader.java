package org.woojukang.remixlab.global.infra;


import com.google.cloud.WriteChannel;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.woojukang.remixlab.global.config.exception.BaseExceptionEnum;
import org.woojukang.remixlab.global.config.exception.domain.BaseException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.UUID;

@Primary
@Component
@RequiredArgsConstructor
public class GcsStorageUploader implements CloudStorageUploader{

    private final Storage storage;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;


    @Override
    public String upload
            (MultipartFile multipartFile)
            throws IOException {

        if (multipartFile.isEmpty()) {
            return null;
        }

        // 콘텐츠 UUID 지정
        String uuid = UUID.randomUUID().toString();
        // 콘텐츠 타입 설정
        String contentType = multipartFile.getContentType();

        // 업로드 정보 생성
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, uuid)
                .setContentType(contentType)
                .build();

        // 실제 업로드
        storage.create(blobInfo, multipartFile.getBytes());

        return "https://storage.googleapis.com/" + bucketName + "/" + uuid;

    }

    // base64 전용 업로더
    @Override
    public String upload
            (byte[] bytes,
             String contentType) {

        try {
            String uuid = UUID.randomUUID().toString();

            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, uuid)
                    .setContentType(contentType)
                    .build();

            storage.create(blobInfo, bytes);

            return "https://storage.googleapis.com/" + bucketName + "/" + uuid;

        } catch (Exception e) {
            throw new BaseException(BaseExceptionEnum.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public String videoUpload(InputStream inputStream){

        String uuid = UUID.randomUUID().toString();

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName,uuid)
                    .setContentType("video/mp4")
                    .build();

        try (InputStream is = inputStream;
             WriteChannel writer = storage.writer(blobInfo);
             ReadableByteChannel reader = Channels.newChannel(is)) {

            ByteBuffer buffer = ByteBuffer.allocate(8 * 1024);

            while (reader.read(buffer) > 0) {
                buffer.flip();
                writer.write(buffer);
                buffer.clear();
            }

            return "https://storage.googleapis.com/" + bucketName + "/" + uuid;

        } catch (IOException e){
            throw new BaseException(BaseExceptionEnum.FILE_UPLOAD_FAILED);
        }
    }

}
