package com.lt.service;

import com.lt.dto.ShopCouponDto;
import com.lt.pojo.ShopCoupon;
import com.lt.pojo.ShopCouponHistory;
import com.lt.vo.LoginUser;
import com.lt.vo.MyBothCouponVo;
import com.lt.vo.Page;

import java.util.List;
import java.util.Map;

public interface ShopCouponService {

    /**
     * 添加
     * @param shopCouponDto
     */
    void save(ShopCouponDto shopCouponDto, LoginUser loginUser);

    /**
     * 删除
     * @param id
     */
    void delete(Long id);

    /**
     * 下架
     * @param id
     */
    void down(Long id);

    /**
     * 分页查询
     * @param page
     * @return
     */
    Page<ShopCoupon> getByPage(Page<ShopCoupon> page);

    /**
     * 查询指定商品允许使用的优惠券
     * @param productId
     * @return
     */
    List<ShopCoupon> getProductCoupon(Long productId,LoginUser loginUser);

    /**
     * 定时更新优惠券过期时间
    */
    void updateTimeoutCoupon();

    /**
     * 领取优惠券
     *
     * @param shopCoupon
     */
    void catchCoupon(ShopCoupon shopCoupon,LoginUser loginUser);

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    ShopCoupon get(Long id);

    /**
     * 查询优惠券领取记录
     *
     * @param id
     * @return
     */
    List<ShopCouponHistory> getHistoryList(Long id);

    /**
     * 查询该用户领取的所有的优惠券
     * @Param: loginUser
    */
    MyBothCouponVo getAllProductCoupon(LoginUser loginUser);

}
