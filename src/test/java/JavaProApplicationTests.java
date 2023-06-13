
import com.google.gson.Gson;
import com.jackdaw.javapro.JavaProApplication;
import com.jackdaw.javapro.mapper.UserMapper;
import com.jackdaw.javapro.model.domain.User;
import com.jackdaw.javapro.service.UserService;
import com.jackdaw.javapro.utils.AlgorithmUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.*;

import static com.jackdaw.javapro.utils.SimilarDegreeByCos.getSimilarDegree;

@SpringBootTest(classes = {JavaProApplication.class})
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

    @Test
    @Transactional
    public void doInsertUsers() throws JSONException {
        //        插入100万条用户数据
        ExecutorService executorService = new ThreadPoolExecutor(60,1000,10000, TimeUnit.MINUTES,new ArrayBlockingQueue<>(10000));

        String[] tagArr={"java","python","goLang","springcloud","javascript","elastic","websocket",
                "怪物猎人","蔚蓝","空洞骑士","塞尔达传说","合金装备","死亡搁浅","CSGO","DOTA2",
                "学生","在职",
                "考研","求职","CET4","CET6","ACM"};

//        单次任务容量
        int batchSize=250;
        int j=0;
        List<CompletableFuture> futureList = new ArrayList<>();
        for (int i=0;i<40;i++){
            List<User> userList = new ArrayList<>();
            while(true){
                j++;
                User user = new User();
                user.setUsername(generateRandomName());
                user.setEmail(generateRandomEmail(user.getUsername()));
                user.setUserAccount(user.getUsername()+Long.toString(System.currentTimeMillis() % 1000));
                user.setUserPassword("jack");
                user.setAvatarUrl("https://cdn.akamai.steamstatic.com/steamcommunity/public/images/items/1629910/045c57ebb6946fdf7e57a53d5768117dd8543862.gif");
                user.setGender(0);
                user.setPhone("1145141919810");
                user.setUserStatus(0);
                user.setUserRole(0);
                user.setTags(generateRandomUserTags(tagArr));
                userList.add(user);
                if (j%batchSize==0){
                    break;
                }
            }
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println(Thread.currentThread().getName());
                userService.saveBatch(userList, batchSize);
            });
            futureList.add(future);
        }
//        阻塞，要等到所有任务执行完才执行下一行代码
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
    }

    public static String generateRandomUserTags(String[] tagArr) throws JSONException {
        // 生成随机数，表示标签的数量
        int numTags = Math.min(new Random().nextInt(11), tagArr.length);
        // 生成随机标签列表
        List<String> userTags = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < numTags; i++) {
            int index = random.nextInt(tagArr.length);
            if (index == 0){
                return null;
            }
            String tag = tagArr[index];
            if (!userTags.contains(tag)) {
                userTags.add(tag);
            }
        }
        // 转换为 JSON 数组
        Gson gson = new Gson();
        String json = gson.toJson(userTags);

        // 返回 JSON 数组
        return json;
    }

    public static String generateRandomName() {
        String[] firstNames = {"Alice", "Bob", "Charlie", "Dave", "Eve", "Frank", "Grace", "Henry", "Ivy", "Jack",
                "Olivia", "Liam", "Sophia", "Noah", "Ava", "William", "Isabella", "James", "Mia", "Benjamin",
                "Emma", "Lucas", "Oliver", "Alexander", "Emily"};
        String[] lastNames ={"Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor",
                "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin", "Thompson", "Garcia", "Martinez", "Robinson",
                "Clark", "Rodriguez", "Lewis", "Lee", "Walker"};
        Random random = new Random();
        String firstName = firstNames[random.nextInt(firstNames.length)];
        String lastName = lastNames[random.nextInt(lastNames.length)];
        return firstName + lastName;
    }


    public static String generateRandomEmail(String name) {
        String[] domains = {"example.com", "test.com", "foo.com", "bar.com","gmail.com","qq.com"};
        String domain = domains[new Random().nextInt(domains.length)];
        return name.toLowerCase() + "@" + domain;
    }

    @Test
    public void tt(){
        ArrayList<String> list1 = new ArrayList<>();
        ArrayList<String> list2 = new ArrayList<>();
        list1.add("java");
        list2.add("java");
        long distance = AlgorithmUtils.minDistance(list1, list2);
        System.out.println(distance);
    }
}
