package com.ss.shoppingweb.controller;

import com.ss.shoppingweb.entity.*;
import com.ss.shoppingweb.exception.UpdateException;
import com.ss.shoppingweb.service.UserService;
import com.ss.shoppingweb.utils.JsonResult;
import com.ss.shoppingweb.utils.JwtUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class  UserController extends BaseController{
    @Autowired
    private UserService userService;

    /**用户注册*/
    @PostMapping("/register")
    public JsonResult<Void> register(@RequestBody User user) {
        // 调用业务对象执行注册
        userService.register(user);
        // 返回
        return new JsonResult<>(OK);
    }

    /**用户登录*/
    @PostMapping("/login")
    public JsonResult<String> login(@RequestBody User user){
        // 调用业务对象的方法执行登录，并获取返回值
        User data = userService.login(user.getName(), user.getPassword());
        String jwt;
        if(data!=null){
            Map<String, Object> claims= new HashMap<>();
            claims.put("name",data.getName());
            //标识普通用户的role为1
            claims.put("role","1");
            claims.put("id",data.getId());
            jwt= JwtUtils.generateJwt(claims);
        }
        else{
            jwt=null;
        }
        // 将以上返回值和状态码OK封装到响应结果中并返回
        return new JsonResult<>(OK, jwt);
    }

    /**用户信息修改*/
    @PostMapping("/updateData")
    public JsonResult<Void> updateData(@RequestBody User user){
        //调用业务层执行修改
        userService.updateData(user);
        //返回
        return new JsonResult<>(OK);
    }

    /**账户充值*/
    @PostMapping("/rechargeAccount")
    public JsonResult<Void> rechargeAccount(@RequestBody UserAccount userAccount){
        userService.rechargeAccount(userAccount.getOwnerId(),userAccount.getAmount());
        return new JsonResult<>(OK);
    }

    //将商品添加到购物车
    @PostMapping("/addToShoppingCart")
    public JsonResult<Void> addToShoppingCart(@RequestBody ShoppingCart shoppingCart){
        userService.addToShoppingCart(shoppingCart);
        return  new JsonResult<>(OK);
    };


    //展示购物车信息
    @PostMapping("/showShoppingCart")
    public JsonResult<List<ShoppingCart>> showShoppingCart(@RequestBody ShoppingCart shoppingCart) throws IOException {
        List<ShoppingCart> data= userService.showShoppingCart(shoppingCart.getUserId());
        return new JsonResult<List<ShoppingCart>>(OK,data);
    };

    //删除商品
    @RequestMapping("/deleteShoppingCart")
    public JsonResult<Void> deleteShoppingCart(@RequestParam String ids){
        Integer length = ids.length();
        String realIds = ids.substring(1,length-1);
        String[] strArr = realIds.split(",");
        ArrayList<Integer> intArr = new ArrayList<>();
        for(String str : strArr){
            Integer id = Integer.valueOf(str);
            intArr.add(id);
        }
        userService.deleteShoppingcart(intArr);
        return new JsonResult<>(OK);
    };

    /**根据用户id查找收货地址*/
    @RequestMapping("/getShippingAddress")
    public JsonResult<List<ShippingAddress>> findUserShippingAddressByUserId(HttpServletRequest request){
        Integer userId=JwtUtils.getJwtId(request.getHeader("token"));
        List<ShippingAddress> data = userService.findUserShippingAddressByUserId(userId);
        return new JsonResult<>(OK,data);
    }


    /**新建收货地址*/
    @PostMapping ("/addShippingAddress")
    public JsonResult<Void> addShippingAddress(@RequestBody ShippingAddress shippingAddress,@RequestHeader("token") String token){
        Integer userId=JwtUtils.getJwtId(token);
        shippingAddress.setUserId(userId);
        userService.addShippingAddress(shippingAddress);
        return new JsonResult<>(OK);
    };

    /**删除收货地址*/
    @PostMapping("/deleteShippingAddress")
    public JsonResult<Void> deleteShippingAddress(@RequestBody ShippingAddress shippingAddress){
        userService.deleteShippingAddress(shippingAddress.getId());
        return new JsonResult<>(OK);
    };

    /**修改收货地址*/
    @RequestMapping("/updateShippingAddress")
    public JsonResult<Void> updateShippingAddress(@RequestBody ShippingAddress shippingAddress){
        userService.updateShippingAddress(shippingAddress);
        return new JsonResult<>(OK);
    };


    /**根据收货地址id查找收货地址*/
    @RequestMapping("/findUserShippingAddressById")
    public JsonResult<ShippingAddress> findUserShippingAddressById(@RequestParam Integer id){
        ShippingAddress data = userService.findUserShippingAddressById(id);
        return new JsonResult<>(OK,data);
    };

    /**接收前端发送的订单信息创建订单*/
    @PostMapping("/createOrders")
    public JsonResult<Void> createOrders(@RequestBody List<Orders> orders,@RequestHeader("token") String token){
        Integer userId=JwtUtils.getJwtId(token);
        String userName=JwtUtils.getJwtName(token);
        //根据commodityId获取activityId
        for(Orders order1:orders){
            Integer activityId=userService.getActivityIdByCommodityId(order1.getCommodityId());
            order1.setActivityId(activityId);
        }
        List<ActivityWithReducedPrice> activityWithReducedPriceList = userService.getTotalReducedMoney(orders);
        //判断活动资金是否还足够
        for (ActivityWithReducedPrice activityWithReducedPrice : activityWithReducedPriceList){
            //不足够，结束活动，抛出异常
            if(activityWithReducedPrice.getReducedPrice()>userService.getActivityDataById(activityWithReducedPrice.getId()).getFunds()){
                userService.activityOver(activityWithReducedPrice.getId());
                throw new UpdateException("此订单中参与活动的商品中，部分活动已结束！请重新下单!");
            }
        }

        //对满减表处理
       for (ActivityWithReducedPrice activityWithReducedPrice : activityWithReducedPriceList){
           //对于每个满减活动，都应该遍历订单，寻找属于该活动的商品
           for(Orders order : orders){
               //属于此项活动时，开始处理
               if(order.getActivityId()==activityWithReducedPrice.getId()){
                   //获取此项商品的总价占该活动所有商品的比例
                   double percent = order.getAmountSum()/activityWithReducedPrice.getTotalPrice();
                   //获取此项商品应该减免的金额
                   double reducedPrice = percent * activityWithReducedPrice.getReducedPrice();
                   //修改order数据
                   order.setAmountSum(order.getAmountSum()-reducedPrice);
                   order.setReducedPrice(reducedPrice);
               }
               else{
                   order.setReducedPrice(0);
               }
           }
       }

        for(Orders order:orders){
            order.setUserId(userId);
            order.setUserName(userName);
            userService.createOrder(order);
        }
        //删除购物车
        for(Orders orders2:orders){
            userService.deleteShoppingcartByUserIdAndCommodityId(userId,orders2.getCommodityId());
        }
        return new JsonResult<>(OK);
    }

    /**支付订单*/
    @PostMapping("/payOrders")
    public JsonResult<Void> payOrders(@RequestBody Orders order,@RequestHeader("token") String token){
        //判断用户余额是否充足
        Integer userId=JwtUtils.getJwtId(token);
        userService.judgeUserAccount(userId,order);
        userService.payOrder(order.getId());
        return new JsonResult<>(OK);
    }

    /**撤销订单*/
    @PostMapping("/withdrawOrders")
    public  JsonResult<Void> withdrawOrders(@RequestBody Orders order){
        userService.withdrawOrders(order.getId());
        return new JsonResult<>(OK);
    }

    /**删除订单*/
    @PostMapping("/deleteOrders")
    public JsonResult<Void> deleteOrders(@RequestBody Orders order){
        userService.deleteOrders(order.getId());
        return new JsonResult<>(OK);
    }

    /**展示待支付订单*/
    @RequestMapping("/getToPayOrders")
    public JsonResult<List<Orders>> getToPayOrders(HttpServletRequest request){
        Integer userId=JwtUtils.getJwtId(request.getHeader("token"));
        List<Orders> data=userService.getToPayOrders(userId);
        return new JsonResult<>(OK,data);
    }

    /**展示已撤销订单*/
    @RequestMapping("/getHaveWithdrawOrders")
    public JsonResult<List<Orders>> getHaveWithdrawOrders(HttpServletRequest request){
        Integer userId=JwtUtils.getJwtId(request.getHeader("token"));
        List<Orders> data=userService.getHaveWithdrawOrders(userId);
        return new JsonResult<>(OK,data);
    }

    /**展示待发货订单*/
    @RequestMapping("/getToDeliveryOrders")
    public JsonResult<List<Orders>> getToDeliveryOrders(HttpServletRequest request){
        Integer userId=JwtUtils.getJwtId(request.getHeader("token"));
        List<Orders> data=userService.getToDeliveryOrders(userId);
        return new JsonResult<>(OK,data);
    }

    /**展示待收货订单*/
    @RequestMapping("/getToFinishOrders")
    public JsonResult<List<Orders>> getToFinishOrders(HttpServletRequest request){
        Integer userId=JwtUtils.getJwtId(request.getHeader("token"));
        List<Orders> data=userService.getHaveDeliveryOrders(userId);
        return new JsonResult<>(OK,data);
    }

    /**请求退款退货*/
    @PostMapping("/requestRefund")
    public JsonResult<Void> requestRefund(@RequestBody Orders order){
        userService.requestRefund(order.getId());
        return new JsonResult<>(OK);
    }

    /**展示待退款订单*/
    @RequestMapping("/getToRefundOrders")
    public JsonResult<List<Orders>> getToRefundOrders(HttpServletRequest request){
        Integer userId=JwtUtils.getJwtId(request.getHeader("token"));
        List<Orders> data=userService.getToRefundOrders(userId);
        return new JsonResult<>(OK,data);
    }

    /**展示已退款订单*/
    @RequestMapping("/getHaveRefundOrders")
    public JsonResult<List<Orders>> getHaveRefundOrders(HttpServletRequest request){
        Integer userId=JwtUtils.getJwtId(request.getHeader("token"));
        List<Orders> data=userService.getHaveRefundOrders(userId);
        return new JsonResult<>(OK,data);
    }

    /**展示已完成订单*/
    @RequestMapping("getHaveFinishOrders")
    public JsonResult<List<Orders>> getHaveFinishOrders(HttpServletRequest request){
        Integer userId=JwtUtils.getJwtId(request.getHeader("token"));
        List<Orders> data=userService.getHaveFinishOrders(userId);
        return new JsonResult<>(OK,data);
    }

    /**确认收货*/
    @PostMapping("/confirmFinish")
    public JsonResult<Void> confirmFinish(@RequestBody Orders order){
        userService.confirmFinishOrders(order.getId());
        return new JsonResult<>(OK);
    }
    /**
     * 根据用户id查找该用户拥有的优惠券
     */
    @RequestMapping("/findUserCouponByUserId")
    public JsonResult<List<Coupon>> findUserCouponByUserId(Integer userId){
        List<Coupon> data = userService.findUserCouponByUserId(userId);
        return  new JsonResult<>(OK,data);
    };

    /**查询此次下单，每个活动能减免多少*/
    @RequestMapping("/getTotalReducedMoney")
    public JsonResult<List<ActivityWithReducedPrice>> getTotalReducedMoney(@RequestBody List<Orders> orders){
            List<ActivityWithReducedPrice> data = userService.getTotalReducedMoney(orders);
        return new JsonResult<>(OK,data);
    };

    /**查询某一活动中商品*/
    @RequestMapping("/showCommoditiesInOneActivity")
    public JsonResult<List<Commodity>> showCommoditiesInOneActivity(@RequestBody Activity activity){
        List<Commodity> data = userService.showCommoditiesInOneActivity(activity.getId());
        return new JsonResult<>(OK,data);
    };

    /**首页推荐商品，按销量*/
    @RequestMapping("/showRecommendedCommodities")
    public JsonResult<List<Commodity>> showRecommendedCommodities(){
        List<Commodity> data = userService.showRecommendedCommodities();
        return new JsonResult<>(OK,data);
    };

    /**首页搜索商品，按销量排序*/
    @RequestMapping("/searchCommodity")
    public JsonResult<List<Commodity>> searchCommodity(@RequestBody String string){
        List<Commodity> data = userService.searchCommodity(string);
        return new JsonResult<>(OK,data);
    };
}
