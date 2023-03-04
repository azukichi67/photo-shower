package com.photoshower.api;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.photoshower.service.ImageService;

/**
 * Imageのコントローラです。
 */
@RestController
@RequestMapping("api/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @GetMapping("/next-image")
    public String getNextUnusedImageData() throws IOException {
        return imageService.getNextUnusedImageData();
    }

}
