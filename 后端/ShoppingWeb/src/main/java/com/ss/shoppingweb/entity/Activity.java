package com.ss.shoppingweb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data //getter方法、setter方法、toString方法、hashCode方法、equals方法
@NoArgsConstructor //无参构造
@AllArgsConstructor//全参构造
public class Activity {
  private Integer id;//编号
  private Integer holdingDays;//持续时间，以日为单位
  private LocalDateTime beginDateTime;//活动开始时间
  private LocalDateTime endDateTime;//活动结束时间
  private Double funds;//活动资金
  private String commodityCategories;//允许参加活动的商品种类，多种商品以逗号分割
  private Integer x;//满X
  private Integer y;//减Y
  private Double fundsLimit;//商店注册资金限制
  private Double monthlySalesMoneyLimit;//商店月销售额限制
  private Integer monthlySalesCountLimit;//商店月销售量限制
}
