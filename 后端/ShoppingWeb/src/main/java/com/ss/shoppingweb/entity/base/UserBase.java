package com.ss.shoppingweb.entity.base;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //getter方法、setter方法、toString方法、hashCode方法、equals方法
@NoArgsConstructor //无参构造
@AllArgsConstructor//全参构造
public class UserBase{
    private Integer id;//编号
    private String name;//名字
    private String password;//密码
    private String phone;//电话号码
    private String idCard;//身份证号
    private String email;//邮箱


}
