package com.lt.controller.commodity;

import com.lt.controller.BaseController;
import com.lt.utils.ShiroUtils;
import com.lt.vo.LoginUser;
import com.lt.vo.Page;
import com.lt.vo.Result;
import com.lt.pojo.ShopCartItem;
import com.lt.service.ShopCartItemService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: Mr.Tian
 * @Date: 2020/12/17 21:47
 * @Version 1.0
 */
@RestController
@RequestMapping("/cartItem")
public class ShopCartItemController extends BaseController {

    @Reference
    private ShopCartItemService shopCartItemService;

    /**
     * 加入购物车
     *
     * @param shopCartItem
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @HystrixCommand
    public Result<?> save(@RequestBody ShopCartItem shopCartItem) {
        LoginUser loginUser = ShiroUtils.getLoginUser();
        this.shopCartItemService.save(shopCartItem,loginUser);
        return new Result<>("加入成功");
    }

    /**
     * 获取当前登录用户购物车的商品数
     *
     * @return
     */
    @RequestMapping(value = "/getProductCount", method = RequestMethod.GET)
    @HystrixCommand
    public Result<?> getProductCount() {
        LoginUser loginUser = ShiroUtils.getLoginUser();
        Integer count = this.shopCartItemService.getProductCount(loginUser);
        return new Result<>(count);
    }

    /**
     * 分页查询
     *
     * @param page
     * @return
     */
    @RequestMapping(value = "/getByPage", method = RequestMethod.POST)
    @HystrixCommand
    public Result<?> getByPage(@RequestBody Page<ShopCartItem> page) {
        LoginUser loginUser=ShiroUtils.getLoginUser();
        List<ShopCartItem> list = this.shopCartItemService.getByPage(page,loginUser);
        return new Result<>(list);
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @RequestMapping(value = "/deleteByIds", method = RequestMethod.PUT)
    @HystrixCommand
    public Result<?> deleteByIds(@RequestBody List<Long> ids) {
        this.shopCartItemService.deleteByIds(ids);
        return new Result<>("删除成功");
    }

    /**
     * 移入收藏
     * @param ids
     * @return
     */
    @RequestMapping(value = "/moveToCollection", method = RequestMethod.POST)
    @HystrixCommand
    public Result<?> moveToCollection(@RequestBody List<Long> ids) {
        LoginUser loginUser=ShiroUtils.getLoginUser();
        this.shopCartItemService.moveToCollection(ids,loginUser);
        return new Result<>("移入成功");
    }

}
