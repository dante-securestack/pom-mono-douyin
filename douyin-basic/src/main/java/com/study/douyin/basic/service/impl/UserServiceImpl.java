package com.study.douyin.basic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.douyin.basic.dao.UserDao;
import com.study.douyin.basic.entity.UserEntity;
import com.study.douyin.basic.feign.SocializeFeignService;
import com.study.douyin.basic.service.UserService;
import com.study.douyin.basic.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {

    @Autowired
    private SocializeFeignService socializeFeignService;

    @Override
    public UserEntity Register(String username, String password) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password))
            return null;
        UserEntity userEntity = new UserEntity();

        //判断当前用户是否存在
        UserEntity user = baseMapper.selectOne(new QueryWrapper<UserEntity>().eq("username", username));
        if (user != null)
            return null;

        //设置用户名
        userEntity.setUsername(username);

        //密码加密存储
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        userEntity.setPassword(passwordEncoder.encode(password));

        //初始化部分数据
        userEntity.setFollowCount(0);
        userEntity.setFollowerCount(0);

        baseMapper.insert(userEntity);
        return userEntity;
    }

    @Override
    public UserEntity Login(String username, String password) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password))
            return null;
        //查询当前用户名对应的用户
        UserEntity user = baseMapper.selectOne(new QueryWrapper<UserEntity>().eq("username", username));
        // 不存在此用户名的用户
        if (user == null)
            return null;

        //数据库中加密的密码
        String passwordDB = user.getPassword();

        //判断用户输入的密码和数据库中密码是否匹配
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (passwordEncoder.matches(password, passwordDB))
            //密码正确则返回用户
            return user;
        //密码错误返回null
        return null;
    }

    @Override
    public User getUserById(int userId, int followId) {
        UserEntity userEntity = this.getById(userId);
        User user = new User();
        // 当前id对应的user存在
        if (userEntity != null) {
            user.setId(userEntity.getUserId());
            user.setName(userEntity.getUsername());
            user.setFollowCount(userEntity.getFollowCount());
            user.setFollowerCount(userEntity.getFollowerCount());
            user.setFollow(socializeFeignService.isFollow(userId, followId));
        }
        return user;
    }
}
