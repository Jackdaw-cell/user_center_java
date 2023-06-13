package com.jackdaw.javapro.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jackdaw.javapro.common.ErrorCode;
import com.jackdaw.javapro.common.SpringBeanContext;
import com.jackdaw.javapro.config.WebSocketConfig;
import com.jackdaw.javapro.exception.BusinessException;
import com.jackdaw.javapro.model.domain.ChatConnect;
import com.jackdaw.javapro.model.domain.User;
import com.jackdaw.javapro.model.onlineMessage.Message;
import com.jackdaw.javapro.service.ChatConnectService;
import com.jackdaw.javapro.service.UserService;
import com.jackdaw.javapro.utils.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.jackdaw.javapro.constant.UserConstant.USER_LOGIN_STATE;

@Slf4j
@ServerEndpoint(value = "/chat",configurator = GetHttpSessionConfigurator.class)
@Component
public class ChatEndPoint {

    //用线程安全的map来保存当前用户
    private static Map<String,ChatEndPoint> onLineUsers = new ConcurrentHashMap<>();

    //声明一个session对象，通过该对象可以发送消息给指定用户，不能设置为静态，每个ChatEndPoint有一个session才能区分.(websocket的session)
    private Session session;

    //保存当前登录浏览器的用户
    private HttpSession httpSession;

//    当前用户ID
    private String currentUserId;

    //    当前用户
    private User currentUser;


    private RedisTemplate redisTemplate;

    //建立连接时发送系统广播
    @OnOpen
    public void onOpen(Session session, EndpointConfig config){
        this.session = session;
        this.redisTemplate=SpringBeanContext.getContext().getBean("stringRedisTemplate",RedisTemplate.class);
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        this.httpSession = httpSession;
//        获取不了session
//        初步猜想：种cookie时的对象是linhaijian.top域名，而ws连接请求的是ws://120.25.122.29 这样子没带上cookie ？
//        ws配置域名访问，nginx配置了
        User userObj =(User) httpSession.getAttribute(USER_LOGIN_STATE);
        if (userObj==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"用户未登录");
        }
        UserService userService=SpringBeanContext.getContext().getBean(UserService.class);
        long userId=userObj.getId();
        User tmpuser = userService.getById(userId);
        if(tmpuser==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"用户未登录");
        }
        this.currentUser=tmpuser;
        this.currentUserId=this.currentUser.getId().toString();
        log.info("用户:"+currentUserId+" 登录验证中..");
        log.info("上线用户名称："+currentUserId);
        onLineUsers.put(currentUserId,this);
        String message = MessageUtils.getMessage(true,null,null, getNames());

//      取连接
//        ChatConnectService chatConnectService=SpringBeanContext.getContext().getBean(ChatConnectService.class);
//        List<ChatConnect> chatConnects = chatConnectService.listConnect(currentUserId);
//        ArrayList<String> redisKeyList = new ArrayList<>();
//        for (ChatConnect con:
//            chatConnects) {
//            String redisKeyGet = "javapro:websocket:history:"+con.getFromName()+":"+con.getToName();
//            String redisKeySend = "javapro:websocket:history:"+con.getToName()+":"+con.getFromName();
//            redisKeyList.add(redisKeyGet);
//            redisKeyList.add(redisKeySend);
//        }
    }

    //获取当前登录的用户
    private Set<String> getNames(){
        return onLineUsers.keySet();
    }
    //发送系统消息
    private void broadcastAllUsers(String message){
        try{
            Set<String> names = onLineUsers.keySet();
            for(String name : names){
                ChatEndPoint chatEndPoint = onLineUsers.get(name);
                chatEndPoint.session.getBasicRemote().sendText(message);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //用户之间的信息发送
//    onMessage不管是接收还是发送都会触发
    @OnMessage
    public void onMessage(String message,Session session){
        try{
            ObjectMapper mapper = new ObjectMapper();
            Message mess = mapper.readValue(message,Message.class);
            String fromName = mess.getFromName();
            String toName = mess.getToName();
            String data = mess.getMessage();
            int messageType = mess.getMessageType();
//            消息类型为【1-获取聊天记录】
            if (messageType==1){
                String redisKeyGet = "javapro:websocket:history:"+fromName+":"+toName;
                String redisKeySend = "javapro:websocket:history:"+toName+":"+fromName;
                List<String> redisKeyList=new ArrayList<>();
                redisKeyList.add(redisKeySend);
                redisKeyList.add(redisKeyGet);
                onLineUsers.get(fromName).session.getBasicRemote().sendText(getHistoryMsgFromRedis(redisKeyList).toString());
                return;
            }
//            类型【2-发送消息】
            if (messageType==2){
                log.info(currentUserId + "向"+toName+"发送的消息：" + data);
                sendMessage(fromName, toName, data);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public List<String> getHistoryMsgFromRedis(List<String> redisKeyList){
        List<String> list = new ArrayList<>();
        for (String redisKey:
            redisKeyList) {
            for (Object o:
                    redisTemplate.opsForList().range(redisKey, 0, -1)) {
                list.add(o.toString());
            }
        }
        return list;
    }

    public void sendMessage(String fromName, String toName,  String data){
        if(StringUtils.hasLength(toName)) {
//                推送消息
//                在线情况 - 发信息通知，并且存缓存和连接/不在线情况 - 不发信息只存缓存和连接
            if (onLineUsers.get(toName) != null) {
                String resultMessage = MessageUtils.getMessage(false, currentUserId, toName, data);
                try {
                    onLineUsers.get(toName).session.getBasicRemote().sendText(resultMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String redisKey1 = "javapro:websocket:history:"+currentUserId+":"+toName;
            String redisKey2 = "javapro:websocket:history:"+toName+":"+currentUserId;
            Boolean flag = redisTemplate.hasKey(redisKey1);
            if (flag == true) {
                messageaddtodb(fromName,toName,data,redisKey1);
                connectaddtodb(fromName,toName);
            }else {
                messageaddtodb(fromName,toName,data,redisKey2);
                connectaddtodb(fromName,toName);
            }
        }
    }

    public void messageaddtodb(String fromName,String toName,String data,String key){
        // 创建 ObjectMapper 对象
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String,String> map1 = new HashMap<String,String>();
        map1.put("fromName",fromName);
        map1.put("toName",toName);
        map1.put("message",data);
        map1.put("time",Long.toString(System.currentTimeMillis()));
        // 将 Map<String, String> 转换为 JSON 字符串
        String jsonStr = null;
        try {
            jsonStr = objectMapper.writeValueAsString(map1);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        redisTemplate.opsForList().rightPush(key, jsonStr);
    }

    public void connectaddtodb(String fromName,String toName){
//                判断连接是否存在
        ChatConnectService chatConnectService=SpringBeanContext.getContext().getBean(ChatConnectService.class);
        List<ChatConnect> connects = chatConnectService.listConnectTarget(fromName, toName);
        if (connects.size()<=0) {
            chatConnectService.addConnect(fromName, toName);
        }else {
            chatConnectService.updateConnectTarget(currentUserId, toName);
        }
    }

    //用户断开连接的断后操作
    @OnClose
    public void onClose(Session session){
//        String username = (String) httpSession.getAttribute("user");
        log.info("离线用户："+ currentUserId);
        if (currentUserId != null){
            onLineUsers.remove(currentUserId);
        }
        httpSession.removeAttribute("user");
        String message = MessageUtils.getMessage(true, currentUserId, null, getNames());
        broadcastAllUsers(message);
    }

    @OnError
    public void onErrot(Session session, Throwable thr){
        thr.printStackTrace();
    }
}
