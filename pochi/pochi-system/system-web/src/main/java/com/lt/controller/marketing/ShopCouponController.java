package com.lt.controller.marketing;

import com.lt.controller.BaseController;
import com.lt.enums.ResultEnums;
import com.lt.enums.StateEnums;
import com.lt.pojo.ShopCouponHistory;
import com.lt.utils.ShiroUtils;
import com.lt.vo.LoginUser;
import com.lt.vo.MyBothCouponVo;
import com.lt.vo.Page;
import com.lt.vo.Result;
import com.lt.pojo.ShopCoupon;
import com.lt.dto.ShopCouponDto;
import com.lt.service.ShopCouponService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Author: Mr.Tian
 * @Date: 2020/12/9 20:58
 * @Version 1.0
 */
@RestController
@RequestMapping("/shopCoupon")
public class ShopCouponController extends BaseController {

    @Reference
    private ShopCouponService  shopCouponService;

    /**
     * 添加
     * @param shopCouponDto
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @HystrixCommand
    public Result<?> save(@RequestBody @Validated ShopCouponDto shopCouponDto) {
        LoginUser loginUser = ShiroUtils.getLoginUser();
        if(shopCouponDto.getProductList().size()==0&& StateEnums.PRODUCT.getCode().equals(shopCouponDto.getCouponType())){
            return new Result<>(ResultEnums.PRODUCT_ERROR);
        }else if(shopCouponDto.getCategoryList().size()==0&& StateEnums.CATEGORY.getCode().equals(shopCouponDto.getCouponType())){
            return new Result<>(ResultEnums.CATEGOTY_ERROR);
        }
        this.shopCouponService.save(shopCouponDto,loginUser);
        return new Result<>("添加成功");
    }

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @HystrixCommand
    public Result<?> delete(@PathVariable Long id) {
        this.shopCouponService.delete(id);
        return new Result<>("删除成功");
    }

    /**
     * 下架
     * @param id
     * @return
     */
    @RequestMapping(value = "/down/{id}", method = RequestMethod.PUT)
    @HystrixCommand
    public Result<?> down(@PathVariable Long id) {
        this.shopCouponService.down(id);
        return new Result<>("下架成功");
    }

    /**
     * 分页查询
     * @param page
     * @return
     */
    @RequestMapping(value = "/getByPage", method = RequestMethod.POST)
    @HystrixCommand
    public Result<?> getByPage(@RequestBody Page<ShopCoupon> page) {
        page = this.shopCouponService.getByPage(page);
        return new Result<>(page);
    }

    /**
     * 查询指定商品可以使用的优惠券
     * 全场通用优惠券
     * 该商品所在分类的优惠券
     * 该商品的优惠券
     * @param productId
     * @return
     */
    @RequestMapping(value = "/getProductCoupon/{productId}", method = RequestMethod.GET)
    @HystrixCommand
    public Result<?> getProductCoupon(@PathVariable Long productId) {
        LoginUser loginUser = ShiroUtils.getLoginUser();
        List<ShopCoupon> list = this.shopCouponService.getProductCoupon(productId,loginUser);
        return new Result<>(list);
    }

    /**
     * 领取优惠券
     *
     * @param shopCoupon
     * @return
     */
    @RequestMapping(value = "/catchCoupon", method = RequestMethod.POST)
    public Result<?> catchCoupon(@RequestBody ShopCoupon shopCoupon) {
        LoginUser loginUser=ShiroUtils.getLoginUser();
        this.shopCouponService.catchCoupon(shopCoupon,loginUser);
        return new Result<>("领取成功");
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public Result<ShopCoupon> get(@PathVariable Long id) {
        ShopCoupon coupon = this.shopCouponService.get(id);
        return new Result<>(coupon);
    }

    /**
     * 查询优惠券领取记录
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/getHistoryList/{id}", method = RequestMethod.GET)
    public Result<List<ShopCouponHistory>> getHistoryList(@PathVariable Long id) {
        List<ShopCouponHistory> list = this.shopCouponService.getHistoryList(id);
        return new Result<>(list);
    }

    /**
     * 查询该用户所有的优惠券可以使用的优惠券
     * 全场通用优惠券
     * 该商品所在分类的优惠券
     * 该商品的优惠券
     * @return
     */
    @RequestMapping(value = "/getAllProductCoupon", method = RequestMethod.GET)
    //@HystrixCommand
    public Result<?> getAllProductCoupon() {
        LoginUser loginUser = ShiroUtils.getLoginUser();
        MyBothCouponVo myBothCouponVo = this.shopCouponService.getAllProductCoupon(loginUser);
        return new Result<>(myBothCouponVo);
    }
}
