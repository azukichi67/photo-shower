package com.photoshower.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.photoshower.domain.Image;
import com.photoshower.repository.ImageRepository;

/**
 * Imageのサービスです。
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Value("${photoshower.image-output-path}")
    private String outputPath;

    /**
     * 次の未使用の画像ファイルのデータ（Base64）を返します。
     * @return 未使用の画像ファイルのデータ（Base64）
     * @throws IOException 
     */
    public String getNextUnusedImageData() throws IOException {

        Image image = imageRepository.selectUnusedImage();

        if (image == null) {
            return null;
        }

        int imageId = image.getImageId();
        imageRepository.updateToUsed(imageId);

        Path filePath = Path.of("%s\\%s".formatted(outputPath, String.valueOf(imageId)));
        try (InputStream is = Files.newInputStream(filePath)) {
            return Base64.getEncoder().encodeToString(is.readAllBytes());
        }
    }

    /**
     * 画像を保存します。
     * @param userId ユーザID
     * @param imageImputStream 画像のImputStream
     * @throws IOException
     */
    public void saveImage(String userId, InputStream imageImputStream) throws IOException {

        Image image = imageRepository.insert(
                Image.builder()
                        .userId(userId)
                        .build());

        Path output = Path.of(outputPath);
        if (!Files.exists(output)) {
            Files.createDirectories(output);
        }

        Path file = Files.createFile(output.resolve(String.valueOf(image.getImageId())));
        Files.copy(imageImputStream, file, StandardCopyOption.REPLACE_EXISTING);
    }

}
