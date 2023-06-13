package com.jackdaw.javapro.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jackdaw.javapro.model.domain.ChatConnect;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author Jackdaw
* @description 针对表【chat_connect(会话连接记录)】的数据库操作Service
* @createDate 2023-05-20 11:47:15
*/
public interface ChatConnectService extends IService<ChatConnect> {

    boolean addConnect(String fromName, String toName);

    List<ChatConnect> listConnect(String fromName);

    List<ChatConnect> listConnectTarget(String fromName, String toName);

    boolean updateConnectTarget(String fromName, String toName);

    boolean deleteConnect(String name);
}
