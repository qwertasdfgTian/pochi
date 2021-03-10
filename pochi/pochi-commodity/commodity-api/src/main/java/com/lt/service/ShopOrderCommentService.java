package com.lt.service;

import com.lt.utils.ShiroUtils;
import com.lt.vo.LoginUser;
import com.lt.vo.Page;
import com.lt.dto.ShopOrderCommentDto;
import com.lt.vo.SearchCommentNumVo;
import com.lt.vo.ShopOrderCommentVo;

import java.util.List;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/23 22:14
 * @Version 1.0
 */
public interface ShopOrderCommentService {
    /**
     * 前台评价
     *
     * @param list
     */
    void save(List<ShopOrderCommentDto> list, LoginUser loginUser);

    /**
     * 分页查询
     *
     * @param page
     * @return
     */
    Page<ShopOrderCommentVo> getByPage(Page<ShopOrderCommentVo> page);

    /**
     * 查询评论数量
     * @Param: id
    */
    SearchCommentNumVo searchCommentNum(Long id);
}
