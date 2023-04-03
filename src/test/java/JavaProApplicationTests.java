
import com.jackdaw.javapro.JavaProApplication;
import com.jackdaw.javapro.service.UserService;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SpringBootTest(classes =JavaProApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class JavaProApplicationTests {

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
}
