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
    * 公告
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_notice")
public class SysNotice implements Serializable {
    @TableId(value = "notice_id", type = IdType.AUTO)
    private Long noticeId;

    /**
     * 公告标题
     */
    @TableField(value = "notice_title")
    @NotBlank(message = "公告标题不能为空")
    private String noticeTitle;

    /**
     * 公告内容
     */
    @TableField(value = "notice_content")
    @NotBlank(message = "公告内容不能为空")
    private String noticeContent;

    /**
     * 排序值
     */
    @TableField(value = "sorted")
    @NotNull(message = "排序编码不能为空")
    private Integer sorted;

    @TableField(value = "created_by")
    private String createdBy;

    @TableField(value = "created_time")
    private String createdTime;

    /**
     * 是否删除，1是0否
     */
    @TableField(value = "deleted")
    private Integer deleted;

    /**
     * 是否启用，1是0否
     */
    @TableField(value = "enabled")
    private Integer enabled;

    private static final long serialVersionUID = 1L;

    public static final String COL_NOTICE_ID = "notice_id";

    public static final String COL_NOTICE_TITLE = "notice_title";

    public static final String COL_NOTICE_CONTENT = "notice_content";

    public static final String COL_SORTED = "sorted";

    public static final String COL_CREATED_BY = "created_by";

    public static final String COL_CREATED_TIME = "created_time";

    public static final String COL_DELETED = "deleted";

    public static final String COL_ENABLED = "enabled";
}