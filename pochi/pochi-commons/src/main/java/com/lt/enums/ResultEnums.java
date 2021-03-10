package com.lt.enums;

import lombok.Getter;

/**
 * @Author: Mr.Tian
 * @Date: 2020/11/8 13:23
 * @Version 1.0
 */
@Getter
public enum ResultEnums {
    /**
     * 除20000外，其余都是失败，每个返回码代表具体的失败场景
     */
    SUCCESS(20000, "操作成功"),
    ERROR(40000, "操作失败！"),
    DATA_NOT_FOUND(40001, "查询失败！"),
    PARAMS_NULL(40002, "参数不能为空！"),
    ROLE_EXISTS(40003, "角色已存在！"),
    PARAMS_ERROR(40005, "参数不合法！"),
    NO_LOGIN(40006, "用户未登录"),
    LOGIN_PARAM_ERROR(40007, "用户名或密码错误"),
    MENU_EXISTS(40008, "菜单已存在"),
    FILE_TYPE_ERROR(40009, "文件类型不支持"),
    DIR_NOT_FOUND(40010, "文件不存在"),
    FILE_UPLOAD_ERROR(40011, "文件上传失败"),
    AUTH_NOT_FOUNT(40012, "权限不足"),
    CATEGORY_EXISTS(40013, "分类已存在"),
    LOGIN_ERROR(40014, "登录失败"),
    USER_NOT_FOUND(40015, "用户不存在"),
    USER_REAL_EXISTS(40016, "用户已存在"),
    MENU_PARENT_EXISTS(40017, "设置上级菜单失败"),
    CATEGORY_PARENT_EXISTS(40017, "设置上级分类失败"),
    USERNAME_EXISTS(40018, "账号已存在，无法进行添加"),
    SERVER_ERROR(40019, "服务器内部异常，请联系管理员"),
    MENU_ERROR(40020, "设置父级菜单失败，请正确设置菜单"),
    CATEGORY_ERROR(40021, "设置父级分类失败，请正确设置分类"),
    PRODUCT_ERROR(40022, "请选择商品"),
    CATEGOTY_ERROR(40023, "请选择分类");

    private Integer code;
    private String msg;

    ResultEnums(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
