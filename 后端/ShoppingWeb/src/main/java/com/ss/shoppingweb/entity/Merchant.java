package com.ss.shoppingweb.entity;

import com.ss.shoppingweb.entity.base.UserBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data //getter方法、setter方法、toString方法、hashCode方法、equals方法
//@NoArgsConstructor //无参构造
@AllArgsConstructor//全参构造
public class Merchant extends UserBase {
}
