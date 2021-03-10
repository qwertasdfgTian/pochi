package com.lt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lt.mapper.ShopUserStatisticMapper;
import com.lt.pojo.*;
import com.lt.vo.LoginUser;
import com.lt.vo.Page;
import com.lt.enums.OperateEnum;
import com.lt.enums.OrderStateEnum;
import com.lt.enums.StateEnums;
import com.lt.mapper.ShopOrderHistoryMapper;
import com.lt.mapper.ShopOrderItemMapper;
import com.lt.mapper.ShopOrderMapper;
import com.lt.dto.ShopOrderCommentDto;
import com.lt.vo.SearchCommentNumVo;
import com.lt.vo.ShopOrderCommentVo;
import com.lt.repository.ShopOrderCommentRepository;
import com.lt.service.ShopOrderCommentService;
import com.lt.utils.DateUtils;
import com.lt.utils.IdWorker;
import com.lt.utils.ShiroUtils;
import com.lt.utils.StringUtils;
import org.apache.dubbo.config.annotation.Method;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: Mr.Tian
 * @Date: 2021/1/23 22:14
 * @Version 1.0
 */
@Service(methods = {@Method(name = "save", retries = 0)})
public class ShopOrderCommentServiceImpl implements ShopOrderCommentService {

    @Autowired
    private ShopOrderCommentRepository shopOrderCommentRepository;
    @Autowired
    private ShopOrderItemMapper shopOrderItemMapper;
    @Autowired
    private ShopOrderMapper shopOrderMapper;
    @Autowired
    private ShopOrderHistoryMapper shopOrderHistoryMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ShopUserStatisticMapper shopUserStatisticMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(List<ShopOrderCommentDto> list, LoginUser loginUser) {
        // 将DTO转换成pojo
        List<ShopOrderComment> commentList = list.stream().map(e -> {
            ShopOrderComment comment = new ShopOrderComment();
            BeanUtils.copyProperties(e, comment);
            comment.setId(idWorker.nextId());
            comment.setMemberIcon(loginUser.getHeader());
            comment.setNickName(loginUser.getNickName());
            comment.setCreateTime(DateUtils.newDateTime());
            comment.setCommentType(StateEnums.COMMENT_SIMPLE.getCode());
            // 处理图片
            if (!CollectionUtils.isEmpty(e.getPicList())) {
                comment.setPics(StringUtils.join(e.getPicList(), ","));
            }
            return comment;
        }).collect(Collectors.toList());

        List<Long> orderItemIds = list.stream().map(ShopOrderCommentDto::getOrderItemId).collect(Collectors.toList());
        QueryWrapper qw=new QueryWrapper();
        qw.in(ShopOrderItem.COL_ID,orderItemIds);
        List<ShopOrderItem> orderItemList = this.shopOrderItemMapper.selectList(qw);
        commentList.forEach(e -> {
            ShopOrderItem orderItem = orderItemList.stream().filter(i -> i.getId().equals(e.getOrderItemId())).findFirst().orElse(new ShopOrderItem());
            e.setProductId(orderItem.getProductId());
            e.setProductName(orderItem.getProductName());
        });
        // 订单状态改为已评价，保存订单操作历史
        this.shopOrderCommentRepository.saveAll(commentList);
        ShopOrder order = this.shopOrderMapper.selectById(list.get(0).getOrderId());
        order.setStatus(OrderStateEnum.FINISH.getCode());
        order.setCommentTime(DateUtils.newDateTime());
        order.setIsComment(1);
        order.setUpdateTime(DateUtils.newDateTime());
        this.shopOrderMapper.updateById(order);
        // 存储订单历史
        ShopOrderHistory orderHistory = new ShopOrderHistory();
        orderHistory.setId(idWorker.nextId());
        orderHistory.setOrderId(order.getId());
        orderHistory.setOperateMan(OperateEnum.USER.getType());
        orderHistory.setOrderStatus(OrderStateEnum.FINISH.getCode());
        this.shopOrderHistoryMapper.insert(orderHistory);
        // 用户统计信息修改
        QueryWrapper qw2=new QueryWrapper();
        qw2.eq(ShopUserStatistic.COL_USER_ID,loginUser.getId());
        ShopUserStatistic shopUserStatistic = this.shopUserStatisticMapper.selectOne(qw2);
        shopUserStatistic.setCommentCount(shopUserStatistic.getCommentCount() + 1);
        this.shopUserStatisticMapper.updateById(shopUserStatistic);
    }

    @Override
    public Page<ShopOrderCommentVo> getByPage(Page<ShopOrderCommentVo> page) {
        Query query = new Query();
        // 设置参数
        Map<String, Object> params = page.getParams();
        Object productId = params.get("productId");
        if (productId != null) {
            query.addCriteria(Criteria.where("product_id").is(Long.parseLong(productId.toString())));
        }
        Object orderId = params.get("orderId");
        if (orderId != null) {
            query.addCriteria(Criteria.where("order_id").is(Long.parseLong(orderId.toString())));
        }
        Integer star = (Integer) params.get("star") ;
        if (star != null && star==4) {
            query.addCriteria(Criteria.where("star").gte(star));
        }
        if (star != null && star==3) {
            query.addCriteria(Criteria.where("star").is(star));
        }
        if (star != null && star==2) {
            query.addCriteria(Criteria.where("star").lte(star));
        }
        int count = (int) mongoTemplate.count(query, ShopOrderComment.class);
        // 构造分页,如果是1000表示只查询前两条评价
        if (star != null && star==1000) {
            query.skip(page.getIndex());
            query.limit(2);
        }else{
            query.skip(page.getIndex());
            query.limit(page.getPageSize());
        }
        // 构造排序条件
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "create_time");
        query.with(Sort.by(order));
        List<ShopOrderComment> list = mongoTemplate.find(query, ShopOrderComment.class);
        if (!CollectionUtils.isEmpty(list)) {
            List<ShopOrderCommentVo> voList = list.stream().map(e -> {
                ShopOrderCommentVo vo = new ShopOrderCommentVo();
                BeanUtils.copyProperties(e, vo);
                if (StringUtils.isNotBlank(e.getPics())) {
                    vo.setPicList(Arrays.asList(e.getPics().split(",")));
                }
                return vo;
            }).collect(Collectors.toList());
            page.setList(voList);
        } else {
            page.setList(new ArrayList<>(0));
        }
        page.setTotalCount(count);
        return page;
    }

    @Override
    public SearchCommentNumVo searchCommentNum(Long id) {
        Query query = new Query();
        SearchCommentNumVo vo=new SearchCommentNumVo();
        // 设置参数
        if (id != null) {
            query.addCriteria(Criteria.where("product_id").is(Long.parseLong(id.toString())));
        }
        query.addCriteria(Criteria.where("star").gte(4));
        vo.setGoodNum ((int)mongoTemplate.count(query, ShopOrderComment.class));
        Query query1 = new Query();
        query1.addCriteria(Criteria.where("star").is(3));
        query1.addCriteria(Criteria.where("product_id").is(Long.parseLong(id.toString())));
        vo.setMiddleNum ((int)mongoTemplate.count(query1, ShopOrderComment.class));
        Query query2 = new Query();
        query2.addCriteria(Criteria.where("star").lte(2));
        query2.addCriteria(Criteria.where("product_id").is(Long.parseLong(id.toString())));
        vo.setFailNum ((int)mongoTemplate.count(query2, ShopOrderComment.class));
        return vo;
    }
}
