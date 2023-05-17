package com.jackdaw.javapro.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jackdaw.javapro.model.domain.User;
import com.jackdaw.javapro.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    private List<Long> mainUserList = Arrays.asList(1L);

    @Resource
    private RedissonClient redissonClient;

////    每天执行，预热推荐用户
    @Scheduled(cron = "0 4 0 * * *")
    public void doCacheRecommentUser(){
//        定时任务加分布式锁
        RLock lock = redissonClient.getLock("javapro:user:docache:lock");
        try {
//          定时任务操作要在抢到锁的前提下进行（只有一机可以执行）
            if (lock.tryLock(0,10000L,TimeUnit.MILLISECONDS)){
                for (Long userId:
                        mainUserList) {
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
                    String redisKey = String.format("yupao:user:recommand:%s", userId);
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
    //            写缓存
                    try {
                        valueOperations.set(redisKey,userPage,30000, TimeUnit.MILLISECONDS);
                    }catch (Exception e){
                        log.error("redis set key error",e);
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
//                只允许当前机器释放锁，防止释放别的机器的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unlock"+Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }
}
