package com.ss.shoppingweb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //getter方法、setter方法、toString方法、hashCode方法、equals方法
@NoArgsConstructor //无参构造
@AllArgsConstructor//全参构造
public class CommodityCategory {
    private Integer id;//商品类别编号
    private Integer shopId;//对应的商店编号
    private String categoryName;//商品类别名字

}
