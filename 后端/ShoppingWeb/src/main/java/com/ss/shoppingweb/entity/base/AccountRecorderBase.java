package com.ss.shoppingweb.entity.base;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data //getter方法、setter方法、toString方法、hashCode方法、equals方法
@NoArgsConstructor //无参构造
@AllArgsConstructor//全参构造
public class AccountRecorderBase {
    private Integer id;//编号
    private Integer initiatorId;//发起方id
    private Integer initiatorRole;//为1时是普通用户账号，为2时是商户账号，为3时是商店账号，为4时是商城中间账户,为5时是商城利润账户
    private String  initiatorName;//发起者名字
    private Integer receiverId;//接收方id
    private Integer receiverRole;//为1时是普通用户账号，为2时是商户账号，为3时是商店账号，为4时是商城中间账户,为5时是商城利润账户
    private String receiverName;//接收方名字
    private double amount;//金额
    private LocalDateTime tradeTime;//交易时间
    private String tradeRecord;//交易注释
    private int state;

    private Integer inAndout;//-1表示出账，1表示入账
}
