package com.lt.service;

import com.lt.dto.SysLogDto;
import com.lt.pojo.SysLog;
import com.lt.vo.Page;

public interface SysLogService {

    /**
     * 保存日志
     * @param sysLog
     */
    void save(SysLog sysLog);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    SysLog get(String id);

    /**
     * 根据ID删除
     * @param id
     */
    void delete(String id);

    /**
     * 分页查询
     * @param sysLogDto
     * @return
     */
    Page<SysLog> getByPage(SysLogDto sysLogDto);

}

