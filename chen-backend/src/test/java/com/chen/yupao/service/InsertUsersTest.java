package com.chen.yupao.service;

import com.chen.yupao.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 导入用户测试
 *
 */
@SpringBootTest
public class InsertUsersTest {

    @Resource
    private UserService userService;

    private ExecutorService executorService = new ThreadPoolExecutor(40, 1000,
            10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));

    /**
     * 批量插入用户
     */
    @Test
    public void doInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 1000;
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("沈哈哈");
            user.setUserAccount("fakechen_" + i);
            user.setAvatarUrl("https://raw.githubusercontent.com/janhjchen/remote-repo/main/docs/assets/i.png");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("123");
            user.setEmail("123@qq.com");
            user.setTags("[]");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode("1" + i);
            userList.add(user);
        }
        //  14 (14101) 秒 10 万条
        //  2min25sec (144752) 秒 100 万条

        userService.saveBatch(userList, 10000);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    /**
     * 并发批量插入用户
     */
    @Test
    public void doConcurrencyInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 分十组
        int batchSize = 25000;
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            List<User> userList = new ArrayList<>();
            while (true) {
                j++;
                User user = new User();
                user.setUsername("陈哈哈");
                user.setUserAccount("fakechen_" + i);
                user.setAvatarUrl("https://raw.githubusercontent.com/janhjchen/remote-repo/main/docs/assets/i.png");
                user.setGender(0);
                user.setUserPassword("12345678");
                user.setPhone("123");
                user.setEmail("123@qq.com");
                user.setTags("[]");
                user.setUserStatus(0);
                user.setUserRole(0);
                user.setPlanetCode("1" + i);
                userList.add(user);
                if (j % batchSize == 0) {
                    break;
                }
            }
            // 异步执行
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("threadName: " + Thread.currentThread().getName());
                userService.saveBatch(userList, batchSize);
            }, executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        // 3 (3646) 秒 10 万条
        // 27s445ms (27286) 秒 100 万条
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
