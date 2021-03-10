package com.lt.dto;

import com.lt.pojo.SysRole;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * 系统用户视图类
 *
 * @Author: Mr.Tian
 * @Date: 2020/11/13 21:34
 * @Version 1.0
 */
@Data
public class SysUserDto implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 微信的openid
     */
    private String openId;

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    private String email;

    /**
     * 昵称
     */
    @NotBlank(message = "昵称不能为空")
    private String nickName;

    /**
     * 头像
     */
    private String header;

    /**
     * 备注
     */
    private String note;

    /**
     * 账号启用状态，1是0否
     */
    private Integer status;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改时间
     */
    private String updateTime;

    /**
     * 最后登录时间
     */
    private String loginTime;
    /**
     * 角色
     */
    private SysRole sysRole;

    /**
     * 权限列表
     */
    private List<String> auths;

}
