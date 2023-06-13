//package com.jackdaw.javapro.service.impl;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.jackdaw.javapro.common.ErrorCode;
//import com.jackdaw.javapro.exception.BusinessException;
//import com.jackdaw.javapro.mapper.ChatConnectMapper;
//import com.jackdaw.javapro.model.domain.ChatConnect;
//import com.jackdaw.javapro.model.domain.Team;
//import com.jackdaw.javapro.model.domain.User;
//import com.jackdaw.javapro.model.domain.UserTeam;
//import com.jackdaw.javapro.service.ChatConnectService;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//
///**
//* @author Jackdaw
//* @description 针对表【chat_connect(会话连接记录)】的数据库操作Service实现
//* @createDate 2023-05-20 11:47:15
//*/
//@Service
//public class ChatConnectServiceImpl extends ServiceImpl<ChatConnectMapper, ChatConnect>
//    implements ChatConnectService {
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean addConnect(String fromName, String toName) {
//        ChatConnect chatConnect = new ChatConnect();
//        chatConnect.setFromName(fromName);
//        chatConnect.setToName(toName);
//        return this.save(chatConnect);
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public List<ChatConnect> listConnect(String name) {
//        QueryWrapper<ChatConnect> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("fromName",name);
//        queryWrapper.or();
//        queryWrapper.eq("toName",name);
//        return this.list(queryWrapper);
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public List<ChatConnect> listConnectTarget(String fromName, String toName) {
//        QueryWrapper<ChatConnect> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("fromName",fromName).and(qw -> qw.eq("toName",toName));
//        queryWrapper.or();
//        queryWrapper.eq("toName",fromName).and(qw -> qw.eq("fromName",toName));
//        return this.list(queryWrapper);
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean updateConnectTarget(String fromName,String toName) {
//        UpdateWrapper<ChatConnect> queryWrapper = new UpdateWrapper<>();
//        // 获取当前日期时间
//        LocalDateTime currentDateTime = LocalDateTime.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        String formattedDateTime = currentDateTime.format(formatter);
//        queryWrapper.set("updateTime",formattedDateTime);
//        queryWrapper.eq("fromName",fromName).and(qw -> qw.eq("toName",toName));
//        queryWrapper.or();
//        queryWrapper.eq("fromName",toName).and(qw -> qw.eq("toName",fromName));
//        return this.update(queryWrapper);
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean deleteConnect(String name) {
//        QueryWrapper<ChatConnect> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("fromName",name);
//        queryWrapper.or();
//        queryWrapper.eq("toName",name);
//        return this.remove(queryWrapper);
//    }
//}
//
//
//
//
