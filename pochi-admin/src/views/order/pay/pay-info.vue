<template>
  <div>
    <el-form label-width="100px">
      <el-row :gutter="20">
        <el-col :span="24" :offset="0">
          <el-form-item label="支付宝订单号">
            {{ order.orderNo }}
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24" :offset="0">
          <el-form-item label="外部订单号">
            {{ order.outTradeNo }}
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24" :offset="0">
          <el-form-item label="商品订单号">
            {{ order.orderId }}
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12" :offset="0">
          <el-form-item label="手机号">
            {{ order.createBy }}
          </el-form-item>
        </el-col>
        <el-col :span="12" :offset="0">
          <el-form-item label="状态">
            <el-tag v-if="order.status === 0">支付中</el-tag>
            <el-tag v-else-if="order.status === 1" type="success">支付成功</el-tag>
            <el-tag v-else type="danger">支付失败</el-tag>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12" :offset="0">
          <el-form-item label="创建时间">
            {{ order.createTime }}
          </el-form-item>
        </el-col>
        <el-col :span="12" :offset="0">
          <el-form-item label="更新时间">
            {{ order.updateTime }}
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script>
import payApi from '@/api/shop/shop-order-pay'
export default {
  props: {
    activeId: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      // 订单
      order: {}
    }
  },
  watch: {
    activeId: {
      immediate: true,
      handler: function() {
        this.getById()
      }
    }
  },
  methods: {
    // 查询详情
    getById() {
      payApi.get(this.activeId).then(res => {
        this.order = res.data
      })
    }
  }
}
</script>

<style>

</style>
