package com.lt.controller.system;

import com.lt.pojo.SysHelp;
import com.lt.vo.Page;
import com.lt.vo.Result;
import com.lt.service.SysHelpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Mr.Tian
 * @Date: 2020/11/23 20:23
 * @Version 1.0
 */
@RestController
@RequestMapping("/sysHelp")
public class SysHelpController {

    @Autowired
    private SysHelpService sysHelpService;

    /**
     * 添加帮助
     * @param sysHelp
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Result<?> save(@RequestBody @Validated SysHelp sysHelp) {
        this.sysHelpService.save(sysHelp);
        return new Result<>("添加成功");
    }


    /**
     * 修改帮助
     * @param sysHelp
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public Result<?> update(@RequestBody @Validated SysHelp sysHelp) {
        this.sysHelpService.update(sysHelp);
        return new Result<>("修改成功");
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public Result<?> delete(@PathVariable Long id) {
        this.sysHelpService.delete(id);
        return new Result<>("删除成功");
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public Result<SysHelp> get(@PathVariable Long id) {
        SysHelp sysHelp = this.sysHelpService.get(id);
        return new Result<>(sysHelp);
    }

    /**
     * 分页查询
     * @param page
     * @return
     */
    @RequestMapping(value = "/getByPage", method = RequestMethod.POST)
    public Result<Page<SysHelp>> getByPage(@RequestBody Page<SysHelp> page) {
        page = this.sysHelpService.getByPage(page);
        return new Result<>(page);
    }

}
