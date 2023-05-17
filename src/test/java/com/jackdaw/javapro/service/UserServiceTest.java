package com.jackdaw.javapro.service;

import com.jackdaw.javapro.model.domain.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
用户服务测试
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testAddUser(){
        User user=new User();
        user.setUsername("jackdaw");
        user.setEmail("jackdaw2333333@gmail.com");
        user.setUserAccount("jackdaw2333333@gmail.com");
        user.setUserPassword("");
        user.setAvatarUrl("");
        user.setGender(0);
        user.setPhone("");
        user.setUserStatus(0);
        boolean result=userService.save(user);
        System.out.println(user.getId());
//        判断值是否相等的断言
        assertTrue(result);
    }

    @Test
    public void testSearchUserByTags() {
        List<String> stringList = Arrays.asList("java", "MHW", "sword");
//        Assert.assertNotNull(userList);
    }
}