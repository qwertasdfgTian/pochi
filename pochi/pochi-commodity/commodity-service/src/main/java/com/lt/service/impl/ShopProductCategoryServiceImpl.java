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
        // 如果父级ID为空，就赋值为0
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
        // 如果父级ID为空，就赋值为0
        if (shopProductCategory.getParentId() == null) {
            shopProductCategory.setParentId(CoreConstant.DEFAULT_PARENT_ID);
        }
        // 如果没改名称，也没有改父级菜单的情况下，当前查出来的就是自己
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
        // 查询所有
        List<ShopProductCategory> list = this.shopProductCategoryMapper.selectList(new QueryWrapper<>());
        // 找到父节点
        return buildTree(list);
    }

    @Override
    public List<ShopProductCategoryVo> getSelectTree() {
        // 查询出所有非三级分类
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
        // 查询所有二级分类
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopProductCategory.COL_LEVEL,2);
        List<ShopProductCategory> secondList = this.shopProductCategoryMapper.selectList(qw);
        // 查询所有一级分类
        QueryWrapper qw1=new QueryWrapper();
        qw.eq(ShopProductCategory.COL_LEVEL,1);
        List<ShopProductCategory> topList = this.shopProductCategoryMapper.selectList(qw1);
        // 遍历一级分类和二级分类
        secondList.forEach(s -> {
            // 遍历一级分类，取出id和s.parentId相同的数据
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
        // 获取父级ID
        ShopProductCategory category = this.shopProductCategoryMapper.selectById(categoryId);
        Long parentId = category.getParentId();
        // 根据父级ID查询分类
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopProductCategory.COL_PARENT_ID,parentId);
        List<ShopProductCategory> categoryList = this.shopProductCategoryMapper.selectList(qw);
        // 查询品牌
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
     * 构造树形结构
     *
     * @param list
     * @return
     */
    private List<ShopProductCategoryVo> buildTree(List<ShopProductCategory> list) {
        return list.stream().filter(e -> e.getParentId().equals(CoreConstant.DEFAULT_PARENT_ID))
                .map(e -> {
                    // 构造成VO
                    ShopProductCategoryVo vo = new ShopProductCategoryVo();
                    BeanUtils.copyProperties(e, vo);
                    return vo;
                }).map(e -> {
                    // 构造子节点
                    e.setChildren(getChildren(e, list));
                    // 如果children是空，就设为null
                    if(CollectionUtils.isEmpty(e.getChildren())) {
                        e.setChildren(null);
                    }
                    return e;
                }).collect(Collectors.toList());
    }

    /**
     * 寻找子节点
     *
     * @param category
     * @param list
     * @return
     */
    private List<ShopProductCategoryVo> getChildren(ShopProductCategoryVo category, List<ShopProductCategory> list) {
        // 找到category所有的子节点
        return list.stream().filter(e -> e.getParentId().equals(category.getId()))
                .map(e -> {
                    // 转成VO
                    ShopProductCategoryVo vo = new ShopProductCategoryVo();
                    BeanUtils.copyProperties(e, vo);
                    return vo;
                }).map(e -> {
                    e.setChildren(getChildren(e, list));
                    // 如果children是空，就设为null
                    if(CollectionUtils.isEmpty(e.getChildren())) {
                        e.setChildren(null);
                    }
                    return e;
                }).collect(Collectors.toList());
    }
}
