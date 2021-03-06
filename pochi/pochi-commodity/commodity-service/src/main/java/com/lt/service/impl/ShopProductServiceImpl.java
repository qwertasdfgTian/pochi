package com.lt.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lt.*;
import com.lt.constant.CoreConstant;
import com.lt.dto.OrderProductDto;
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
        // ?????????ShopProduct
        ShopProduct shopProduct = new ShopProduct();
        BeanUtils.copyProperties(shopProductDto, shopProduct);
        shopProduct.setId(idWorker.nextId());
        // ???????????????
        if (!CollectionUtils.isEmpty(shopProductDto.getAlbumPicList())) {
            // ?????????????????????????????? , ??????
            shopProduct.setAlbumPics(StringUtils.join(shopProductDto.getAlbumPicList(), ","));
        } else {
            // ?????????????????????
            shopProduct.setAlbumPics(shopProduct.getPic());
        }
        //????????????
        ShopBrand brand = this.shopBrandMapper.selectById(shopProduct.getBrandId());
        shopProduct.setBrandName(brand.getName());
        // ??????????????????????????????????????????
        if (StringUtils.isBlank(shopProduct.getSubTitle())) {
            shopProduct.setSubTitle(shopProduct.getName());
        }
        // ?????????????????????
        shopProduct.setCreateBy(loginUser.getUsername());
        shopProduct.setUpdateBy(loginUser.getUsername());
        this.shopProductMapper.insert(shopProduct);
        // ????????????
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

        // ??????list????????????
        List<ShopProduct> list = pages.getRecords();
        Integer totalCount = Math.toIntExact(pages.getTotal());
        // ShopProduct???list??????vo???list
        List<ShopProductVo> productVoList = list.stream().map(e -> {
            ShopProductVo vo = new ShopProductVo();
            BeanUtils.copyProperties(e, vo);
            // ?????????????????????
            String albumPics = e.getAlbumPics();
            if (StringUtils.isNotBlank(albumPics)) {
                // ????????????
                vo.setAlbumPicList(Arrays.asList(albumPics.split(",")));
            }
            return vo;
        }).collect(Collectors.toList());
        // ??????????????????(?????????????????????????????????)
        List<Long> categoryIds = productVoList.stream().map(ShopProductVo::getCategoryId).collect(Collectors.toList());
        if(categoryIds.size()!=0){
            List<ShopProductCategory> categoryList = this.shopProductCategoryMapper.selectBatchIds(categoryIds);
            // ????????????
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
        // ??????id??????
        ShopProduct product = this.shopProductMapper.selectById(id);
        // ?????????VO
        ShopProductVo shopProductVo = new ShopProductVo();
        BeanUtils.copyProperties(product, shopProductVo);
        // ???????????????
        if (StringUtils.isNotBlank(product.getAlbumPics())) {
            shopProductVo.setAlbumPicList(Arrays.asList(product.getAlbumPics().split(",")));
        }
        // ??????categoryIds
        // ???????????????id
        Long categoryId = product.getCategoryId();
        Deque<Long> deque = new ArrayDeque<>(4);
        while (categoryId != null && !categoryId.equals(CoreConstant.DEFAULT_PARENT_ID)) {
            // ???????????????ID????????????
            ShopProductCategory category = this.shopProductCategoryMapper.selectById(categoryId);
            if (category != null) {
                // ??????
                deque.push(category.getId());
                categoryId = category.getParentId();
            } else {
                break;
            }
        }
        // ?????????vo???
        shopProductVo.setCategoryIds(new ArrayList<>(deque));
        // ??????????????????????????????????????????
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopProductPack.COL_PRODUCT_ID,shopProductVo.getId());
        ShopProductPack productPack = this.shopProductPackMapper.selectOne(qw);
        if (productPack != null) {
            // ??????????????????????????????????????????
            ShopPack shopPack = this.shopPackMapper.selectById(productPack.getPackCode());
            shopProductVo.setShopPack(shopPack);
            // ????????????????????????????????????
            QueryWrapper qw1=new QueryWrapper();
            qw1.eq(ShopProductPack.COL_PACK_CODE,productPack.getPackCode());
            List<ShopProductPack> productPackList = this.shopProductPackMapper.selectList(qw1);
            // ????????????iD??????
            if (!CollectionUtils.isEmpty(productPackList)) {
                List<Long> productIds = productPackList.stream().map(ShopProductPack::getProductId).collect(Collectors.toList());
                // ????????????ID??????????????????
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
        // ???????????????
        List<String> albumPicList = shopProductDto.getAlbumPicList();
        if (CollectionUtils.isEmpty(albumPicList)) {
            shopProduct.setAlbumPics(shopProduct.getPic());
        } else {
            shopProduct.setAlbumPics(StringUtils.join(albumPicList, ","));
        }
        ShopBrand brand = this.shopBrandMapper.selectById(shopProduct.getBrandId());
        shopProduct.setBrandName(brand.getName());
        // ??????????????????????????????????????????
        if (StringUtils.isBlank(shopProduct.getSubTitle())) {
            shopProduct.setSubTitle(shopProduct.getName());
        }
        // ?????????????????????
        shopProduct.setUpdateBy(loginUser.getUsername());
        this.shopProductMapper.updateById(shopProduct);
        // ??????????????????
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
        // ????????????id
        List<Long> productIds = packList.stream().map(ShopProductPack::getProductId).collect(Collectors.toList());
        QueryWrapper qw=new QueryWrapper();
        qw.eq(ShopProduct.COL_DELETED,StateEnums.NOT_DELETED.getCode());
        qw.in(ShopProduct.COL_ID,productIds);
        return this.shopProductMapper.selectList(qw);
    }

    @Override
    public Page<ShopProductVo> getByPageHasNotPack(Page<ShopProductVo> page) {
        // ??????list????????????
        List<ShopProduct> list = this.shopProductMapper.getByPageHasNotPack(page);
        Integer totalCount = this.shopProductMapper.countByPageHasNotPack(page);
        // ShopProduct???list??????vo???list
        List<ShopProductVo> productVoList = list.stream().map(e -> {
            ShopProductVo vo = new ShopProductVo();
            BeanUtils.copyProperties(e, vo);
            return vo;
        }).collect(Collectors.toList());
        // ??????????????????
        List<Long> categoryIds = productVoList.stream().map(ShopProductVo::getCategoryId).collect(Collectors.toList());
        List<ShopProductCategory> categoryList = shopProductCategoryMapper.selectBatchIds(categoryIds);
        // ????????????
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
        // ??????id??????
        ShopProduct product = this.shopProductMapper.selectById(id);
        // ?????????VO
        ShopProductVo shopProductVo = new ShopProductVo();
        BeanUtils.copyProperties(product, shopProductVo);
        // ???????????????
        if (StringUtils.isNotBlank(product.getAlbumPics())) {
            shopProductVo.setAlbumPicList(Arrays.asList(product.getAlbumPics().split(",")));
        }
        return shopProductVo;
    }

    @Override
    public List<ShopProduct> getRankByProduct(Long productId) {
        // ????????????
        ShopProduct product = this.shopProductMapper.selectById(productId);
        Long categoryId = product.getCategoryId();
        return this.shopProductMapper.getRankByCategory(categoryId);
    }

    @Override
    public Page<ShopProductEs> search(ShopProductSearchDto shopProductDto) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // ??????????????????,????????????????????????????????????????????????ik?????????????????????
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
        // ????????????
        queryBuilder.withPageable(PageRequest.of(shopProductDto.getPageNumber() - 1, shopProductDto.getPageSize()));
        // ????????????
        String sortColumn = ProductSortEnum.getSort(shopProductDto.getSorted());
        if (StringUtils.isNotBlank(sortColumn)) {
            SortOrder order = SortOrder.DESC;
            if (SortTypeEnum.ASC.getType().equals(shopProductDto.getSortType())) {
                order = SortOrder.ASC;
            }
            queryBuilder.withSort(SortBuilders.fieldSort(sortColumn).order(order));
        }
        SearchHits<ShopProductEs> hits = this.elasticsearchRestTemplate.search(queryBuilder.build(), ShopProductEs.class);
        // ??????????????????
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

    @Override
    public void updateStock(OrderProductDto orderProductDto) {
        this.shopProductMapper.updateStock(orderProductDto);
    }

    /**
     * ????????????????????????????????????
     *
     * @param shopProductDto ????????????????????????
     * @param shopProduct    ????????????
     */
    private void handleProductPack(ShopProductDto shopProductDto, ShopProduct shopProduct) {
        ShopPack shopPack = shopProductDto.getShopPack();
        if (shopPack.getId() != null) {
            Long packCode = shopPack.getId();
            //  * ???????????????????????????
            // ???????????????????????????
            List<ShopProduct> products = new ArrayList<>();
            // ?????????????????????products
            products.add(shopProduct);
            // ??????ShopProductPack
            handleProductPack(packCode, products);
        }
    }

    /**
     * ???????????????????????????
     *
     * @param packCode
     * @param products
     */
    private void handleProductPack(Long packCode, List<ShopProduct> products) {
        // ??????ShopProductPack
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
        // ???????????????????????????
        ShopPack newPack = this.shopPackMapper.selectById(packCode);
        newPack.setProductCount(newPack.getProductCount() + 1);
        this.shopPackMapper.updateById(newPack);
    }


    /**
     * ????????????????????????????????????
     *
     * @param shopProductDto ???????????????????????????
     * @param shopProduct    ????????????
     */
    private void handleUpdateProductPack(ShopProductDto shopProductDto, ShopProduct shopProduct) {
        ShopPack shopPack = shopProductDto.getShopPack();
        if (shopPack.getId() != null) {
            //  * ???????????????????????????
            List<ShopProduct> products = new ArrayList<>();
            // ?????????????????????products
            products.add(shopProduct);
            handleUpdateProductPack(shopPack, products);
        }
    }

    /**
     * ????????????????????????????????????
     *
     * @param shopPack
     * @param products       ?????????????????????????????????
     */
    private void handleUpdateProductPack(ShopPack shopPack, List<ShopProduct> products) {
        // ?????????????????????products????????????????????????????????????????????????????????????
        List<Long> productIds = products.stream().map(ShopProduct::getId).collect(Collectors.toList());
        QueryWrapper qw=new QueryWrapper();
        qw.in(ShopProductPack.COL_PRODUCT_ID,productIds);
        List<ShopProductPack> shopProductPackList = this.shopProductPackMapper.selectList(qw);
        if (!CollectionUtils.isEmpty(shopProductPackList)) {
            shopProductPackList.forEach(e -> {
                // ???????????????????????????
                Long oldPackCode = e.getPackCode();
                ShopPack oldPack = this.shopPackMapper.selectById(oldPackCode);
                oldPack.setProductCount(oldPack.getProductCount() - 1);
                // ?????????????????????0????????????????????????????????????
                if (oldPack.getProductCount() == 0) {
                    this.shopPackMapper.deleteById(oldPackCode);
                } else {
                    this.shopPackMapper.updateById(oldPack);
                }
            });
        }
        // ????????????????????????
        QueryWrapper qw1=new QueryWrapper();
        qw1.in(ShopProductPack.COL_PRODUCT_ID,productIds);
        this.shopProductPackMapper.delete(qw1);
        handleProductPack(shopPack.getId(), products);
    }
}
