package com.lt.service;

import com.lt.dto.ShopPackDto;
import com.lt.pojo.ShopPack;
import com.lt.vo.LoginUser;
import com.lt.vo.Page;
import com.lt.vo.ShopProductPackVo;

import java.util.List;

public interface ShopPackService {

    /**
     * 添加
     * @param shopPackDto
     */
    void save(ShopPackDto shopPackDto, LoginUser loginUser);

    /**
     * 修改
     * @param shopPackDto
     */
    void update(ShopPackDto shopPackDto);

    /**
     * 根据 id查询
     * @param id
     * @return
     */
    ShopPackDto getById(Long id);

    /**
     * 根据id删除
     * @param id
     */
    void delete(Long id);

    /**
     * 分页查询
     * @param page
     * @return
     */
    Page<ShopPack> getByPage(Page<ShopPack> page);

    /**
     * 根据商品ID查询套装
     * @param productId
     * @return
     */
    List<ShopProductPackVo> getByProductId(Long productId);

}
