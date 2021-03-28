package com.lt.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class GoShopSeckillDto implements Serializable {

    // 秒杀商品的id
    @NotNull(message = "商品id不能是空")
    private Long productId;

    // 秒杀过来的用户的id
    @NotNull(message = "用户id不能是空")
    private Long userId;

}
