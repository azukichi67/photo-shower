package com.photoshower.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
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
            RETURNING image_id
            """)
    public int insert(Image image);

}
