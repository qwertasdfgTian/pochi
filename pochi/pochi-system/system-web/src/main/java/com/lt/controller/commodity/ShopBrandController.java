package com.lt.controller.commodity;

import com.lt.controller.BaseController;
import com.lt.dto.ShopBrandDto;
import com.lt.pojo.ShopBrand;
import com.lt.vo.Page;
import com.lt.vo.Result;
import com.lt.vo.ShopBrandVo;
import com.lt.service.ShopBrandService;
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
 * @Date: 2020/11/24 23:38
 * @Version 1.0
 */
@RestController
@RequestMapping("/brand")
public class ShopBrandController extends BaseController {

    @Reference
    private ShopBrandService shopBrandService;

    /**
     * 添加
     *
     * @param shopBrandDto
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @HystrixCommand
    public Result<?> save(@RequestBody @Validated ShopBrandDto shopBrandDto) {
        this.shopBrandService.save(shopBrandDto);
        return new Result<>("添加成功");
    }

    /**
     * 修改
     *
     * @param shopBrandDto
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @HystrixCommand
    public Result<?> update(@RequestBody @Validated ShopBrandDto shopBrandDto) {
        this.shopBrandService.update(shopBrandDto);
        return new Result<>("修改成功");
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
        this.shopBrandService.delete(id);
        return new Result<>("删除成功");
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
        ShopBrandVo vo = this.shopBrandService.get(id);
        return new Result<>(vo);
    }

    /**
     * 根据id查询用于修改的数据回显
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/getUpdate/{id}", method = RequestMethod.GET)
    @HystrixCommand
    public Result<?> getUpdate(@PathVariable Long id) {
        ShopBrandDto vo = this.shopBrandService.getUpdate(id);
        return new Result<>(vo);
    }

    /**
     * 分页查询
     *
     * @param page
     * @return
     */
    @RequestMapping(value = "/getByPage", method = RequestMethod.POST)
    @HystrixCommand
    public Result<?> getByPage(@RequestBody Page<ShopBrand> page) {
        page = this.shopBrandService.getByPage(page);
        return new Result<>(page);
    }

    /**
     * 根据商品分类ID查询
     *
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "/getByCategoryId/{categoryId}", method = RequestMethod.GET)
    @HystrixCommand
    public Result<?> getByCategoryId(@PathVariable Long categoryId) {
        List<ShopBrand> brandList = this.shopBrandService.getByCategoryId(categoryId);
        return new Result<>(brandList);
    }

    /**
     * 根据名称查询
     * @param name
     * @return
     */
    @RequestMapping(value = "/getByName/{name}", method = RequestMethod.GET)
    @HystrixCommand
    public Result<?> getByName(@PathVariable String name) {
        List<ShopBrand> list = this.shopBrandService.getByName(name);
        return new Result<>(list);
    }
}
