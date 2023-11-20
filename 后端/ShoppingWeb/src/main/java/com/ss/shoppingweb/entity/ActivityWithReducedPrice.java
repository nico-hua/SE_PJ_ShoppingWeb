package com.ss.shoppingweb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data //getter方法、setter方法、toString方法、hashCode方法、equals方法
@NoArgsConstructor //无参构造
@AllArgsConstructor//全参构造
public class ActivityWithReducedPrice {
    private Integer id;//编号
    private double totalPrice;//此活动该次下单的商品原价之和
    private double reducedPrice;//此活动该次下单满减的额度
}
