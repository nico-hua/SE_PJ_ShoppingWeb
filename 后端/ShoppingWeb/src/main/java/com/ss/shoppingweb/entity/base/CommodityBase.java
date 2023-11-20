package com.ss.shoppingweb.entity.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.util.List;

@Data //getter方法、setter方法、toString方法、hashCode方法、equals方法
@NoArgsConstructor //无参构造
@AllArgsConstructor//全参构造
public class CommodityBase {
    private String commodityName;//商品名字
    private String introduction;//商品介绍
    private double price;//商品价格
    private String imageUrl;//图片路径，多个图片之间以逗号隔开
    private MultipartFile[] image;//仅仅用作接收图片，不存入数据库，无需在数据库单设一列
    private String[] imageUrls;//转化成String类型的图片，仅仅用于传图片给前端，不修改数据库，无需在数据库中单设一列



}
