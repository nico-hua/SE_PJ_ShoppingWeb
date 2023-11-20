package com.ss.shoppingweb.entity;

import com.ss.shoppingweb.entity.base.CommodityBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data //getter方法、setter方法、toString方法、hashCode方法、equals方法
@NoArgsConstructor //无参构造
@AllArgsConstructor//全参构造
public class Commodity extends CommodityBase {
    private int id;//编号
    private int shopId;//商店编号
    private String categoryName;//商品种类名称
    private int applyState;//商品申请状态： 0表示商品申请在审核中 1表示商品申请成功,申请失败将删除数据
    private int businessState;//商品营业状态： 1表示商品正常上架中，-1表示下架
    private LocalDateTime listTime;//上架的时间
    private LocalDateTime fixTime;//最后一次修改的时间
    private Integer activityId;//所参与活动的id，为0或空代表不参加任何活动
    private Integer activityState;//该商品参与活动的申请状态，为0表示未通过，为1表示通过，正在活动中
    private Integer monthlySalesCount;//月销量
}
