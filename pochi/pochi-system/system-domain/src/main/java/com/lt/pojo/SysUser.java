package com.lt.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
    * 后台用户表
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_user")
public class SysUser implements Serializable {
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 账号
     */
    @TableField(value = "username")
    private String username;

    /**
     * 微信小程序openid
     */
    @TableField(value = "openid")
    private String openId;

    /**
     * 密码
     */
    @TableField(value = "password")
    private String password;

    /**
     * 邮箱
     */
    @TableField(value = "email")
    private String email;

    /**
     * 昵称
     */
    @TableField(value = "nick_name")
    private String nickName;

    /**
     * 头像
     */
    @TableField(value = "header")
    private String header;

    /**
     * 备注信息
     */
    @TableField(value = "note")
    private String note;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private String createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private String updateTime;

    /**
     * 最后登录时间
     */
    @TableField(value = "login_time")
    private String loginTime;

    /**
     * 帐号启用状态：0->禁用；1->启用
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 是否删除，1是0否
     */
    @TableField(value = "deleted")
    private Integer deleted;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_USERNAME = "username";

    public static final String COL_OPENID = "openid";

    public static final String COL_PASSWORD = "password";

    public static final String COL_EMAIL = "email";

    public static final String COL_NICK_NAME = "nick_name";

    public static final String COL_HEADER = "header";

    public static final String COL_NOTE = "note";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";

    public static final String COL_LOGIN_TIME = "login_time";

    public static final String COL_STATUS = "status";

    public static final String COL_DELETED = "deleted";
}