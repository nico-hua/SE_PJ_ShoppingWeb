package com.ss.shoppingweb.mapper;

import com.ss.shoppingweb.entity.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserMapper {
    /**
     * 插入用户数据
     */
    @Insert("INSERT INTO user (name, password, phone, idCard, email) VALUES (#{name}, #{password}, #{phone}, #{idCard}, #{email})")
    Integer insertUser(User user);

    /**
     * 根据用户名查询用户数据
     */
    @Select("SELECT * FROM user where name=#{name}")
    User findUserByName(String name);


    /**
     * 根据手机号查询用户数据
     */
    @Select("SELECT * FROM user where phone=#{phone}")
    User findUserByPhone(String phone);

    /**
     * 根据身份证号查询用户数据
     */
    @Select("SELECT * FROM user where idCard=#{idCard}")
    User findUserByIdcard(String identity);

    /**
     * 根据邮箱查询用户数据
     */
    @Select("SELECT * FROM user where email=#{email}")
    User findUserByEmail(String email);

    /**
     * 根据用户名查询用户数据
     */
    @Select("SELECT * FROM user where name=#{name} and idCard!=#{idCard}")
    User findUserByNameExcpIdcard(String name, String idCard);

    /**
     * 根据手机号查询用户数据
     */
    @Select("SELECT * FROM user where phone=#{phone} and idCard!=#{idCard}")
    User findUserByPhoneExcpIdcard(String phone, String idCard);

    /**
     * 根据邮箱查询用户数据
     */
    @Select("SELECT * FROM user where email=#{email} and idCard!=#{idCard}")
    User findUserByEmailExcpIdcard(String email, String idCard);


    /**
     * 根据身份证修改用户数据
     */
    @Update("update user set name=#{name},password=#{password},phone=#{phone},email=#{email} where idCard=#{idCard}")
    Integer updateData(User user);

    /**
     * 插入用户账户数据
     */
    @Insert("Insert into useraccount (ownerId,amount) values (#{ownerId},#{amount})")
    Integer insertUserAccount(UserAccount userAccount);

    /**
     * 根据id查账户
     */
    @Select("Select * from useraccount where ownerId=#{ownerId}")
    UserAccount findUserAccountByOwnerId(Integer ownerId);

    /**
     * 根据id查流水记录
     */
    @Select("Select * from useraccountrecorder where userId=#{userId}")
    List<UserAccountRecorder> findUserAccountRecorderByUserId(Integer userId);

    /**
     * 根据id查流水记录
     */
    @Select("Select * from useraccountrecorder where userId=#{userId} and tradeTime >= #{previous}")
    List<UserAccountRecorder> findUserAccountRecorderByUserIdLimitTime(Integer userId, LocalDateTime previous);

    /**
     * 修改账户
     */
    @Update("Update useraccount set amount=#{amount} where ownerId=#{ownerId}")
    Integer updateAccount(double amount, Integer ownerId);


    /**
     * 将商品添加到购物车
     */
    @Insert("INSERT INTO shoppingcart (userId,shopId,commodityId,commodityName,introduction,price,commodityNum,commodityCategoryName,businessState,imageUrl,commodityShopName) VALUES (#{userId}, #{shopId},#{commodityId},#{commodityName},#{introduction},#{price},#{commodityNum},#{commodityCategoryName},#{businessState},#{imageUrl},#{commodityShopName}) ")
    Integer addToShoppingCart(ShoppingCart shoppingCart);

    /**
     * 将购物车中指定商品数量加1
     */
    @Update("Update shoppingcart set commodityNum=#{commodityNum} where id=#{id}")
    Integer addShoppingCartCommodityNumber(ShoppingCart shoppingCart);

    /**
     * 将购物车中指定商品数量减1
     */
    @Update("Update shoppingcart set commodityNum=#{commodityNum} where id=#{id}")
    Integer subShoppingCartCommodityNumber(ShoppingCart shoppingCart);

    /**
     * 根据购物车中商品的id查找该项数据
     */
    @Select("select * from shoppingcart where id = #{id} ")
    ShoppingCart getShoppingCartDataById(Integer id);

    /**
     * 查找某位用户的购物车中是否有某种商品
     */
    @Select("select * from shoppingcart where userId = #{userId} and commodityId = #{commodityId} ")
    ShoppingCart getShoppingCartDataByUserIdAndCommodityId(Integer userId, Integer commodityId);

    /**
     * 展示购物车信息
     */
    @Select("select * from shoppingcart where userId = #{id}")
    List<ShoppingCart> showShoppingCart(Integer id);

    /**
     * 删除单个商品
     */
    @Delete("delete from shoppingcart where id=#{id}")
    Integer deleteShoppingCart(Integer id);

    /**删除购物车*/
    @Delete("Delete from shoppingcart where userId=#{userId} and commodityId=#{commodityId}")
    Integer deleteShoppingcartByUserIdAndCommodityId(Integer userId,Integer commodityId);


    /**
     * 根据商品ID查询商品信息
     */
    @Select("select * from commodity where id = #{id} ")
    Commodity getCommodityDataById(Integer id);


    /**
     * 通过商店ID查询商店信息
     */
    @Select("SELECT * from shop where id = #{shopId}")
    Shop getShopDataByShopId(Integer shopId);


    /**
     * 根据用户id查找收货地址
     */
    @Select("Select * from shippingaddress where userId=#{userId}")
    List<ShippingAddress> findUserShippingAddressByUserId(Integer userId);

    /**
     * 根据收货地址id查找收货地址
     */
    @Select("Select * from shippingaddress where id=#{id}")
    ShippingAddress findUserShippingAddressById(Integer id);

    /**
     * 新建收货地址
     */
    @Insert("Insert into shippingaddress (name,phone,address,userId) values (#{name},#{phone},#{address},#{userId})")
    Integer addShippingAddress(ShippingAddress shippingAddress);

    /**
     * 删除收货地址
     */
    @Delete("delete from shippingaddress where id=#{id}")
    Integer deleteShippingAddress(Integer id);

    /**
     * 修改收货地址
     */
    @Update("Update shippingaddress set name=#{name},phone=#{phone},address=#{address}  where id=#{id}")
    Integer updateShippingAddress(ShippingAddress shippingAddress);

    /**新建订单*/
    @Insert("Insert into orders (userId,shopId,commodityId,activityId,userName,shopName,commodityName,commodityPrice,commodityNum,reducedPrice,amountSum,purchaseTime,address,payState,withdrawState,deliveryState,finishState,refundRequest,refundState,name,phone) values (#{userId},#{shopId},#{commodityId},#{activityId},#{userName},#{shopName},#{commodityName},#{commodityPrice},#{commodityNum},#{reducedPrice},#{amountSum},#{purchaseTime},#{address},#{payState},#{withdrawState},#{deliveryState},#{finishState},#{refundRequest},#{refundState},#{name},#{phone})")
    Integer insertOrders(Orders orders);

    /**根据订单编号获取订单*/
    @Select("Select * from orders where id=#{orderId}")
    Orders findOrderByOrderId(Integer orderId);

    /**修改订单支付状态*/
    @Update("Update orders set payState=1 where id=#{orderId}")
    Integer updatePayStateBYOrderId(Integer orderId);

    /**插入用户流水记录*/
    @Insert("Insert into useraccountrecorder (userId,initiatorRole,initiatorId,initiatorName,receiverRole,receiverId,receiverName,amount,tradeTime,tradeRecord,inAndout) values (#{userId},#{initiatorRole},#{initiatorId},#{initiatorName},#{receiverRole},#{receiverId},#{receiverName},#{amount},#{tradeTime},#{tradeRecord},#{inAndout})")
    Integer insertUserAccountRecorder(UserAccountRecorder userAccountRecorder);

    /**获取中间账户*/
    @Select("Select * from middleaccount")
    MiddleAccount findMiddleAccount();

    /**修改中间账户*/
    @Update("Update middleaccount set amount=#{amount}")
    Integer updateMiddleAccount(double amount);

    /**插入中间商城流水记录*/
    @Insert("Insert into middleaccountrecorder (initiatorRole,initiatorId,initiatorName,receiverRole,receiverId,receiverName,amount,tradeTime,tradeRecord,inAndout) values (#{initiatorRole},#{initiatorId},#{initiatorName},#{receiverRole},#{receiverId},#{receiverName},#{amount},#{tradeTime},#{tradeRecord},#{inAndout})")
    Integer insertMiddleAccountRecorder(MiddleAccountRecorder middleAccountRecorder);

    /**修改撤销状态*/
    @Update("Update orders set withdrawState=1 where id=#{orderId}")
    Integer updateWithdrawStatwByOrderId(Integer orderId);

    /**删除订单*/
    @Delete("Delete from orders where id=#{orderId}")
    Integer deleteOrderByOrderId(Integer orderId);

    /**获取待支付订单*/
    @Select("Select * from orders where userId=#{userId} and payState=0 and withdrawState=0")
    List<Orders> getToPayOrdersByUserId(Integer userId);

    /**获取已撤销订单*/
    @Select("Select * from orders where userId=#{userId} and payState=0 and withdrawState=1")
    List<Orders> getHaveWithdrawOrdersByUserId(Integer userId);

    /**获取待发货订单*/
    @Select("Select * from orders where userId=#{userId} and payState=1 and deliveryState=0 and refundRequest=0")
    List<Orders> getToDeliveryOrdersByUserId(Integer userId);

    /**获取待收货订单*/
    @Select("Select * from orders where userId=#{userId} and payState=1 and deliveryState=1 and finishState=0 and refundRequest=0")
    List<Orders> getHaveDeliveryOrdersByUserId(Integer userId);

    /**修改订单退款申请*/
    @Update("Update orders set refundRequest=1 where id=#{orderId}")
    Integer UpdateRefundRequestByOrderId(Integer orderId);

    /**获取待退款订单*/
    @Select("Select * from orders where userId=#{userId} and payState=1 and refundRequest=1 and refundState=0")
    List<Orders> getToRefundOrdersByUserId(Integer userId);

    /**获取已退款订单*/
    @Select("Select * from orders where userId=#{userId} and payState=1 and refundRequest=1 and refundState=1")
    List<Orders> getHaveRefundOrdersByUserId(Integer userId);

    /**获取已完成订单*/
    @Select("Select * from orders where userId=#{userId} and payState=1 and deliveryState=1 and finishState=1")
    List<Orders> getHaveFinishOrdersByUserId(Integer userId);


    /**修改订单完成状态*/
    @Update("Update orders set finishState=1 where id=#{orderId}")
    Integer UpdateFinishStateByOrderId(Integer orderId);

    /**获得商城利润账户*/
    @Select("Select * from adminaccount where id=1")
    AdminAccount findAdminAccount();

    /**修改商城利润账户*/
    @Update("Update adminaccount set amount=#{amount} where id=1")
    Integer UpdateAdminAccount(double amount);

    /**插入商城利润账户流水记录*/
    @Insert("Insert into adminaccountrecorder (adminId,initiatorRole,initiatorId,initiatorName,receiverRole,receiverId,receiverName,amount,tradeTime,tradeRecord,inAndout) values (#{adminId},#{initiatorRole},#{initiatorId},#{initiatorName},#{receiverRole},#{receiverId},#{receiverName},#{amount},#{tradeTime},#{tradeRecord},#{inAndout})")
    Integer insertAdminAccountRecorder(AdminAccountRecorder adminAccountRecorder);

    /**根据商店id查找商店账户*/
    @Select("Select * from shopaccount where shopId=#{shopId}")
    ShopAccount findShopAccountByShopId(Integer shopId);

    /**修改商店账户*/
    @Update("Update shopaccount set amount=#{amount} where shopId=#{shopId}")
    Integer updateShopAccount(double amount,Integer shopId);

    /**插入商店账户流水记录*/
    @Insert("Insert into shopaccountrecorder (shopId,initiatorRole,initiatorId,initiatorName,receiverRole,receiverId,receiverName,amount,tradeTime,tradeRecord,inAndout) values (#{shopId},#{initiatorRole},#{initiatorId},#{initiatorName},#{receiverRole},#{receiverId},#{receiverName},#{amount},#{tradeTime},#{tradeRecord},#{inAndout})")
    Integer insertShopAccountRecorder(ShopAccountRecorder shopAccountRecorder);

    /**
     * 根据用户id查找该用户拥有的优惠券
     */
    @Select("Select * from coupon where userId=#{userId}")
    List<Coupon> findUserCouponByUserId(Integer userId);

    /**查询目前正在举行的活动*/
    @Select("select * from activity ")
    List<Activity> getActivitiesNow();

    /**根据活动ID查找活动数据*/
    @Select("select * from activity where id = #{id} ")
    Activity getActivityDataById(Integer id);

    /**活动结束，所有参与该活动的商品结束参与活动状态*/

    @Update("update commodity set activityId = 0 , activityState = -1 where activityId = #{activityId} ")
    Integer activityOverAffectCommodity(Integer activityId);

    /**活动结束，所有参与该活动的商店结束参与活动状态*/

    @Update("update shop set activityId = 0 where activityId = #{activityId} ")
    Integer activityOverAffectShop(Integer activityId);

    /**活动结束，活动删除*/

    @Delete("DELETE from activity where id = #{activityId} ")
    Integer activityOver(Integer activityId);

    /**查询某一活动中商品*/
    @Select("select * from commodity where activityId = #{id}")
    List<Commodity> showCommoditiesInOneActivity(Integer id);

    @Update("Update activity set funds=#{funds} where id = #{activityId} ")
    Integer updateActivityFunds(Integer activityId,double funds);

    /**首页推荐商品，按销量*/
    @Select("Select * from commodity order by monthlySalesCount")
    List<Commodity> showRecommendedCommodities();

    /**首页搜索商品，按销量排序*/
    @Select("SELECT * FROM commodity WHERE commodityName LIKE CONCAT('%', #{string}, '%')")
    List<Commodity> searchCommodity(String string );

    /**修改商店月销售额*/
    @Update("Update shop set monthlySalesMoney=#{monthlySalesMoney} where id=#{id}")
    Integer updateMonthlySalesMoneyByShopId(Integer id,double monthlySalesMoney);

    /**修改商店月销售量*/
    @Update("Update shop set monthlySalesCount=#{monthlySalesCount} where id=#{id}")
    Integer updateMonthlySalesCountByShopId(Integer id,Integer monthlySalesCount);

    /**修改商品月销售量*/
    @Update("Update commodity set monthlySalesCount=#{monthlySalesCount} where id=#{id}")
    Integer updateMonthlySalesCountByCommodityId(Integer id,Integer monthlySalesCount);

    /**获取activityId*/
    @Select("Select activityId from commodity where id=#{commodityId}")
    Integer getActivityIdByCommodityId(Integer commodityId);
}





















