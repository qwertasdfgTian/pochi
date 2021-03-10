package com.lt.controller.system;

import com.lt.pojo.SysBanner;
import com.lt.vo.Page;
import com.lt.vo.Result;
import com.lt.service.SysBannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 轮播图控制器
 *
 * @Author: Mr.Tian
 * @Date: 2020/11/18 22:00
 * @Version 1.0
 */
@RestController
@RequestMapping("/sysBanner")
public class SysBannerController {

    @Autowired
    private SysBannerService sysBannerService;

    /**
     * 添加
     *
     * @param sysBanner
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Result<?> save(@RequestBody @Validated SysBanner sysBanner) {
        this.sysBannerService.save(sysBanner);
        return new Result<>("添加成功");
    }

    /**
     * 修改
     *
     * @param sysBanner
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public Result<?> update(@RequestBody @Validated SysBanner sysBanner) {
        this.sysBannerService.update(sysBanner);
        return new Result<>("修改成功");
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public Result<SysBanner> get(@PathVariable Long id) {
        SysBanner sysBanner = this.sysBannerService.get(id);
        return new Result<>(sysBanner);
    }

    /**
     * 根据id删除
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public Result<?> delete(@PathVariable Long id) {
        this.sysBannerService.delete(id);
        return new Result<>("删除成功");
    }

    /**
     * 根据id启用
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/enable/{id}", method = RequestMethod.PUT)
    public Result<?> enable(@PathVariable Long id) {
        this.sysBannerService.enable(id);
        return new Result<>("启用成功");
    }

    /**
     * 根据id弃用
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/disable/{id}", method = RequestMethod.PUT)
    public Result<?> disable(@PathVariable Long id) {
        this.sysBannerService.disable(id);
        return new Result<>("弃用成功");
    }

    /**
     * 分页查询
     * @param page
     * @return
     */
    @RequestMapping(value = "/getByPage", method = RequestMethod.POST)
    public Result<Page<SysBanner>> getByPage(@RequestBody Page<SysBanner> page) {
        page = this.sysBannerService.getByPage(page);
        return new Result<>(page);
    }

    /**
     * 查询首页轮播图
     * @return
     */
    @RequestMapping(value = "/getBannerList", method = RequestMethod.GET)
    public Result<List<SysBanner>> getBannerList() {
        List<SysBanner> list = this.sysBannerService.getBannerList();
        return new Result<>(list);
    }

    /**
     * 点击量+1
     * @param id
     * @return
     */
    @RequestMapping(value = "/addClickCount/{id}", method = RequestMethod.PUT)
    public Result<?> addClickCount(@PathVariable Long id) {
        this.sysBannerService.addClickCount(id);
        return new Result<>();
    }

}
