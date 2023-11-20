package com.ss.shoppingweb.service;

import com.ss.shoppingweb.entity.*;


import java.io.IOException;
import java.util.List;

public interface AdminService {
    /**展示出数据库中所有开店待审核的店铺*/
    List<Shop> showAllOpenShopToBeReviewed();

    /**展示出数据库中所有关店待审核的店铺*/
    List<ShopHint> showAllCloseShopToBeReviewed();

    /**展示出数据库中所有已通过审核的店铺*/
    List<Shop> showAllShopHaveReviewed();

    /**对开店申请驳回*/
    void refuseOpen(Integer shopId);

    /**对开店申请批准*/
    void agreeOpen(Integer shopId);

    /**对关店申请驳回*/
    void refuseClose(Integer shopId);

    /**对关店申请批准*/
    void agreeClose(Integer shopId);

    /**登录*/
    Admin login(String name, String password);

    //获取商户数据
    Admin getData(String name);

    //获取账户数据
    AdminAccount getAccount(String name);

    //获取流水记录
    List<AdminAccountRecorder> getAccountRecorder(Integer id,Integer timeInterval);

    //获取中间账户流水
    List<MiddleAccountRecorder> getMiddleAccountRecorder(Integer timeInterval);

    //账户充值
    void rechargeAccount(Integer ownerId,double amount);
    /**
     * 对商品上架批准
     */
    void agreeCommodity(Commodity commodity);


    /**
     * 对商品上架驳回
     */
    void refuseCommodity(Commodity commodity) throws IOException;


    /**展示出数据库中所有待审核的商品*/
    List<Commodity> showAllCommodityToBeReviewed() throws IOException;

    /**对商品修改批准*/
    void agreeFixCommodity(CommodityFixed commodityFixed) throws IOException;

    /**对商品修改驳回*/
    void refuseFixCommodity(CommodityFixed commodityFixed) throws IOException;

    /**展示出数据库中所有修改待审核的商品*/
    List<CommodityFixed> showAllCommodityFixedToBeReviewed() throws IOException;

    /**管理员发起活动*/
    void holdActivity(Activity activity);

    /**查看指定活动所有待审核的申请*/
    List<Commodity> findAllCommoditiesWaitingToBeReviewedByActivityId(Integer activityId);

    /**对指定商品批准参加活动*/
    void allowInActivity(Integer id);

    /**对指定商品驳回参加活动*/
    void refuseInActivity(Integer id);

    /**根据活动ID查找活动数据*/
    Activity getActivityDataById(Integer id);

    /**查看指定活动所有待审核的申请*/
    List<Commodity> findAllCommoditiesWaitingToBeReviewed();
}
