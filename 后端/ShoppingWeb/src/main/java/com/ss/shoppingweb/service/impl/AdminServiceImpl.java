package com.ss.shoppingweb.service.impl;

import com.ss.shoppingweb.entity.*;
import com.ss.shoppingweb.mapper.AdminMapper;
import com.ss.shoppingweb.service.AdminService;
import com.ss.shoppingweb.exception.InsertException;
import com.ss.shoppingweb.exception.PasswordNotMatchException;
import com.ss.shoppingweb.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminMapper adminMapper;

    /**获取所有开店待审核的店铺*/
    public List<Shop> showAllOpenShopToBeReviewed(){
        List<Shop> shops=adminMapper.showAllShop(0);
        for(Shop shop:shops){
            List<String> list=adminMapper.findCommoditycategoryByShopId(shop.getId());
            String category=String.join(",",list);
            shop.setCategory(category);
        }
        return shops;
    }

    /**获取所有关店待审核的店铺*/
    public List<ShopHint> showAllCloseShopToBeReviewed(){
        List<Shop> shops=adminMapper.showAllShop(-1);
        for(Shop shop:shops){
            List<String> list=adminMapper.findCommoditycategoryByShopId(shop.getId());
            String category=String.join(",",list);
            shop.setCategory(category);
        }
        List<ShopHint> shopHints=new ArrayList<>();
        for(Shop shop:shops){
            ShopHint shopHint=new ShopHint();
            shopHint.setId(shop.getId());
            shopHint.setMerchantId(shop.getId());
            shopHint.setCategory(shop.getCategory());
            shopHint.setShopName(shop.getShopName());
            shopHint.setIdCard(shop.getIdCard());
            shopHint.setAddress(shop.getAddress());
            shopHint.setIntroduction(shop.getIntroduction());
            shopHint.setFunds(shop.getFunds());
            shopHint.setRegisterdate(shop.getRegisterdate());
            shopHint.setState(shop.getState());
            //查找购物车
            List<Orders> shoppingCarts=adminMapper.findOrdersByShopId(shop.getId());
            if(shoppingCarts.size()!=0){
                shopHint.setStateHint(4007);
                shopHint.setMessageHint("有商品订单尚未完成，不能删除商店！");
            }
            else{
                shopHint.setStateHint(200);
                shopHint.setMessageHint("");
            }
            shopHints.add(shopHint);
        }
        return shopHints;
    }

    /**展示出所有已通过审核的店铺*/
    public List<Shop> showAllShopHaveReviewed(){
        List<Shop> shops=adminMapper.showAllShop(1);
        for(Shop shop:shops){
            List<String> list=adminMapper.findCommoditycategoryByShopId(shop.getId());
            String category=String.join(",",list);
            shop.setCategory(category);
        }
        return shops;
    }

    /**对关店申请驳回*/
    public void refuseClose(Integer shopId){
        Integer rows = adminMapper.refuseClose(shopId);
        if (rows != 1) {
            throw new InsertException("修改商店数据出现未知错误，请联系系统管理员");
        }
    };


    /**对关店申请批准*/
    public void agreeClose(Integer shopId){
        Shop shop=adminMapper.findShopByShopId(shopId);
        ShopAccount shopAccount=adminMapper.findShopAccountByShopId(shopId);
        Integer merchantId=shop.getMerchantId();
        Merchant merchant=adminMapper.findMerchantById(merchantId);
        double shopAmount=shopAccount.getAmount();
        MerchantAccount merchantAccount=adminMapper.findMerchantAccountByOwnerId(merchantId);
        double merchantAmount=merchantAccount.getAmount();
        double amountAdded=shopAmount+merchantAmount;
         //资⾦转移⾄商户个⼈账户
        Integer rows1=adminMapper.updateMerchantAmount(amountAdded,merchantId);
        if (rows1 != 1) {
            throw new InsertException("修改商户账户数据出现未知错误，请联系系统管理员");
        }
        //添加商户流水记录
        MerchantAccountRecorder merchantAccountRecorder=new MerchantAccountRecorder();
        merchantAccountRecorder.setMerchantId(merchantId);
        merchantAccountRecorder.setInitiatorRole(3);
        merchantAccountRecorder.setInitiatorId(shop.getId());
        merchantAccountRecorder.setInitiatorName(shop.getShopName());
        merchantAccountRecorder.setReceiverRole(2);
        merchantAccountRecorder.setReceiverId(merchantId);
        merchantAccountRecorder.setReceiverName(merchant.getName());
        merchantAccountRecorder.setTradeTime(LocalDateTime.now());
        merchantAccountRecorder.setAmount(shopAmount);
        merchantAccountRecorder.setTradeRecord("删除商店后的商店账户资金");
        merchantAccountRecorder.setInAndout(1);
        Integer rows4=adminMapper.insertMerchantAccountRecorder(merchantAccountRecorder);
        if(rows4!=1){
            throw new InsertException("添加商户流水出现问题，请联系系统管理员！");
        }
        //删除店铺
        Integer rows2 = adminMapper.agreeClose(shopId);
        if (rows2 != 1) {
            throw new InsertException("删除商店数据出现未知错误，请联系系统管理员");
        }
        //删除商店账户
        Integer rows3=adminMapper.deleteShopAccount(shopId);
        if (rows3 != 1) {
            throw new InsertException("删除商店账户数据出现未知错误，请联系系统管理员");
        }
        /**删除商品种类信息*/
        adminMapper.deleteCategory(shopId);
        //购物车中商品失效
        adminMapper.setShoppingCartState(shopId);
    };

    /**对开店申请驳回*/
    public void refuseOpen(Integer shopId){
        Integer rows = adminMapper.refuseOpen(shopId);
        if (rows != 1) {
            throw new InsertException("删除商店数据出现未知错误，请联系系统管理员");
        }
        //删除导入的商品种类
        adminMapper.deleteCategory(shopId);
    };


    /**对开店申请批准*/
    public void agreeOpen(Integer shopId){
        Integer rows1 = adminMapper.agreeOpen(shopId);
        if (rows1 != 1) {
            throw new InsertException("修改商店数据出现未知错误，请联系系统管理员");
        }
        /**关联商店账户*/
        ShopAccount shopAccount=new ShopAccount();
        shopAccount.setShopId(shopId);
        shopAccount.setAmount(0);
        Integer rows2 = adminMapper.insertShopAccount(shopAccount);
        if(rows2!=1){
            throw new InsertException("添加用户账户数据出现未知错误，请联系系统管理员");
        }
        /*将商店注册资金转移到管理员账户*/
        MiddleAccountRecorder middleAccountRecorder=adminMapper.findMiddleAccountRecorder(3,shopId,4,1);
        double amount= middleAccountRecorder.getAmount();
        rechargeAccount(1, amount);
        //管理员流水记录
        AdminAccountRecorder adminAccountRecorder=new AdminAccountRecorder();
        adminAccountRecorder.setAdminId(1);
        adminAccountRecorder.setInitiatorRole(3);
        adminAccountRecorder.setInitiatorId(shopId);
        adminAccountRecorder.setInitiatorName(middleAccountRecorder.getInitiatorName());
        adminAccountRecorder.setReceiverRole(5);
        adminAccountRecorder.setReceiverId(1);
        adminAccountRecorder.setReceiverName("商城利润账户");
        adminAccountRecorder.setAmount(amount);
        adminAccountRecorder.setTradeTime(LocalDateTime.now());
        adminAccountRecorder.setTradeRecord(middleAccountRecorder.getTradeRecord());
        adminAccountRecorder.setInAndout(1);
        Integer rows5=adminMapper.insertAdminAccountRecorder(adminAccountRecorder);
        if(rows5!=1){
            throw new InsertException("导入管理员流水记录出现错误，请联系系统管理员！");
        }
        /*修改中间账户和中间流水记录*/
        MiddleAccountRecorder middleAccountRecorder1=new MiddleAccountRecorder();
        middleAccountRecorder1.setInitiatorId(1);
        middleAccountRecorder1.setInitiatorRole(4);
        middleAccountRecorder1.setInitiatorName("商城中间账户");
        middleAccountRecorder1.setReceiverId(1);
        middleAccountRecorder1.setReceiverRole(5);
        middleAccountRecorder1.setReceiverName("商城利润账户");
        middleAccountRecorder1.setAmount(amount);
        middleAccountRecorder1.setTradeTime(LocalDateTime.now());
        middleAccountRecorder1.setTradeRecord("批准开店注册资金转移");
        middleAccountRecorder1.setInAndout(-1);
        Integer row3=adminMapper.insertMiddleAccountRecorder(middleAccountRecorder1);
        if(row3!=1){
            throw new InsertException("修改中间流水数据出现未知错误，请联系系统管理员");
        }
        //根据id查账户
        MiddleAccount middleAccount=adminMapper.findMiddleAccountById(1);
        //修改账户
        double amountSub=middleAccount.getAmount()-amount;
        Integer rows4=adminMapper.updateMiddleAccount(amountSub, 1);
        if(rows4!=1){
            throw new InsertException("修改中间账户出现问题，请联系系统管理员！");
        }
    };


    /**管理员登录*/
    public Admin login(String name,String password){
        //根据管理员名查询用户数据
        Admin admin=adminMapper.findAdminByName(name);
        //判断用户是否存在
        if(admin==null){
            throw new UserNotFoundException("管理员"+name+"不存在！");
        }
        //判断密码是否输入正确
        if(!admin.getPassword().equals(password)){
            throw new PasswordNotMatchException("密码输入不正确！");
        }
        return admin;
    }

    /**获取数据*/
    @Override
    public Admin getData(String name){
        Admin adminSql=adminMapper.findAdminByName(name);
        return adminSql;
    }

    /**获取账户数据*/
    @Override
    public AdminAccount getAccount(String name){
        //获取用户数据
        Admin adminSql=adminMapper.findAdminByName(name);
        //获取账户数据
        AdminAccount adminAccountSql=adminMapper.findAdminAccountByOwnerId(adminSql.getId());
        return adminAccountSql;
    }


    /**获取流水记录*/
    @Override
    public List<AdminAccountRecorder> getAccountRecorder(Integer id,Integer timeInterval){
        List<AdminAccountRecorder> adminAccountRecorder;
        if(timeInterval==0){
            //获取账户流水
            adminAccountRecorder=adminMapper.findAdminAccountRecorderByAdminId(id);
        }
        else{
            LocalDateTime today=LocalDateTime.now();
            LocalDateTime previous=today.minusDays(timeInterval);
            adminAccountRecorder=adminMapper.findAdminAccountRecorderByAdminIdLimitTime(id,previous);
        }
        return adminAccountRecorder;
    }

    /**获取中间账户流水*/
    @Override
    public List<MiddleAccountRecorder> getMiddleAccountRecorder(Integer timeInterval){
        List<MiddleAccountRecorder> middleAccountRecorder;
        if(timeInterval==0){
            middleAccountRecorder=adminMapper.findAllMiddleAccountRecorder();
        }
        else{
            LocalDateTime today=LocalDateTime.now();
            LocalDateTime previous=today.minusDays(timeInterval);
            middleAccountRecorder=adminMapper.findMiddleAccountRecorderLimitTime(previous);
        }
        return middleAccountRecorder;
    }

    /**账户充值*/
    @Override
    public void rechargeAccount(Integer ownerId, double amount) {
        //根据id查账户
        AdminAccount adminAccount=adminMapper.findAdminAccountByOwnerId(ownerId);
        //修改账户
        double amountAdded=amount+adminAccount.getAmount();
        Integer rows=adminMapper.updateAccount(amountAdded, ownerId);
        if(rows!=1){
            throw new InsertException("修改账户出现问题，请联系系统管理员！");
        }
    }

    /**
     * 对商品上架批准
     */
    public void agreeCommodity(Commodity commodity){
        commodity.setListTime(LocalDateTime.now());
        commodity.setApplyState(1);
        commodity.setShopId(adminMapper.findCommodityById(commodity.getId()).getShopId());
        commodity.setCommodityName(adminMapper.findCommodityById(commodity.getId()).getCommodityName());
        Integer rows = adminMapper.agreeCommodity(commodity);
        if (rows != 1) {
            throw new InsertException("批准商品上架出现未知错误，请联系系统管理员");
        }
        rows = adminMapper.recordListSuccess(commodity);
        if(rows !=1){
            throw new InsertException("批准商品上架的记录存储出现未知错误，请联系系统管理员");
        }
    };


    /**
     * 对商品上架驳回
     */
    public void refuseCommodity(Commodity commodity) throws IOException {
        commodity.setListTime(LocalDateTime.now());
        commodity.setApplyState(-1);
        commodity.setShopId(adminMapper.findCommodityById(commodity.getId()).getShopId());
        commodity.setCommodityName(adminMapper.findCommodityById(commodity.getId()).getCommodityName());
        //驳回上架
        Integer rows = adminMapper.refuseCommodity(commodity);
        if (rows != 1) {
            throw new InsertException("驳回商品上架出现未知错误，请联系系统管理员");
        }
        //驳回记录
        rows = adminMapper.recordListFail(commodity);
        if(rows !=1){
            throw new InsertException("驳回商品上架的记录存储出现未知错误，请联系系统管理员");
        }
        //删除图片
        String[] strings =commodity.getImageUrl().split(",");
        for(String path:strings){
            Path relative = Paths.get(path);
            Files.delete(relative);
        }
    };


    /**展示出数据库中所有待审核的商品*/
    public List<Commodity> showAllCommodityToBeReviewed() throws IOException {
        List<Commodity> commodities= adminMapper.showAllCommodityToBeReviewed();
        for (Commodity commodity:commodities){
            String urls = commodity.getImageUrl();
            String[] realUrls = urls.split(",");
            commodity.setImageUrls(realUrls);
        }
        return commodities;
    };

    /**对商品修改批准*/
    public void agreeFixCommodity(CommodityFixed commodityFixed) throws IOException {
        CommodityFixed commodityFixed1 = adminMapper.getCommodityFixedDataById(commodityFixed.getId());
        commodityFixed1.setFixState(1);
        commodityFixed1.setFixTime(LocalDateTime.now());
        //删除暂存数据
        Integer rows = adminMapper.agreeFixCommodity(commodityFixed1);
        if (rows != 1) {
            throw new InsertException("批准商品修改删除暂存数据出现未知错误，请联系系统管理员");
        }
        //若修改图片，删除原有图片
        if(commodityFixed1.getImageUrl().length()!=0) {
            Commodity commodity = adminMapper.findCommodityById(commodityFixed1.getFixId());
            //删除图片
            String[] strings =commodity.getImageUrl().split(",");
            for(String path:strings){
                Path relative = Paths.get(path);
                Files.delete(relative);
            }
            //更新商品信息
            rows = adminMapper.updateFixedCommodity(commodityFixed1);
            if (rows != 1) {
                throw new InsertException("商品修改数据出现未知错误，请联系系统管理员");
            }
        }
        else {
            adminMapper.updateFixedCommodityWithoutImageUrl(commodityFixed1);

        }

        //产生记录
        rows = adminMapper.recordFixListSuccess(commodityFixed1);
        if (rows != 1) {
            throw new InsertException("商品修改成功记录出现未知错误，请联系系统管理员");
        }

    };

    /**对商品修改驳回*/
    public void refuseFixCommodity(CommodityFixed commodityFixed) throws IOException {
        CommodityFixed commodityFixed1 = adminMapper.getCommodityFixedDataById(commodityFixed.getId());
        commodityFixed1.setFixState(-1);
        commodityFixed1.setFixTime(LocalDateTime.now());
        //删除暂存数据
        Integer rows = adminMapper.refuseFixCommodity(commodityFixed1);
        if (rows != 1) {
            throw new InsertException("商品不存在");
        }
        //删除暂存图片
        if(commodityFixed1.getImageUrl().length()!=0) {
            String[] strings = commodityFixed1.getImageUrl().split(",");
            for(String path:strings){
                Path relative = Paths.get(path);
                Files.delete(relative);
            }
        }

        //产生记录
        rows = adminMapper.recordFixListFail(commodityFixed1);
        if (rows != 1) {
            throw new InsertException("驳回商品修改记录出现未知错误，请联系系统管理员");
        }
    };

    /**展示出数据库中所有修改待审核的商品*/
    public List<CommodityFixed> showAllCommodityFixedToBeReviewed() throws IOException {
        List<CommodityFixed> commodityFixeds = adminMapper.showAllCommodityFixedToBeReviewed();
        for (CommodityFixed commodityFixed : commodityFixeds) {
            String urls = commodityFixed.getImageUrl();
            if (urls.length() != 0) {
                String[] realUrls = urls.split(",");
                commodityFixed.setImageUrls(realUrls);
            }

        }
        return commodityFixeds;
    }

    /**管理员发起活动*/
    public void holdActivity(Activity activity) {
        /**获取商场利润账户的数据*/
        AdminAccount adminAccount = adminMapper.findAdminAccountByOwnerId(1);
        MiddleAccount middleAccount=adminMapper.findMiddleAccountById(1);
        /**判断资金是否足够开启活动*/
        if(adminAccount.getAmount()>=activity.getFunds()) {
            /**进行转账*/
            adminMapper.updateAccount(adminAccount.getAmount()-activity.getFunds(),1);
            adminMapper.updateMiddleAccount(middleAccount.getAmount()+activity.getFunds(),1);
            /**转账流水记录*/
            MiddleAccountRecorder middleAccountRecorder=new MiddleAccountRecorder();
            middleAccountRecorder.setInitiatorRole(5);
            middleAccountRecorder.setInitiatorId(1);
            middleAccountRecorder.setInitiatorName("商城利润账户");
            middleAccountRecorder.setReceiverRole(4);
            middleAccountRecorder.setReceiverId(1);
            middleAccountRecorder.setReceiverName("商城中间账户");
            middleAccountRecorder.setAmount(activity.getFunds());
            middleAccountRecorder.setTradeTime(LocalDateTime.now());
            middleAccountRecorder.setTradeRecord("活动补贴资金");
            middleAccountRecorder.setInAndout(1);
            Integer rows1=adminMapper.insertMiddleAccountRecorder(middleAccountRecorder);
            if(rows1!=1){
                throw new InsertException("导入中间商城流水记录失败，请联系系统管理员！");
            }
            AdminAccountRecorder adminAccountRecorder=new AdminAccountRecorder();
            adminAccountRecorder.setAdminId(1);
            adminAccountRecorder.setInitiatorRole(5);
            adminAccountRecorder.setInitiatorId(1);
            adminAccountRecorder.setInitiatorName("商城利润账户");
            adminAccountRecorder.setReceiverRole(4);
            adminAccountRecorder.setReceiverId(1);
            adminAccountRecorder.setReceiverName("商城中间账户");
            adminAccountRecorder.setAmount(activity.getFunds());
            adminAccountRecorder.setTradeTime(LocalDateTime.now());
            adminAccountRecorder.setTradeRecord("活动补贴资金");
            adminAccountRecorder.setInAndout(-1);
            Integer rows2=adminMapper.insertAdminAccountRecorder(adminAccountRecorder);
            if(rows2!=1){
                throw new InsertException("导入商城利润账户流水记录失败，请联系系统管理员！");
            }
            /**完善数据*/
            LocalDateTime beginDateTime = LocalDateTime.now();
            LocalDateTime endDateTime = beginDateTime.plusDays(activity.getHoldingDays());
            activity.setBeginDateTime(beginDateTime);
            activity.setEndDateTime(endDateTime);
            adminMapper.holdActivity(activity);

            //时间到了，结束活动
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    adminMapper.activityOverAffectCommodity(activity.getId());
                    adminMapper.activityOverAffectShop(activity.getId());
                    adminMapper.activityOver(activity.getId());
                }
            }, Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant()));
        }
        else {
            throw new InsertException("余额不足，开启失败！");
        }
    };


    /**查看指定活动所有待审核的申请*/
    public List<Commodity> findAllCommoditiesWaitingToBeReviewedByActivityId(Integer activityId ){
        return  adminMapper.findAllCommoditiesWaitingToBeReviewedByActivityId(activityId);
    };


    /**对指定商品批准参加活动*/
    public void allowInActivity(Integer id){
        adminMapper.allowInActivity(id);
        //设置商店活动id
        Commodity commodity = adminMapper.getCommodityDataByCommodityId(id);
        adminMapper.getInActivityAffectShop(commodity.getShopId(),commodity.getActivityId());

    };

    /**对指定商品驳回参加活动*/
    public void refuseInActivity(Integer id){
        adminMapper.refuseInActivity(id);
   }

    /**根据活动ID查找活动数据*/
    public Activity getActivityDataById(Integer id){
        return  adminMapper.getActivityDataById(id);
    };

    /**查看指定活动所有待审核的申请*/
    public List<Commodity> findAllCommoditiesWaitingToBeReviewed(){
        return adminMapper.findAllCommoditiesWaitingToBeReviewed();
    };
}

