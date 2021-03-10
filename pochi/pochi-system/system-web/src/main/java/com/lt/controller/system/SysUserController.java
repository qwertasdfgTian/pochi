package com.lt.controller.system;

import com.lt.config.shiro.SysUserRealm;
import com.lt.config.shiro.UserToken;
import com.lt.dto.SysUserDto;
import com.lt.enums.ResultEnums;
import com.lt.exception.PochiException;
import com.lt.pojo.SysUser;
import com.lt.utils.ShiroUtils;
import com.lt.vo.LoginUser;
import com.lt.vo.Page;
import com.lt.vo.Result;
import com.lt.service.SysUserService;
import com.lt.vo.TokenVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

/**
 * @Program: pochi
 * @Description: 系统用户控制器
 * @Author: Mr.Tian
 * @Create: 2021-01-20 11:27
 **/
@RestController
@RequestMapping("/sysUser")
@Slf4j
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 保存用户
     *
     * @param sysUserDto
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @RequiresPermissions("sys:user:add")
    public Result<?> save(@RequestBody @Validated SysUserDto sysUserDto) {
        // 参数校验
        if (StringUtils.isBlank(sysUserDto.getUsername())) {
            return new Result<>(ResultEnums.PARAMS_NULL, "用户名不能为空！");
        }
        if (StringUtils.isBlank(sysUserDto.getPassword())) {
            return new Result<>(ResultEnums.PARAMS_NULL, "密码不能为空");
        }
        SysUser byUsername = this.sysUserService.getByUsername(sysUserDto.getUsername());
        if(byUsername!=null){
            throw new PochiException(ResultEnums.USERNAME_EXISTS);
        }
        this.sysUserService.save(sysUserDto);
        return new Result<>("添加成功");
    }

    /**
     * 修改用户
     * 修改接口一般不提供密码修改功能
     *
     * @param sysUserDto
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @RequiresPermissions("sys:user:update")
    public Result<?> update(@RequestBody @Validated SysUserDto sysUserDto) {
        this.sysUserService.update(sysUserDto);
        return new Result<>("修改成功");
    }

    /**
     * 删除接口
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @RequiresPermissions("sys:user:delete")
    public Result<?> delete(@PathVariable Long id) {
        this.sysUserService.delete(id);
        return new Result<>("删除成功");
    }

    /**
     * 启用用户
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/enable/{id}", method = RequestMethod.PUT)
    public Result<?> enable(@PathVariable Long id) {
        this.sysUserService.enable(id);
        return new Result<>("启用成功");
    }

    /**
     * 封禁用户
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/disable/{id}", method = RequestMethod.PUT)
    public Result<?> disable(@PathVariable Long id) {
        this.sysUserService.disable(id);
        return new Result<>("禁用成功");
    }

    /**
     * 分页查询
     *
     * @param page
     * @return
     */
    @RequestMapping(value = "/getByPage", method = RequestMethod.POST)
    public Result<Page<SysUser>> getByPage(@RequestBody Page<SysUser> page) {
        page = this.sysUserService.getByPage(page);
        return new Result<>(page);
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public Result<SysUserDto> get(@PathVariable Long id) {
        SysUserDto sysUser = this.sysUserService.get(id);
        return new Result<>(sysUser);
    }

    /**
     * 登录
     *
     * @param sysUser
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result<TokenVo> login(@RequestBody SysUser sysUser) {
        // 校验用户名密码
        if (sysUser == null || StringUtils.isBlank(sysUser.getUsername()) || StringUtils.isBlank(sysUser.getPassword())) {
            return new Result<>(ResultEnums.LOGIN_PARAM_ERROR);
        }
        // 使用shiro进行登录
        Subject subject = SecurityUtils.getSubject();
        AuthenticationToken authenticationToken = new UserToken(sysUser.getUsername(), sysUser.getPassword(), SysUserRealm.class);
        try {
            subject.login(authenticationToken);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<>(ResultEnums.LOGIN_PARAM_ERROR);
        }
        // 登录成功
        Serializable sessionId = subject.getSession().getId();
        // 更新登录时间
        this.sysUserService.updateLoginTime(sysUser.getUsername());
        return new Result<>(new TokenVo(sessionId));
    }

    /**
     * 获取登录用户
     * @return
     */
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public Result<LoginUser> info() {
        LoginUser sysUser = ShiroUtils.getLoginUser();
        return new Result<>(sysUser);
    }

    /**
     * 退出登录
    */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public Result<?> logout() {
        Subject currentUser = SecurityUtils.getSubject();
        currentUser.logout();
        return new Result<>("退出成功");
    }
}
