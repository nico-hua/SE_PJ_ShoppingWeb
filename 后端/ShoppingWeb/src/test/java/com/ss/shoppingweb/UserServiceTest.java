package com.ss.shoppingweb;

import com.ss.shoppingweb.service.UserService;
import com.ss.shoppingweb.service.impl.UserServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserServiceTest extends  ShoppingWebApplicationTests {
    @Autowired
    private UserServiceImpl userService;

    @Test
    public void showCommoditiesInOneActivity(){
        Assert.assertSame("",1,userService.showCommoditiesInOneActivity(1).get(0).getId());
    }

}
