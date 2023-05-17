package com.jackdaw.javapro.once;

import com.jackdaw.javapro.mapper.UserMapper;
import com.jackdaw.javapro.model.domain.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

/**
 * 单次任务：插入一千万条用户数据
 */
@Component
public class insertUsers {
    @Resource
    private UserMapper userMapper;

    /**
     * 批量插入用户
     * Scheduled注解会被spring容器扫描，
     * initialDelay规定首次执行延时，fixedRate规定再次执行延时
     * 这里以long最大值为延时，假实现执行单次任务（因为下一刻好久好久以后才执行）
     */
//    @Scheduled(initialDelay = 50000,fixedRate =Long.MAX_VALUE )
    public void doInsertUsers(){
//        spring的计时工具
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM=10000000;
        for (int i=0;i<INSERT_NUM;i++){
            User user = new User();
            user.setUsername("测试用户");
            user.setEmail("114514@1919");
            user.setUserAccount("jack");
            user.setUserPassword("jack");
            user.setAvatarUrl("https://cdn.akamai.steamstatic.com/steamcommunity/public/images/items/1629910/045c57ebb6946fdf7e57a53d5768117dd8543862.gif");
            user.setGender(0);
            user.setPhone("1145141919810");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode("");
            user.setTags("[\"java\",\"SQL\"]");
            userMapper.insert(user);
        }
        stopWatch.stop();
    }

    public static void main(String[] args){
//        生成当前类实例。插入1000万条数据
        new insertUsers().doInsertUsers();
    }
}
