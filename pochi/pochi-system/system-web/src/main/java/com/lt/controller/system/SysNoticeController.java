package com.lt.controller.system;

import com.lt.pojo.SysNotice;
import com.lt.vo.Result;
import com.lt.vo.Page;
import com.lt.service.SysNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: Mr.Tian
 * @Date: 2020/11/20 21:11
 * @Version 1.0
 */
@RestController
@RequestMapping("/sysNotice")
public class SysNoticeController {

    @Autowired
    private SysNoticeService sysNoticeService;

    /**
     * 添加
     *
     * @param sysNotice
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Result<?> save(@RequestBody @Validated SysNotice sysNotice) {
        this.sysNoticeService.save(sysNotice);
        return new Result<>("添加成功");
    }

    /**
     * 修改
     *
     * @param sysNotice
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public Result<?> update(@RequestBody @Validated SysNotice sysNotice) {
        this.sysNoticeService.update(sysNotice);
        return new Result<>("修改成功");
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public Result<SysNotice> get(@PathVariable Long id) {
        SysNotice sysNotice = this.sysNoticeService.get(id);
        return new Result<>(sysNotice);
    }

    /**
     * 根据id删除
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public Result<?> delete(@PathVariable Long id) {
        this.sysNoticeService.delete(id);
        return new Result<>("删除成功");
    }

    /**
     * 启用
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/enable/{id}", method = RequestMethod.PUT)
    public Result<?> enable(@PathVariable Long id) {
        this.sysNoticeService.enable(id);
        return new Result<>("启用成功");
    }

    /**
     * 禁用
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/disable/{id}", method = RequestMethod.PUT)
    public Result<?> disable(@PathVariable Long id) {
        this.sysNoticeService.disable(id);
        return new Result<>("禁用成功");
    }

    /**
     * 分页查询
     *
     * @param page
     * @return
     */
    @RequestMapping(value = "/getByPage", method = RequestMethod.POST)
    public Result<Page<SysNotice>> getByPage(@RequestBody Page<SysNotice> page) {
        page = this.sysNoticeService.getByPage(page);
        return new Result<>(page);
    }

    /**
     * 查询通知公告列表
     *
     * @return
     */
    @RequestMapping(value = "/getNoticeList", method = RequestMethod.GET)
    public Result<List<SysNotice>> getNoticeList() {
        List<SysNotice> list = this.sysNoticeService.getNoticeList();
        return new Result<>(list);
    }

}
