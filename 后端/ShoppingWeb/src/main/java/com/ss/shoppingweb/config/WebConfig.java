package com.ss.shoppingweb.config;

import com.ss.shoppingweb.Interceptor.LoginCheckInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private LoginCheckInterceptor loginCheckInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login","/admin/showAllShopHaveReviewed","/register","/merchant/showCommodities","/merchant/showShopData","/merchant/showCommodityDataByCommodityId","/merchant/getActivitiesNo","/user/showCommoditiesInOneActivity");
    }
}
