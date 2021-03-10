package com.lt.service;

import com.lt.dto.ShopProductSearchDto;
import com.lt.vo.CategoryBrandVo;
import com.lt.vo.Page;
import com.lt.pojo.ShopProductCategory;
import com.lt.vo.ShopProductCategoryVo;

import java.util.List;

public interface ShopProductCategoryService {

    /**
     * 添加
     * @param shopProductCategory
     */
    void save(ShopProductCategory shopProductCategory);

    /**
     * 修改
     * @param shopProductCategory
     */
    void update(ShopProductCategory shopProductCategory);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    ShopProductCategory get(Long id);

    /**
     * 根据id删除
     * @param id
     * @return
     */
    void delete(Long id);

    /**
     * 分页查询
     * @param page
     * @return
     */
    Page<ShopProductCategory> getByPage(Page<ShopProductCategory> page);

    /**
     * 树形查询
     * @return
     */
    List<ShopProductCategoryVo> getTree();

    /**
     * 查询树形下拉框
     * @return
     */
    List<ShopProductCategoryVo> getSelectTree();

    /**
     * 查询所有二级分类
     * @return
     */
    List<ShopProductCategory> getAllSecond();

    /**
     * 查询首页导航宫格
     * @return
     */
    List<ShopProductCategory> getNavList();

    /**
     * 根据分类ID查询同级的分类列表和品牌列表
     * @Param: categoryId
    */
    CategoryBrandVo getCategoryAndBrandById(Long categoryId);

}
