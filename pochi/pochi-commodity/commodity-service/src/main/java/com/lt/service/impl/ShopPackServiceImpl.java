package com.lt.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lt.dto.ShopPackDto;
import com.lt.enums.StateEnums;
import com.lt.mapper.ShopProductMapper;
import com.lt.mapper.ShopProductPackMapper;
import com.lt.pojo.ShopProduct;
import com.lt.pojo.ShopProductCategory;
import com.lt.pojo.ShopProductPack;
import com.lt.utils.IdWorker;
import com.lt.vo.LoginUser;
import com.lt.vo.Page;
import com.lt.vo.ShopProductPackVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Method;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.lt.pojo.ShopPack;
import com.lt.mapper.ShopPackMapper;
import com.lt.service.ShopPackService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service(methods = {@Method(name = "save", retries = 0)})
public class ShopPackServiceImpl implements ShopPackService{

    @Autowired
    private ShopPackMapper shopPackMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private ShopProductMapper shopProductMapper;
    @Autowired
    private ShopProductPackMapper shopProductPackMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(ShopPackDto shopPackDto,LoginUser loginUser) {
        // 拷贝属性
        ShopPack shopPack = new ShopPack();
        BeanUtils.copyProperties(shopPackDto, shopPack);
        // ShopPack入库
        shopPack.setCreateBy(loginUser.getUsername());
        long packCode = idWorker.nextId();
        shopPack.setId(packCode);
        int productCount = 0;
        List<ShopProduct> productList = shopPackDto.getProductList();
        if (!CollectionUtils.isEmpty(productList)) {
            productCount = shopPackDto.getProductList().size();
        }
        shopPack.setProductCount(productCount);
        // 保存
        this.shopPackMapper.insert(shopPack);
        if (!CollectionUtils.isEmpty(productList)) {
            // 保存商品套装关联表
            List<Long> productIds = productList.stream().map(ShopProduct::getId).collect(Collectors.toList());
            List<ShopProduct> products = this.shopProductMapper.selectBatchIds(productIds);
            // 构造ShopProductPack
            List<ShopProductPack> packList = products.stream().map(e -> {
                ShopProductPack shopProductPack = new ShopProductPack();
                shopProductPack.setProductId(e.getId());
                shopProductPack.setPackCode(packCode);
                shopProductPack.setPrice(e.getPrice());
                shopProductPack.setStock(e.getStock());
                shopProductPack.setLowStock(e.getLowStock());
                shopProductPack.setSpecName(e.getSpecs());
                if (StringUtils.isBlank(shopProductPack.getSpecName())) {
                    shopProductPack.setSpecName(e.getName());
                }
                shopProductPack.setProductName(e.getName());
                return shopProductPack;
            }).collect(Collectors.toList());
            // 入库
            for (ShopProductPack shopProductPack : packList) {
                this.shopProductPackMapper.insert(shopProductPack);
            }
        }
    }

    @Override
    public void update(ShopPackDto shopPackDto) {
        long packCode = shopPackDto.getId();
        // 拷贝属性
        ShopPack shopPack = new ShopPack();
        BeanUtils.copyProperties(shopPackDto, shopPack);
        int productCount = 0;
        List<ShopProduct> productList = shopPackDto.getProductList();
        if (!CollectionUtils.isEmpty(productList)) {
            productCount = shopPackDto.getProductList().size();
        }
        shopPack.setProductCount(productCount);
        // 修改
        this.shopPackMapper.updateById(shopPack);
        // 删除关联表数据
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopProductPack.COL_PACK_CODE,packCode);
        this.shopProductPackMapper.delete(qw);
        if (!CollectionUtils.isEmpty(productList)) {
            // 保存商品套装关联表
            List<Long> productIds = productList.stream().map(ShopProduct::getId).collect(Collectors.toList());
            List<ShopProduct> products = this.shopProductMapper.selectBatchIds(productIds);
            // 构造ShopProductPack
            List<ShopProductPack> packList = products.stream().map(e -> {
                ShopProductPack shopProductPack = new ShopProductPack();
                shopProductPack.setProductId(e.getId());
                shopProductPack.setPackCode(packCode);
                shopProductPack.setPrice(e.getPrice());
                shopProductPack.setStock(e.getStock());
                shopProductPack.setLowStock(e.getLowStock());
                shopProductPack.setSpecName(e.getSpecs());
                if (StringUtils.isBlank(shopProductPack.getSpecName())) {
                    shopProductPack.setSpecName(e.getName());
                }
                shopProductPack.setProductName(e.getName());
                return shopProductPack;
            }).collect(Collectors.toList());
            // 入库
            for (ShopProductPack shopProductPack : packList) {
                this.shopProductPackMapper.insert(shopProductPack);
            }
        }
    }

    @Override
    public ShopPackDto getById(Long id) {
        ShopPack pack = this.shopPackMapper.selectById(id);
        ShopPackDto shopPackDto = new ShopPackDto();
        BeanUtils.copyProperties(pack, shopPackDto);
        // 根据id查询套装
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopProductPack.COL_PACK_CODE,id);
        List<ShopProductPack> packList = this.shopProductPackMapper.selectList(qw);
        if (!CollectionUtils.isEmpty(packList)) {
            List<Long> productIds = packList.stream().map(ShopProductPack::getProductId).collect(Collectors.toList());
            QueryWrapper qw1=new QueryWrapper();
            qw1.eq(ShopProduct.COL_DELETED, StateEnums.NOT_DELETED.getCode());
            qw1.in(ShopProduct.COL_ID,productIds);
            List<ShopProduct> productList = this.shopProductMapper.selectList(qw1);
            shopPackDto.setProductList(productList);
        }
        return shopPackDto;
    }

    @Override
    public void delete(Long id) {
        this.shopPackMapper.deleteById(id);
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopProductPack.COL_PACK_CODE,id);
        this.shopProductPackMapper.delete(qw);
    }

    @Override
    public Page<ShopPack> getByPage(Page<ShopPack> page) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ShopPack> pages=new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page.getPageNumber(),page.getPageSize());
        QueryWrapper<ShopPack> qw=new QueryWrapper<>();
        ShopPack shopPack=new ShopPack();
        BeanUtil.copyProperties(page.getParams(),shopPack);
        qw.like(StringUtils.isNotBlank(shopPack.getName()),ShopPack.COL_NAME,shopPack.getName());
        this.shopPackMapper.selectPage(pages,qw);
        page.setList(pages.getRecords());
        page.setTotalCount(Math.toIntExact(pages.getTotal()));
        return page;
    }

    @Override
    public List<ShopProductPackVo> getByProductId(Long productId) {
        // 先获取套装编号
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopProductPack.COL_PRODUCT_ID,productId);
        ShopProductPack productPack = this.shopProductPackMapper.selectOne(qw);
        ShopProduct product = this.shopProductMapper.selectById(productId);
        if (productPack == null) {
            List<ShopProductPackVo> resultList = new ArrayList<>(1);
            // 说明该商品没有加入任何套装。将该商品自己视为一个套装
            ShopProductPackVo packVo = new ShopProductPackVo();
            packVo.setProductId(productId);
            packVo.setPic(product.getPic());
            packVo.setPrice(product.getPrice());
            packVo.setProductName(product.getName());
            packVo.setSpecName(product.getSpecs());
            packVo.setStock(product.getStock());
            if (StringUtils.isBlank(packVo.getSpecName())) {
                packVo.setSpecName(packVo.getProductName());
            }
            resultList.add(packVo);
            return resultList;
        }
        Long packCode = productPack.getPackCode();
        // 商品套装编号不为空
        // 根据套装编号查询所有商品
        QueryWrapper qw1=new QueryWrapper();
        qw1.eq(ShopProductPack.COL_PACK_CODE,packCode);
        List<ShopProductPack> productPackList = this.shopProductPackMapper.selectList(qw1);
        // 取出所有商品编号
        List<Long> productIds = productPackList.stream().map(ShopProductPack::getProductId).collect(Collectors.toList());
        // 查询所有商品
        QueryWrapper qw2=new QueryWrapper();
        qw2.in(ShopProduct.COL_ID,productIds);
        qw2.eq(ShopProduct.COL_DELETED,StateEnums.NOT_DELETED.getCode());
        List<ShopProduct> productList = this.shopProductMapper.selectBatchIds(productIds);
        // 转换成VO
        return productList.stream().map(e -> {
            ShopProductPackVo packVo = new ShopProductPackVo();
            packVo.setProductId(e.getId());
            packVo.setPic(e.getPic());
            packVo.setPackCode(packCode);
            packVo.setPrice(e.getPrice());
            packVo.setProductName(e.getName());
            packVo.setSpecName(e.getSpecs());
            packVo.setStock(e.getStock());
            if (StringUtils.isBlank(packVo.getSpecName())) {
                packVo.setSpecName(packVo.getProductName());
            }
            return packVo;
        }).collect(Collectors.toList());
    }
}
