package com.photoshower.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.photoshower.domain.Image;

@Repository
@Mapper
public interface ImageRepository {

    @Select("""
            INSERT INTO images(
              user_id
              , is_used
            )
            VALUES(
              #{userId}
              , #{isUsed}
            )
            RETURNING *
            """)
    public Image insert(Image image);

    @Select("""
            SELECT
              *
            FROM
              images
            WHERE
              is_used IS FALSE
            ORDER BY
              image_id
            LIMIT 1
            """)
    public Image selectUnusedImage();

    @Update("""
            UPDATE
              images
            SET
              is_used = TRUE
            WHERE
              image_id = #{imageId}
            """)
    public void updateToUsed(@Param("imageId") int imageId);

}
