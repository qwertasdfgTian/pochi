package com.lt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lt.pojo.ShopProduct;
import com.lt.vo.Page;
import com.lt.vo.ShopProductVo;

import java.util.List;

public interface ShopProductMapper extends BaseMapper<ShopProduct> {
    /**
     * 分页查询不存在套装的商品
     * @param page
     * @return
     */
    List<ShopProduct> getByPageHasNotPack(Page<ShopProductVo> page);

    /**
     * 查询没有套装的商品总数
     * @param page
     * @return
     */
    Integer countByPageHasNotPack(Page<ShopProductVo> page);

    /**
     * 随机查询新品前十条
    */
    List<ShopProduct> getNewProduct();

    /**
     * 查询推荐商品
    */
    List<ShopProduct> getRecommendList();

    /**
     * 查询所在类目排行榜前6的商品
     * @Param: categoryId
    */
    List<ShopProduct> getRankByCategory(Long categoryId);
}
