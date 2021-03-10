package com.lt.service;

import com.lt.pojo.SysNotice;
import com.lt.vo.Page;

import java.util.List;

public interface SysNoticeService {

    /**
     * 添加
     * @param sysNotice
     */
    void save(SysNotice sysNotice);

    /**
     * 修改
     * @param sysNotice
     */
    void update(SysNotice sysNotice);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    SysNotice get(Long id);

    /**
     * 根据id删除
     * @param id
     */
    void delete(Long id);

    /**
     * 启用
     * @param id
     */
    void enable(Long id);

    /**
     * 禁用
     * @param id
     */
    void disable(Long id);

    /**
     * 分页查询
     * @param page
     * @return
     */
    Page<SysNotice> getByPage(Page<SysNotice> page);

    /**
     * 查询首页通知公告列表
     * @return
     */
    List<SysNotice> getNoticeList();

}
