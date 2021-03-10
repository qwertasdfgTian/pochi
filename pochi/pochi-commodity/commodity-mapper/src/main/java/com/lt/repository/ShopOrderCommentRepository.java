package com.lt.repository;

import com.lt.pojo.ShopOrderComment;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/23 22:11
 * @Version 1.0
 */
public interface ShopOrderCommentRepository extends MongoRepository<ShopOrderComment, Long> {
}
