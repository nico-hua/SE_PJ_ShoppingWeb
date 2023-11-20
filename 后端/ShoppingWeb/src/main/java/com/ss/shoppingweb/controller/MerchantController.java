package com.ss.shoppingweb.controller;

import com.ss.shoppingweb.entity.Merchant;
import com.ss.shoppingweb.entity.MerchantAccount;
import com.ss.shoppingweb.entity.Shop;
import com.ss.shoppingweb.entity.ShopAccount;
import com.ss.shoppingweb.entity.*;
import com.ss.shoppingweb.service.MerchantService;
import com.ss.shoppingweb.utils.JsonResult;
import com.ss.shoppingweb.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/merchant")
public class MerchantController extends BaseController {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private MerchantService merchantService;

    /**
     * 申请开店
     */
    @PostMapping("/openShopApply")
    public JsonResult<Void> openShopApply(@RequestBody Shop shop) {
        merchantService.openShopApply(shop);
        return new JsonResult<>(OK);
    }

    /**
     * 商户注册
     */
    @PostMapping("/register")
    public JsonResult<Void> register(@RequestBody Merchant merchant) {
        // 调用业务对象执行注册
        merchantService.register(merchant);
        // 返回
        return new JsonResult<>(OK);
    }

    /**
     * 商户登录
     */
    @PostMapping("/login")
    public JsonResult<String> login(@RequestBody Merchant merchant) {
        // 调用业务对象的方法执行登录，并获取返回值
        Merchant data = merchantService.login(merchant.getName(), merchant.getPassword());
        String jwt;
        if (data != null) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("name", data.getName());
            //标识商户的role为2
            claims.put("role", "2");
            claims.put("id", data.getId());
            jwt = JwtUtils.generateJwt(claims);
        } else {
            jwt = null;
        }
        // 将以上返回值和状态码OK封装到响应结果中并返回
        return new JsonResult<>(OK, jwt);
    }

    /**
     * 用户信息修改
     */
    @PostMapping("/updateData")
    public JsonResult<Void> updateData(@RequestBody Merchant merchant) {
        //调用业务层执行修改
        merchantService.updateData(merchant);
        //返回
        return new JsonResult<>(OK);
    }

    /**
     * 账户充值
     */
    @PostMapping("/rechargeAccount")
    public JsonResult<Void> rechargeAccount(@RequestBody MerchantAccount merchantAccount) {
        merchantService.rechargeAccount(merchantAccount.getOwnerId(), merchantAccount.getAmount());
        return new JsonResult<>(OK);
    }

    /**
     * 获取商店账户
     */
    @RequestMapping("/getShopAccount")
    public JsonResult<ShopAccount> getShopAccount(HttpServletRequest request) {
        String name = jwtUtils.getJwtName(request.getHeader("token"));
        ShopAccount data = merchantService.getShopAccount(name);
        return new JsonResult<>(OK, data);
    }

    /**
     * 获取商店信息
     */
    @RequestMapping("getShopData")
    public JsonResult<Shop> getShopData(HttpServletRequest request) {
        String name = jwtUtils.getJwtName(request.getHeader("token"));
        Shop data = merchantService.getShopData(name);
        return new JsonResult<>(OK, data);
    }

    /**
     * 商店账户充值
     */
    @PostMapping("/rechargeShopAccount")
    public JsonResult<Void> rechargeShopAccount(@RequestBody ShopAccount shopAccount) {
        merchantService.rechargeShopAccount(shopAccount.getId(), shopAccount.getAmount());
        return new JsonResult<>(OK);
    }

    /**
     * 关店申请
     */
    @RequestMapping("/closeShopApply")
    public JsonResult<Void> closeShopApply(HttpServletRequest request) {
        String name = jwtUtils.getJwtName(request.getHeader("token"));
        merchantService.closeShopApply(name);
        return new JsonResult<>(OK);
    }

    /**
     * 商户申请上架商品
     */
    //需要接收文件，不能使用json格式
    @PostMapping("/addCommodity")
    public JsonResult<Void> addCommodity(String commodityName, String introduction, double price, MultipartFile[] image, String categoryName, Integer merchantId) throws IOException {
        Commodity commodity = new Commodity();
        String imageUrl = "";
        for (MultipartFile theImage : image) {
            //获取原始文件名
            String originalFilename = theImage.getOriginalFilename();
            //构建新的文件名
            String extname = originalFilename.substring(originalFilename.lastIndexOf('.'));//这是文件扩展名
            String newFileName = UUID.randomUUID().toString() + extname;//这是随机名+文件扩展名
            //将文件存储在服务器的磁盘目录
            theImage.transferTo(new File("/home/www/dist/images/" + newFileName));
            String add = "http://120.46.164.17/images/" + newFileName;
            imageUrl = imageUrl + add + ",";
        }
        int length = imageUrl.length();
        String url = imageUrl.substring(0, length - 1);
        commodity.setImageUrl(url);
        Integer shopId = merchantService.getShopDataByMerchantId(merchantId).getId();
        commodity.setShopId(shopId);
        commodity.setCommodityName(commodityName);
        commodity.setIntroduction(introduction);
        commodity.setPrice(price);
        commodity.setCategoryName(categoryName);
        commodity.setActivityId(0);
        commodity.setActivityState(0);
        merchantService.addCommodity(commodity);
        return new JsonResult<>(OK);
    }

    /**
     * 商户查看某一商店的上架申请记录
     */
    @RequestMapping("/selectRecords")
    public JsonResult<List<ListRecord>> selectRecords(@RequestBody ListRecord listRecord) {
        List<ListRecord> data = merchantService.selectRecords(listRecord.getShopId(), (listRecord.getPageNum() - 1) * 10);
        return new JsonResult<List<ListRecord>>(OK, data);
    }

    /**
     * 根据商店ID查看商店信息
     */
    @RequestMapping("/showShopData")
    public JsonResult<Shop> showShopData(@RequestBody Shop shop) {
        Shop data = merchantService.getShopDataByShopId(shop.getId());
        return new JsonResult<>(OK, data);
    }

    /**
     * 根据商户ID查看商店信息
     */
    @RequestMapping("/showShopDataByMerchantId")
    public JsonResult<Shop> showShopDataByMerchantId(@RequestBody Shop shop) {
        Shop data = merchantService.getShopDataByMerchantId(shop.getMerchantId());
        return new JsonResult<>(OK, data);
    }


    /**
     * 商户查看某一商店的上架商品
     */
    @RequestMapping("/showCommodities")
    public JsonResult<List<Commodity>> showCommodities(@RequestBody Shop shop) throws IOException {
        List<Commodity> data = merchantService.showCommodity(shop.getId());
        return new JsonResult<>(OK, data);
    }

    /**
     * 通过id获取商品信息
     */
    @RequestMapping("/showCommodityDataByCommodityId")
    public JsonResult<Commodity> showCommodityDataById(@RequestBody Commodity commodity) {
        Commodity data = merchantService.getCommodityDataByCommodityId(commodity.getId());
        return new JsonResult<>(OK, data);
    }


    /**
     * 商户申请修改商品
     */
    //需要接收文件，不能使用json格式
    @PostMapping("/fixCommodity")
    public JsonResult<Void> fixCommodity(Integer commodityId, String commodityName, String introduction, double price, MultipartFile[] image, String categoryName) throws IOException {
        CommodityFixed commodityFixed = new CommodityFixed();
        Commodity commodity = merchantService.getCommodityDataByCommodityId(commodityId);
        String url = "";
        if (image != null) {
            String imageUrl = "";
            for (MultipartFile theImage : image) {
                //获取原始文件名
                String originalFilename = theImage.getOriginalFilename();
                //构建新的文件名
                String extname = originalFilename.substring(originalFilename.lastIndexOf('.'));//这是文件扩展名
                String newFileName = UUID.randomUUID().toString() + extname;//这是随机名+文件扩展名
                //将文件存储在服务器的磁盘目录
                theImage.transferTo(new File("/home/www/dist/images/" + newFileName));
                String add = "http://120.46.164.17/images/" + newFileName;
                imageUrl = imageUrl + add + ",";
                int length = imageUrl.length();
                url = imageUrl.substring(0, length - 1);
            }
        }
        commodityFixed.setFixId(commodity.getId());
        commodityFixed.setImageUrl(url);
        commodityFixed.setShopId(commodity.getShopId());
        commodityFixed.setCommodityName(commodityName);
        commodityFixed.setIntroduction(introduction);
        commodityFixed.setPrice(price);
        commodityFixed.setCategoryName(categoryName);
        commodityFixed.setApplyState(commodity.getApplyState());
        commodityFixed.setBusinessState(commodity.getBusinessState());
        commodityFixed.setListTime(commodity.getListTime());
        merchantService.fixCommodity(commodityFixed);
        return new JsonResult<>(OK);
    }

    /**
     * 商户查看某一商店的修改申请记录
     */
    @RequestMapping("/selectFixRecords")
    public JsonResult<List<FixRecord>> selectFixRecords(@RequestBody FixRecord fixRecord) {
        List<FixRecord> data = merchantService.findFixRecords(fixRecord.getShopId(), (fixRecord.getPageNum() - 1) * 10);
        return new JsonResult<List<FixRecord>>(OK, data);
    }

    /**
     * 商户下架商品
     */
    //下架商品，删除商品数据
    @PostMapping("/deleteCommodity")
    public JsonResult<Void> deleteCommodity(@RequestBody Commodity commodity) throws IOException {
        merchantService.deleteCommodity(commodity);
        return new JsonResult<>(OK);
    }


    /**
     * 根据商店ID查询上架记录总数
     */
    @RequestMapping("/getTotalNumOfListRecord")
    public JsonResult<Integer> getTotalNumOfListRecord(@RequestBody Shop shop) {
        Integer data = merchantService.getTotalNumOfListRecord(shop.getId());
        return new JsonResult<Integer>(OK, data);
    }

    /**
     * 根据商店ID查询修改记录总数
     */
    @RequestMapping("/getTotalNumOfFixRecord")
    public JsonResult<Integer> getTotalNumOfFixRecord(@RequestBody Shop shop) {
        Integer data = merchantService.getTotalNumOfFixRecord(shop.getId());
        return new JsonResult<Integer>(OK, data);
    }

    ;

    /**
     * 根据商户id获取商店账户流水
     */
    @RequestMapping("/getShopAccountRecorder")
    public JsonResult<List<ShopAccountRecorder>> getShopAccountRecorder(@RequestHeader("token") String token, @RequestParam("timeInterval") Integer timeInterval) {
        Integer merchantId = JwtUtils.getJwtId(token);
        List<ShopAccountRecorder> data = merchantService.getShopAccountRecorder(merchantId, timeInterval);
        return new JsonResult<>(OK, data);
    }

    /**查询目前正在举行的活动*/
    @RequestMapping("/getActivitiesNow")
    public JsonResult<List<Activity>> getActivitiesNow(){
       List<Activity> data = merchantService.getActivitiesNow();
        return new JsonResult<>(OK,data);
    };

    /**对指定id的商品申请参加指定id的活动*/
    @PostMapping("/getInActivity")
    public JsonResult<Void> getInActivity(@RequestBody Orders orders){
        merchantService.getInActivity(orders.getCommodityId(), orders.getActivityId());
        return new JsonResult<>(OK);
    };

    /**获取待发货的订单*/
    @RequestMapping("/getShopToDeliveryOrders")
    public JsonResult<List<Orders>> getShopToDeliveryOrders(HttpServletRequest request){
        Integer merchantId=JwtUtils.getJwtId(request.getHeader("token"));
        List<Orders> data=merchantService.getShopToDeliveryOrders(merchantId);
        return new JsonResult<>(OK,data);
    }

    /**商店发货*/
    @PostMapping("/shopDeliveryOrders")
    public JsonResult<Void> shopDeliveryOrders(@RequestBody Orders order){
        merchantService.shopDeliveryOrders(order.getId());
        return new JsonResult<>(OK);
    }

    /**获取商店退货退款订单*/
    @RequestMapping("/getShopRefundOrders")
    public JsonResult<List<Orders>> getShopRefundOrders(HttpServletRequest request){
        Integer merchantId=JwtUtils.getJwtId(request.getHeader("token"));
        List<Orders> data=merchantService.getShopRefundOrders(merchantId);
        return new JsonResult<>(OK,data);
    }

    /***同意退款退货*/
    @PostMapping("/agreeRefund")
    public JsonResult<Void> agreeRefund(@RequestBody Orders order){
        merchantService.agreeRefund(order.getId());
        return new JsonResult<>(OK);
    }

    /**根据活动ID查找活动数据*/
    @RequestMapping("/getActivityDataById")
    public JsonResult<Activity> getActivityDataById(Integer id){
        Activity data = merchantService.getActivityDataById(id);
        return new JsonResult<>(OK,data);
    };

}
