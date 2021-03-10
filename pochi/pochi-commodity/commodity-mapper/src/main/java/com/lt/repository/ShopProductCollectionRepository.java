package com.lt.repository;

import com.lt.pojo.ShopProductCollection;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: Mr.Tian
 * @Date: 2020/12/20 14:48
 * @Version 1.0
 */

public interface ShopProductCollectionRepository extends MongoRepository<ShopProductCollection, Long> {

    /**
     * 根据商品编号和创建人查询
     * @param productId 商品编号
     * @param createBy 创建人
     * @return
     */
    ShopProductCollection getByProductIdAndCreateBy(Long productId, String createBy);

    /**
     * 根据商品编号集合与创建人查询
     * @param productIds 商品ID集合
     * @param createBy 创建人
     * @return
     */
    List<ShopProductCollection> getByProductIdInAndCreateBy(List<Long> productIds, String createBy);

}
