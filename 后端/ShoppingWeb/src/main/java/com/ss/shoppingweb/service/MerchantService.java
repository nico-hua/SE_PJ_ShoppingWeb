package com.ss.shoppingweb.service;

import com.ss.shoppingweb.entity.*;


import java.io.IOException;
import java.util.List;

public interface MerchantService {
    //申请开店
    void openShopApply(Shop shop);

    //商户注册
    void register(Merchant merchant);

    //商户登录
    Merchant login(String name,String password);

    //获取商户数据
    Merchant getData(String name);

    //获取账户数据
    MerchantAccount getAccount(String name);

    //用户信息修改
    void updateData(Merchant merchant);

    //账户充值
    void rechargeAccount(Integer ownerId,double amount);

    //获取商店账户
    ShopAccount getShopAccount(String name);

    //获取流水记录
    List<MerchantAccountRecorder> getAccountRecorder(Integer id,Integer timeInterval);

    //获取商店流水记录
    List<ShopAccountRecorder> getShopAccountRecorder(Integer merchantId,Integer timeInterval);

    //商店账户充值
    void rechargeShopAccount(Integer id,double amount);

    //商户关店申请
    void closeShopApply(String name);

    //根据商户名字获取商店信息
    Shop getShopData(String name);
    //商户上架商品
    void addCommodity(Commodity commodity);

    //商户查看商品上架申请记录
    List<ListRecord> selectRecords(Integer shopId, Integer pageNum);

    //根据商店ID展示已经上架的商品
    List<Commodity> showCommodity(Integer shopId) throws IOException;

    //修改商品信息
    void fixCommodity(CommodityFixed commodityFixed);

    //查看该商店商品修改申请记录
    List<FixRecord> findFixRecords(Integer shopId, Integer pageNum);


    //下架商品，删除商品数据
    void deleteCommodity(Commodity commodity) throws IOException;

    //下架商品，购物车同步信息

    /**查询商店信息*/
    Shop getShopDataByName(String shopName);

    /**通过商店主人ID查询商店信息*/
    Shop getShopDataByMerchantId(Integer merchantId);


    /**通过商店ID查询商店信息*/
    Shop getShopDataByShopId(Integer shopId);

    /**根据商品ID查询商品信息*/
    Commodity getCommodityDataByCommodityId(Integer id);


    /**根据商店ID查询上架记录总数*/
    Integer getTotalNumOfListRecord(Integer shopId);

    /**根据商店ID查询修改记录总数*/
    Integer getTotalNumOfFixRecord(Integer shopId);

    /**根据商户id获取商店待发货订单*/
    List<Orders> getShopToDeliveryOrders(Integer merchantId);

    /**商店发货*/
    void shopDeliveryOrders(Integer orderId);

    /**根据商户id获取商店退款退货订单*/
    List<Orders> getShopRefundOrders(Integer merchantId);

    /**同意退款退货*/
    void agreeRefund(Integer orderId);
    /**查询目前正在举行的活动*/
    List<Activity> getActivitiesNow();

    /**对指定id的商品申请参加指定id的活动*/
    void getInActivity(Integer commodityId,Integer activityId);

    /**根据活动ID查找活动数据*/
    Activity getActivityDataById(Integer id);
}
