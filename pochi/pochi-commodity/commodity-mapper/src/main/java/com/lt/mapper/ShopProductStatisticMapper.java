package com.lt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lt.pojo.ShopProductStatistic;

public interface ShopProductStatisticMapper extends BaseMapper<ShopProductStatistic> {

    /**
     * 更新浏览次数
     * @Param: id
    */
    void updateHistory(Long id);

    /**
     * 查询商品记录
     * @Param: id
    */
    ShopProductStatistic getByProductId(Long id);

    /**
     * 减少商品数
     * @Param: productId
    */
    void removeCollectionCount(Long productId);

    /**
     * 添加商品数
    */
    void addCollectionCount(Long productId);
}