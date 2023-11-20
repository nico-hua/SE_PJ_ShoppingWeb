package com.ss.shoppingweb.entity;

import com.ss.shoppingweb.entity.base.CommodityBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;




@Data //getter方法、setter方法、toString方法、hashCode方法、equals方法
@NoArgsConstructor //无参构造
@AllArgsConstructor//全参构造
public class Coupon {
    private int id;//编号
    private int userId;//用户id
    private double value;//优惠券面值
    private String name;//优惠券名称
    private int tab;//1表示抵消一定金额，2表示打折

}
