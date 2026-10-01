package com.zeewoncode.pdd_server.mapper;

import com.zeewoncode.entity.User;
import com.zeewoncode.req.UpdateProfileReq;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 查询买家信息
     * @param phone
     * @return
     */
    @Select("select * from user where phone = #{phone} and deleted_flag = 0")
    User selectByPhoneAndUser(String phone);

    /**
     * 插入用户信息
     * @param user
     */
    @Insert("insert into user (phone, password, nickname, status, user_type, deleted_flag, created_at, updated_at) " +
            "values (#{phone}, #{password}, #{nickname}, #{status}, #{userType}, #{deletedFlag}, #{createdAt}, #{updatedAt})")
    void insert(User user);

    /**
     * 根据id查询用户信息
     * @param userId
     * @return
     */
    @Select("select * from user where id = #{userId} and deleted_flag = 0")
    User selectById(Integer userId);

    /**
     * 根据id更新用户信息
     * @param user
     */
    void updateById(User user);
}
