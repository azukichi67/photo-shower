package com.photoshower.domain;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Image {

    private int imageId;

    private String userId;

    private boolean isUsed;

    private OffsetDateTime createAt;
}
