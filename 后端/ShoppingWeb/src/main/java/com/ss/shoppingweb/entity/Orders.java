package com.ss.shoppingweb.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data //getter方法、setter方法、toString方法、hashCode方法、equals方法
@NoArgsConstructor //无参构造
@AllArgsConstructor//全参构造
public class Orders {
    private Integer id;//订单编号
    private Integer userId;//用户编号
    private Integer shopId;//商店编号
    private Integer commodityId;//商品编号
    private String userName;//用户名
    private String shopName;//商店名
    private String commodityName;//商品名
    private double commodityPrice;//商品价格
    private Integer commodityNum;//商品数量
    private double amountSum;//商品总价
    private LocalDateTime purchaseTime;//购买时间
    private String address;//收货地址
    private Integer payState;//支付状态：0未支付,1已支付
    private Integer withdrawState;//撤销状态:0未撤销,1已撤销
    private Integer deliveryState;//发货状态:0未发货,1已发货
    private Integer finishState;//完成状态:0未完成,1已完成
    private Integer refundRequest;//退款申请:1表示申请退款
    private Integer refundState;//退款状态:0表示为退款,1表示已退款
    private Integer activityId;//商品参加的活动的id，为0代表不参加
    private double reducedPrice;//该商品被满减的份额
    private String name;//收货人姓名
    private String phone;//收货人电话号码
}
