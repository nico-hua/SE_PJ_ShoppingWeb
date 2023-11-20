package com.ss.shoppingweb;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShoppingWebApplication.class) //设置springboot启动类
@WebAppConfiguration//由于是Web项目，Junit需要模拟ServletContext，因此我们需要给我们的测试类加上@WebAppConfiguration。
class ShoppingWebApplicationTests {

    @Before
    public void init() {
        System.out.println("junit开始测试-----------------");
    }

    @After
    public void after() {
        System.out.println("junit测试结束-----------------");
    }


}
