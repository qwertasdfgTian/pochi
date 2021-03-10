package com.lt.controller.system;

import com.lt.exception.PochiException;
import com.lt.enums.ResultEnums;
import com.lt.pojo.SysMenu;
import com.lt.vo.RouterVo;
import com.lt.vo.SysMenuVo;
import com.lt.vo.Page;
import com.lt.vo.Result;
import com.lt.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 菜单控制器
 *
 * @Author: Mr.Tian
 * @Date: 2020/11/15 0:00
 * @Version 1.0
 */
@RestController
@RequestMapping("/sysMenu")
public class SysMenuController {

    @Autowired
    private SysMenuService sysMenuService;

    /**
     * 添加菜单
     *
     * @param sysMenu
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Result<?> save(@RequestBody @Validated SysMenu sysMenu) {
        SysMenu sysMenu1 = this.sysMenuService.get(sysMenu.getParentId());
        if(sysMenu.getMenuType()!=1&&sysMenu1==null){
            throw new PochiException(ResultEnums.MENU_ERROR);
        }else if(sysMenu.getMenuType()!=1&&sysMenu.getMenuType()-1!=sysMenu1.getMenuType()){
            throw new PochiException(ResultEnums.MENU_ERROR);
        }
        if(sysMenu.getMenuType()==1){
            if(sysMenu.getParentId()!=0&&sysMenu.getMenuType()!=1)
                throw new PochiException(ResultEnums.MENU_ERROR);
        }
        // 判断如果保存的是权限的话设置隐藏
        if(sysMenu.getMenuType()==3){
            sysMenu.setVisible(0);
        }
        this.sysMenuService.save(sysMenu);
        return new Result<>("添加成功");
    }

    /**
     * 修改菜单
     *
     * @param sysMenu
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public Result<?> update(@RequestBody @Validated SysMenu sysMenu) {
        SysMenu sysMenu1 = this.sysMenuService.get(sysMenu.getParentId());
        if(sysMenu.getMenuType()!=1&&sysMenu1==null){
            throw new PochiException(ResultEnums.MENU_ERROR);
        }else if(sysMenu.getMenuType()!=1&&sysMenu.getMenuType()-1!=sysMenu1.getMenuType()){
            throw new PochiException(ResultEnums.MENU_ERROR);
        }
        if(sysMenu.getMenuType()==1){
            if(sysMenu.getParentId()!=0&&sysMenu.getMenuType()!=1)
                throw new PochiException(ResultEnums.MENU_ERROR);
        }
        if(sysMenu.getParentId()==sysMenu.getMenuId()){
            throw new PochiException(ResultEnums.MENU_PARENT_EXISTS);
        }
        this.sysMenuService.update(sysMenu);
        return new Result<>("修改成功");
    }

    /**
     * 根据id删除
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public Result<?> delete(@PathVariable Long id) {
        this.sysMenuService.delete(id);
        return new Result<>("删除成功");
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public Result<SysMenu> get(@PathVariable Long id) {
        SysMenu sysMenu = this.sysMenuService.get(id);
        return new Result<>(sysMenu);
    }

    /**
     * 分页查询
     *
     * @param page
     * @return
     */
    @RequestMapping(value = "/getByPage", method = RequestMethod.POST)
    public Result<Page<SysMenu>> getByPage(@RequestBody Page<SysMenu> page) {
        page = this.sysMenuService.getByPage(page);
        return new Result<>(page);
    }

    /**
     * 树形查询
     *
     * @return
     */
    @RequestMapping(value = "/getTreeList", method = RequestMethod.GET)
    public Result<List<SysMenuVo>> getTreeList() {
        List<SysMenuVo> list = this.sysMenuService.getTreeList();
        return new Result<>(list);
    }

    /**
     * 根据角色ID查询选中的菜单
     * 这里不查询父级菜单
     *
     * @param roleId
     * @return
     */
    @RequestMapping(value = "/getRoleSelectMenu/{roleId}", method = RequestMethod.GET)
    public Result<List<Long>> getRoleSelectMenu(@PathVariable Long roleId) {
        List<Long> ids = this.sysMenuService.getRoleSelectMenu(roleId);
        return new Result<>(ids);
    }

    /**
     * 获取动态路由
     *
     * @return
     */
    @RequestMapping(value = "/getRouters", method = RequestMethod.GET)
    public Result<List<RouterVo>> getRouters() {
        List<RouterVo> list = this.sysMenuService.getRouters();
        return new Result<>(list);
    }

}
