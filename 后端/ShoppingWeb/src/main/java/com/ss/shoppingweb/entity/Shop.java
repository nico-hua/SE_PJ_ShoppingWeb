package com.ss.shoppingweb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


@Data //getter方法、setter方法、toString方法、hashCode方法、equals方法
@NoArgsConstructor //无参构造
@AllArgsConstructor//全参构造
public class Shop {
    private Integer id;//编号
    private Integer merchantId;//对应的商户编号
    private String shopName;//商店名字
    private String category;//商品种类
    private String idCard;//身份证号
    private String introduction;//商店简介
    private String address;//备案地址
    private double funds;//注册资金
    private String registerdate;//注册日期
    private Integer state;//店铺状态——待审核：0、正常营业：1、请求关店：-1
    private Double monthlySalesMoney;//月销售额度
    private Integer monthlySalesCount;//月销售量
    private Integer activityId;//活动id

}
