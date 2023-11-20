package com.ss.shoppingweb.service.impl;

import com.ss.shoppingweb.entity.*;
import com.ss.shoppingweb.exception.*;
import com.ss.shoppingweb.mapper.UserMapper;
import com.ss.shoppingweb.service.UserService;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**用户注册*/
    @Override
    public void register(User user) {
        String name=user.getName();
        String phone=user.getPhone();
        String idCard=user.getIdCard();
        String email=user.getEmail();
        /**判断用户名是否重复*/
        User userSql = userMapper.findUserByName(name);
        if (userSql != null) {
            throw new NameDuplicateException("用户名" + name + "重复！");
        }
        /**判断手机号是否已经注册过*/
        userSql = userMapper.findUserByPhone(phone);
        if (userSql != null) {
            throw new PhoneDuplicateException("手机号" + phone + "已经注册过！");
        }
        /**判断身份证号是否已经注册过*/
        userSql = userMapper.findUserByIdcard(idCard);
        if (userSql != null) {
            throw new IdcardDuplicateException("身份证号" + idCard + "已经注册过！");
        }
        /**判断邮箱是否已经注册过*/
        userSql= userMapper.findUserByEmail(email);
        if (userSql != null) {
            throw new EmailDuplicateException("邮箱" + email + "已经注册过！");
        }
        /**密码加密*/
        String password=passwordEncoder.encode(user.getPassword());
        user.setPassword(password);
        /**判断用户数据知否正常插入数据库*/
        Integer rows1 = userMapper.insertUser(user);
        if (rows1 != 1) {
            throw new InsertException("添加用户数据出现未知错误，请联系系统管理员");
        }
        /**关联个人账户*/
        User userInserted=userMapper.findUserByName(name);
        UserAccount userAccount=new UserAccount();
        userAccount.setOwnerId(userInserted.getId());
        userAccount.setAmount(0);
        Integer rows2 = userMapper.insertUserAccount(userAccount);
        if(rows2!=1){
            throw new InsertException("添加用户账户数据出现未知错误，请联系系统管理员");
        }
    }

    /**用户登录*/
    @Override
    public User login(String name,String password){
        //根据用户名查询用户数据
        User user=userMapper.findUserByName(name);
        //判断用户是否存在
        if(user==null){
            throw new UserNotFoundException("用户"+name+"不存在！");
        }
        //判断密码是否输入正确
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new PasswordNotMatchException("密码输入不正确！");
        }
        return user;
    }

    /**信息修改*/
    @Override
    public void updateData(User user){
        String name=user.getName();
        String phone=user.getPhone();
        String email=user.getEmail();
        String idCard=user.getIdCard();
        /**判断用户名是否重复*/
        User userSql = userMapper.findUserByNameExcpIdcard(name,idCard);
        if (userSql != null) {
            throw new NameDuplicateException("用户名" + name + "重复！");
        }
        /**判断手机号是否重复*/
        userSql = userMapper.findUserByPhoneExcpIdcard(phone,idCard);
        if (userSql != null) {
            throw new PhoneDuplicateException("手机号" + phone + "重复！");
        }
        /**判断邮箱是否重复*/
        userSql= userMapper.findUserByEmailExcpIdcard(email,idCard);
        if (userSql != null) {
            throw new EmailDuplicateException("邮箱" + email + "重复！");
        }
        /**密码加密*/
        String password=passwordEncoder.encode(user.getPassword());
        user.setPassword(password);
        /**修复数据*/
        Integer rows=userMapper.updateData(user);
        if(rows!=1){
            throw new InsertException("修改用户数据出现未知错误，请联系系统管理员");
        }
    }

    /**获取数据*/
    @Override
    public User getData(String name){
        User userSql=userMapper.findUserByName(name);
        return userSql;
    }

    /**获取账户数据*/
    @Override
    public UserAccount getAccount(String name){
        //获取用户数据
        User userSql=userMapper.findUserByName(name);
        //获取账户数据
        UserAccount userAccountSql=userMapper.findUserAccountByOwnerId(userSql.getId());
        return userAccountSql;
    }

    /**获取流水记录*/
    @Override
    public List<UserAccountRecorder> getAccountRecorder(Integer id,Integer timeInterval){
        //获取用户数据
        List<UserAccountRecorder> userAccountRecorder;
        //获取全部账户流水
        if(timeInterval==0){
           userAccountRecorder=userMapper.findUserAccountRecorderByUserId(id);
        }
        //获取近七天或一个月的账户流水
        else{
            LocalDateTime today=LocalDateTime.now();
            LocalDateTime previous=today.minusDays(timeInterval);
            userAccountRecorder=userMapper.findUserAccountRecorderByUserIdLimitTime(id,previous);
        }

        return userAccountRecorder;
    }

    /**充值*/
    @Override
    public void rechargeAccount(Integer ownerId,double amount){
        //根据id查账户
        UserAccount userAccount=userMapper.findUserAccountByOwnerId(ownerId);
        //修改账户
        double amountAdded=amount+userAccount.getAmount();
        Integer rows=userMapper.updateAccount(amountAdded, ownerId);
        if(rows!=1){
            throw new InsertException("修改账户出现问题，请联系系统管理员！");
        }
    }


    //将商品添加到购物车
    public void addToShoppingCart(ShoppingCart shoppingCart){
        ShoppingCart shoppingCart1 = userMapper.getShoppingCartDataByUserIdAndCommodityId(shoppingCart.getUserId(),shoppingCart.getCommodityId());
        if(shoppingCart1==null){
            Commodity commodity = userMapper.getCommodityDataById(shoppingCart.getCommodityId());
            Shop shop = userMapper.getShopDataByShopId(commodity.getShopId());
            shoppingCart.setBusinessState(commodity.getBusinessState());
            shoppingCart.setCommodityCategoryName(commodity.getCategoryName());
            shoppingCart.setCommodityName(commodity.getCommodityName());
            shoppingCart.setIntroduction(commodity.getIntroduction());
            shoppingCart.setPrice(commodity.getPrice());
            shoppingCart.setImageUrl(commodity.getImageUrl());
            shoppingCart.setCommodityShopName(shop.getShopName());
            Integer rows = userMapper.addToShoppingCart(shoppingCart);
            if(rows!=1){
                throw new InsertException("商品加入购物车出现问题，请联系系统管理员！");
            }
        }else{
            shoppingCart1.setCommodityNum(shoppingCart.getCommodityNum()+shoppingCart1.getCommodityNum());
            userMapper.addShoppingCartCommodityNumber(shoppingCart1);
        }
    }

    /**将购物车中指定商品数量加1*/
    public void addShoppingCartCommodityNumber(Integer id){
        ShoppingCart shoppingCart = userMapper.getShoppingCartDataById(id);
        shoppingCart.setCommodityNum(shoppingCart.getCommodityNum()+1);
        userMapper.addShoppingCartCommodityNumber(shoppingCart);
    };

    /**将购物车中指定商品数量减1*/
    public void subShoppingCartCommodityNumber(Integer id) {
        ShoppingCart shoppingCart = userMapper.getShoppingCartDataById(id);
        if (shoppingCart.getCommodityNum() == 1) {
            userMapper.deleteShoppingCart(id);

        } else {
            shoppingCart.setCommodityNum(shoppingCart.getCommodityNum() - 1);
            userMapper.subShoppingCartCommodityNumber(shoppingCart);
        }
    };


    //展示购物车信息
    public List<ShoppingCart> showShoppingCart(Integer id) throws IOException {
        List<ShoppingCart> shoppingCarts = userMapper.showShoppingCart(id);
        for (ShoppingCart shoppingCart:shoppingCarts) {
            if (shoppingCart.getBusinessState() == 1) {
                String urls = shoppingCart.getImageUrl();
                String[] realUrls = urls.split(",");
                shoppingCart.setImageUrls(realUrls);
            }
        }
        return shoppingCarts;

    };

    //删除商品
    public void deleteShoppingcart(List<Integer> ids){
        for(Integer id : ids){
            Integer rows = userMapper.deleteShoppingCart(id);
            if(rows!=1){
                throw new InsertException("商品移出购物车出现问题，请联系系统管理员！");
            }
        }
    };

    /**根据用户id查找收货地址*/
    public List<ShippingAddress> findUserShippingAddressByUserId(Integer userId){
        return userMapper.findUserShippingAddressByUserId(userId);
    };

    /**新建收货地址*/
    public void addShippingAddress(ShippingAddress shippingAddress){
        userMapper.addShippingAddress(shippingAddress);
    };

    /**删除收货地址*/
    public void deleteShippingAddress(Integer id){
        userMapper.deleteShippingAddress(id);
    };


    /**修改收货地址*/
    public void updateShippingAddress(ShippingAddress shippingAddress){
        userMapper.updateShippingAddress(shippingAddress);
    };

    /**根据收货地址id查找收货地址*/
    public ShippingAddress findUserShippingAddressById(Integer id){
        return  userMapper.findUserShippingAddressById(id);
    };

    //创建订单
    public void createOrder(Orders order){
        order.setPurchaseTime(LocalDateTime.now());
        order.setPayState(0);
        order.setWithdrawState(0);
        order.setDeliveryState(0);
        order.setFinishState(0);
        order.setRefundRequest(0);
        order.setRefundState(0);
        Integer rows=userMapper.insertOrders(order);
        if(rows!=1){
            throw new InsertException("向数据库中导入订单信息出错，请联系系统管理员！");
        }
    }
    /**
     * 根据用户id查找该用户拥有的优惠券
     */
    public List<Coupon> findUserCouponByUserId(Integer userId){
        return  userMapper.findUserCouponByUserId(userId);
    };


    /**判断用户余额是否充足*/
    public void judgeUserAccount(Integer userId,Orders order){
        //根据用户id获取用户账户
        UserAccount userAccount=userMapper.findUserAccountByOwnerId(userId);
        //根据订单id获取订单信息
        Orders orderSql=userMapper.findOrderByOrderId(order.getId());
        if(orderSql.getAmountSum()>userAccount.getAmount()){
            throw new PocketNotAdequateException("用户余额不足，请充值！");
        }
    }

    /**支付订单*/
    public void payOrder(Integer orderId){
        //根据订单编号获取订单信息
        Orders order=userMapper.findOrderByOrderId(orderId);
        UserAccount userAccount=userMapper.findUserAccountByOwnerId(order.getUserId());
        //修改订单支付状态
        Integer rows1=userMapper.updatePayStateBYOrderId(orderId);
        if(rows1!=1){
            throw new UpdateException("修改订单支付状态出错，请联系系统管理员！");
        }
        //用户余额减少，增加流水记录
        double amount1=userAccount.getAmount()-order.getAmountSum();
        Integer rows2=userMapper.updateAccount(amount1,order.getUserId());
        if(rows2!=1){
            throw new UpdateException("修改用户账户出错，请联系系统管理员！");
        }
        UserAccountRecorder userAccountRecorder=new UserAccountRecorder();
        userAccountRecorder.setUserId(order.getUserId());
        userAccountRecorder.setInitiatorRole(1);
        userAccountRecorder.setInitiatorId(order.getUserId());
        userAccountRecorder.setInitiatorName(order.getUserName());
        userAccountRecorder.setReceiverRole(3);
        userAccountRecorder.setReceiverId(order.getShopId());
        userAccountRecorder.setReceiverName(order.getShopName());
        userAccountRecorder.setTradeRecord("购买商品");
        userAccountRecorder.setAmount(order.getAmountSum());
        userAccountRecorder.setTradeTime(LocalDateTime.now());
        userAccountRecorder.setInAndout(-1);
        Integer rows3=userMapper.insertUserAccountRecorder(userAccountRecorder);
        if(rows3!=1){
            throw new InsertException("插入用户流水出错，请联系系统管理员！");
        }
        //中间商城账户余额增加，增加流水记录
        MiddleAccount middleAccount=userMapper.findMiddleAccount();
        double amount2=middleAccount.getAmount()+order.getAmountSum();
        Integer rows4=userMapper.updateMiddleAccount(amount2);
        if(rows4!=1){
            throw new UpdateException("修改中间账户出错，请联系系统管理员！");
        }
        MiddleAccountRecorder middleAccountRecorder=new MiddleAccountRecorder();
        middleAccountRecorder.setInitiatorRole(1);
        middleAccountRecorder.setInitiatorId(order.getUserId());
        middleAccountRecorder.setInitiatorName(order.getUserName());
        middleAccountRecorder.setReceiverRole(4);
        middleAccountRecorder.setReceiverId(1);
        middleAccountRecorder.setReceiverName("中间商城账户");
        middleAccountRecorder.setAmount(order.getAmountSum());
        middleAccountRecorder.setTradeTime(LocalDateTime.now());
        middleAccountRecorder.setTradeRecord("购买商品");
        middleAccountRecorder.setInAndout(1);
        Integer rows5=userMapper.insertMiddleAccountRecorder(middleAccountRecorder);
        if(rows5!=1){
            throw new InsertException("插入中间账户流水出错，请联系系统管理员！");
        }
    }

    /**撤销订单*/
    public void withdrawOrders(Integer orderId){
        //将对应的订单的withdrawState修改为1
        Integer rows1=userMapper.updateWithdrawStatwByOrderId(orderId);
        if(rows1!=1){
            throw new UpdateException("修改订单撤销状态出错，请联系系统管理员！");
        }
    }

    /**删除订单*/
    public void deleteOrders(Integer orderId){
        Integer rows1=userMapper.deleteOrderByOrderId(orderId);
        if(rows1!=1){
            throw new DeleteException("删除订单出错，请联系系统管理员！");
        }
    }

    /**获取待支付订单*/
    public List<Orders> getToPayOrders(Integer userId){
        return userMapper.getToPayOrdersByUserId(userId);
    }

    /**获取已撤销订单*/
    public List<Orders> getHaveWithdrawOrders(Integer userId) {
        return userMapper.getHaveWithdrawOrdersByUserId(userId);
    }

    /**获取待发货订单*/
    public List<Orders> getToDeliveryOrders(Integer userId){
        return userMapper.getToDeliveryOrdersByUserId(userId);
    }

    /**获取待收货订单*/
    public List<Orders> getHaveDeliveryOrders(Integer userId){
        return userMapper.getHaveDeliveryOrdersByUserId(userId);
    }

    /**请求退款退货*/
    @Override
    public void requestRefund(Integer orderId) {
        Integer rows1=userMapper.UpdateRefundRequestByOrderId(orderId);
        if(rows1!=1){
            throw new UpdateException("修改订单退款请求失败，请联系系统管理员！");
        }
    }

    /**获取待退款订单*/
    @Override
    public List<Orders> getToRefundOrders(Integer userId) {
        return userMapper.getToRefundOrdersByUserId(userId);
    }

    /**获取已退款订单*/
    @Override
    public List<Orders> getHaveRefundOrders(Integer userId) {
        return userMapper.getHaveRefundOrdersByUserId(userId);
    }

    /**获取已完成订单*/
    @Override
    public List<Orders> getHaveFinishOrders(Integer userId) {
        return userMapper.getHaveFinishOrdersByUserId(userId);
    }

    /**确认收货*/
    @Override
    public void confirmFinishOrders(Integer orderId) {
        //修改订单状态为完成
        Integer rows1=userMapper.UpdateFinishStateByOrderId(orderId);
        if(rows1!=1){
            throw new UpdateException("修改订单完成状态失败，请联系系统管理员");
        }
        //获取订单信息
        Orders order=userMapper.findOrderByOrderId(orderId);
        DecimalFormat df = new DecimalFormat("#.##");
        double AdminAmount=Double.parseDouble(df.format(order.getAmountSum()*0.05));
        double ShopAmount=Double.parseDouble(df.format(order.getAmountSum()*0.95));
        //商城中间商户资金减少
        MiddleAccount middleAccount=userMapper.findMiddleAccount();
        double amount1=middleAccount.getAmount()-order.getAmountSum();
        Integer rows2=userMapper.updateMiddleAccount(amount1);
        if(rows2!=1){
            throw new UpdateException("修改中间商城出错，请联系系统管理员！");
        }
        MiddleAccountRecorder middleAccountRecorder1=new MiddleAccountRecorder();
        middleAccountRecorder1.setInitiatorRole(4);
        middleAccountRecorder1.setInitiatorId(1);
        middleAccountRecorder1.setInitiatorName("商城中间账户");
        middleAccountRecorder1.setReceiverRole(5);
        middleAccountRecorder1.setReceiverId(1);
        middleAccountRecorder1.setReceiverName("商城利润账户");
        middleAccountRecorder1.setAmount(AdminAmount);
        middleAccountRecorder1.setTradeTime(LocalDateTime.now());
        middleAccountRecorder1.setTradeRecord("商品交易佣金");
        middleAccountRecorder1.setInAndout(-1);
        Integer rows3=userMapper.insertMiddleAccountRecorder(middleAccountRecorder1);
        if(rows3!=1){
            throw new InsertException("导入中间商城流水记录失败,请联系系统管理员！");
        }
        MiddleAccountRecorder middleAccountRecorder2=new MiddleAccountRecorder();
        middleAccountRecorder2.setInitiatorRole(4);
        middleAccountRecorder2.setInitiatorId(1);
        middleAccountRecorder2.setInitiatorName("商城中间账户");
        middleAccountRecorder2.setReceiverRole(3);
        middleAccountRecorder2.setReceiverId(order.getShopId());
        middleAccountRecorder2.setReceiverName(order.getShopName());
        middleAccountRecorder2.setAmount(ShopAmount);
        middleAccountRecorder2.setTradeTime(LocalDateTime.now());
        middleAccountRecorder2.setTradeRecord("商品交易所得");
        middleAccountRecorder2.setInAndout(-1);
        Integer rows4=userMapper.insertMiddleAccountRecorder(middleAccountRecorder2);
        if(rows4!=1){
            throw new InsertException("导入中间商城流水记录失败，请联系系统管理员！");
        }
        //商城利润账户资金增加
        AdminAccount adminAccount=userMapper.findAdminAccount();
        double amount2=adminAccount.getAmount()+AdminAmount;
        Integer rows5=userMapper.UpdateAdminAccount(amount2);
        if(rows5!=1){
            throw new UpdateException("修改商城利润账户失败，请联系系统管理员！");
        }
        AdminAccountRecorder adminAccountRecorder=new AdminAccountRecorder();
        adminAccountRecorder.setAdminId(1);
        adminAccountRecorder.setInitiatorRole(4);
        adminAccountRecorder.setInitiatorId(1);
        adminAccountRecorder.setInitiatorName("商城中间账户");
        adminAccountRecorder.setReceiverRole(5);
        adminAccountRecorder.setReceiverId(1);
        adminAccountRecorder.setReceiverName("商城利润账户");
        adminAccountRecorder.setAmount(AdminAmount);
        adminAccountRecorder.setTradeTime(LocalDateTime.now());
        adminAccountRecorder.setTradeRecord("商品交易佣金");
        adminAccountRecorder.setInAndout(1);
        Integer rows6=userMapper.insertAdminAccountRecorder(adminAccountRecorder);
        if(rows6!=1){
            throw new InsertException("导入商城利润账户流水失败，请联系系统管理员！");
        }
        //商店账户资金增加,如果有满减则要将满减额度发往商户
        ShopAccount shopAccount=userMapper.findShopAccountByShopId(order.getShopId());
        double amount3=shopAccount.getAmount()+ShopAmount;
        Integer rows7=userMapper.updateShopAccount(amount3,order.getShopId());
        if(rows7!=1){
            throw new UpdateException("修改商店账户失败，请联系系统管理员！");
        }
        ShopAccountRecorder shopAccountRecorder=new ShopAccountRecorder();
        shopAccountRecorder.setShopId(order.getShopId());
        shopAccountRecorder.setInitiatorRole(4);
        shopAccountRecorder.setInitiatorId(1);
        shopAccountRecorder.setInitiatorName("商城中间账户");
        shopAccountRecorder.setReceiverRole(3);
        shopAccountRecorder.setReceiverId(order.getShopId());
        shopAccountRecorder.setReceiverName(order.getShopName());
        shopAccountRecorder.setAmount(ShopAmount);
        shopAccountRecorder.setTradeTime(LocalDateTime.now());
        shopAccountRecorder.setTradeRecord("商品完成交易所得");
        shopAccountRecorder.setInAndout(1);
        Integer rows8=userMapper.insertShopAccountRecorder(shopAccountRecorder);
        if(rows8!=1){
            throw new InsertException("导入商店账户流水失败，请联系系统管理员！");
        }
        //判断满减是否为0，若不为0则将满减金额发给shop
        if(Math.abs(order.getReducedPrice())>1e-2){
            Activity activity=userMapper.getActivityDataById(order.getActivityId());
            Integer rows9=userMapper.updateActivityFunds(order.getActivityId(),activity.getFunds()-order.getReducedPrice());
            if(rows9!=1){
                throw new UpdateException("修改活动补贴资金异常，请联系系统管理员！");
            }
            MiddleAccount middleAccount1=userMapper.findMiddleAccount();
            Integer rows10=userMapper.updateMiddleAccount(middleAccount1.getAmount()-order.getReducedPrice());
            if(rows10!=1){
                throw new UpdateException("修改中间商城出错，请联系系统管理员！");
            }
            MiddleAccountRecorder middleAccountRecorder3=new MiddleAccountRecorder();
            middleAccountRecorder3.setInitiatorRole(4);
            middleAccountRecorder3.setInitiatorId(1);
            middleAccountRecorder3.setInitiatorName("商城中间账户");
            middleAccountRecorder3.setReceiverRole(3);
            middleAccountRecorder3.setReceiverId(order.getShopId());
            middleAccountRecorder3.setReceiverName(order.getShopName());
            middleAccountRecorder3.setAmount(order.getReducedPrice());
            middleAccountRecorder3.setTradeTime(LocalDateTime.now());
            middleAccountRecorder3.setTradeRecord("活动满减补贴资金");
            middleAccountRecorder3.setInAndout(-1);
            Integer rows11=userMapper.insertMiddleAccountRecorder(middleAccountRecorder3);
            if(rows11!=1){
                throw new InsertException("导入中间商城流水记录失败，请联系系统管理员！");
            }
            ShopAccount shopAccount1=userMapper.findShopAccountByShopId(order.getShopId());
            Integer rows12=userMapper.updateShopAccount(shopAccount1.getAmount()+order.getReducedPrice(),order.getShopId());
            if(rows12!=1){
                throw new UpdateException("修改商店账户失败，请联系系统管理员！");
            }
            ShopAccountRecorder shopAccountRecorder1=new ShopAccountRecorder();
            shopAccountRecorder1.setShopId(order.getShopId());
            shopAccountRecorder1.setInitiatorRole(4);
            shopAccountRecorder1.setInitiatorId(1);
            shopAccountRecorder1.setInitiatorName("商城中间账户");
            shopAccountRecorder1.setReceiverRole(3);
            shopAccountRecorder1.setReceiverId(order.getShopId());
            shopAccountRecorder1.setReceiverName(order.getShopName());
            shopAccountRecorder1.setAmount(order.getReducedPrice());
            shopAccountRecorder1.setTradeTime(LocalDateTime.now());
            shopAccountRecorder1.setTradeRecord("活动满减补贴资金");
            shopAccountRecorder1.setInAndout(1);
            Integer rows13=userMapper.insertShopAccountRecorder(shopAccountRecorder1);
            if(rows13!=1){
                throw new InsertException("导入商店账户流水失败，请联系系统管理员！");
            }
        }
        //修改商店月销售量和销售额
        Shop shop=userMapper.getShopDataByShopId(order.getShopId());
        Integer rows13=userMapper.updateMonthlySalesMoneyByShopId(order.getShopId(),shop.getMonthlySalesMoney()+order.getAmountSum());
        if(rows13!=1){
            throw new UpdateException("修改商店月销售额失败，请联系系统管理员！");
        }
        Integer rows14=userMapper.updateMonthlySalesCountByShopId(order.getShopId(),shop.getMonthlySalesCount()+order.getCommodityNum());
        if(rows14!=1){
            throw new UpdateException("修改商店月销售量失败，请联系系统管理员！");
        }
        //修改商品的月销售量
        Commodity commodity=userMapper.getCommodityDataById(order.getCommodityId());
        Integer rows15=userMapper.updateMonthlySalesCountByCommodityId(order.getCommodityId(),commodity.getMonthlySalesCount()+order.getCommodityNum());
        if(rows15!=1){
            throw new UpdateException("修改商品月销售量失败，请联系系统管理员！");
        }

    }

    /**查询此次下单，每个活动能减免多少*/
    public List<ActivityWithReducedPrice> getTotalReducedMoney(List<Orders> orders){
        //新建一个数据表，用以返回
        List<ActivityWithReducedPrice> activityWithReducedPriceList = new ArrayList<ActivityWithReducedPrice>();
        //获取当前举行的活动
        List<Activity> activities = userMapper.getActivitiesNow();
        //对每个活动，经行遍历
        for (Activity activity : activities){
            //每个活动创建一个activityWithReducedPrice对象来存储，此次下单中，参与该活动的商品的金额总数和满减额度
            ActivityWithReducedPrice activityWithReducedPrice = new ActivityWithReducedPrice(activity.getId(),0,0);
            //对下单中的所有商品遍历
            for (Orders order : orders){
                //通过id获取商品数据
                Commodity commodity = userMapper.getCommodityDataById(order.getCommodityId());
                //如果商品参与了此次活动，则计数
                if(commodity.getActivityId()==activity.getId()){
                    activityWithReducedPrice.setTotalPrice(activityWithReducedPrice.getTotalPrice()+commodity.getPrice()*order.getCommodityNum());
                }
            }
            //总价除以X，得出要满减几次
            int times = (int)activityWithReducedPrice.getTotalPrice()/activity.getX();
            if(times==0){
                //该活动不满减
            }
            else{
                //该活动要满减
                activityWithReducedPrice.setReducedPrice(activity.getY()*times);
                activityWithReducedPriceList.add(activityWithReducedPrice);
            }
        }
        return  activityWithReducedPriceList;
    };

    /**根据活动ID查找活动数据*/
    public Activity getActivityDataById(Integer id){
        return  userMapper.getActivityDataById(id);
    };

    /**活动结束，所有参与该活动的商品结束参与活动状态,删除活动*/
    public void activityOver(Integer activityId){
        userMapper.activityOverAffectCommodity(activityId);
        userMapper.activityOverAffectShop(activityId);
        userMapper.activityOver(activityId);
    };

    /**查询某一活动中商品*/
    public List<Commodity> showCommoditiesInOneActivity(Integer id){
        return  userMapper.showCommoditiesInOneActivity(id);
    };

    /**首页推荐商品，按销量*/
    public List<Commodity> showRecommendedCommodities(){
        return userMapper.showRecommendedCommodities();
    };


    /**首页搜索商品，按销量排序*/
    public List<Commodity> searchCommodity(String string){
        return  userMapper.searchCommodity(string);
    };

    /**获取activityId*/
    public Integer getActivityIdByCommodityId(Integer commodityId){
        return userMapper.getActivityIdByCommodityId(commodityId);
    }

    /**删除购物车*/
    @Override
    public void deleteShoppingcartByUserIdAndCommodityId(Integer userId, Integer commodityId) {
        userMapper.deleteShoppingcartByUserIdAndCommodityId(userId,commodityId);
    }


}
