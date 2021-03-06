package com.lt.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lt.constant.CoreConstant;
import com.lt.dto.ShopProductSearchDto;
import com.lt.enums.ProductSortEnum;
import com.lt.enums.ResultEnums;
import com.lt.enums.SortTypeEnum;
import com.lt.es.ShopProductEs;
import com.lt.exception.PochiException;
import com.lt.mapper.ShopBrandCategoryMapper;
import com.lt.mapper.ShopBrandMapper;
import com.lt.pojo.ShopBrand;
import com.lt.pojo.ShopBrandCategory;
import com.lt.vo.CategoryBrandVo;
import com.lt.vo.Page;
import com.lt.vo.ShopProductCategoryVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Method;
import org.apache.dubbo.config.annotation.Service;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.lt.mapper.ShopProductCategoryMapper;
import com.lt.pojo.ShopProductCategory;
import com.lt.service.ShopProductCategoryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service(methods = {@Method(name = "save", retries = 0)})
public class ShopProductCategoryServiceImpl implements ShopProductCategoryService{

    @Autowired
    private ShopProductCategoryMapper shopProductCategoryMapper;
    @Autowired
    private ShopBrandCategoryMapper shopBrandCategoryMapper;
    @Autowired
    private ShopBrandMapper shopBrandMapper;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(ShopProductCategory shopProductCategory) {
        // ????????????ID?????????????????????0
        if (shopProductCategory.getParentId() == null) {
            shopProductCategory.setParentId(CoreConstant.DEFAULT_PARENT_ID);
        }
        QueryWrapper qw =new QueryWrapper();
        qw.eq(null!=shopProductCategory.getParentId(),shopProductCategory.COL_PARENT_ID,shopProductCategory.getParentId());
        qw.eq(StringUtils.isNotBlank(shopProductCategory.getName()),shopProductCategory.COL_NAME,shopProductCategory.getName());
        ShopProductCategory category = this.shopProductCategoryMapper.selectOne(qw);
        if (category != null) {
            throw new PochiException(ResultEnums.CATEGORY_EXISTS);
        }
        this.shopProductCategoryMapper.insert(shopProductCategory);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ShopProductCategory shopProductCategory) {
        // ????????????ID?????????????????????0
        if (shopProductCategory.getParentId() == null) {
            shopProductCategory.setParentId(CoreConstant.DEFAULT_PARENT_ID);
        }
        // ??????????????????????????????????????????????????????????????????????????????????????????
        QueryWrapper qw =new QueryWrapper();
        qw.eq(null!=shopProductCategory.getParentId(),shopProductCategory.COL_PARENT_ID,shopProductCategory.getParentId());
        qw.eq(StringUtils.isNotBlank(shopProductCategory.getName()),shopProductCategory.COL_NAME,shopProductCategory.getName());
        ShopProductCategory category = this.shopProductCategoryMapper.selectOne(qw);
        if (category != null && !category.getId().equals(shopProductCategory.getId())) {
            throw new PochiException(ResultEnums.CATEGORY_EXISTS);
        }
        this.shopProductCategoryMapper.updateById(shopProductCategory);
    }

    @Override
    public ShopProductCategory get(Long id) {
        return this.shopProductCategoryMapper.selectById(id);
    }

    @Override
    public void delete(Long id) {
        this.shopProductCategoryMapper.deleteById(id);
    }

    @Override
    public Page<ShopProductCategory> getByPage(Page<ShopProductCategory> page) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ShopProductCategory> pages=new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page.getPageNumber(),page.getPageSize());
        QueryWrapper<ShopProductCategory> qw=new QueryWrapper<>();
        ShopProductCategory shopProductCategory=new ShopProductCategory();
        BeanUtil.copyProperties(page.getParams(),shopProductCategory);
        qw.orderByAsc(ShopProductCategory.COL_SORT);
        this.shopProductCategoryMapper.selectPage(pages,qw);
        page.setList(pages.getRecords());
        page.setTotalCount(Math.toIntExact(pages.getTotal()));
        return page;
    }

    @Override
    public List<ShopProductCategoryVo> getTree() {
        // ????????????
        List<ShopProductCategory> list = this.shopProductCategoryMapper.selectList(new QueryWrapper<>());
        // ???????????????
        return buildTree(list);
    }

    @Override
    public List<ShopProductCategoryVo> getSelectTree() {
        // ??????????????????????????????
        QueryWrapper qw=new QueryWrapper();
        List<Integer> level=new ArrayList<>();
        level.add(1);
        level.add(2);
        qw.in(ShopProductCategory.COL_LEVEL,level);
        List<ShopProductCategory> list = this.shopProductCategoryMapper.selectList(qw);
        return buildTree(list);
    }

    @Override
    public List<ShopProductCategory> getAllSecond() {
        // ????????????????????????
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopProductCategory.COL_LEVEL,2);
        List<ShopProductCategory> secondList = this.shopProductCategoryMapper.selectList(qw);
        // ????????????????????????
        QueryWrapper qw1=new QueryWrapper();
        qw.eq(ShopProductCategory.COL_LEVEL,1);
        List<ShopProductCategory> topList = this.shopProductCategoryMapper.selectList(qw1);
        // ?????????????????????????????????
        secondList.forEach(s -> {
            // ???????????????????????????id???s.parentId???????????????
            ShopProductCategory parent = topList.stream().filter(t -> t.getId().equals(s.getParentId())).findFirst().orElse(null);
            if (parent != null) {
                s.setName(parent.getName() + CoreConstant.CONCAT_NAME + s.getName());
            }
        });
        return secondList;
    }

    @Override
    public List<ShopProductCategory> getNavList() {
        return this.shopProductCategoryMapper.getNavList();
    }

    @Override
    public CategoryBrandVo getCategoryAndBrandById(Long categoryId) {
        // ????????????ID
        ShopProductCategory category = this.shopProductCategoryMapper.selectById(categoryId);
        Long parentId = category.getParentId();
        // ????????????ID????????????
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopProductCategory.COL_PARENT_ID,parentId);
        List<ShopProductCategory> categoryList = this.shopProductCategoryMapper.selectList(qw);
        // ????????????
        QueryWrapper qw1=new QueryWrapper();
        qw1.eq(ShopBrandCategory.COL_CATEGORY_ID,parentId);
        List<ShopBrandCategory> brandCategoryList = this.shopBrandCategoryMapper.selectList(qw1);
        if (!CollectionUtils.isEmpty(brandCategoryList)) {
            List<Long> brandIds = brandCategoryList.stream().map(ShopBrandCategory::getBrandId).collect(Collectors.toList());
            List<ShopBrand> brandList = this.shopBrandMapper.selectBatchIds(brandIds);
            return new CategoryBrandVo(categoryList, brandList);
        }
        return new CategoryBrandVo(categoryList, new ArrayList<>(0));
    }

    /**
     * ??????????????????
     *
     * @param list
     * @return
     */
    private List<ShopProductCategoryVo> buildTree(List<ShopProductCategory> list) {
        return list.stream().filter(e -> e.getParentId().equals(CoreConstant.DEFAULT_PARENT_ID))
                .map(e -> {
                    // ?????????VO
                    ShopProductCategoryVo vo = new ShopProductCategoryVo();
                    BeanUtils.copyProperties(e, vo);
                    return vo;
                }).map(e -> {
                    // ???????????????
                    e.setChildren(getChildren(e, list));
                    // ??????children??????????????????null
                    if(CollectionUtils.isEmpty(e.getChildren())) {
                        e.setChildren(null);
                    }
                    return e;
                }).collect(Collectors.toList());
    }

    /**
     * ???????????????
     *
     * @param category
     * @param list
     * @return
     */
    private List<ShopProductCategoryVo> getChildren(ShopProductCategoryVo category, List<ShopProductCategory> list) {
        // ??????category??????????????????
        return list.stream().filter(e -> e.getParentId().equals(category.getId()))
                .map(e -> {
                    // ??????VO
                    ShopProductCategoryVo vo = new ShopProductCategoryVo();
                    BeanUtils.copyProperties(e, vo);
                    return vo;
                }).map(e -> {
                    e.setChildren(getChildren(e, list));
                    // ??????children??????????????????null
                    if(CollectionUtils.isEmpty(e.getChildren())) {
                        e.setChildren(null);
                    }
                    return e;
                }).collect(Collectors.toList());
    }
}
