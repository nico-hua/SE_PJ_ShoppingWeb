package com.ss.shoppingweb.entity;

import com.ss.shoppingweb.entity.base.CommodityBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data //getter方法、setter方法、toString方法、hashCode方法、equals方法
@NoArgsConstructor //无参构造
@AllArgsConstructor//全参构造
public class ShoppingCart extends CommodityBase {
    private int id;//编号
    private int shopId;//商店编号
    private int userId;//用户编号
    private int commodityId;//商品编号
    private int businessState;//商品营业状态： 1表示商品正常上架中，下架将删除数据
    private String commodityCategoryName;//商品种类名称
    private String commodityShopName;//商店名字
    private int commodityNum;//商品数量

}
