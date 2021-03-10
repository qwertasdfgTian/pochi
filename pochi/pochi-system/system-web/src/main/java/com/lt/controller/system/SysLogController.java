package com.lt.controller.system;

import com.lt.dto.SysLogDto;
import com.lt.pojo.SysLog;
import com.lt.vo.Page;
import com.lt.vo.Result;
import com.lt.service.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Mr.Tian
 * @Date: 2020/11/17 23:58
 * @Version 1.0
 */
@RestController
@RequestMapping("/sysLog")
public class SysLogController {

    @Autowired
    private SysLogService sysLogService;

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    @RequestMapping(value = "/get/{id}",  method = RequestMethod.GET)
    public Result<SysLog> get(@PathVariable String id) {
        SysLog sysLog = this.sysLogService.get(id);
        return new Result<>(sysLog);
    }

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public Result<?> delete(@PathVariable String id) {
        this.sysLogService.delete(id);
        return new Result<>("删除成功");
    }

    /**
     * 分页查询
     * @param sysLogDto
     * @return
     */
    @RequestMapping(value = "/getByPage", method = RequestMethod.POST)
    public Result<Page<SysLog>> getByPage(@RequestBody SysLogDto sysLogDto) {
        Page<SysLog> page = this.sysLogService.getByPage(sysLogDto);
        return new Result<>(page);
    }

}
