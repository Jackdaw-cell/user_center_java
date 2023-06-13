package com.jackdaw.javapro.service.impl;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jackdaw.javapro.common.ErrorCode;
import com.jackdaw.javapro.exception.BusinessException;
import com.jackdaw.javapro.model.domain.User;
import com.jackdaw.javapro.service.UserService;
import com.jackdaw.javapro.mapper.UserMapper;
import com.jackdaw.javapro.utils.AlgorithmUtils;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.jackdaw.javapro.constant.UserConstant.ADMIN_ROLE;
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
        safeUser.setTags(originUser.getTags());
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

    /**
     * 根据标签查询用户,所有标签满足才返回
     * @param tagsList
     * @return
     */
    @Override
    public List<User> searchUserByTags(List<String> tagsList, User loginUser,long pageNum, long num) {
//        if (CollectionUtils.isEmpty(tagsList)) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id","username","tags","avatarUrl");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);
        tagsList.removeIf(String::isEmpty);
        return distinMatch(userList,loginUser,tagsList,pageNum,num);
    }


    @Override
    public Integer updateUser(User user, User loginUser) {
//        TODO：如果user除了id其他属性都为null，则直接返回不需要任何操作（用户没改）
        Long userId = user.getId();
//      用户存在性
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        如果是管理员-允许更新任何用户，用户-只允许更新自己
        if (!isAdmin(loginUser) && !userId.equals(loginUser.getId())) {
             throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = userMapper.selectById(userId);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return userMapper.updateById(user);
    }

    /**
     * 获取当前用户信息
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Object user = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return (User) user;
    }

    /**
     * 判断当前登录的用户是否为管理员
     */
    @Override
    public boolean isAdmin(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

    /**
     * 判断当前登录的用户是否为管理员
     */
    @Override
    public boolean isAdmin(User user) {
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

    /**
     * 找到标签相近的人
     * @param num
     * @param loginUser
     * @return
     */
    @Override
    public List<User> matchUsers(long pageNum, long num, User loginUser) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id","username","tags","avatarUrl");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {}.getType());
        if (tagList == null) {
            tagList = new ArrayList<>();
        }else{
            tagList.removeIf(String::isEmpty);
        }
        return distinMatch(userList, loginUser, tagList, pageNum, num);
    }

    /**
     * 距离匹配算法
     * @param userList
     * @param loginUser
     * @param tagList
     * @param num
     * @return
     */
    private List<User> distinMatch(List<User> userList,User loginUser,List<String> tagList,long pageNum,long num){
        if (tagList.size()<=0) {
            // 创建随机数生成器
            Random random = new Random();
            List<User> ramdaList = new ArrayList<>();
            for (int i = 0; i < num; i++) {
                // 生成一个随机索引
                int randomIndex = random.nextInt(userList.size());
                // 获取对应的元素并将其添加到抽取列表中
                User selectedElement = userList.get(randomIndex);
                ramdaList.add(selectedElement);
            }
            return ramdaList;
        }
        Gson gson = new Gson();
        // 用户列表的下标 => 相似度
        List<Pair<User, Long>> list = new ArrayList<>();
        // 依次计算所有用户和当前用户的相似度
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String userTags = user.getTags();
            // 无标签或者为当前用户自己
            if (StringUtils.isBlank(userTags) || user.getId().equals(loginUser.getId())) {
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            if (tagList.contains(user.getUsername())){
                list.add(new Pair<>(user, -1L));
                continue;
            }
            if (tagList.contains(Long.toString(user.getId()))){
                list.add(new Pair<>(user, -1L));
                continue;
            }
            // 计算分数
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            list.add(new Pair<>(user, distance));
        }
        // 按编辑距离由小到大排序
        List<Pair<User, Long>> topUserPairList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
//                .limit(num)
                .collect(Collectors.toList());
        // 原本顺序的 userId 列表
        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", userIdList);
        userQueryWrapper.last("limit "+pageNum+","+num);
        // 1, 3, 2
        // User1、User2、User3
        // 1 => User1, 2 => User2, 3 => User3
        Map<Long, List<User>> userIdUserListMap = this.list(userQueryWrapper)
                .stream()
                .map(user -> getSafetyUser(user))
                .collect(Collectors.groupingBy(User::getId));
        List<User> finalUserList = new ArrayList<>();
        for (Long userId : userIdList) {
            if (userIdUserListMap.get(userId)==null){
                continue;
            }
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }
        return finalUserList;
    }

    private  List<User> searchUsersByTagsBySQL(List<String> tagsList){
        if (CollectionUtils.isEmpty(tagsList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
       QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        for (String tagName:
             tagsList) {
            queryWrapper=queryWrapper.like("tags",tagName);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
//        函数式接口
//        每个遍历的元素调用方法并覆写
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());

    }
}




