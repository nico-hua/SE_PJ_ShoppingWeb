package com.ss.shoppingweb.Interceptor;

import com.alibaba.fastjson.JSONObject;
import com.ss.shoppingweb.utils.JsonResult;
import com.ss.shoppingweb.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = Logger.getLogger(LoginCheckInterceptor.class.getName());
    /**目标资源方法运行前运行*/
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取请求url
        String url=request.getRequestURI().toString();
        boolean result=true;
        log.info("请求的url：{}",url);
        //判断url中是否包含login
        if(url.contains("login")){
            log.info("登录操作，放行！");
        }
        else if(url.contains("register")){
            log.info("注册操作，放行！");
        }
        else{
            //获取请求头中的令牌
            String jwt=request.getHeader("token");
            //判断令牌是否存在
            JsonResult error=new JsonResult();
            if(!StringUtils.hasLength(jwt)){
                log.info("请求头token为空,返回未登录的信息");
                error.setState(3000);
                error.setMessage("NOT_LOGIN");
                String notLogin= JSONObject.toJSONString(error);
                response.getWriter().write(notLogin);
                result=false;
            }
            //解析token
            try{
                JwtUtils.parseJwt(jwt);
            }catch (Exception e){
//            e.printStackTrace();
                LOGGER.log(Level.SEVERE, "出现异常！", e);
                log.info("解析令牌失败，返回错误的登录信息");
                error.setState(3000);
                error.setMessage("NOT_LOGIN");
                String notLogin= JSONObject.toJSONString(error);
                response.getWriter().write(notLogin);
                result=false;
            }
        }
        //放行
        log.info("令牌合法，放行");
        return result;
    }

    /**目标资源运行后运行*/
//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
//    }

    /**视图渲染完毕后运行*/
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
//    }
}
