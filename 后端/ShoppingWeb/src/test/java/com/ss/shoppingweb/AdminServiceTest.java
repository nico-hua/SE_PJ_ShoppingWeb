package com.ss.shoppingweb;

import com.ss.shoppingweb.entity.Activity;
import com.ss.shoppingweb.service.impl.AdminServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AdminServiceTest extends ShoppingWebApplicationTests {
    @Autowired
    private AdminServiceImpl adminService;

    @Test
    public void testHoldActivity(){
        Activity activity = new Activity();
        activity.setId(1);
        activity.setHoldingDays(4);
        activity.setFunds(1000.0);
        activity.setX(100);
        activity.setY(20);
        activity.setFundsLimit(100.0);
        activity.setMonthlySalesCountLimit(10);
        activity.setMonthlySalesMoneyLimit(100.0);
        activity.setCommodityCategories("food");
        adminService.holdActivity(activity);
        Activity activity1 = adminService.getActivityDataById(5);
        Assert.assertSame("设置活动持续时间错误",4,activity1.getHoldingDays());
    }

    @Test
    public void testGetActivityDataById(){
        Activity activity1 = adminService.getActivityDataById(3);
        Assert.assertSame("设置活动持续时间错误",4,activity1.getHoldingDays());
    }
}
