package com.jackdaw.javapro.service;

import com.jackdaw.javapro.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
* @author Jackdaw
* @description 针对表【user】的数据库操作Service
* @createDate 2023-03-19 13:54:01
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAcount
     * @param userPassword
     * @param checkPassword
     * @return 用户id
     */
    long userRegister(String userAcount,String userPassword,String checkPassword) throws NoSuchAlgorithmException;

    /**
     * 用户登录
     * @param userAcount
     * @param userPassword
     * @return 用户信息
     */
    User doLogin(String userAcount, String userPassword, HttpServletRequest httpServletRequest);


    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    int userLogout(HttpServletRequest request);

    public List<User> searchUserByTags(List<String> tagsList,User loginUser,long pageNum, long num);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    Integer updateUser(User user,User loginUser);

    /**
     * 获取当前已登录的用户信息
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 判断是否为管理员
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 判断是否为管理员
     * @param user
     * @return
     */
    boolean isAdmin(User user);

    /**
     * 匹配用户
     * @param num
     * @param loginUser
     * @return
     */
    List<User> matchUsers(long pageNum, long num, User loginUser);
}
