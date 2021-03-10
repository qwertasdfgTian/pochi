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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
    * 帮助中心
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_help")
public class SysHelp implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 帮助内容
     */
    @TableField(value = "content")
    @NotBlank(message = "帮助内容不能为空")
    private String content;

    /**
     * 标题
     */
    @TableField(value = "title")
    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 创建时间
     */
    @TableField(value = "created_time")
    private String createdTime;

    /**
     * 创建人
     */
    @TableField(value = "created_by")
    private String createdBy;

    /**
     * 更新人
     */
    @TableField(value = "update_by")
    private String updateBy;

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

    public static final String COL_CONTENT = "content";

    public static final String COL_TITLE = "title";

    public static final String COL_CREATED_TIME = "created_time";

    public static final String COL_CREATED_BY = "created_by";

    public static final String COL_UPDATE_BY = "update_by";

    public static final String COL_UPDATE_TIME = "update_time";

    public static final String COL_DELETED = "deleted";
}