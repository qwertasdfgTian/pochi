package com.lt.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lt.dto.ShopBrandDto;
import com.lt.mapper.ShopBrandCategoryMapper;
import com.lt.mapper.ShopBrandMapper;
import com.lt.mapper.ShopProductCategoryMapper;
import com.lt.pojo.ShopBrand;
import com.lt.pojo.ShopBrandCategory;
import com.lt.pojo.ShopProductCategory;
import com.lt.service.ShopBrandService;
import com.lt.vo.Page;
import com.lt.vo.ShopBrandVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Method;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service(methods = {@Method(name = "save", retries = 0)})
public class ShopBrandServiceImpl implements ShopBrandService{

    @Autowired
    private ShopBrandMapper shopBrandMapper;
    @Autowired
    private ShopBrandCategoryMapper shopBrandCategoryMapper;
    @Autowired
    private ShopProductCategoryMapper shopProductCategoryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(ShopBrandDto shopBrandDto) {
        // 转成实体类
        ShopBrand shopBrand = new ShopBrand();
        BeanUtils.copyProperties(shopBrandDto, shopBrand);
        // 将品牌存库
        this.shopBrandMapper.save(shopBrand);
        // 将品牌-分类存库
        // 判断分类ID集合是否为空，不为空就入库
        if (!CollectionUtils.isEmpty(shopBrandDto.getCategoryIds())) {
            List<ShopBrandCategory> brandCategoryList = shopBrandDto.getCategoryIds().stream().map(cid -> {
                ShopBrandCategory shopBrandCategory = new ShopBrandCategory();
                shopBrandCategory.setCategoryId(cid);
                shopBrandCategory.setBrandId(shopBrand.getId());
                return shopBrandCategory;
            }).collect(Collectors.toList());
            // 存库
            for (ShopBrandCategory shopBrandCategory : brandCategoryList) {
                this.shopBrandCategoryMapper.insert(shopBrandCategory);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ShopBrandDto shopBrandDto) {
        // 转成实体类
        ShopBrand shopBrand = new ShopBrand();
        BeanUtils.copyProperties(shopBrandDto, shopBrand);
        // 修改
        this.shopBrandMapper.updateById(shopBrand);
        // 将品牌-分类删除
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopBrandCategory.COL_BRAND_ID,shopBrand.getId());
        this.shopBrandCategoryMapper.delete(qw);
        // 将品牌-分类存库
        // 判断分类ID集合是否为空，不为空就入库
        if (!CollectionUtils.isEmpty(shopBrandDto.getCategoryIds())) {
            List<ShopBrandCategory> brandCategoryList = shopBrandDto.getCategoryIds().stream().map(cid -> {
                ShopBrandCategory shopBrandCategory = new ShopBrandCategory();
                shopBrandCategory.setCategoryId(cid);
                shopBrandCategory.setBrandId(shopBrand.getId());
                return shopBrandCategory;
            }).collect(Collectors.toList());
            // 存库
            for (ShopBrandCategory shopBrandCategory : brandCategoryList) {
                this.shopBrandCategoryMapper.insert(shopBrandCategory);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        this.shopBrandMapper.deleteById(id);
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopBrandCategory.COL_BRAND_ID,id);
        this.shopBrandCategoryMapper.delete(qw);
    }

    @Override
    public ShopBrandVo get(Long id) {
        // 查询出品牌
        ShopBrand shopBrand = this.shopBrandMapper.selectById(id);
        // 转成VO
        ShopBrandVo shopBrandVo = new ShopBrandVo();
        BeanUtils.copyProperties(shopBrand, shopBrandVo);
        // 根据品牌id查询品牌-分类关联关系
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopBrandCategory.COL_BRAND_ID,id);
        List<ShopBrandCategory> shopBrandCategoryList = this.shopBrandCategoryMapper.selectList(qw);
        if (!CollectionUtils.isEmpty(shopBrandCategoryList)) {
            // 取出分类ID
            List<Long> categoryIds = shopBrandCategoryList.stream().map(ShopBrandCategory::getCategoryId).collect(Collectors.toList());
            // 根据关联关系查询分类
            List<ShopProductCategory> categoryList = this.shopProductCategoryMapper.selectBatchIds(categoryIds);
            List<ShopProductCategory> categories=new ArrayList<>();
            for (ShopProductCategory category : categoryList) {
                if (category.getLevel()==2)
                    categories.add(category);
            }
            shopBrandVo.setCategoryList(categories);
        }
        return shopBrandVo;
    }

    @Override
    public ShopBrandDto getUpdate(Long id) {
        ShopBrand shopBrand = this.shopBrandMapper.selectById(id);
        // 转成DTO
        ShopBrandDto shopBrandDto = new ShopBrandDto();
        BeanUtils.copyProperties(shopBrand, shopBrandDto);
        // 查询品牌-分类关系
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopBrandCategory.COL_BRAND_ID,id);
        List<ShopBrandCategory> brandCategoryList = this.shopBrandCategoryMapper.selectList(qw);
        // 判断集合是否为空，不为空就取出分类ID集合
        if (!CollectionUtils.isEmpty(brandCategoryList)) {
            List<Long> categoryIds = brandCategoryList.stream().map(ShopBrandCategory::getCategoryId).collect(Collectors.toList());
            // set进dto
            List<ShopProductCategory> categoryList = this.shopProductCategoryMapper.selectBatchIds(categoryIds);
            List<Long> categories=new ArrayList<>();
            for (ShopProductCategory category : categoryList) {
                if (category.getLevel()==2)
                    categories.add(category.getId());
            }
            shopBrandDto.setCategoryIds(categories);
        }
        return shopBrandDto;
    }

    @Override
    public Page<ShopBrand> getByPage(Page<ShopBrand> page) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ShopBrand> pages=new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page.getPageNumber(),page.getPageSize());
        QueryWrapper<ShopBrand> qw=new QueryWrapper<>();
        ShopBrand shopBrand=new ShopBrand();
        BeanUtil.copyProperties(page.getParams(),shopBrand);
        qw.orderByAsc(ShopBrand.COL_SORT);
        qw.like(StringUtils.isNotBlank(shopBrand.getName()),ShopBrand.COL_NAME,shopBrand.getName());
        this.shopBrandMapper.selectPage(pages,qw);
        page.setList(pages.getRecords());
        page.setTotalCount(Math.toIntExact(pages.getTotal()));
        return page;
    }

    @Override
    public List<ShopBrand> getByCategoryId(Long categoryId) {
        // 根据分类ID查询关联关系
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopBrandCategory.COL_CATEGORY_ID,categoryId);
        List<ShopBrandCategory> brandCategoryList = this.shopBrandCategoryMapper.selectList(qw);
        if(CollectionUtils.isEmpty(brandCategoryList)) {
            return new ArrayList<>(0);
        }
        // 取出所有品牌ID
        List<Long> brandIds = brandCategoryList.stream().map(ShopBrandCategory::getBrandId).collect(Collectors.toList());
        // 根据品牌ID集合查询
        return this.shopBrandMapper.selectBatchIds(brandIds);
    }

    @Override
    public List<ShopBrand> getByName(String name) {
        QueryWrapper qw=new QueryWrapper();
        qw.like(ShopBrand.COL_NAME,name);
        List<ShopBrand> shopBrands = this.shopBrandMapper.selectList(qw);
        return shopBrands;
    }
}
