package com.jackdaw.javapro.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jackdaw.javapro.common.BaseResponse;
import com.jackdaw.javapro.common.ErrorCode;
import com.jackdaw.javapro.common.ResultUtils;
import com.jackdaw.javapro.exception.BusinessException;
import com.jackdaw.javapro.model.domain.User;
import com.jackdaw.javapro.model.request.UserLoginRequest;
import com.jackdaw.javapro.model.request.UserRegisterRequest;
import com.jackdaw.javapro.service.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.jackdaw.javapro.constant.UserConstant.ADMIN_ROLE;
import static com.jackdaw.javapro.constant.UserConstant.USER_LOGIN_STATE;

@Api(value = "初始化首页")
@RestController
//@CrossOrigin()  //允许任何访问
//@CrossOrigin(origins = {"http://localhost:3000"})
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) throws NoSuchAlgorithmException {

        if (userRegisterRequest == null) {
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
    public BaseResponse<Integer> userLogout(HttpServletRequest httpServletRequest) throws NoSuchAlgorithmException {

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
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request){
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

//    TODO：要分页，不然响应体要炸
//    每次请求num加10，做到类似分页效果，坏处：越加载越卡（redis缓存）
    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUsersByTags(@RequestParam(required = false) List<String> tagNameList ,@RequestParam(required = false) long pageNum,@RequestParam(required = false) long num,HttpServletRequest request){
        if (CollectionUtils.isEmpty(tagNameList)){
//            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (tagNameList.size()<=0){
            tagNameList=new ArrayList<>();
        }
        List<User> userList = userService.searchUserByTags(tagNameList,loginUser,pageNum,num);
        return ResultUtils.success(userList);
    }

//    TODO：推荐多个用户
    @GetMapping("/recommand")
    public BaseResponse<JSONObject> recommand(long pageSize,long pageNum,HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        String redisKey = String.format("javapro:user:recommend:%s:%s:%s", loginUser.getId(),pageSize,pageNum);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
//        转型问题
        JSONObject userPage = (JSONObject) valueOperations.get(redisKey);
        if (userPage!=null){
            return ResultUtils.success(userPage);
        }
//        先查缓存后查库
        try{
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.ne("username",loginUser.getUsername());
            Page<User> userPageToRedis = userService.page(new Page<>(pageNum, pageSize), queryWrapper);
            valueOperations.set(redisKey,userPageToRedis,3000000, TimeUnit.MILLISECONDS);
        }catch (Exception e){
            log.error("redis set key error",e);
        }
        userPage = (JSONObject) valueOperations.get(redisKey);
        return ResultUtils.success(userPage);
    }

    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user,HttpServletRequest request){
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        鉴定权限
        User loginUser = userService.getLoginUser(request);
        Integer result = userService.updateUser(user,loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request){
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

    /**
     * 获取最匹配的用户
     *
     * @param num
     * @param request
     * @return
     */
    @GetMapping("/match")
    public BaseResponse<List<User>> matchUsers(long pageNum,long num, HttpServletRequest request) {
//        if (num <= 0 || num > 20) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.matchUsers(pageNum, num, user));
    }
}
