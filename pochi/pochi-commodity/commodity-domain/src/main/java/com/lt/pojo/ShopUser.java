package com.lt.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.lt.vo.LoginUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会员表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "shop_user")
public class ShopUser implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 手机号
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 密码
     */
    @TableField(value = "password")
    private String password;

    /**
     * 昵称
     */
    @TableField(value = "nickname")
    private String nickname;

    /**
     * 状态，1正常0封禁
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 头像
     */
    @TableField(value = "header")
    private String header;

    /**
     * 性别，1男2女
     */
    @TableField(value = "gender")
    private Integer gender;

    /**
     * 签名
     */
    @TableField(value = "note")
    private String note;

    /**
     * openid
     */
    @TableField(value = "openid")
    private String openId;;

    /**
     * 积分
     */
    @TableField(value = "point")
    private BigDecimal point;

    /**
     * 生日
     */
    @TableField(value = "birthday")
    private String birthday;

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
     * 是否删除，1是0否
     */
    @TableField(value = "deleted")
    private Integer deleted;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_PHONE = "phone";

    public static final String COL_PASSWORD = "password";

    public static final String COL_NICKNAME = "nickname";

    public static final String COL_STATUS = "status";

    public static final String COL_HEADER = "header";

    public static final String COL_GENDER = "gender";

    public static final String COL_NOTE = "note";

    public static final String COL_OPENID = "openid";

    public static final String COL_POINT = "point";

    public static final String COL_BIRTHDAY = "birthday";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";

    public static final String COL_DELETED = "deleted";

    /**
     * 转换为loginUser对象
     * @return
     */
    public LoginUser toLoginUser() {
        LoginUser user = new LoginUser();
        user.setId(id);
        user.setUsername(phone);
        user.setNickName(nickname);
        user.setHeader(header);
        user.setOpenId(openId);
        user.setSex(gender);
        user.setBirthday(birthday);
        return user;
    }
}