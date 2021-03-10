package com.lt.controller.commodity;

import com.lt.controller.BaseController;
import com.lt.utils.ShiroUtils;
import com.lt.vo.LoginUser;
import com.lt.vo.Page;
import com.lt.vo.Result;
import com.lt.pojo.ShopProductCollection;
import com.lt.vo.ShopProductCollectionVo;
import com.lt.service.ShopProductCollectionService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Mr.Tian
 * @Date: 2020/12/20 14:49
 * @Version 1.0
 */
@RestController
@RequestMapping("/collection")
public class ShopProductCollectionController extends BaseController {

    @Reference
    private ShopProductCollectionService shopProductCollectionService;

    /**
     * 切换收藏状态
     *
     * @param shopProductCollection
     * @return
     */
    @RequestMapping(value = "/changeCollection", method = RequestMethod.POST)
    @HystrixCommand
    public Result<?> changeCollection(@RequestBody ShopProductCollection shopProductCollection) {
        LoginUser loginUser= ShiroUtils.getLoginUser();
        this.shopProductCollectionService.changeCollection(shopProductCollection,loginUser);
        return new Result<>("操作成功！");
    }

    /**
     * 根据商品ID查询
     *
     * @param productId
     * @return
     */
    @RequestMapping(value = "/getByProductId/{productId}", method = RequestMethod.GET)
    @HystrixCommand
    public Result<?> getByProductId(@PathVariable Long productId) {
        LoginUser loginUser= ShiroUtils.getLoginUser();
        ShopProductCollection shopProductCollection = this.shopProductCollectionService.getByProductId(productId,loginUser);
        return new Result<>(shopProductCollection);
    }

    /**
     * 分页查询
     *
     * @param page
     * @return
     */
    @RequestMapping(value = "/getByPage", method = RequestMethod.POST)
    @HystrixCommand
    public Result<?> getByPage(@RequestBody Page<ShopProductCollection> page) {
        LoginUser loginUser = ShiroUtils.getLoginUser();
        Page<ShopProductCollectionVo> resultPage = this.shopProductCollectionService.getByPage(page,loginUser);
        return new Result<>(resultPage);
    }

    /**
     * 根据创建人查询收藏商品总数
     * @return
     */
    @RequestMapping(value = "/getSumCollection", method = RequestMethod.GET)
    @HystrixCommand
    public Result<?> getSumCollection() {
        LoginUser loginUser= ShiroUtils.getLoginUser();
        Integer count = this.shopProductCollectionService.getSumCollection(loginUser);
        return new Result<>(count);
    }
}
