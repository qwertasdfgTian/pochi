package com.lt.repository;

import com.lt.pojo.ShopProductCollection;
import com.lt.pojo.ShopProductHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Author: Mr.Tian
 * @Date: 2020/12/18 21:14
 * @Version 1.0
 */
public interface ShopProductHistoryRepository extends MongoRepository<ShopProductHistory, Long> {

    ShopProductHistory getByProductIdAndCreateBy(Long productId, String createBy);
}
