package com.lt.controller.system;

import com.lt.enums.ResultEnums;
import com.lt.exception.PochiException;
import com.lt.pojo.SysRole;
import com.lt.service.SysRoleService;
import com.lt.vo.Page;
import com.lt.vo.Result;
import com.lt.vo.SysRoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Program: pochi
 * @Description:
 * @Author: Mr.Tian
 * @Create: 2021-01-21 17:11
 **/
@RestController
@RequestMapping("/sysRole")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 添加
     * @param sysRole
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Result<?> save(@RequestBody @Validated SysRoleVo sysRole) {
        SysRole byRoleName=this.sysRoleService.getByRoleName(sysRole.getRoleName());
        if(byRoleName!=null){
            throw new PochiException(ResultEnums.ROLE_EXISTS);
        }
        this.sysRoleService.save(sysRole);
        return new Result<>("添加成功");
    }

    /**
     * 修改
     * @param sysRole
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public Result<?> update(@RequestBody @Validated SysRoleVo sysRole) {
        SysRole byRoleName=this.sysRoleService.getByRoleName(sysRole.getRoleName());
        if(byRoleName!=null&&byRoleName.getRoleId()!=sysRole.getRoleId()){
            throw new PochiException(ResultEnums.ROLE_EXISTS);
        }
        this.sysRoleService.update(sysRole);
        return new Result<>("修改成功");
    }

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public Result<?> delete(@PathVariable Long id) {
        this.sysRoleService.delete(id);
        return new Result<>("删除成功");
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public Result<SysRoleVo> get(@PathVariable Long id) {
        SysRoleVo sysRole = this.sysRoleService.get(id);
        return new Result<>(sysRole);
    }

    /**
     * 分页查询
     * @param page
     * @return
     */
    @RequestMapping(value = "/getByPage", method = RequestMethod.POST)
    public Result<Page<SysRole>> getByPage(@RequestBody Page<SysRole> page) {
        page = this.sysRoleService.getByPage(page);
        return new Result<>(page);
    }

    /**
     * 查询所有角色
     * @return
     */
    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public Result<List<SysRole>> getAll() {
        List<SysRole> list = this.sysRoleService.getAll();
        return new Result<>(list);
    }


}
