package com.lt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lt.vo.LoginUser;
import com.lt.vo.Page;
import com.lt.mapper.ShopProductMapper;
import com.lt.mapper.ShopProductStatisticMapper;
import com.lt.pojo.ShopProduct;
import com.lt.pojo.ShopProductCollection;
import com.lt.pojo.ShopProductStatistic;
import com.lt.vo.ShopProductCollectionVo;
import com.lt.repository.ShopProductCollectionRepository;
import com.lt.service.ShopProductCollectionService;
import com.lt.utils.DateUtils;
import com.lt.utils.IdWorker;
import com.lt.utils.ShiroUtils;
import org.apache.dubbo.config.annotation.Method;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Mr.Tian
 * @Date: 2020/12/20 14:49
 * @Version 1.0
 */
@Service(methods = {@Method(name = "changeCollection", retries = 0)})
public class ShopProductCollectionServiceImpl implements ShopProductCollectionService {

    @Autowired
    private ShopProductCollectionRepository shopProductCollectionRepository;
    @Autowired
    private ShopProductMapper shopProductMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ShopProductStatisticMapper shopProductStatisticMapper;

    @Override
    public void changeCollection(ShopProductCollection shopProductCollection, LoginUser loginUser) {
        // 先查询，如果存在，就直接删除，如果不存在，就添加
        String username = loginUser.getUsername();
        ShopProductCollection collection = this.shopProductCollectionRepository.getByProductIdAndCreateBy(shopProductCollection.getProductId(), username);
        if (collection != null) {
            this.shopProductCollectionRepository.deleteById(collection.getId());
            this.shopProductStatisticMapper.removeCollectionCount(collection.getProductId());
        } else {
            ShopProduct product = this.shopProductMapper.selectById(shopProductCollection.getProductId());
            collection = new ShopProductCollection();
            collection.setId(idWorker.nextId());
            collection.setProductId(shopProductCollection.getProductId());
            collection.setCreateBy(username);
            collection.setCreateTime(DateUtils.newDateTime());
            collection.setProductPic(product.getPic());
            collection.setProductName(product.getName());
            collection.setProductBrand(product.getBrandName());
            collection.setProductPrice(product.getPrice());
            collection.setProductCategoryId(product.getCategoryId());
            this.shopProductCollectionRepository.save(collection);
            this.shopProductStatisticMapper.addCollectionCount(collection.getProductId());
        }
    }

    @Override
    public ShopProductCollection getByProductId(Long productId,LoginUser loginUser) {
        return this.shopProductCollectionRepository.getByProductIdAndCreateBy(productId, loginUser.getUsername());
    }

    @Override
    public Page<ShopProductCollectionVo> getByPage(Page<ShopProductCollection> page,LoginUser loginUser) {
        // 构造一个查询对象
        Query query = new Query();
        // 设置参数
        String username = loginUser.getUsername();
        query.addCriteria(Criteria.where("create_by").is(username));
        // 计算分页参数
        Integer pageNumber = page.getPageNumber();
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
            page.setPageNumber(pageNumber);
        }
        Integer pageSize = page.getPageSize();
        if (pageSize == null || pageSize < 1) {
            pageSize = 20;
            page.setPageSize(pageSize);
        }
        long count = this.mongoTemplate.count(query, ShopProductCollection.class);
        // 设置跳过多少条
        query.skip((pageNumber - 1) * pageSize);
        // 取出多少条
        query.limit(pageSize);
        // 排序对象
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "create_time");
        // 设置排序对象
        query.with(Sort.by(order));
        List<ShopProductCollection> list = this.mongoTemplate.find(query, ShopProductCollection.class);
        // 处理list
        List<Long> productIds = list.stream().map(ShopProductCollection::getProductId).collect(Collectors.toList());
        List<ShopProductCollectionVo> voList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(productIds)) {
            QueryWrapper qw=new QueryWrapper();
            qw.in(ShopProductStatistic.COL_PRODUCT_ID,productIds);
            List<ShopProductStatistic> statisticList = this.shopProductStatisticMapper.selectList(qw);
            voList = list.stream().map(e -> {
                ShopProductCollectionVo vo = new ShopProductCollectionVo();
                BeanUtils.copyProperties(e, vo);
                // 匹配到该商品对应的收藏信息
                ShopProductStatistic statistic = statisticList.stream().filter(s -> s.getProductId().equals(e.getProductId())).findFirst().orElse(new ShopProductStatistic());
                vo.setCollectionCount(statistic.getCollectionCount());
                return vo;
            }).collect(Collectors.toList());
        }
        Page<ShopProductCollectionVo> resultPage = new Page<>();
        resultPage.setPageSize(page.getPageSize());
        resultPage.setPageNumber(page.getPageNumber());
        resultPage.setList(voList);
        resultPage.setTotalCount((int) count);
        return resultPage;
    }

    @Override
    public Integer getSumCollection(LoginUser loginUser) {
        // 构造一个查询对象
        Query query = new Query();
        // 设置参数
        String username = loginUser.getUsername();
        query.addCriteria(Criteria.where("create_by").is(username));
        long count = this.mongoTemplate.count(query, ShopProductCollection.class);
        return (int)count;
    }
}
