package com.lt.service;

import com.lt.pojo.ShopProductHistory;
import com.lt.vo.LoginUser;
import com.lt.vo.Page;

import java.util.List;
import java.util.Map;

public interface ShopProductHistoryService {

    /**
     * 保存
     * @param shopProductHistory
     */
    void save(ShopProductHistory shopProductHistory, LoginUser loginUser);

    /**
     * 清空历史记录
     */
    void clearHistory(LoginUser loginUser);

    /**
     * 分页查询
     * @param page
     * @return
     */
    Map<String, List<ShopProductHistory>> getByPage(Page<ShopProductHistory> page,LoginUser loginUser);

    /**
     * 根据创建人查询浏览记录总条数
     * @return
     */
    Integer getSumHistory(LoginUser loginUser);

    /**
     * 根据id删除浏览记录
     *
     * @return
     */
    void deleteHistoryById(LoginUser loginUser,Long productId);
}
