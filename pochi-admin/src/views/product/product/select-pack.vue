<template>
  <div>
    <!-- 搜索栏 -->
    <div class="search-form">
      <el-form :model="page.params" :inline="true" size="small">
        <el-form-item>
          <el-input v-model="page.params.name" placeholder="套装名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="search">查询</el-button>
          <el-button type="warning" icon="el-icon-refresh" @click="Reset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <!-- 搜索栏结束 -->
    <!-- 数据表格 -->
    <div class="data-table">
      <el-table
        v-loading="loading"
        header-row-class-name="pochi-table-header"
        :data="dataPage.list"
        highlight-current-row
        stripe
        style="width: 100%"
        @current-change="selectPack"
      >
        <el-table-column prop="id" label="套装编号" width="180px" />
        <el-table-column prop="name" label="套装名称" />
        <el-table-column prop="productCount" label="商品数" width="80px" />
        <el-table-column prop="createTime" label="创建时间" width="160px" />
        <el-table-column prop="createBy" label="创建人" width="120px" />
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
  </div>
</template>

<script>
import packApi from '@/api/shop/shop-pack.js'
export default {
  data() {
    return {
      // 分页对象
      page: {
        params: {},
        // 当前页
        pageNumber: 1,
        // 每页条数
        pageSize: 10
      },
      // 数据对象
      dataPage: {},
      loading: false
    }
  },
  created() {
    this.getByPage()
  },
  methods: {
    // 搜索
    search() {
      this.page.pageNumber = 1
      this.getByPage()
    },
    // 重置
    Reset() {
      this.page.pageNumber = 1
      this.page.params = {}
      this.getByPage()
    },
    // 选中当前行触发
    selectPack(row, oldVal) {
      this.$emit('selectSubmit', row)
    },
    // 分页查询
    getByPage() {
      this.loading = true
      packApi.getByPage(this.page).then(res => {
        this.dataPage = res.data
      })
      this.loading = false
    },
    // 每页条数发生改变
    handleSizeChange(val) {
      this.page.pageSize = val
      this.getByPage()
    },
    // 当前页发生改变
    handleCurrentChange(val) {
      this.page.pageNumber = val
      this.getByPage()
    }
  }
}
</script>

<style>

</style>
