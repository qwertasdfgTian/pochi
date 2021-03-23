package com.lt.constant;

/**
 * 核心常量
 * @Author: Mr.Tian
 * @Date: 2020/11/8 18:58
 * @Version 1.0
 */
public final class CoreConstant {

    /**
     * 请求头携带token的key
     */
    public static final String TOKEN_HEADER = "Authorization";

    /**
     * 默认的父级菜单编号
     */
    public static final Long DEFAULT_PARENT_ID = 0L;

    /**
     * 菜单不显示
     */
    public static final Integer HIDDEN_STATE = 0;

    /**
     * 不跳转
     */
    public static final String NO_REDIRECT = "noRedirect";

    /**
     * 路径间隔符
     */
    public static final String URL_SPLIT = "/";

    /**
     * 菜单默认的组件地址
     */
    public static final String DEFAULT_COMPONENT = "Layout";

    /**
     * 名称连接符
     */
    public static final String CONCAT_NAME = " -> ";

    /**
     * 存入redis的key
    */
    public static final String ACCESS_TOKEN_KEY = "ACCESS_TOKEN:";

    /**
     * 存入redis的限购标记
     */
    public static final String REDIS_PRODUCT_PREFIX = "product:";

    /**
     * 存入redis的库存
     */
    public static final String REDIS_PRODUCT_STOCK_PREFIX = "product_stock:";

    /**
     * 存入zookeeper的jvm缓存同步标记
     */
    public static final String ZK_PRODUCT_SOLD_OUT_FLAG = "/product_sold_out_flag";

    /**
     * 存入redis的限购标记
     */
    public static String getZKSoldOutProductPath(Long productId) {
        return ZK_PRODUCT_SOLD_OUT_FLAG + "/" + productId;
    }
}
