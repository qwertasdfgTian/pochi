package com.lt.controller.commodity;

import com.lt.controller.BaseController;
import com.lt.utils.ShiroUtils;
import com.lt.vo.*;
import com.lt.dto.ShopOrderCommentDto;
import com.lt.service.ShopOrderCommentService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/23 22:14
 * @Version 1.0
 */
@RestController
@RequestMapping("/orderComment")
public class ShopOrderCommentController extends BaseController {

    @Reference
    private ShopOrderCommentService shopOrderCommentService;

    /**
     * 前台评价
     * @param list
     * @return
     */
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public Result<?> save(@RequestBody List<ShopOrderCommentDto> list) {
        LoginUser loginUser = ShiroUtils.getLoginUser();
        shopOrderCommentService.save(list,loginUser);
        return new Result<>("评价成功");
    }

    /**
     * 分页查询
     * @param page
     * @return
     */
    @RequestMapping(value = "/getByPage", method = RequestMethod.POST)
    public Result<Page<ShopOrderCommentVo>> getByPage(@RequestBody Page<ShopOrderCommentVo> page) {
        page = shopOrderCommentService.getByPage(page);
        return new Result<>(page);
    }

    /**
     * 查询各种评论的数量
    */
    @RequestMapping(value = "/searchCommentNum/{id}", method = RequestMethod.GET)
    //@HystrixCommand
    public Result<?> searchCommentNum(@PathVariable Long id) {
        SearchCommentNumVo vo = this.shopOrderCommentService.searchCommentNum(id);
        return new Result<>(vo);
    }

}
