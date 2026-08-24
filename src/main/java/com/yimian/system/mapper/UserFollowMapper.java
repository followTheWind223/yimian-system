package com.yimian.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yimian.system.entity.User;
import com.yimian.system.entity.UserFollow;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserFollowMapper extends BaseMapper<UserFollow> {

    UserFollow selectActive(@Param("followerId") Long followerId, @Param("followeeId") Long followeeId);

    UserFollow selectAny(@Param("followerId") Long followerId, @Param("followeeId") Long followeeId);

    int softDelete(@Param("followerId") Long followerId, @Param("followeeId") Long followeeId);

    int restore(@Param("id") Long id);

    int countFollowing(@Param("userId") Long userId);

    int countFollowers(@Param("userId") Long userId);

    List<User> selectFollowingUsers(@Param("userId") Long userId);

    List<User> selectFollowerUsers(@Param("userId") Long userId);

    List<User> selectMutualFollowUsers(@Param("userId") Long userId,
                                       @Param("keyword") String keyword,
                                       @Param("limit") int limit);
}
