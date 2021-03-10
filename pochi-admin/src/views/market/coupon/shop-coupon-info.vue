<template>
  <div>
    <el-form>
      <div class="statistic">
        <el-row>
          <el-col :span="5" :offset="0">
            <el-form-item>
              <div slot="label">名称</div>
              {{ coupon.name }}
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="0">
            <el-form-item>
              <div slot="label">使用类型</div>
              <span v-if="coupon.couponType === 0">全场通用</span>
              <span v-if="coupon.couponType === 1">指定分类</span>
              <span v-if="coupon.couponType === 2">指定商品</span>
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="0">
            <el-form-item>
              <div slot="label">使用门槛</div>
              {{ coupon.minPoint }}
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="0">
            <el-form-item>
              <div slot="label">面值</div>
              {{ coupon.amount }}
            </el-form-item>
          </el-col>
          <el-col :span="4" :offset="0">
            <el-form-item>
              <div slot="label">状态</div>
              {{ coupon.status===1?'正常':'过期' }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="5" :offset="0">
            <el-form-item>
              <div slot="label">有效期</div>
              {{ coupon.startTime }} 至 {{ coupon.endTime }}
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="0">
            <el-form-item>
              <div slot="label">发行数</div>
              {{ coupon.publishCount }}
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="0">
            <el-form-item>
              <div slot="label">已领取</div>
              {{ coupon.receiveCount }}
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="0">
            <el-form-item>
              <div slot="label">已使用</div>
              {{ coupon.useCount }}
            </el-form-item>
          </el-col>
          <el-col :span="4" :offset="0">
            <el-form-item>
              <div slot="label">剩余数</div>
              {{ coupon.restCount }}
            </el-form-item>
          </el-col>
        </el-row>
      </div>
    </el-form>
    <!-- 领取历史 -->
    <el-card class="history-card" shadow="never" :body-style="{ padding: '20px' }">
      <div slot="header">
        <span>领取历史</span>
      </div>
      <div class="history-table">
        <el-table header-row-class-name="pochi-table-header" :data="historyList" stripe>
          <el-table-column prop="id" label="编号" />
          <el-table-column prop="createBy" label="领取人" />
          <el-table-column prop="useStatus" label="使用状态">
            <template slot-scope="{row}">
              <el-tag v-if="row.useStatus === 0">未使用</el-tag>
              <el-tag v-if="row.useStatus === 1" type="success">已使用</el-tag>
              <el-tag v-if="row.useStatus === 2" type="info">已过期</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="面值" />
          <el-table-column prop="minPoint" label="使用门槛" />
          <el-table-column prop="useTime" label="使用时间" />
          <el-table-column prop="orderId" label="订单ID" />
          <el-table-column prop="createTime" label="领取时间" />
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script>
import couponApi from '@/api/market/shop-coupon'
export default {
  props: {
    activeId: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      // 优惠券对象
      coupon: {},
      // 优惠券领取记录数组
      historyList: []
    }
  },
  watch: {
    activeId: {
      immediate: true,
      handler: function() {
        this.getById()
        this.getHistoryList()
      }
    }
  },
  methods: {
    // 查询详情
    getById() {
      couponApi.get(this.activeId).then((res) => {
        this.coupon = res.data
      })
    },
    // 查询优惠券领取记录
    getHistoryList() {
      couponApi.getHistoryList(this.activeId).then((res) => {
        this.historyList = res.data
      })
    }
  }
}
</script>

<style scoped>
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

.history-card {
  margin-top: 20px;
}
</style>
