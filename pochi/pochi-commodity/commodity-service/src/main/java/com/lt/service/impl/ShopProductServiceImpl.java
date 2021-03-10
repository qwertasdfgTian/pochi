package com.lt.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lt.*;
import com.lt.constant.CoreConstant;
import com.lt.dto.ShopProductDto;
import com.lt.dto.ShopProductSearchDto;
import com.lt.enums.ProductSortEnum;
import com.lt.enums.SortTypeEnum;
import com.lt.enums.StateEnums;

import java.util.*;
import java.util.stream.Collectors;

import com.lt.es.ShopProductEs;
import com.lt.mapper.*;
import com.lt.service.ShopProductService;
import com.lt.utils.IdWorker;
import com.lt.vo.LoginUser;
import com.lt.vo.Page;
import com.lt.vo.ShopProductVo;
import com.lt.pojo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Method;
import org.apache.dubbo.config.annotation.Service;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.util.CollectionUtils;

@Service(methods = {@Method(name = "save", retries = 0)})
public class ShopProductServiceImpl implements ShopProductService {

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private ShopProductMapper shopProductMapper;
    @Autowired
    private ShopBrandMapper shopBrandMapper;
    @Autowired
    private ShopProductCategoryMapper shopProductCategoryMapper;
    @Autowired
    private ShopProductPackMapper shopProductPackMapper;
    @Autowired
    private ShopPackMapper shopPackMapper;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public void save(ShopProductDto shopProductDto,LoginUser loginUser) {
        // 转换成ShopProduct
        ShopProduct shopProduct = new ShopProduct();
        BeanUtils.copyProperties(shopProductDto, shopProduct);
        shopProduct.setId(idWorker.nextId());
        // 处理轮播图
        if (!CollectionUtils.isEmpty(shopProductDto.getAlbumPicList())) {
            // 转成一个字符串，使用 , 拼接
            shopProduct.setAlbumPics(StringUtils.join(shopProductDto.getAlbumPicList(), ","));
        } else {
            // 用商品图片代替
            shopProduct.setAlbumPics(shopProduct.getPic());
        }
        //品牌名称
        ShopBrand brand = this.shopBrandMapper.selectById(shopProduct.getBrandId());
        shopProduct.setBrandName(brand.getName());
        // 副标题如果为空，就设置为标题
        if (StringUtils.isBlank(shopProduct.getSubTitle())) {
            shopProduct.setSubTitle(shopProduct.getName());
        }
        // 创建人、修改人
        shopProduct.setCreateBy(loginUser.getUsername());
        shopProduct.setUpdateBy(loginUser.getUsername());
        this.shopProductMapper.insert(shopProduct);
        // 处理套装
        handleProductPack(shopProductDto, shopProduct);
    }

    @Override
    public Page<ShopProductVo> getByPage(Page<ShopProductVo> page) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ShopProduct> pages=new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page.getPageNumber(),page.getPageSize());
        QueryWrapper<ShopProduct> qw=new QueryWrapper<>();
        ShopProduct shopProduct=new ShopProduct();
        BeanUtil.copyProperties(page.getParams(),shopProduct);
        qw.orderByAsc(ShopProduct.COL_SORT);
        qw.eq(ShopProduct.COL_DELETED, StateEnums.NOT_DELETED.getCode());
        qw.like(StringUtils.isNotBlank(shopProduct.getName()),ShopProduct.COL_NAME,shopProduct.getName());
        qw.like(StringUtils.isNotBlank(shopProduct.getCreateBy()),ShopProduct.COL_CREATE_BY,shopProduct.getCreateBy());
        qw.eq(null!=shopProduct.getPublishStatus(),ShopProduct.COL_PUBLISH_STATUS,shopProduct.getPublishStatus());
        qw.eq(null!=shopProduct.getNewStatus(),ShopProduct.COL_NEW_STATUS,shopProduct.getNewStatus());
        qw.eq(null!=shopProduct.getRecommendStatus(),ShopProduct.COL_RECOMMEND_STATUS,shopProduct.getRecommendStatus());
        qw.eq(null!=shopProduct.getBrandId(),ShopProduct.COL_BRAND_ID,shopProduct.getBrandId());
        this.shopProductMapper.selectPage(pages,qw);

        // 查询list和总条数
        List<ShopProduct> list = pages.getRecords();
        Integer totalCount = Math.toIntExact(pages.getTotal());
        // ShopProduct的list转成vo的list
        List<ShopProductVo> productVoList = list.stream().map(e -> {
            ShopProductVo vo = new ShopProductVo();
            BeanUtils.copyProperties(e, vo);
            // 处理一下轮播图
            String albumPics = e.getAlbumPics();
            if (StringUtils.isNotBlank(albumPics)) {
                // 转成集合
                vo.setAlbumPicList(Arrays.asList(albumPics.split(",")));
            }
            return vo;
        }).collect(Collectors.toList());
        // 处理分类名称(处理什么都没查到的情况)
        List<Long> categoryIds = productVoList.stream().map(ShopProductVo::getCategoryId).collect(Collectors.toList());
        if(categoryIds.size()!=0){
            List<ShopProductCategory> categoryList = this.shopProductCategoryMapper.selectBatchIds(categoryIds);
            // 封装进去
            productVoList.forEach(e -> {
                ShopProductCategory category = categoryList.stream().filter(c -> c.getId().equals(e.getCategoryId())).findFirst().orElse(new ShopProductCategory());
                e.setCategory(category);
            });
        }
        page.setList(productVoList);
        page.setTotalCount(totalCount);
        return page;
    }

    @Override
    public ShopProductVo getUpdate(Long id) {
        // 根据id查询
        ShopProduct product = this.shopProductMapper.selectById(id);
        // 转换成VO
        ShopProductVo shopProductVo = new ShopProductVo();
        BeanUtils.copyProperties(product, shopProductVo);
        // 处理轮播图
        if (StringUtils.isNotBlank(product.getAlbumPics())) {
            shopProductVo.setAlbumPicList(Arrays.asList(product.getAlbumPics().split(",")));
        }
        // 处理categoryIds
        // 拿到子节点id
        Long categoryId = product.getCategoryId();
        Deque<Long> deque = new ArrayDeque<>(4);
        while (categoryId != null && !categoryId.equals(CoreConstant.DEFAULT_PARENT_ID)) {
            // 根据子节点ID查询分类
            ShopProductCategory category = this.shopProductCategoryMapper.selectById(categoryId);
            if (category != null) {
                // 入列
                deque.push(category.getId());
                categoryId = category.getParentId();
            } else {
                break;
            }
        }
        // 封装到vo里
        shopProductVo.setCategoryIds(new ArrayList<>(deque));
        // 根据商品查询该商品所在的关联
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopProductPack.COL_PRODUCT_ID,shopProductVo.getId());
        ShopProductPack productPack = this.shopProductPackMapper.selectOne(qw);
        if (productPack != null) {
            // 根据取出来的套装编号查询套装
            ShopPack shopPack = this.shopPackMapper.selectById(productPack.getPackCode());
            shopProductVo.setShopPack(shopPack);
            // 根据套装编号查询商品关联
            QueryWrapper qw1=new QueryWrapper();
            qw1.eq(ShopProductPack.COL_PACK_CODE,productPack.getPackCode());
            List<ShopProductPack> productPackList = this.shopProductPackMapper.selectList(qw1);
            // 获取商品iD集合
            if (!CollectionUtils.isEmpty(productPackList)) {
                List<Long> productIds = productPackList.stream().map(ShopProductPack::getProductId).collect(Collectors.toList());
                // 根据商品ID集合查询商品
                QueryWrapper qw2=new QueryWrapper();
                qw2.eq(ShopProduct.COL_DELETED,StateEnums.NOT_DELETED.getCode());
                qw2.in(ShopProduct.COL_ID,productIds);
                List<ShopProduct> productList = this.shopProductMapper.selectList(qw2);
                shopProductVo.setProductList(productList);
            }
        }
        return shopProductVo;
    }

    @Override
    public void update(ShopProductDto shopProductDto,LoginUser loginUser) {
        ShopProduct shopProduct = new ShopProduct();
        BeanUtils.copyProperties(shopProductDto, shopProduct);
        // 处理轮播图
        List<String> albumPicList = shopProductDto.getAlbumPicList();
        if (CollectionUtils.isEmpty(albumPicList)) {
            shopProduct.setAlbumPics(shopProduct.getPic());
        } else {
            shopProduct.setAlbumPics(StringUtils.join(albumPicList, ","));
        }
        ShopBrand brand = this.shopBrandMapper.selectById(shopProduct.getBrandId());
        shopProduct.setBrandName(brand.getName());
        // 副标题如果为空，就设置为标题
        if (StringUtils.isBlank(shopProduct.getSubTitle())) {
            shopProduct.setSubTitle(shopProduct.getName());
        }
        // 创建人、修改人
        shopProduct.setUpdateBy(loginUser.getUsername());
        this.shopProductMapper.updateById(shopProduct);
        // 处理商品套装
        handleUpdateProductPack(shopProductDto, shopProduct);
    }

    @Override
    public void delete(Long id) {
        ShopProduct shopProduct=new ShopProduct();
        shopProduct.setId(id);
        shopProduct.setDeleted(StateEnums.DELETED.getCode());
        this.shopProductMapper.updateById(shopProduct);
    }

    @Override
    public void publish(Long id) {
        ShopProduct product = shopProductMapper.selectById(id);
        product.setPublishStatus(StateEnums.ENABLED.getCode());
        this.shopProductMapper.updateById(product);
    }

    @Override
    public void unPublish(Long id) {
        ShopProduct product = shopProductMapper.selectById(id);
        product.setPublishStatus(StateEnums.NOT_ENABLE.getCode());
        this.shopProductMapper.updateById(product);
    }

    @Override
    public void news(Long id) {
        ShopProduct product = shopProductMapper.selectById(id);
        product.setNewStatus(StateEnums.ENABLED.getCode());
        this.shopProductMapper.updateById(product);
    }

    @Override
    public void old(Long id) {
        ShopProduct product = shopProductMapper.selectById(id);
        product.setNewStatus(StateEnums.NOT_ENABLE.getCode());
        this.shopProductMapper.updateById(product);
    }

    @Override
    public void recommend(Long id) {
        ShopProduct product = shopProductMapper.selectById(id);
        product.setRecommendStatus(StateEnums.ENABLED.getCode());
        this.shopProductMapper.updateById(product);
    }

    @Override
    public void unRecommend(Long id) {
        ShopProduct product = shopProductMapper.selectById(id);
        product.setRecommendStatus(StateEnums.NOT_ENABLE.getCode());
        this.shopProductMapper.updateById(product);
    }

    @Override
    public List<ShopProduct> getProductDetailList(Long packCode) {
        QueryWrapper qw1=new QueryWrapper();
        qw1.eq(ShopProductPack.COL_PACK_CODE,packCode);
        List<ShopProductPack> packList = this.shopProductPackMapper.selectList(qw1);
        if (CollectionUtils.isEmpty(packList)) {
            return new ArrayList<>(0);
        }
        // 获取商品id
        List<Long> productIds = packList.stream().map(ShopProductPack::getProductId).collect(Collectors.toList());
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopProduct.COL_DELETED,StateEnums.NOT_DELETED.getCode());
        qw.in(ShopProduct.COL_ID,productIds);
        return this.shopProductMapper.selectList(qw);
    }

    @Override
    public Page<ShopProductVo> getByPageHasNotPack(Page<ShopProductVo> page) {
        // 查询list和总条数
        List<ShopProduct> list = this.shopProductMapper.getByPageHasNotPack(page);
        Integer totalCount = this.shopProductMapper.countByPageHasNotPack(page);
        // ShopProduct的list转成vo的list
        List<ShopProductVo> productVoList = list.stream().map(e -> {
            ShopProductVo vo = new ShopProductVo();
            BeanUtils.copyProperties(e, vo);
            return vo;
        }).collect(Collectors.toList());
        // 处理分类名称
        List<Long> categoryIds = productVoList.stream().map(ShopProductVo::getCategoryId).collect(Collectors.toList());
        List<ShopProductCategory> categoryList = shopProductCategoryMapper.selectBatchIds(categoryIds);
        // 封装进去
        productVoList.forEach(e -> {
            ShopProductCategory category = categoryList.stream().filter(c -> c.getId().equals(e.getCategoryId())).findFirst().orElse(new ShopProductCategory());
            e.setCategory(category);
        });
        page.setList(productVoList);
        page.setTotalCount(totalCount);
        return page;
    }

    @Override
    public List<ShopProduct> getNewProduct() {
        return this.shopProductMapper.getNewProduct();
    }

    @Override
    public List<ShopProduct> getRecommendList() {
        return this.shopProductMapper.getRecommendList();
    }

    @Override
    public ShopProductVo get(Long id) {
        // 根据id查询
        ShopProduct product = this.shopProductMapper.selectById(id);
        // 转换成VO
        ShopProductVo shopProductVo = new ShopProductVo();
        BeanUtils.copyProperties(product, shopProductVo);
        // 处理轮播图
        if (StringUtils.isNotBlank(product.getAlbumPics())) {
            shopProductVo.setAlbumPicList(Arrays.asList(product.getAlbumPics().split(",")));
        }
        return shopProductVo;
    }

    @Override
    public List<ShopProduct> getRankByProduct(Long productId) {
        // 查询商品
        ShopProduct product = this.shopProductMapper.selectById(productId);
        Long categoryId = product.getCategoryId();
        return this.shopProductMapper.getRankByCategory(categoryId);
    }

    @Override
    public Page<ShopProductEs> search(ShopProductSearchDto shopProductDto) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 构造查询条件,如果查询的关键字不为空，根绝三个ik分词的内容查询
        if (StringUtils.isNotBlank(shopProductDto.getKeyword())) {
            boolQuery.must(QueryBuilders.multiMatchQuery(shopProductDto.getKeyword(),
                    "name", "product_content", "product_comment"));
        }
        if (shopProductDto.getCategoryId() != null) {
            boolQuery.must(QueryBuilders.termQuery("category_id", shopProductDto.getCategoryId()));
        }
        if (shopProductDto.getBrandId() != null) {
            boolQuery.must(QueryBuilders.termQuery("brand_id", shopProductDto.getBrandId()));
        }
        if (shopProductDto.getPriceStart() != null) {
            boolQuery.must(QueryBuilders.rangeQuery("price").gte(shopProductDto.getPriceStart()));
        }
        if (shopProductDto.getPriceEnd() != null) {
            boolQuery.must(QueryBuilders.rangeQuery("price").lte(shopProductDto.getPriceEnd()));
        }
        queryBuilder.withQuery(boolQuery);
        // 构造分页
        queryBuilder.withPageable(PageRequest.of(shopProductDto.getPageNumber() - 1, shopProductDto.getPageSize()));
        // 构造排序
        String sortColumn = ProductSortEnum.getSort(shopProductDto.getSorted());
        if (StringUtils.isNotBlank(sortColumn)) {
            SortOrder order = SortOrder.DESC;
            if (SortTypeEnum.ASC.getType().equals(shopProductDto.getSortType())) {
                order = SortOrder.ASC;
            }
            queryBuilder.withSort(SortBuilders.fieldSort(sortColumn).order(order));
        }
        SearchHits<ShopProductEs> hits = this.elasticsearchRestTemplate.search(queryBuilder.build(), ShopProductEs.class);
        // 拿到查询内容
        List<ShopProductEs> list = hits.get().map(SearchHit::getContent).collect(Collectors.toList());
        long totalCount = hits.getTotalHits();
        Page<ShopProductEs> page = new Page<>();
        page.setPageNumber(shopProductDto.getPageNumber());
        page.setPageSize(shopProductDto.getPageSize());
        page.setList(list);
        page.setTotalCount((int) totalCount);
        return page;
    }

    @Override
    public List<ShopProduct> getByIds(List<Long> ids) {
        return this.shopProductMapper.selectBatchIds(ids);
    }

    /**
     * 处理添加商品时的套装逻辑
     *
     * @param shopProductDto 待添加商品的参数
     * @param shopProduct    当前商品
     */
    private void handleProductPack(ShopProductDto shopProductDto, ShopProduct shopProduct) {
        ShopPack shopPack = shopProductDto.getShopPack();
        if (shopPack.getId() != null) {
            Long packCode = shopPack.getId();
            //  * 用户选择了关联套装
            // 保存商品套装关联表
            List<ShopProduct> products = new ArrayList<>();
            // 将该商品加入到products
            products.add(shopProduct);
            // 构造ShopProductPack
            handleProductPack(packCode, products);
        }
    }

    /**
     * 处理商品的套装逻辑
     *
     * @param packCode
     * @param products
     */
    private void handleProductPack(Long packCode, List<ShopProduct> products) {
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
        for (ShopProductPack shopProductPack : packList) {
            this.shopProductPackMapper.insert(shopProductPack);
        }
        // 更新套装的商品数量
        ShopPack newPack = this.shopPackMapper.selectById(packCode);
        newPack.setProductCount(newPack.getProductCount() + 1);
        this.shopPackMapper.updateById(newPack);
    }


    /**
     * 处理修改时的商品套装逻辑
     *
     * @param shopProductDto 前端传参的商品对象
     * @param shopProduct    当前商品
     */
    private void handleUpdateProductPack(ShopProductDto shopProductDto, ShopProduct shopProduct) {
        ShopPack shopPack = shopProductDto.getShopPack();
        if (shopPack.getId() != null) {
            //  * 用户选择了关联套装
            List<ShopProduct> products = new ArrayList<>();
            // 将该商品加入到products
            products.add(shopProduct);
            handleUpdateProductPack(shopPack, products);
        }
    }

    /**
     * 处理修改商品时的套装逻辑
     *
     * @param shopPack
     * @param products       商品列表，包含当前商品
     */
    private void handleUpdateProductPack(ShopPack shopPack, List<ShopProduct> products) {
        // 这里应该查询出products所有商品所在的关联关系，并去更新商品套装
        List<Long> productIds = products.stream().map(ShopProduct::getId).collect(Collectors.toList());
        QueryWrapper qw=new QueryWrapper();
        qw.in(ShopProductPack.COL_PRODUCT_ID,productIds);
        List<ShopProductPack> shopProductPackList = this.shopProductPackMapper.selectList(qw);
        if (!CollectionUtils.isEmpty(shopProductPackList)) {
            shopProductPackList.forEach(e -> {
                // 减少原套装的商品数
                Long oldPackCode = e.getPackCode();
                ShopPack oldPack = this.shopPackMapper.selectById(oldPackCode);
                oldPack.setProductCount(oldPack.getProductCount() - 1);
                // 如果商品数等于0，就删除该套装，否则修改
                if (oldPack.getProductCount() == 0) {
                    this.shopPackMapper.deleteById(oldPackCode);
                } else {
                    this.shopPackMapper.updateById(oldPack);
                }
            });
        }
        // 删除旧的关联关系
        QueryWrapper qw1=new QueryWrapper();
        qw1.in(ShopProductPack.COL_PRODUCT_ID,productIds);
        this.shopProductPackMapper.delete(qw1);
        handleProductPack(shopPack.getId(), products);
    }
}
