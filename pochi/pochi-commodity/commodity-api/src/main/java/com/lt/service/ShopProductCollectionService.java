package com.lt.service;

import com.lt.vo.LoginUser;
import com.lt.vo.Page;
import com.lt.pojo.ShopProductCollection;
import com.lt.vo.ShopProductCollectionVo;

public interface ShopProductCollectionService {

    /**
     * 切换收藏状态
     * @param shopProductCollection
     */
    void changeCollection(ShopProductCollection shopProductCollection, LoginUser loginUser);

    /**
     * 根据商品Id查询
     * @param productId
     * @return
     */
    ShopProductCollection getByProductId(Long productId,LoginUser loginUser);

    /**
     * 分页查询
     * @param page
     * @return
     */
    Page<ShopProductCollectionVo> getByPage(Page<ShopProductCollection> page,LoginUser loginUser);

    /**
     * 根据创建人查询收藏商品总数
     * @Param: loginUser
    */
    Integer getSumCollection(LoginUser loginUser);
}
