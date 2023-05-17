
import com.jackdaw.javapro.JavaProApplication;
import com.jackdaw.javapro.mapper.UserMapper;
import com.jackdaw.javapro.model.domain.User;
import com.jackdaw.javapro.service.UserService;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import static com.jackdaw.javapro.utils.SimilarDegreeByCos.getSimilarDegree;

@SpringBootTest(classes =JavaProApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class JavaProApplicationTests {

    @Resource
    private UserMapper userMapper;

    @Resource
    UserService userService;

    @Test
   public void testDigest() throws NoSuchAlgorithmException {
        String newPassword = DigestUtils.md5DigestAsHex(("abcd" + "mypassword").getBytes());
        System.out.println(newPassword);
    }

    @Test
    public void userRegister() throws NoSuchAlgorithmException {
        String userAccount="jadw";
        String userPassword = "";
        String checkPassword = "123456";
        /**
         * 测试1
         */
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1,result);
        /*
         *测试2
         */
        userAccount="yu";
        long result2 = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1,result2);
        /**
         * 测试3
         */
        userAccount="ja dw";
        long result3 = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1,result3);
        /**
         * 测试4
         */
        userAccount="jadw";
        userPassword = "123456789";
        checkPassword = "123456789";
        long result4 = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertTrue(result4>0);
    }


    /**
     * 测试余弦匹配
     */
    @Test
    public void testYvxian() {
        String str1 = "gold silver truck";
        String str2 = "Shipment of gold damaged in a fire";
        String str3 = "Delivery of silver arrived in a silver truck";
        String str4 = "Shipment of gold arrived in a truck";
        String str5 = "gold gold gold gold gold gold";
        String str6 = "gold silver truck";

        System.out.println(getSimilarDegree(str1, str2));
        System.out.println(getSimilarDegree(str1, str3));
        System.out.println(getSimilarDegree(str1, str4));
        System.out.println(getSimilarDegree(str1, str5));
        System.out.println(getSimilarDegree(str1, str6));
    }

//    插入100万条用户数据
//    private ExecutorService executorService = new ThreadPoolExecutor(60,1000,10000, TimeUnit.MINUTES,new ArrayBlockingQueue<>(10000));
//
//    @Test
//    public void doInsertUsers(){
////        开启线程数为10
//        int batchSize=250;
//        int j=0;
//        List<CompletableFuture> futureList = new ArrayList<>();
//        for (int i=0;i<40;i++){
//            List<User> userList = new ArrayList<>();
//            while(true){
//                j++;
//                User user = new User();
//                user.setUsername("测试用户");
//                user.setEmail("114514@1919");
//                user.setUserAccount("jack");
//                user.setUserPassword("jack");
//                user.setAvatarUrl("https://cdn.akamai.steamstatic.com/steamcommunity/public/images/items/1629910/045c57ebb6946fdf7e57a53d5768117dd8543862.gif");
//                user.setGender(0);
//                user.setPhone("1145141919810");
//                user.setUserStatus(0);
//                user.setUserRole(0);
//                user.setTags("[\"java\",\"SQL\"]");
//                userList.add(user);
//                if (j%batchSize==0){
//                    break;
//                }
//            }
//            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//                System.out.println(Thread.currentThread().getName());
//                userService.saveBatch(userList, batchSize);
//            });
//            futureList.add(future);
//        }
////        阻塞，要等到所有任务执行完才执行下一行代码
//        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
//    }

}
