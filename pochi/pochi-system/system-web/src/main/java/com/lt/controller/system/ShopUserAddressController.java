package com.lt.controller.system;

import com.lt.vo.Result;
import com.lt.pojo.ShopUserAddress;
import com.lt.service.ShopUserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/18 21:03
 * @Version 1.0
 */
@RestController
@RequestMapping("/address")
public class ShopUserAddressController {

    @Autowired
    private ShopUserAddressService shopUserAddressService;

    /**
     * 添加地址
     * @param shopUserAddress
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Result<?> save(@RequestBody ShopUserAddress shopUserAddress) {
        this.shopUserAddressService.save(shopUserAddress);
        return new Result<>("添加成功");
    }

    /**
     * 修改地址
     * @param shopUserAddress
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public Result<?> update(@RequestBody ShopUserAddress shopUserAddress) {
        this.shopUserAddressService.update(shopUserAddress);
        return new Result<>("修改成功");
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public Result<?> delete(@PathVariable Long id) {
        this.shopUserAddressService.delete(id);
        return new Result<>("删除成功");
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public Result<?> get(@PathVariable Long id) {
        ShopUserAddress shopUserAddress = this.shopUserAddressService.get(id);
        return new Result<>(shopUserAddress);
    }

    /**
     * 查询用户的收货地址列表
     * @return
     */
    @RequestMapping(value = "/getUserAddress", method = RequestMethod.GET)
    public Result<List<ShopUserAddress>> getUserAddress() {
        List<ShopUserAddress> list = this.shopUserAddressService.getUserAddress();
        return new Result<>(list);
    }


}
