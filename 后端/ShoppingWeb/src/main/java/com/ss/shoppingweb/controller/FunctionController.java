package com.ss.shoppingweb.controller;

import com.ss.shoppingweb.service.AdminService;
import com.ss.shoppingweb.service.MerchantService;
import com.ss.shoppingweb.service.UserService;
import com.ss.shoppingweb.utils.JsonResult;
import com.ss.shoppingweb.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class
FunctionController extends BaseController {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserService userService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private AdminService adminService;
    /**
     * 获取用户信息
     * */
    @RequestMapping("/getData")
    public JsonResult<Object> getData(HttpServletRequest request){
        String role=jwtUtils.getJwtRole(request.getHeader("token"));
        String name=jwtUtils.getJwtName(request.getHeader("token"));
        Integer id=jwtUtils.getJwtId(request.getHeader("token"));
        Object data=new Object();
        switch (role){
            //普通用户
            case "1":
                data=userService.getData(name);
                break;
            //商户
            case "2":
                data=merchantService.getData(name);
                break;
            //管理员
            case "3":
                data=adminService.getData(name);
                break;
            default:
                break;
        }
        JsonResult<Object> result=new JsonResult<>(OK,data);
        result.setMessage(role);
        return result;
    }

    /**
     * 获取账户信息
     * */
    @RequestMapping("/getAccount")
    public JsonResult<Object> getAccount(HttpServletRequest request){
        String role=jwtUtils.getJwtRole(request.getHeader("token"));
        String name=jwtUtils.getJwtName(request.getHeader("token"));
        Object data=new Object();
        switch (role){
            //普通用户
            case "1":
                data=userService.getAccount(name);
                break;
            //商户
            case "2":
                data=merchantService.getAccount(name);
                break;
            //管理员
            case "3":
                data=adminService.getAccount(name);
                break;
            default:
                break;
        }
        JsonResult<Object> result=new JsonResult<>(OK,data);
        result.setMessage(role);
        return result;
    }

    /**
     * 获取账户流水
     * */
    @RequestMapping ("/getAccountRecorder")
    public JsonResult<Object> getAccountRecorder(@RequestHeader("token") String token, @RequestParam("timeInterval") Integer timeInterval){
        String role=jwtUtils.getJwtRole(token);
        Integer id=jwtUtils.getJwtId(token);
        Object data=new Object();
        switch (role){
            //普通用户
            case "1":
                data=userService.getAccountRecorder(id,timeInterval);
                break;
            //商户
            case "2":
                data=merchantService.getAccountRecorder(id,timeInterval);
                break;
            //管理员
            case "3":
                data=adminService.getAccountRecorder(id,timeInterval);
                break;
            default:
                break;
        }
        JsonResult<Object> result=new JsonResult<>(OK,data);
        result.setMessage(role);
        return result;
    }
}
