package com.lt.controller.commodity;

import com.lt.controller.BaseController;
import com.lt.utils.ShiroUtils;
import com.lt.vo.LoginUser;
import com.lt.vo.Page;
import com.lt.vo.Result;
import com.lt.pojo.ShopProductHistory;
import com.lt.service.ShopProductHistoryService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author: Mr.Tian
 * @Date: 2020/12/18 21:22
 * @Version 1.0
 */
@RestController
@RequestMapping("/history")
public class ShopProductHistoryController extends BaseController {

    @Reference
    private ShopProductHistoryService shopProductHistoryService;

    /**
     * 保存
     *
     * @param shopProductHistory
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @HystrixCommand
    public Result<?> save(@RequestBody ShopProductHistory shopProductHistory) {
        LoginUser loginUser = ShiroUtils.getLoginUser();
        this.shopProductHistoryService.save(shopProductHistory,loginUser);
        return new Result<>("添加成功");
    }

    /**
     * 清空历史
     *
     * @return
     */
    @RequestMapping(value = "/clearHistory", method = RequestMethod.DELETE)
    @HystrixCommand
    public Result<?> clearHistory() {
        LoginUser loginUser = ShiroUtils.getLoginUser();
        this.shopProductHistoryService.clearHistory(loginUser);
        return new Result<>("清空成功");
    }

    /**
     * 根据id删除浏览记录
     *
     * @return
     */
    @RequestMapping(value = "/deleteHistoryById/{productId}", method = RequestMethod.DELETE)
    @HystrixCommand
    public Result<?> deleteHistoryById(@PathVariable Long productId) {
        LoginUser loginUser = ShiroUtils.getLoginUser();
        this.shopProductHistoryService.deleteHistoryById(loginUser,productId);
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
    public Result<?> getByPage(@RequestBody Page<ShopProductHistory> page) {
        LoginUser loginUser = ShiroUtils.getLoginUser();
        Map<String, List<ShopProductHistory>> data = this.shopProductHistoryService.getByPage(page,loginUser);
        return new Result<>(data);
    }

    /**
     * 根据创建人查询浏览记录总条数
     * @return
     */
    @RequestMapping(value = "/getSumHistory", method = RequestMethod.GET)
    @HystrixCommand
    public Result<?> getSumHistory() {
        LoginUser loginUser= ShiroUtils.getLoginUser();
        Integer count = this.shopProductHistoryService.getSumHistory(loginUser);
        return new Result<>(count);
    }
}
