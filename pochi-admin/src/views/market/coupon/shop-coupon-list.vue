<template>
  <div>
    <!-- 搜索表单 -->
    <div class="search-form">
      <el-form :model="page.params" :inline="true" size="small">
        <el-form-item>
          <el-input v-model="page.params.name" clearable placeholder="" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="search">查询</el-button>
          <el-button type="warning" icon="el-icon-refresh" @click="Reset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <!-- 搜索表单结束 -->
    <!-- 添加按钮 -->
    <div class="button-group">
      <el-button type="primary" size="small" icon="el-icon-plus" @click="toAdd">添加</el-button>
    </div>
    <!-- 添加按钮结束 -->
    <!-- 数据表格 -->
    <div class="data-table">
      <el-table
        v-loading="loading"
        header-row-class-name="pochi-table-header"
        :data="dataPage.list"
        stripe
        style="width: 10"
      >
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="couponType" label="类型" />
        <el-table-column prop="amount" label="面值" />
        <el-table-column prop="minPoint" label="门槛" />
        <el-table-column label="数量">
          <template slot-scope="{row}">
            <div>发行数：{{ row.publishCount }}</div>
            <div>已领取：{{ row.receiveCount }}</div>
            <div>已使用：{{ row.useCount }}</div>
            <div>剩余数：{{ row.restCount }}</div>
          </template>
        </el-table-column>
        <el-table-column label="有效期">
          <template slot-scope="{row}">
            <div>{{ row.startTime }}</div>
            <div>至</div>
            <div>{{ row.endTime }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template slot-scope="{row}">
            <el-tag v-if="row.status === 1" type="success">未过期</el-tag>
            <el-tag v-else type="info">已过期</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column prop="createBy" label="创建人" />
        <el-table-column label="操作" width="200px">
          <template slot-scope="{row}">
            <el-button type="text" icon="el-icon-document" @click="toInfo(row.id)">详情</el-button>
            <el-button type="text" icon="el-icon-edit" @click="toDown(row.id)">下架</el-button>
            <el-button type="text" icon="el-icon-delete" @click="toDelete(row.id)">删除</el-button>
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
    <!-- 添加弹窗 -->
    <el-dialog
      v-if="addDialog"
      title="添加优惠券"
      :visible.sync="addDialog"
      width="40%"
    >
      <shop-coupon-add @after="getByPage" @close="closeDialog" />
    </el-dialog>
    <!-- 添加弹窗结束 -->
    <!-- 详情弹窗 -->
    <el-dialog
      title="优惠券详情"
      :visible.sync="infoDialog"
      width="80%"
    >
      <shop-coupon-info :active-id="activeId" />
    </el-dialog>
    <!-- 详情弹窗结束 -->
  </div>
</template>

<script>
import couponApi from '@/api/market/shop-coupon'
import shopCouponAdd from './shop-coupon-add'
import shopCouponInfo from './shop-coupon-info'
export default {
  components: {
    shopCouponAdd,
    shopCouponInfo
  },
  data() {
    return {
      // 分页对象
      page: {
        // 搜索参数
        params: {},
        // 当前页
        pageNumber: 1,
        // 每页条数
        pageSize: 10
      },
      // 数据分页对象
      dataPage: {},
      // 控制添加弹窗展示
      addDialog: false,
      // 控制详情弹窗展示
      infoDialog: false,
      loading: false,
      activeId: null
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
    // 搜索
    search() {
      this.page.pageNumber = 1
      this.getByPage()
    },
    // 分页查询
    getByPage() {
      this.loading = true
      couponApi.getByPage(this.page).then(res => {
        this.dataPage = res.data
      })
      this.loading = false
    },
    // 每页条数发生改变
    handleSizeChange(val) {
      this.page.pageSize = val
      this.getByPage()
    },
    // 当前页发生变化
    handleCurrentChange(val) {
      this.page.pageNumber = val
      this.getByPage()
    },
    // 添加
    toAdd() {
      this.addDialog = true
    },
    // 详情
    // 详情
    toInfo(id) {
      this.infoDialog = true
      this.activeId = id
    },
    // 下架
    toDown(id) {
      this.$confirm('下架后无法上架，是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        couponApi.down(id).then(res => {
          this.$message.success(res.msg)
          this.getByPage()
        })
      })
    },
    // 删除
    toDelete(id) {
      this.$confirm('是否删除该优惠券?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        couponApi.delete(id).then(res => {
          this.$message.success(res.msg)
          this.getByPage()
        })
      })
    },
    // 关闭所有弹窗
    closeDialog() {
      this.addDialog = false
    }
  }
}
</script>

<style>

</style>
