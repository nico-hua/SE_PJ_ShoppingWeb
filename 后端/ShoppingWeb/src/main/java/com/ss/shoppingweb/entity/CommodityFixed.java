package com.ss.shoppingweb.entity;

import com.ss.shoppingweb.entity.base.CommodityBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data //getter方法、setter方法、toString方法、hashCode方法、equals方法
@NoArgsConstructor //无参构造
@AllArgsConstructor//全参构造
public class CommodityFixed extends CommodityBase {
    private int id;//编号
    private int shopId;//商店编号
    private String categoryName;//商品种类名称
    private int applyState;//商品申请状态：-1表示商品申请失败 0表示商品申请在审核中 1表示商品申请成功
    private int businessState;//商品营业状态：-1表示商品下架 1表示商品正常上架中
    private int fixState;//商品修改状态:-1表示修改失败，0表示审核中，1表示成功
    private LocalDateTime listTime;//上架的时间
    private LocalDateTime fixTime;//最后一次修改的时间
    private int fixId;//待修改商品的ID
}
