<template>
  <div>
    <!-- 搜索表单 -->
    <div class="search-form">
      <el-form :model="page.params" :inline="true" size="small">
        <el-form-item>
          <el-input v-model="page.params.createBy" clearable placeholder="手机号" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="page.params.id" clearable placeholder="订单号" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="page.params.status" placeholder="订单状态" clearable>
            <el-option label="待付款" :value="0" />
            <el-option label="待确认" :value="1" />
            <el-option label="待发货" :value="2" />
            <el-option label="待签收" :value="3" />
            <el-option label="待评价" :value="4" />
            <el-option label="已完成" :value="5" />
            <el-option label="无效订单" :value="6" />
            <el-option label="已关闭" :value="7" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="search">查询</el-button>
          <el-button type="warning" icon="el-icon-refresh" @click="Reset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <!-- 搜索表单结束 -->
    <!-- 数据表格 -->
    <div class="data-table">
      <el-table
        v-loading="loading"
        header-row-class-name="pochi-table-header"
        :data="dataPage.list"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="订单号" />
        <el-table-column prop="createBy" label="下单账号" />
        <el-table-column prop="totalAmount" label="总金额" />
        <el-table-column prop="payAmount" label="应付金额" />
        <el-table-column prop="couponAmount" label="优惠券抵扣" />
        <el-table-column prop="status" label="订单状态">
          <template slot-scope="{row}">
            <!-- 订单状态：0待付款；1待确认；2待发货；3已发货（待签收）；4已签收（待评价）；5已完成；6无效订单，7已关闭' -->
            <el-tag v-if="row.status === 0">待付款</el-tag>
            <el-tag v-else-if="row.status === 1">待确认</el-tag>
            <el-tag v-else-if="row.status === 2">待发货</el-tag>
            <el-tag v-else-if="row.status === 3">待签收</el-tag>
            <el-tag v-else-if="row.status === 4">待评价</el-tag>
            <el-tag v-else-if="row.status === 5" type="success">已完成</el-tag>
            <el-tag v-else-if="row.status === 6" type="info">无效订单</el-tag>
            <el-tag v-else-if="row.status === 7" type="info">已关闭</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="intgration" label="积分" />
        <el-table-column prop="confirmStatus" label="确认收货状态">
          <template slot-scope="{row}">
            <el-tag v-if="row.confirmStatus === 0" type="info">未收货</el-tag>
            <el-tag v-else>已收货</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paymentTime" label="支付时间" />
        <el-table-column prop="deliveryTime" label="发货时间" />
        <el-table-column prop="commentTime" label="评价时间" />
        <el-table-column prop="createTime" label="下单时间" />
        <el-table-column label="操作" width="170px">
          <template slot-scope="{row}">
            <el-button type="text" icon="el-icon-document" @click="toInfo(row.id)">详情</el-button>
            <el-dropdown class="handle-button">
              <span class="el-dropdown-link">
                操作<i class="el-icon-arrow-down el-icon--right" />
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item>
                  <el-button type="text" icon="el-icon-delete">删除</el-button>
                </el-dropdown-item>
                <el-dropdown-item>
                  <el-button type="text" icon="el-icon-document" @click="getComment(row.id)">查看评论</el-button>
                </el-dropdown-item>
                <el-dropdown-item>
                  <el-button v-if="row.status === 2" type="text" icon="el-icon-delete" @click="sendProduct(row)">发货</el-button>
                </el-dropdown-item>
                <el-dropdown-item>
                  <el-button v-if="row.status === 0" type="text" icon="el-icon-delete" @click="closeOrder(row.id)">关闭</el-button>
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <!-- 数据表格结束 -->
    <!-- 分页组件开始 -->
    <div class="pageable">
      <el-pagination
        :current-page="page.pageNumber"
        :page-sizes="[10, 20, 30, 50]"
        :page-size="10"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="dataPage.totalCount"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
    <!-- 分页组件结束 -->
    <!-- 评论列表弹窗 -->
    <el-dialog
      title="商品评论"
      :visible.sync="commentDialog"
      width="80%"
    >
      <comment-list :order-id="activeId" />
    </el-dialog>
    <!-- 评论列表弹窗结束 -->
  </div>
</template>

<script>
import orderApi from '@/api/shop/shop-order'
import commentList from '@/views/order/comment/comment-list'
export default {
  components: {
    commentList
  },
  props: {
    userId: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      // 分页对象
      page: {
        // 当前页
        pageNumber: 1,
        // 每页条数
        pageSize: 10,
        // 查询参数
        params: {}
      },
      // 数据分页对象
      dataPage: {},
      // 控制评论列表展示
      commentDialog: false,
      // 当前点击的订单ID
      activeId: null
    }
  },
  watch: {
    userId: function() {
      this.getByPage()
    }
  },
  created() {
    this.getByPage()
  },
  methods: {
    // 重置
    Reset() {
      this.page.pageNumber = 1
      this.page.params = {}
      this.getByPage()
    },
    // 查看评论
    getComment(id) {
      this.activeId = id
      this.commentDialog = true
    },
    // 详情
    toInfo(id) {
      this.$router.push('/order/orderInfo/' + id)
    },
    // 发货
    sendProduct(row) {
      this.$confirm(`
      <p>是否确认发货？操作后无法取消</p>
      <p>收货人：<span style="color: red">${row.receiverName} - ${row.receiverPhone}</span></p>
      <p>收货地址：${row.receiverProvince}${row.receiverCity}${row.receiverRegion}${row.receiverDetailAddress}</p>
      `, '提示', {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }).then(() => {
        orderApi.changeOrderStatus({ id: row.id, status: 3 }).then(res => {
          this.$message.success(res.msg)
          this.getByPage()
        })
      })
    },
    // 关闭订单
    closeOrder(id) {
      this.$confirm('关闭订单后无法重新开启，是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        orderApi.changeOrderStatus({ id: id, status: 7 }).then(res => {
          this.$message.success(res.msg)
          this.getByPage()
        })
      })
    },
    // 分页查询
    getByPage() {
      this.loading = true
      if (this.userId) {
        this.page.params.createBy = this.userId
      }
      orderApi.getByPage(this.page).then((res) => {
        this.dataPage = res.data
      })
      this.loading = false
    },
    // 搜索
    search() {
      this.page.pageNumber = 1
      this.getByPage()
    },
    // 当前页发生改变
    handleCurrentChange(val) {
      this.page.pageNumber = val
      this.getByPage()
    },
    // 每页条数发生改变
    handleSizeChange(val) {
      this.page.pageSize = val
      this.getByPage()
    }
  }
}
</script>

<style>
</style>
