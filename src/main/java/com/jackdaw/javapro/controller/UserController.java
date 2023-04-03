package com.jackdaw.javapro.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jackdaw.javapro.common.BaseResponse;
import com.jackdaw.javapro.common.ErrorCode;
import com.jackdaw.javapro.common.ResultUtils;
import com.jackdaw.javapro.exception.BusinessException;
import com.jackdaw.javapro.model.domain.User;
import com.jackdaw.javapro.model.domain.request.UserLoginRequest;
import com.jackdaw.javapro.model.domain.request.UserRegisterRequest;
import com.jackdaw.javapro.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.jackdaw.javapro.constant.UserConstant.ADMIN_ROLE;
import static com.jackdaw.javapro.constant.UserConstant.USER_LOGIN_STATE;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) throws NoSuchAlgorithmException {

        if (userRegisterRequest == null) {
//            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"用户名或密码为空");
        }
        long result=userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest httpServletRequest) throws NoSuchAlgorithmException {

        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"请求失败");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount,userPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"请求参数为空");
        }
        User user=userService.doLogin(userAccount, userPassword, httpServletRequest);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogin(HttpServletRequest httpServletRequest) throws NoSuchAlgorithmException {

        if (httpServletRequest == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"请求错误");
        }
        int result= userService.userLogout(httpServletRequest);
        return ResultUtils.success(result);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser=(User) userObj;
        if (currentUser==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"用户未登录");
        }
//        校验用户是否合法
        long userId=currentUser.getId();
        User user=userService.getById(userId);
        return ResultUtils.success(user);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUser(String username, HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user=(User) userObj;
        if (user==null ||user.getUserRole()!=ADMIN_ROLE){
            ArrayList<User> objects = new ArrayList<>();
            return ResultUtils.success(objects);
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isNoneBlank(username)) {
            userQueryWrapper.like("username",username);
        }
        List<User> userList = userService.list(userQueryWrapper);
        List<User> userCollection = userList.stream().map(users -> {
            return userService.getSafetyUser(users);
        }).collect(Collectors.toList());
        return ResultUtils.success(userCollection);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request){
        System.out.println(request.getSession());
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user=(User) userObj;
        if (user==null ||user.getUserRole()!=ADMIN_ROLE){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除失败");
        }
        if (id<=0) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"用户不存在");
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }


}
