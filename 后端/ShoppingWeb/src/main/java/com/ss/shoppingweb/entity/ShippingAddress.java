package com.ss.shoppingweb.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data //getter方法、setter方法、toString方法、hashCode方法、equals方法
@NoArgsConstructor //无参构造
@AllArgsConstructor//全参构造
public class ShippingAddress {
    private int id;//编号
    private String name;//姓名
    private String phone;//电话号码
    private String address;//具体地址
    private int userId;//用户id
}
