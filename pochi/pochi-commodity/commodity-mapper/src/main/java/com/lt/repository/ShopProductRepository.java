package com.lt.repository;

import com.lt.es.ShopProductEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/17 20:47
 * @Version 1.0
 */
public interface ShopProductRepository extends ElasticsearchRepository<ShopProductEs, String> {
}
