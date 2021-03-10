package com.lt.controller.commodity;

import com.lt.dto.ShopProductDto;
import com.lt.controller.BaseController;
import com.lt.dto.ShopProductSearchDto;
import com.lt.es.ShopProductEs;
import com.lt.pojo.ShopProduct;
import com.lt.utils.ShiroUtils;
import com.lt.vo.LoginUser;
import com.lt.vo.ShopProductVo;
import com.lt.vo.Page;
import com.lt.vo.Result;
import com.lt.service.ShopProductService;
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
@RequestMapping("/product")
public class ShopProductController extends BaseController {

    @Reference(timeout = 50000)
    private ShopProductService shopProductService;

    /**
     * 添加商品
     *
     * @param shopProductDto
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @HystrixCommand
    public Result<?> save(@RequestBody @Validated ShopProductDto shopProductDto) {
        LoginUser loginUser= ShiroUtils.getLoginUser();
        this.shopProductService.save(shopProductDto,loginUser);
        return new Result<>("添加成功");
    }

    /**
     * 分页查询
     *
     * @param page
     * @return
     */
    @RequestMapping(value = "/getByPage", method = RequestMethod.POST)
    @HystrixCommand
    public Result<?> getByPage(@RequestBody Page<ShopProductVo> page) {
        page = this.shopProductService.getByPage(page);
        return new Result<>(page);
    }

    /**
     * 根据ID集合查询
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "/getByIds", method = RequestMethod.POST)
    @HystrixCommand
    public Result<?> getByIds(@RequestBody List<Long> ids) {
        List<ShopProduct> list = this.shopProductService.getByIds(ids);
        return new Result<>(list);
    }

    /**
     * 分页查询没有套装的商品
     *
     * @param page
     * @return
     */
    @RequestMapping(value = "/getByPageHasNotPack", method = RequestMethod.POST)
    @HystrixCommand
    public Result<?> getByPageHasNotPack(@RequestBody Page<ShopProductVo> page) {
        page = this.shopProductService.getByPageHasNotPack(page);
        return new Result<>(page);
    }

    /**
     * 查询回显数据
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/getUpdate/{id}", method = RequestMethod.GET)
    @HystrixCommand
    public Result<?> getUpdate(@PathVariable Long id) {
        ShopProductVo vo = this.shopProductService.getUpdate(id);
        return new Result<>(vo);
    }

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    @HystrixCommand
    public Result<?> get(@PathVariable Long id) {
        ShopProductVo vo = this.shopProductService.get(id);
        return new Result<>(vo);
    }

    /**
     * 修改商品
     *
     * @param shopProductDto
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @HystrixCommand
    public Result<?> update(@RequestBody @Validated ShopProductDto shopProductDto) {
        LoginUser loginUser= ShiroUtils.getLoginUser();
        this.shopProductService.update(shopProductDto,loginUser);
        return new Result<>("修改成功");
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @HystrixCommand
    public Result<?> delete(@PathVariable Long id) {
        this.shopProductService.delete(id);
        return new Result<>("删除成功");
    }

    /**
     * 上架
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/publish/{id}", method = RequestMethod.PUT)
    @HystrixCommand
    public Result<?> publish(@PathVariable Long id) {
        this.shopProductService.publish(id);
        return new Result<>("已上架");
    }

    /**
     * 下架
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/unPublish/{id}", method = RequestMethod.PUT)
    @HystrixCommand
    public Result<?> unPublish(@PathVariable Long id) {
        this.shopProductService.unPublish(id);
        return new Result<>("已下架");
    }

    /**
     * 新品
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/news/{id}", method = RequestMethod.PUT)
    @HystrixCommand
    public Result<?> news(@PathVariable Long id) {
        this.shopProductService.news(id);
        return new Result<>("修改成功");
    }

    /**
     * 非新品
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/old/{id}", method = RequestMethod.PUT)
    @HystrixCommand
    public Result<?> old(@PathVariable Long id) {
        this.shopProductService.old(id);
        return new Result<>("修改成功");
    }

    /**
     * 推荐
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/recommend/{id}", method = RequestMethod.PUT)
    @HystrixCommand
    public Result<?> recommend(@PathVariable Long id) {
        this.shopProductService.recommend(id);
        return new Result<>("推荐成功");
    }

    /**
     * 不推荐
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/unRecommend/{id}", method = RequestMethod.PUT)
    @HystrixCommand
    public Result<?> unRecommend(@PathVariable Long id) {
        this.shopProductService.unRecommend(id);
        return new Result<>("修改成功");
    }

    /**
     * 查询商品清单
     * @param packCode
     * @return
     */
    @RequestMapping(value = "/getProductDetailList/{packCode}", method = RequestMethod.GET)
    @HystrixCommand
    public Result<?> getProductDetailList(@PathVariable Long packCode) {
        List<ShopProduct> productList = shopProductService.getProductDetailList(packCode);
        return new Result<>(productList);
    }

    /**
     * 查询新品推荐
     * @return
     */
    @RequestMapping(value = "/getNewProduct", method = RequestMethod.GET)
    @HystrixCommand
    public Result<?> getNewProduct() {
        List<ShopProduct> list = this.shopProductService.getNewProduct();
        return new Result<>(list);
    }

    /**
     * 查询推荐商品
     * @return
     */
    @RequestMapping(value = "/getRecommendList", method = RequestMethod.GET)
    @HystrixCommand
    public Result<?> getRecommendList() {
        List<ShopProduct> list = this.shopProductService.getRecommendList();
        return new Result<>(list);
    }

    /**
     * 查询排行榜
     * 查询所在类目销量最高的6条数据
     * @param productId
     * @return
     */
    @RequestMapping(value = "/getRankByProduct/{productId}", method = RequestMethod.GET)
    @HystrixCommand
    public Result<?> getRankByProduct(@PathVariable Long productId) {
        List<ShopProduct> list = this.shopProductService.getRankByProduct(productId);
        return new Result<>(list);
    }

    /**
     * 商品搜索
     *
     * @param shopProductDto
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    //@HystrixCommand
    public Result<?> search(@RequestBody ShopProductSearchDto shopProductDto) {
        Page<ShopProductEs> page = this.shopProductService.search(shopProductDto);
        return new Result<>(page);
    }
}
