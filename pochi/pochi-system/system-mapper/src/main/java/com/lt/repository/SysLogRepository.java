package com.lt.repository;

import com.lt.pojo.SysLog;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Author: Mr.Tian
 * @Date: 2020/11/17 23:46
 * @Version 1.0
 */
public interface SysLogRepository extends MongoRepository<SysLog, String> {
}
