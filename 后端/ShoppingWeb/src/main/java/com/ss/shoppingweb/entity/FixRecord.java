package com.ss.shoppingweb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data //getter方法、setter方法、toString方法、hashCode方法、equals方法
@NoArgsConstructor //无参构造
@AllArgsConstructor//全参构造
public class FixRecord  {
    private int id;
    private int shopId;
    private int commodityId;

    private String commodityName;
    private int state;//-1标记失败，1标记成功，0表示待审核
    private LocalDateTime fixTime;

    private int pageNum; //分页页码
}
