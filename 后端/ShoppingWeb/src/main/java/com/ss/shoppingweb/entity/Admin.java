package com.ss.shoppingweb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //getter方法、setter方法、toString方法、hashCode方法、equals方法
@NoArgsConstructor //无参构造
@AllArgsConstructor//全参构造
public class Admin {
    private Integer id;//编号
    private String name;//管理员用户名
    private String password;//管理员密码


}
