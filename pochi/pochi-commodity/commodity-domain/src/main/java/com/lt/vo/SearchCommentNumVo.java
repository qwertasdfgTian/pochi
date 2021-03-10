package com.lt.vo;

import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;

/**
 * @Program: pochi
 * @Description:
 * @Author: Mr.Tian
 * @Create: 2021-03-02 16:50
 **/
@Data
public class SearchCommentNumVo implements Serializable {

    // 好评数
    private Integer goodNum;
    // 中评数
    private Integer middleNum;
    // 差评数
    private Integer failNum;
}
