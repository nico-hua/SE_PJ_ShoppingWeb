package com.ss.shoppingweb.entity.base;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //getter方法、setter方法、toString方法、hashCode方法、equals方法
@NoArgsConstructor //无参构造
@AllArgsConstructor//全参构造
public class AccountBase {
    private Integer id;//编号

    private double amount;//余额

    private Integer ownerId;//用户编号


    }

