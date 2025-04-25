package com.jihun.myshop.global.service;

import com.jihun.myshop.global.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import static com.jihun.myshop.global.exception.ErrorCode.*;

@Service
@Slf4j
public class FileStorageService {

    @Value("${file.upload-path}")
    private String uploadPath;

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 이미지 파일을 저장하고 접근 가능한 URL을 반환합니다.
     *
     * @param file 업로드된 이미지 파일
     * @return 저장된 이미지의 URL 경로
     */
    public String storeImage(MultipartFile file) {
        try {
            // 빈 파일 체크
            if (file.isEmpty()) {
                throw new CustomException(FILE_UPLOAD_ERROR, "업로드할 파일이 비어있습니다.");
            }

            // 파일 유효성 검사 (이미지 파일만 허용)
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new CustomException(INVALID_FILE_TYPE, "이미지 파일만 업로드 가능합니다.");
            }

            // 파일명 생성 (UUID + 원본 파일명)
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID() + extension;

            // 저장 경로 생성
            String subDirectory = String.valueOf(System.currentTimeMillis() % 100); // 0-99 하위 디렉토리 분산
            String relativePath = subDirectory + "/" + newFilename;
            Path targetPath = Paths.get(uploadPath, subDirectory);

            // 디렉토리가 없으면 생성
            if (!Files.exists(targetPath)) {
                Files.createDirectories(targetPath);
            }

            // 파일 저장
            Path targetFilePath = targetPath.resolve(newFilename);
            Files.copy(file.getInputStream(), targetFilePath, StandardCopyOption.REPLACE_EXISTING);

            // 접근 가능한 URL 경로 반환
            return uploadDir + "/" + relativePath;

        } catch (IOException ex) {
            log.error("이미지 파일 저장 중 오류 발생: {}", ex.getMessage());
            throw new CustomException(FILE_UPLOAD_ERROR, "이미지 저장 중 오류가 발생했습니다: " + ex.getMessage());
        }
    }

    /**
     * 파일을 삭제합니다.
     *
     * @param fileUrl 삭제할 파일의 URL
     * @return 성공 여부
     */
    public boolean deleteImage(String fileUrl) {
        try {
            if (fileUrl == null || fileUrl.isEmpty()) {
                return false;
            }

            // URL에서 상대 경로 추출
            String relativePath = fileUrl.replace(uploadDir + "/", "");
            Path filePath = Paths.get(uploadPath, relativePath);

            // 파일이 존재하면 삭제
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                return true;
            }

            return false;
        } catch (IOException ex) {
            log.error("이미지 파일 삭제 중 오류 발생: {}", ex.getMessage());
            throw new CustomException(FILE_DELETE_ERROR, "이미지 삭제 중 오류가 발생했습니다: " + ex.getMessage());
        }
    }
}