package com.lt.service.impl;

import com.lt.mapper.ShopProductMapper;
import com.lt.mapper.ShopProductStatisticMapper;
import com.lt.pojo.ShopProduct;
import com.lt.pojo.ShopProductCollection;
import com.lt.pojo.ShopProductHistory;
import com.lt.pojo.ShopProductStatistic;
import com.lt.repository.ShopProductHistoryRepository;
import com.lt.service.ShopProductHistoryService;
import com.lt.utils.DateUtils;
import com.lt.utils.IdWorker;
import com.lt.vo.LoginUser;
import com.lt.vo.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Method;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service(methods = {@Method(name = "save", retries = 0)})
@Slf4j
public class ShopProductHistoryServiceImpl implements ShopProductHistoryService {

    @Autowired
    private ShopProductHistoryRepository shopProductHistoryRepository;
    @Autowired
    private ShopProductStatisticMapper shopProductStatisticMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private ShopProductMapper shopProductMapper;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(ShopProductHistory shopProductHistory,LoginUser loginUser) {
        try {
            shopProductHistory.setId(idWorker.nextId());
            ShopProduct product = this.shopProductMapper.selectById(shopProductHistory.getProductId());
            shopProductHistory.setProductBrand(product.getBrandName());
            shopProductHistory.setProductCategoryId(product.getCategoryId());
            shopProductHistory.setProductName(product.getName());
            shopProductHistory.setProductPic(product.getPic());
            shopProductHistory.setProductPrice(product.getPrice());
            shopProductHistory.setCreateBy(loginUser.getUsername());
            shopProductHistory.setCreateDay(DateUtils.newDate());
            shopProductHistory.setCreateTime(DateUtils.newDateTime());

            ShopProductHistory byProductIdAndCreateBy = this.shopProductHistoryRepository.getByProductIdAndCreateBy(shopProductHistory.getProductId(), loginUser.getUsername());
            if(byProductIdAndCreateBy==null){
                this.shopProductHistoryRepository.save(shopProductHistory);
            }else{
                shopProductHistory.setId(byProductIdAndCreateBy.getId());
                shopProductHistory.setCreateDay(DateUtils.newDate());
                this.shopProductHistoryRepository.save(shopProductHistory);
            }


            ShopProductStatistic statistic = this.shopProductStatisticMapper.getByProductId(product.getId());
            if (statistic == null) {
                ShopProductStatistic shopProductStatistic=new ShopProductStatistic();
                shopProductStatistic.setProductId(product.getId());
                this.shopProductStatisticMapper.insert(shopProductStatistic);
            } else {
                this.shopProductStatisticMapper.updateHistory(statistic.getId());
            }
        } catch (Exception e) {
            log.error("保存历史记录异常：", e);
        }
    }

    @Override
    public void clearHistory(LoginUser loginUser) {
        Query query = new Query();
        query.addCriteria(Criteria.where("create_by").is(loginUser.getUsername()));
        this.mongoTemplate.remove(query, ShopProductHistory.class);
    }

    @Override
    public Map<String, List<ShopProductHistory>> getByPage(Page<ShopProductHistory> page, LoginUser loginUser) {
        // 构造查询对象
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
        // 设置跳过多少条
        query.skip((pageNumber - 1) * pageSize);
        // 取出多少条
        query.limit(pageSize);
        // 构造排序对象
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "create_time");
        // 设置排序对象
        query.with(Sort.by(order));
        // 查询
        List<ShopProductHistory> list = this.mongoTemplate.find(query, ShopProductHistory.class);
        Map<String, List<ShopProductHistory>> dataMap = list.stream().collect(Collectors.groupingBy(ShopProductHistory::getCreateDay));
        return dataMap;
    }

    @Override
    public Integer getSumHistory(LoginUser loginUser) {
        // 构造一个查询对象
        Query query = new Query();
        // 设置参数
        String username = loginUser.getUsername();
        query.addCriteria(Criteria.where("create_by").is(username));
        long count = this.mongoTemplate.count(query, ShopProductHistory.class);
        return (int)count;
    }

    @Override
    public void deleteHistoryById(LoginUser loginUser,Long productId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("create_by").is(loginUser.getUsername()));
        query.addCriteria(Criteria.where("product_id").is(productId));
        this.mongoTemplate.remove(query, ShopProductHistory.class);
    }
}
