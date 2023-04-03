package com.jackdaw.javapro.service.impl;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jackdaw.javapro.common.ErrorCode;
import com.jackdaw.javapro.exception.BusinessException;
import com.jackdaw.javapro.model.domain.User;
import com.jackdaw.javapro.service.UserService;
import com.jackdaw.javapro.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.jackdaw.javapro.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author Jackdaw
* @description 针对表【user】的数据库操作Service实现
* @createDate 2023-03-19 13:54:01
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    /**
     * 这里可以用  userMapper.方法  执行sql
     * 也可以 this.方法  执行sql
     * 两者拥有的方法不同，都可以执行sql操作
     */
    @Resource
    UserMapper userMapper;

//    盐值
    public static final String SALT="jadw";

    @Override
    public long userRegister(String userAcount, String userPassword, String checkPassword) throws NoSuchAlgorithmException {
//  1、校验,用到commons的检验方法
        if (StringUtils.isAnyBlank(userAcount,userPassword,checkPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"请求参数为空");
        }
        if (userAcount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户名少于4位");
        }
        if (userPassword.length()<8||checkPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户名少于8位");
        }
//        校验用户名是否存在特殊字符
        String validPattern="['~!@#$%^&*()_+|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）【】‘：；“”’。、，？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAcount);
        if (matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"非法字符串");
        }
//        校验两次输入的密码
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户名少于4");
        }
//        查询用户是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAcount);
        long count = userMapper.selectCount(queryWrapper);
        if (count >0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"用户已存在");
        }

//  2、加密
        String newPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
// 3、插入数据
        User user = new User();
        user.setUserAccount(userAcount);
        user.setUserPassword(newPassword);
        boolean saveResult = this.save(user);
        if (!saveResult){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"用户注册失败");
        }
        return user.getId();
    }

    @Override
    public User doLogin(String userAcount, String userPassword, HttpServletRequest httpServletRequest) {
//  1、校验字符合法性
        if (StringUtils.isAnyBlank(userAcount,userPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"参数为空");
        }
        if (userAcount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户名过短");
        }
        if (userPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码过长");
        }
        String validPattern="['~!@#$%^&*()_+|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）【】‘：；“”’。、，？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAcount);
        if (matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数不合法");
        }
// 2、查询用户是否已存在
        String newPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAcount);
        queryWrapper.eq("userPassword",newPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user==null){
            log.error("user info files,userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"用户不存在");
        }
//3、用户脱敏
        User safeUser=getSafetyUser(user);
//4、记录用户登录态
        httpServletRequest.getSession().setAttribute(USER_LOGIN_STATE,safeUser);
        return safeUser;
    }

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    /**
    * tips:选中变量名，shift+F6可以对多个变量名一键重构
    */
    @Override
    public User getSafetyUser(User originUser){
        User safeUser = new User();
        safeUser.setId(originUser.getId());
        safeUser.setUsername(originUser.getUsername());
        safeUser.setEmail(originUser.getEmail());
        safeUser.setUserAccount(originUser.getUserAccount());
        safeUser.setUserPassword(originUser.getUserPassword());
        safeUser.setAvatarUrl(originUser.getAvatarUrl());
        safeUser.setGender(originUser.getGender());
        safeUser.setPhone(originUser.getPhone());
        safeUser.setUserStatus(originUser.getUserStatus());
        safeUser.setUserRole(originUser.getUserRole());
        return safeUser;
    }

    /**
     * 用户登录态
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
//        移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;

    }
}




