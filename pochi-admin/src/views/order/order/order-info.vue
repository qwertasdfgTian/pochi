<template>
  <div>
    <!-- 订单步骤 -->
    <div class="order-step">
      <el-steps :active="currentStep" finish-status="success" align-center>
        <el-step v-for="(item, index) in stepList" :key="index" :title="item.name" :description="item.time" />
      </el-steps>
    </div>
    <!-- 详情卡片 -->
    <el-card shadow="never">
      <div slot="header">
        <span>
          当前订单状态：
          <el-tag v-if="order.status === 0">待付款</el-tag>
          <el-tag v-else-if="order.status === 1">待确认</el-tag>
          <el-tag v-else-if="order.status === 2">待发货</el-tag>
          <el-tag v-else-if="order.status === 3">待签收</el-tag>
          <el-tag v-else-if="order.status === 4">待评价</el-tag>
          <el-tag v-else-if="order.status === 5" type="success">已完成</el-tag>
          <el-tag v-else-if="order.status === 6" type="info">无效订单</el-tag>
          <el-tag v-else-if="order.status === 7" type="info">已关闭</el-tag>
        </span>
      </div>
      <div class="order-body">
        <!-- 基本信息 -->
        <el-card shadow="never" class="order-card">
          <div slot="header">订单基本信息</div>
          <el-form>
            <div class="statistic">
              <el-row>
                <el-col :span="6" :offset="0">
                  <el-form-item>
                    <div slot="label">订单号</div>
                    {{ order.id }}
                  </el-form-item>
                </el-col>
                <el-col :span="6" :offset="0">
                  <el-form-item>
                    <div slot="label">下单用户</div>
                    {{ order.createBy }}
                  </el-form-item>
                </el-col>
                <el-col :span="6" :offset="0">
                  <el-form-item>
                    <div slot="label">订单类型</div>
                    {{ order.orderType === 0?'普通订单':'秒杀订单' }}
                  </el-form-item>
                </el-col>
                <el-col :span="6" :offset="0">
                  <el-form-item>
                    <div slot="label">物流公司</div>
                    {{ order.deliveryCompany }}
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="6" :offset="0">
                  <el-form-item>
                    <div slot="label">物流单号</div>
                    {{ order.deliverySn }}
                  </el-form-item>
                </el-col>
                <el-col :span="6" :offset="0">
                  <el-form-item>
                    <div slot="label">自动确认天数</div>
                    {{ order.autoConfirmDay }}
                  </el-form-item>
                </el-col>
                <el-col :span="6" :offset="0">
                  <el-form-item>
                    <div slot="label">积分</div>
                    {{ order.integration }}
                  </el-form-item>
                </el-col>
                <el-col :span="6" :offset="0">
                  <el-form-item>
                    <div slot="label">确认收货状态</div>
                    {{ order.confirmStatus===0?'未确认':'已确认' }}
                  </el-form-item>
                </el-col>
              </el-row>
            </div>
          </el-form>
        </el-card>
        <!-- 基本信息结束 -->
        <!-- 收货人信息 -->
        <el-card shadow="never" class="order-card">
          <div slot="header">订单收货信息</div>
          <el-form>
            <div class="statistic">
              <el-row>
                <el-col :span="6" :offset="0">
                  <el-form-item>
                    <div slot="label">收货人</div>
                    {{ order.receiverName }}
                  </el-form-item>
                </el-col>
                <el-col :span="6" :offset="0">
                  <el-form-item>
                    <div slot="label">收货电话</div>
                    {{ order.receiverPhone }}
                  </el-form-item>
                </el-col>
                <el-col :span="4" :offset="0">
                  <el-form-item>
                    <div slot="label">邮政编码</div>
                    {{ order.receiverPostCode }}
                  </el-form-item>
                </el-col>
                <el-col :span="8" :offset="0">
                  <el-form-item>
                    <div slot="label">收货地址</div>
                    {{ order.receiverProvince }}
                    {{ order.receiverCity }}
                    {{ order.receiverRegion }}
                    {{ order.receiverDetailAddress }}
                  </el-form-item>
                </el-col>
              </el-row>
            </div>
          </el-form>
        </el-card>
        <!-- 收货人信息结束 -->
        <!-- 商品信息 -->
        <el-card shadow="never" class="order-card">
          <div slot="header">
            <span>订单商品信息</span>
          </div>
          <div class="data-table">
            <el-table
              header-row-class-name="pochi-table-header"
              :data="order.itemList"
              stripe
              style="width: 100%"
            >
              <el-table-column prop="productName" label="商品名称" />
              <el-table-column prop="productPic" label="图片">
                <template slot-scope="{row}">
                  <el-image :src="row.productPic" style="width: 100px; height: 125px" fit="fill" :preview-src-list="[row.productPic]" />
                </template>
              </el-table-column>
              <el-table-column prop="productBrand" label="品牌" />
              <el-table-column prop="productPrice" label="价格" />
              <el-table-column prop="productQuantity" label="购买数量" />
              <el-table-column prop="integration" label="积分" />
            </el-table>
          </div>
          <div class="totalAmount">
            合计金额：<span style="color:red">{{ totalAmount }}</span>
          </div>
        </el-card>
        <!-- 商品信息结束 -->

        <!-- 费用信息 -->
        <el-card shadow="never" class="order-card">
          <div slot="header">订单费用信息</div>
          <el-form>
            <div class="statistic">
              <el-row>
                <el-col :span="6" :offset="0">
                  <el-form-item>
                    <div slot="label">总金额</div>
                    {{ order.totalAmount }}
                  </el-form-item>
                </el-col>
                <el-col :span="6" :offset="0">
                  <el-form-item>
                    <div slot="label">应付金额</div>
                    {{ order.payAmount }}
                  </el-form-item>
                </el-col>
                <el-col :span="6" :offset="0">
                  <el-form-item>
                    <div slot="label">运费金额</div>
                    {{ order.freightAmount }}
                  </el-form-item>
                </el-col>
                <el-col :span="6" :offset="0">
                  <el-form-item>
                    <div slot="label">优惠金额</div>
                    {{ order.couponAmount }}
                  </el-form-item>
                </el-col>
              </el-row>
            </div>
          </el-form>
        </el-card>
        <!-- 费用信息结束 -->
        <!-- 支付信息 -->
        <el-card shadow="never" class="order-card">
          <div slot="header">订单支付信息</div>
          <el-form>
            <div class="statistic">
              <el-row>
                <el-col :span="8" :offset="0">
                  <el-form-item>
                    <div slot="label">支付宝订单号</div>
                    {{ payOrder.orderNo?payOrder.orderNo:' ' }}
                  </el-form-item>
                </el-col>
                <el-col :span="8" :offset="0">
                  <el-form-item>
                    <div slot="label">外部订单号</div>
                    {{ payOrder.outTradeNo }}
                  </el-form-item>
                </el-col>
                <el-col :span="8" :offset="0">
                  <el-form-item>
                    <div slot="label">商品订单号</div>
                    {{ payOrder.orderId }}
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="8" :offset="0">
                  <el-form-item>
                    <div slot="label">支付金额</div>
                    {{ payOrder.payAmount }}
                  </el-form-item>
                </el-col>
                <el-col :span="8" :offset="0">
                  <el-form-item>
                    <div slot="label">支付时间</div>
                    {{ payOrder.createTime }}
                  </el-form-item>
                </el-col>
                <el-col :span="8" :offset="0">
                  <el-form-item>
                    <div slot="label">支付状态</div>
                    <el-tag v-if="payOrder.status === 0">支付中</el-tag>
                    <el-tag v-else-if="payOrder.status === 1" type="success">支付成功</el-tag>
                    <el-tag v-else type="danger">支付失败</el-tag>
                  </el-form-item>
                </el-col>
              </el-row>
            </div>
          </el-form>
        </el-card>
        <!-- 支付信息结束 -->
        <!-- 操作记录 -->
        <el-card shadow="never" class="order-card">
          <div slot="header">
            <span>订单操作记录</span>
          </div>
          <div class="data-table">
            <el-table
              header-row-class-name="pochi-table-header"
              :data="historyList"
              stripe
              style="width: 100%"
            >
              <el-table-column prop="orderId" label="订单ID" />
              <el-table-column prop="operateMan" label="操作人" />
              <el-table-column prop="orderStatus" label="操作状态">
                <template slot-scope="{row}">
                  <el-tag v-if="row.orderStatus === 0">待付款</el-tag>
                  <el-tag v-else-if="row.orderStatus === 1">待确认</el-tag>
                  <el-tag v-else-if="row.orderStatus === 2">待发货</el-tag>
                  <el-tag v-else-if="row.orderStatus === 3">待签收</el-tag>
                  <el-tag v-else-if="row.orderStatus === 4">待评价</el-tag>
                  <el-tag v-else-if="row.orderStatus === 5" type="success">已完成</el-tag>
                  <el-tag v-else-if="row.orderStatus === 6" type="info">无效订单</el-tag>
                  <el-tag v-else-if="row.orderStatus === 7" type="info">已关闭</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="操作时间" />
            </el-table>
          </div>
        </el-card>
        <!-- 操作记录结束 -->
      </div>
    </el-card>
    <!-- 详情卡片结束 -->
  </div>
</template>

<script>
import orderApi from '@/api/shop/shop-order'
import payApi from '@/api/shop/shop-order-pay'
export default {
  data() {
    return {
      // 订单ID
      orderId: null,
      // 订单详情
      order: {},
      // 操作历史
      historyList: [],
      // 支付订单
      payOrder: {},
      // 当前进度条
      currentStep: 0,
      // 步骤条
      stepList: [
        { id: 0, name: '待付款', time: '' },
        { id: 1, name: '待确认', time: '' },
        { id: 2, name: '待发货', time: '' },
        { id: 3, name: '待签收', time: '' },
        { id: 4, name: '待评价', time: '' },
        { id: 5, name: '已完成', time: '' },
        { id: 6, name: '无效订单', time: '' },
        { id: 7, name: '已关闭', time: '' }
      ]
    }
  },
  computed: {
    totalAmount() {
      let money = 0
      if (this.order.itemList) {
        this.order.itemList.forEach(e => {
          money += e.productPrice * e.productQuantity
        })
      }
      return money
    }
  },
  created() {
    this.orderId = this.$route.params.id
    this.getById()
    this.getHistory()
    this.getPayOrder()
  },
  methods: {
    // 查询详情
    getById() {
      orderApi.get(this.orderId).then((res) => {
        this.order = res.data
        this.currentStep = this.order.status + 1
      })
    },
    // 查询历史数据
    getHistory() {
      orderApi.getHistory(this.orderId).then((res) => {
        this.historyList = res.data
        if (this.historyList) {
          // 操作步骤条，赋值操作时间
          this.stepList.forEach(s => {
            const item = this.historyList.find(h => h.orderStatus === s.id)
            if (item) {
              s.time = item.createTime
            }
          })
        }
      })
    },
    // 查询支付订单
    getPayOrder() {
      payApi.getByOrderId(this.orderId).then((res) => {
        this.payOrder = res.data
      })
    }
  }
}
</script>

<style scoped>
.order-step {
  margin-bottom: 20px;
}
.totalAmount {
  font-size: 14px;
  margin-top: 10px;
  text-align: right;
}
.order-card {
  margin-bottom: 20px;
}
.statistic >>> .el-row {
  border-top: 1px solid #dcdfe6;
  border-bottom: 1px solid #dcdfe6;
  border-right: 1px solid #dcdfe6;
  margin-bottom: -1px;
}

.statistic >>> .el-col {
  border-left: 1px solid #dcdfe6;
}

.statistic >>> .el-form-item__label {
  border-right: 1px solid #dcdfe6;
  text-align: center;
  width: 100%;
  background-color: #f2f6fc;
}

.statistic >>> .el-form-item {
  line-height: 45px;
  margin-bottom: 0px;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-right: -1px;
}

.statistic >>> .el-form-item__content {
  text-align: center;
}
</style>
