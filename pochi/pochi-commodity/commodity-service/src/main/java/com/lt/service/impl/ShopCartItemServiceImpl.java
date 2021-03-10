package com.lt.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lt.enums.StateEnums;
import com.lt.mapper.ShopProductMapper;
import com.lt.pojo.ShopPack;
import com.lt.pojo.ShopProduct;
import com.lt.pojo.ShopProductCollection;
import com.lt.repository.ShopProductCollectionRepository;
import com.lt.utils.DateUtils;
import com.lt.utils.IdWorker;
import com.lt.vo.LoginUser;
import com.lt.vo.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Method;
import org.apache.dubbo.config.annotation.Service;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lt.pojo.ShopCartItem;
import com.lt.mapper.ShopCartItemMapper;
import com.lt.service.ShopCartItemService;
import org.springframework.util.CollectionUtils;

@Service(methods = {@Method(name = "save", retries = 0)})
public class ShopCartItemServiceImpl implements ShopCartItemService{

    @Autowired
    private ShopCartItemMapper shopCartItemMapper;
    @Autowired
    private ShopProductCollectionRepository shopProductCollectionRepository;
    @Autowired
    private ShopProductMapper shopProductMapper;
    @Autowired
    private IdWorker idWorker;

    @Override
    public void save(ShopCartItem shopCartItem,LoginUser loginUser) {
        // 根据用户编号和商品编号查询当前用户是否已经将该商品加入购物车
        shopCartItem.setCreateBy(loginUser.getUsername());
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopCartItem.COL_PRODUCT_ID,shopCartItem.getProductId());
        qw.eq(ShopCartItem.COL_CREATE_BY,shopCartItem.getCreateBy());
        ShopCartItem cartItem = this.shopCartItemMapper.selectOne(qw);
        if (cartItem == null) {
            ShopProduct product = this.shopProductMapper.selectById(shopCartItem.getProductId());
            // 添加购物车
            shopCartItem.setPrice(product.getPrice());
            if (shopCartItem.getQuantity() == null) {
                shopCartItem.setQuantity(1);
            }
            shopCartItem.setProductName(product.getName());
            shopCartItem.setProductPic(product.getPic());
            this.shopCartItemMapper.insert(shopCartItem);
        } else {
            // 修改商品数
            ShopCartItem shopCartItem1=new ShopCartItem();
            shopCartItem1.setId(cartItem.getId());
            shopCartItem1.setQuantity(cartItem.getQuantity()+shopCartItem.getQuantity());
            this.shopCartItemMapper.updateById(shopCartItem1);
        }
    }

    @Override
    public Integer getProductCount(LoginUser loginUser) {
        String username = loginUser.getUsername();
        return shopCartItemMapper.getProductCountByUser(username);
    }

    @Override
    public List<ShopCartItem> getByPage(Page<ShopCartItem> page,LoginUser loginUser) {
        String username = loginUser.getUsername();
        QueryWrapper<ShopCartItem> qw=new QueryWrapper<>();
        ShopCartItem shopCartItem=new ShopCartItem();
        BeanUtil.copyProperties(page.getParams(),shopCartItem);
        shopCartItem.setCreateBy(username);
        qw.eq(ShopCartItem.COL_DELETED, StateEnums.NOT_DELETED.getCode());
        qw.eq(ShopCartItem.COL_STATUS,1);
        qw.eq(ShopCartItem.COL_CREATE_BY,shopCartItem.getCreateBy());
        qw.orderByDesc(ShopCartItem.COL_CREATE_TIME);
        return this.shopCartItemMapper.selectList(qw);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        QueryWrapper qw=new QueryWrapper();
        qw.in(ShopCartItem.COL_ID,ids);
        ShopCartItem shopCartItem=new ShopCartItem();
        shopCartItem.setDeleted(StateEnums.DELETED.getCode());
        this.shopCartItemMapper.update(shopCartItem,qw);
    }

    @Override
    public void moveToCollection(List<Long> ids,LoginUser loginUser) {
        /// 获取创建人
        String username = loginUser.getUsername();
        // 查询购物车，查到所有商品ID
        List<ShopCartItem> cartItemList = this.shopCartItemMapper.selectBatchIds(ids);
        List<Long> productIds = cartItemList.stream().map(ShopCartItem::getProductId).collect(Collectors.toList());
        // 查询商品信息
        List<ShopProduct> productList = this.shopProductMapper.selectBatchIds(productIds);
        // 查询商品收藏情况
        List<ShopProductCollection> collectionList = this.shopProductCollectionRepository.getByProductIdInAndCreateBy(productIds, username);
        if (CollectionUtils.isEmpty(collectionList)) {
            collectionList = new ArrayList<>(0);
        }
        // 构造商品收藏列表
        final List<ShopProductCollection> finalCollectionList = collectionList;
        List<ShopProductCollection> list = new ArrayList<>();
        productList.stream().forEach(e -> {
            ShopProductCollection collection = new ShopProductCollection();
            collection.setId(idWorker.nextId());
            collection.setProductId(e.getId());
            collection.setCreateBy(username);
            // 判断当前商品在collectionList中是否存在
            if (!finalCollectionList.contains(collection)) {
                collection.setCreateTime(DateUtils.newDateTime());
                collection.setProductPic(e.getPic());
                collection.setProductName(e.getName());
                collection.setProductBrand(e.getBrandName());
                collection.setProductPrice(e.getPrice());
                collection.setProductCategoryId(e.getCategoryId());
                list.add(collection);
            }
        });
        // 存库
        this.shopProductCollectionRepository.saveAll(list);
        this.shopCartItemMapper.deleteBatchIds(ids);
    }
}
