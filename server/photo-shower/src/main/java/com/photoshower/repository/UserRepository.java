package com.photoshower.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.photoshower.domain.User;

@Repository
@Mapper
public interface UserRepository {

    @Insert("""
            INSERT INTO users(
              user_id
              , name
            )
            VALUES(
              #{userId}
              , #{name}
            )
            """)
    public void insert(User user);

    @Select("""
            SELECT
              *
            FROM
              users
            WHERE
              user_id = #{userId}
                """)
    public User select(@Param("userId") String userId);

}
