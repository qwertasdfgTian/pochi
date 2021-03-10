package com.lt.controller.commodity;

import com.lt.pojo.ShopPack;
import com.lt.service.ShopPackService;
import com.lt.utils.ShiroUtils;
import com.lt.vo.LoginUser;
import com.lt.vo.Page;
import com.lt.vo.Result;
import com.lt.dto.ShopPackDto;
import com.lt.controller.BaseController;
import com.lt.vo.ShopProductPackVo;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: Mr.Tian
 * @Date: 2020/12/2 22:19
 * @Version 1.0
 */
@RestController
@RequestMapping("/shopPack")
public class ShopPackController extends BaseController {

    @Reference
    private ShopPackService shopPackService;

    /**
     * 添加
     *
     * @param shopPackDto
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @HystrixCommand
    public Result<?> save(@RequestBody @Validated ShopPackDto shopPackDto) {
        LoginUser loginUser= ShiroUtils.getLoginUser();
        this.shopPackService.save(shopPackDto,loginUser);
        return new Result<>("添加成功");
    }

    /**
     * 修改
     *
     * @param shopPackDto
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @HystrixCommand
    public Result<?> update(@RequestBody @Validated ShopPackDto shopPackDto) {
        this.shopPackService.update(shopPackDto);
        return new Result<>("修改成功");
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    @HystrixCommand
    public Result<?> get(@PathVariable Long id) {
        ShopPackDto packDto = this.shopPackService.getById(id);
        return new Result<>(packDto);
    }

    /**
     * 根据id删除
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @HystrixCommand
    public Result<?> delete(@PathVariable Long id) {
        this.shopPackService.delete(id);
        return new Result<>("删除成功");
    }

    /**
     * 分页查询
     *
     * @param page
     * @return
     */
    @RequestMapping(value = "/getByPage", method = RequestMethod.POST)
    @HystrixCommand
    public Result<?> getByPage(@RequestBody Page<ShopPack> page) {
        page = this.shopPackService.getByPage(page);
        return new Result<>(page);
    }

    /**
     * 根据商品编号查询套装
     *
     * @param productId
     * @return
     */
    @RequestMapping(value = "/getByProductId/{productId}", method = RequestMethod.GET)
    @HystrixCommand
    public Result<?> getByProductId(@PathVariable Long productId) {
        List<ShopProductPackVo> list = this.shopPackService.getByProductId(productId);
        return new Result<>(list);
    }

}
