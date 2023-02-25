package com.photoshower.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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
     * 次の未使用の画像ファイルパスを返します。
     * @return 未使用の画像ファイルパス
     */
    public String getNextUnusedImagePath() {

        Image image = imageRepository.selectUnusedImage();
        int imageId = image.getImageId();
        imageRepository.updateToUsed(imageId);

        return "%s\\%s".formatted(outputPath, String.valueOf(imageId));
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
