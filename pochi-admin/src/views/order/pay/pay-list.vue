<template>
  <div>
    <!-- 搜索表单 -->
    <div class="search-form">
      <el-form :model="page.params" :inline="true" size="small">
        <el-form-item>
          <el-input v-model="page.params.createBy" clearable placeholder="手机号" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="page.params.orderNo" clearable placeholder="支付宝订单号" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="page.params.outTradeNo" clearable placeholder="外部订单号" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="page.params.orderId" clearable placeholder="商品订单号" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="page.params.status" placeholder="订单状态" clearable>
            <el-option label="支付中" :value="0" />
            <el-option label="支付成功" :value="1" />
            <el-option label="支付失败" :value="-1" />
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
        <el-table-column prop="outTradeNo" label="外部订单号" />
        <el-table-column prop="orderNo" label="支付宝订单号" />
        <el-table-column prop="orderId" label="商品订单号" />
        <el-table-column prop="payAmount" label="支付金额" />
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column prop="createBy" label="手机号" />
        <el-table-column prop="status" label="支付状态">
          <template slot-scope="{row}">
            <el-tag v-if="row.status === 0">支付中</el-tag>
            <el-tag v-else-if="row.status === 1" type="success">支付成功</el-tag>
            <el-tag v-else type="danger">支付失败</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="状态更新时间" />
        <el-table-column label="操作" width="170px">
          <template slot-scope="{row}">
            <el-button type="text" icon="el-icon-document" @click="toInfo(row.id)">详情</el-button>
            <el-dropdown class="handle-button">
              <span class="el-dropdown-link">
                操作<i class="el-icon-arrow-down el-icon--right" />
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item>
                  <el-button type="text" icon="el-icon-delete" @click="deleteById(row.id)">删除</el-button>
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
    <!-- 详情弹窗 -->
    <el-dialog
      title="订单详情"
      :visible.sync="infoDialog"
      width="30%"
    >
      <pay-info :active-id="activeId" />
    </el-dialog>

    <!-- 详情弹窗结束 -->
  </div>
</template>

<script>
import payApi from '@/api/shop/shop-order-pay'
import payInfo from './pay-info'
export default {
  components: {
    payInfo
  },
  data() {
    return {
      // 分页查询对象
      page: {
        // 当前页
        pageNumber: 1,
        // 每页条数
        pageSize: 10,
        // 参数
        params: {}
      },
      // 数据分页对象
      dataPage: {},
      // 控制详情弹窗展示
      infoDialog: false,
      // 当前点击的ID
      activeId: null,
      loading: false
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
    deleteById(id) {
      this.$confirm('是否删除该支付宝订单?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        payApi.deleteById(id).then(res => {
          this.$message.success(res.msg)
          this.getByPage()
        })
      })
    },
    // 详情
    toInfo(id) {
      this.infoDialog = true
      this.activeId = id
    },
    // 分页查询
    getByPage() {
      this.loading = true
      payApi.getByPage(this.page).then((res) => {
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
