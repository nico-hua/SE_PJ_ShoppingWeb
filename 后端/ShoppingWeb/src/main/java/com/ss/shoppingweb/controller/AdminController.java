package com.ss.shoppingweb.controller;

import com.ss.shoppingweb.entity.Admin;
import com.ss.shoppingweb.entity.AdminAccount;
import com.ss.shoppingweb.entity.Shop;
import com.ss.shoppingweb.entity.*;
import com.ss.shoppingweb.service.AdminService;
import com.ss.shoppingweb.utils.JsonResult;
import com.ss.shoppingweb.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController extends BaseController{
    @Autowired
    private AdminService adminService;
    @Autowired
    private JwtUtils jwtUtils;


    /**展示所有开店待审核的商铺*/
    @RequestMapping("/showAllOpenShopToBeReviewed")
    public JsonResult<List<Shop>> showAllOpenShopToBeReviewed(HttpServletRequest request){
        String role=jwtUtils.getJwtRole(request.getHeader("token"));
        List<Shop> list=adminService.showAllOpenShopToBeReviewed();
        JsonResult<List<Shop>> result=new JsonResult<>(OK,list);
        result.setMessage(role);
        return result;
    }

    /**展示所有关店待审核的商铺*/
    @RequestMapping("/showAllCloseShopToBeReviewed")
    public JsonResult<List<ShopHint>> showAllCloseShopToBeReviewed(HttpServletRequest request){
        String role=jwtUtils.getJwtRole(request.getHeader("token"));
        List<ShopHint> list=adminService.showAllCloseShopToBeReviewed();
        JsonResult<List<ShopHint>> result=new JsonResult<>(OK,list);
        result.setMessage(role);
        return result;
    }

    /**展示所有正常营业的店铺*/
    @RequestMapping("/showAllShopHaveReviewed")
    public JsonResult<List<Shop>> showAllShopHaveReviewed(){
        List<Shop> result=adminService.showAllShopHaveReviewed();
        return new JsonResult<>(OK,result);
    }
    /**开店驳回*/
    @PostMapping("/refuseOpen")
    public JsonResult<Void> refuseOpen(@RequestBody Shop shop) {
        adminService.refuseOpen(shop.getId());
        return new JsonResult<>(OK);
    }

    /**开店批准*/
    @PostMapping("/agreeOpen")
    public JsonResult<Void> agreeOpen(@RequestBody Shop shop){
        adminService.agreeOpen(shop.getId());
        return new JsonResult<>(OK);
    }

    /**关店驳回*/
    @PostMapping("/refuseClose")
    public JsonResult<Void> refuseClose(@RequestBody Shop shop) {
        adminService.refuseClose(shop.getId());
        return new JsonResult<>(OK);
    }

    /**关店批准*/
    @PostMapping("/agreeClose")
    public JsonResult<Void> agreeClose(@RequestBody Shop shop){
        adminService.agreeClose(shop.getId());
        return new JsonResult<>(OK);
    }

    /**登录*/
    @PostMapping("/login")
    public JsonResult<String> login(@RequestBody Admin admin){
        // 调用业务对象的方法执行登录，并获取返回值
        Admin data = adminService.login(admin.getName(), admin.getPassword());
        String jwt;
        if(data!=null){
            Map<String, Object> claims= new HashMap<>();
            claims.put("name",data.getName());
            //标识管理员的role为3
            claims.put("role","3");
            claims.put("id",data.getId());
            jwt= JwtUtils.generateJwt(claims);
        }
        else{
            jwt=null;
        }
        // 将以上返回值和状态码OK封装到响应结果中并返回
        return new JsonResult<>(OK, jwt);
    }

    /**账户充值*/
    @PostMapping("/rechargeAccount")
    public JsonResult<Void> rechargeAccount(@RequestBody AdminAccount adminAccount){
        adminService.rechargeAccount(adminAccount.getOwnerId(),adminAccount.getAmount());
        return new JsonResult<>(OK);
    }

    /**对商品上架批准*/
    @PostMapping("/agreeCommodity")
    public JsonResult<Void> agreeCommodity(@RequestBody Commodity commodity){
        adminService.agreeCommodity(commodity);
        return new JsonResult<Void>(OK);
    };


    /**对商品上架驳回*/
    @PostMapping("/refuseCommodity")
    public JsonResult<Void> refuseCommodity(@RequestBody Commodity commodity) throws IOException {
        adminService.refuseCommodity(commodity);
        return new JsonResult<Void>(OK);
    };


    /**展示出数据库中所有待审核的商品*/
    @RequestMapping("/showAllCommodityToBeReviewed")
    public JsonResult<List<Commodity>> showAllCommodityToBeReviewed(HttpServletRequest request) throws IOException {
        String role=jwtUtils.getJwtRole(request.getHeader("token"));
        List<Commodity> list = adminService.showAllCommodityToBeReviewed();
        JsonResult<List<Commodity>> result=new JsonResult<>(OK,list);
        result.setMessage(role);
        return result;
    };

    /**对商品修改批准*/
    @PostMapping("/agreeFixCommodity")
    public  JsonResult<Void> agreeFixCommodity(@RequestBody CommodityFixed commodityFixed) throws IOException {
        adminService.agreeFixCommodity(commodityFixed);
        return new JsonResult<Void>(OK);
    }


    /**对商品修改驳回*/
    @PostMapping("/refuseFixCommodity")
    public JsonResult<Void> refuseFixCommodity(@RequestBody CommodityFixed commodityFixed) throws IOException {
        adminService.refuseFixCommodity(commodityFixed);
        return new JsonResult<Void>(OK);
    };

    /**展示出数据库中所有修改待审核的商品*/
    @RequestMapping("/showAllCommodityFixedToBeReviewed")
    public JsonResult<List<CommodityFixed>> showAllCommodityFixedToBeReviewed(HttpServletRequest request) throws IOException {
        String role=jwtUtils.getJwtRole(request.getHeader("token"));
        List<CommodityFixed> list = adminService.showAllCommodityFixedToBeReviewed();
        JsonResult<List<CommodityFixed>> result=new JsonResult<>(OK,list);
        result.setMessage(role);
        return result;
    };

    /**展示中间账户流水*/
    @RequestMapping("/getMiddleAccountRecorder")
    public JsonResult<List<MiddleAccountRecorder>> getMiddleAccountRecorder(@RequestParam("timeInterval") Integer timeInterval){
        List<MiddleAccountRecorder> data=adminService.getMiddleAccountRecorder(timeInterval);
        return new JsonResult<>(OK,data);
    }

    /**管理员发起活动*/
    @RequestMapping("/holdActivity")
    public JsonResult<Void> holdActivity(@RequestBody Activity activity){
        adminService.holdActivity(activity);
        return new JsonResult<>(OK);
    };

    /**查看指定活动所有待审核的申请*/
    @RequestMapping("/findAllCommoditiesWaitingToBeReviewedByActivityId")
    public JsonResult<List<Commodity>> findAllCommoditiesWaitingToBeReviewedByActivityId(@RequestParam Integer activityId){
        List<Commodity> data = adminService.findAllCommoditiesWaitingToBeReviewedByActivityId(activityId);
        return new JsonResult<>(OK,data);
    };

    /**对指定商品批准参加活动*/
    @RequestMapping("/allowInActivity")
    public JsonResult<Void> allowInActivity(@RequestBody Activity activity){
        adminService.allowInActivity(activity.getId());
        return new JsonResult<Void>(OK);
    };

    /**对指定商品驳回参加活动*/
    @RequestMapping("/refuseInActivity")
    public JsonResult<Void> refuseInActivity(@RequestBody Activity activity){
        adminService.refuseInActivity(activity.getId());
        return new JsonResult<Void>(OK);
    };


    /**查看指定活动所有待审核的申请*/
    @RequestMapping("/findAllCommoditiesWaitingToBeReviewed")
    public JsonResult<List<Commodity>> findAllCommoditiesWaitingToBeReviewed(){
        List<Commodity> data = adminService.findAllCommoditiesWaitingToBeReviewed();
        return new JsonResult<>(OK,data);
    };
}
