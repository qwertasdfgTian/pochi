package com.lt.service.impl;

import com.lt.dto.SysLogDto;
import com.lt.pojo.SysLog;
import com.lt.repository.SysLogRepository;
import com.lt.utils.DateUtils;
import com.lt.utils.IdWorker;
import com.lt.vo.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import java.util.List;

import com.lt.service.SysLogService;
import org.springframework.util.CollectionUtils;

@Service
public class SysLogServiceImpl implements SysLogService {

    @Autowired
    private SysLogRepository sysLogRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdWorker idWorker;
    /**
     * 保存日志
     * @Param: sysLog
    */
    @Override
    public void save(SysLog sysLog) {
        sysLog.setLogId(idWorker.nextId() + "");
        sysLog.setCreatedBy("admin");
        // 创建时间
        sysLog.setCreatedDate(DateUtils.newDateTime());
        this.sysLogRepository.save(sysLog);
    }

    @Override
    public SysLog get(String id) {
        return this.sysLogRepository.findById(id).get();
    }

    @Override
    public void delete(String id) {
        this.sysLogRepository.deleteById(id);
    }

    @Override
    public Page<SysLog> getByPage(SysLogDto sysLogDto) {
        // 构造一个查询对象
        Query query = new Query();
        // 排除列表页不需要的大字段
        query.fields().exclude("log_params");
        query.fields().exclude("log_result");
        query.fields().exclude("log_message");
        // 设置查询参数
        if (StringUtils.isNotBlank(sysLogDto.getLogUrl())) {
            // 如果url不为空，就构造url的查询条件
            query.addCriteria(Criteria.where("log_url").regex(sysLogDto.getLogUrl()));
        }
        if (sysLogDto.getLogStatus() != null) {
            // 根据状态查询
            query.addCriteria(Criteria.where("log_status").is(sysLogDto.getLogStatus()));
        }
        // 根据controller查询
        if (StringUtils.isNotBlank(sysLogDto.getLogController())) {
            query.addCriteria(Criteria.where("log_controller").regex(sysLogDto.getLogUrl()));
        }
        // 根据时间区间查询
        if (!CollectionUtils.isEmpty(sysLogDto.getCreatedDate())) {
            /// 创建时间集合不为空，就使用创建时间查询
            // 同一个列出现多次会报错，这里可以使用andOperator的方法
            query.addCriteria(Criteria.where("created_date").gt(sysLogDto.getCreatedDate().get(0))
                    .andOperator(Criteria.where("created_date").lt(sysLogDto.getCreatedDate().get(1))));
        }
        // 处理分页条件
        Integer pageNumber = sysLogDto.getPageNumber();
        Integer pageSize = sysLogDto.getPageSize();
        if (pageSize == null || pageSize < 1) {
            pageSize = 20;
            sysLogDto.setPageSize(pageSize);
        }
        // 查询总条数
        long count = this.mongoTemplate.count(query, SysLog.class);
        // 构造分页
        // 跳过多少条
        query.skip((pageNumber - 1) * pageSize);
        // 取出多少条
        query.limit(pageSize);
        // 构造排序对象，默认根据创建时间倒序排列，并根据响应时间倒序排列
        Sort.Order dateOrder = new Sort.Order(Sort.Direction.DESC, "created_date");
        Sort.Order timeOrder = new Sort.Order(Sort.Direction.DESC, "log_time");
        // 设置排序对象
        query.with(Sort.by(dateOrder, timeOrder));
        // 查询
        List<SysLog> logList = this.mongoTemplate.find(query, SysLog.class);
        // 创建Page对象，封装返回结果
        Page<SysLog> page = new Page<>();
        page.setList(logList);
        page.setTotalCount((int) count);
        page.setPageNumber(pageNumber);
        page.setPageSize(pageSize);
        return page;
    }
}

