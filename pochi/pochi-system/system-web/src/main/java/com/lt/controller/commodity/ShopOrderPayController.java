package com.lt.controller.commodity;

import com.lt.controller.BaseController;
import com.lt.utils.ShiroUtils;
import com.lt.vo.LoginUser;
import com.lt.vo.OrderTypeNumVo;
import com.lt.vo.Page;
import com.lt.vo.Result;
import com.lt.pojo.ShopOrderPay;
import com.lt.service.ShopOrderPayService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/25 21:19
 * @Version 1.0
 */
@RestController
@RequestMapping("/orderPay")
public class ShopOrderPayController extends BaseController {

    @Reference
    private ShopOrderPayService shopOrderPayService;

    /**
     * 分页查询
     *
     * @param page
     * @return
     */
    @RequestMapping(value = "/getByPage", method = RequestMethod.POST)
    public Result<?> getByPage(@RequestBody Page<ShopOrderPay> page) {
        page = this.shopOrderPayService.getByPage(page);
        return new Result<>(page);
    }

    /**
     * 查询详情
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public Result<?> get(@PathVariable Long id) {
        ShopOrderPay pay = this.shopOrderPayService.get(id);
        return new Result<>(pay);
    }

    /**
     * 根据商品订单ID查询
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/getByOrderId/{id}", method = RequestMethod.GET)
    public Result<?> getByOrderId(@PathVariable Long id) {
        ShopOrderPay pay = this.shopOrderPayService.getByOrderId(id);
        return new Result<>(pay);
    }

    /**
     * 根据id删除
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public Result<?> delete(@PathVariable Long id) {
        this.shopOrderPayService.delete(id);
        return new Result<>("删除成功");
    }
    /**
     * 根据创建人查询本月累计消费
     * @return
     */
    @RequestMapping(value = "/getConsumption", method = RequestMethod.GET)
    //@HystrixCommand
    public Result<?> getConsumption() {
        LoginUser loginUser= ShiroUtils.getLoginUser();
        BigDecimal consumption = this.shopOrderPayService.getConsumption(loginUser);
        return new Result<>(consumption);
    }
}
