package com.lt.service;

import com.lt.dto.ShopProductDto;
import com.lt.dto.ShopProductSearchDto;
import com.lt.es.ShopProductEs;
import com.lt.pojo.ShopProduct;
import com.lt.vo.LoginUser;
import com.lt.vo.ShopProductVo;
import com.lt.vo.Page;

import java.util.List;

public interface ShopProductService {

    /**
     * 添加商品
     * @param shopProductDto
     */
    void save(ShopProductDto shopProductDto, LoginUser loginUser);

    /**
     * 分页查询
     * @param page
     * @return
     */
    Page<ShopProductVo> getByPage(Page<ShopProductVo> page);

    /**
     * 查询回显
     * @param id
     * @return
     */
    ShopProductVo getUpdate(Long id);

    /**
     * 修改
     * @param shopProductDto
     */
    void update(ShopProductDto shopProductDto,LoginUser loginUser);

    /**
     * 删除
     * @param id
     */
    void delete(Long id);

    /**
     * 上架
     * @param id
     */
    void publish(Long id);

    /**
     * 下架
     * @param id
     */
    void unPublish(Long id);

    /**
     * 新品
     * @param id
     */
    void news(Long id);

    /**
     * 取消新品
     * @param id
     */
    void old(Long id);

    /**
     * 推荐
     * @param id
     */
    void recommend(Long id);

    /**
     * 取消推荐
     * @param id
     */
    void unRecommend(Long id);

    /**
     * 根据套装编号查询商品清单
     * @param packCode
     * @return
     */
    List<ShopProduct> getProductDetailList(Long packCode);

    /**
     * 分页查询没有套装的商品
     * @param page
     * @return
     */
    Page<ShopProductVo> getByPageHasNotPack(Page<ShopProductVo> page);


    /**
     * 查询新品商品
     * @return
     */
    List<ShopProduct> getNewProduct();

    /**
     * 查询推荐商品
     * @return
     */
    List<ShopProduct> getRecommendList();

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    ShopProductVo get(Long id);

    /**
     * 根据商品编号查询所在类目下销量最高的6条数据
     * @param productId
     * @return
     */
    List<ShopProduct> getRankByProduct(Long productId);

    /**
     * 搜索
     *
     * @param shopProductDto
     * @return
     */
    Page<ShopProductEs> search(ShopProductSearchDto shopProductDto);


    /**
     * 根据ID集合查询
     * @param ids
     * @return
     */
    List<ShopProduct> getByIds(List<Long> ids);

}
