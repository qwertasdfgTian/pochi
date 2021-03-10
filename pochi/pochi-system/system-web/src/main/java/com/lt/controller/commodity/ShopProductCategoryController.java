package com.lt.controller.commodity;

import com.lt.controller.BaseController;
import com.lt.dto.ShopProductSearchDto;
import com.lt.enums.ResultEnums;
import com.lt.vo.CategoryBrandVo;
import com.lt.vo.Page;
import com.lt.vo.Result;
import com.lt.pojo.ShopProductCategory;
import com.lt.vo.ShopProductCategoryVo;
import com.lt.service.ShopProductCategoryService;
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
 * @Date: 2020/11/23 23:38
 * @Version 1.0
 */
@RestController
@RequestMapping("/shopProductCategory")
public class ShopProductCategoryController extends BaseController {

    @Reference(timeout = 5000)
    private ShopProductCategoryService shopProductCategoryService;

    /**
     * 添加分类
     *
     * @param shopProductCategory
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @HystrixCommand
    public Result<?> save(@RequestBody @Validated ShopProductCategory shopProductCategory) {
        ShopProductCategory shopProductCategory1 = this.shopProductCategoryService.get(shopProductCategory.getParentId());
        if(null==shopProductCategory1&&shopProductCategory.getLevel()!=1){
            return new Result<>(ResultEnums.CATEGORY_ERROR);
        }else if(shopProductCategory.getLevel()!=1&&shopProductCategory1.getLevel()+1!=shopProductCategory.getLevel()){
            return new Result<>(ResultEnums.CATEGORY_ERROR);
        }
        this.shopProductCategoryService.save(shopProductCategory);
        return new Result<>("添加成功");
    }

    /**
     * 修改分类
     *
     * @param shopProductCategory
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @HystrixCommand
    public Result<?> update(@RequestBody @Validated ShopProductCategory shopProductCategory) {
        ShopProductCategory shopProductCategory1 = this.shopProductCategoryService.get(shopProductCategory.getParentId());
        if(null==shopProductCategory1&&shopProductCategory.getLevel()!=1){
            return new Result<>(ResultEnums.CATEGORY_ERROR);
        }else if(shopProductCategory.getLevel()!=1&&shopProductCategory1.getLevel()+1!=shopProductCategory.getLevel()){
            return new Result<>(ResultEnums.CATEGORY_ERROR);
        }
        if(shopProductCategory.getParentId()==Math.toIntExact(shopProductCategory.getId())){
            return new Result<>(ResultEnums.CATEGORY_PARENT_EXISTS);
        }
        this.shopProductCategoryService.update(shopProductCategory);
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
        ShopProductCategory category = this.shopProductCategoryService.get(id);
        return new Result<>(category);
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
        this.shopProductCategoryService.delete(id);
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
    public Result<?> getByPage(@RequestBody Page<ShopProductCategory> page) {
        page = this.shopProductCategoryService.getByPage(page);
        return new Result<>(page);
    }

    /**
     * 树形列表查询
     *
     * @return
     */
    @RequestMapping(value = "/getTree", method = RequestMethod.GET)
    @HystrixCommand
    public Result<?> getTree() {
        List<ShopProductCategoryVo> list = this.shopProductCategoryService.getTree();
        return new Result<>(list);
    }

    /**
     * 查询树形下拉框
     *
     * @return
     */
    @RequestMapping(value = "/getSelectTree", method = RequestMethod.GET)
    @HystrixCommand
    public Result<?> getSelectTree() {
        List<ShopProductCategoryVo> list = this.shopProductCategoryService.getSelectTree();
        return new Result<>(list);
    }

    /**
     * 获取所有的二级分类
     *
     * @return
     */
    @RequestMapping(value = "/getAllSecond", method = RequestMethod.GET)
    @HystrixCommand
    public Result<?> getAllSecond() {
        List<ShopProductCategory> list = this.shopProductCategoryService.getAllSecond();
        return new Result<>(list);
    }

    /**
     * 查询首页分类
     *
     * @return
     */
    @RequestMapping(value = "/getNavList", method = RequestMethod.GET)
    @HystrixCommand
    public Result<?> getNavList() {
        List<ShopProductCategory> list = this.shopProductCategoryService.getNavList();
        return new Result<>(list);
    }

    /**
     * 根据分类ID查询同级的分类列表和品牌列表
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "/getCategoryAndBrandById/{categoryId}", method = RequestMethod.GET)
    @HystrixCommand
    public Result<?> getCategoryAndBrandById(@PathVariable Long categoryId) {
        CategoryBrandVo vo = this.shopProductCategoryService.getCategoryAndBrandById(categoryId);
        return new Result<>(vo);
    }

}
