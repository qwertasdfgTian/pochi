<template>
  <div>
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
    <!-- 操作按钮组 -->
    <div class="button-group">
      <el-button type="primary" size="small" icon="el-icon-plus" @click="toAdd">添加</el-button>
    </div>
    <!-- 操作按钮组结束 -->
    <!-- 数据表格 -->
    <div class="data-table">
      <el-table
        v-loading="loading"
        header-row-class-name="pochi-table-header"
        :data="dataPage.list"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="套装编号" width="180px" />
        <el-table-column prop="name" label="套装名称" />
        <el-table-column prop="productCount" label="商品数" width="80px" />
        <el-table-column prop="createTime" label="创建时间" width="160px" />
        <el-table-column prop="createBy" label="创建人" width="120px" />
        <el-table-column label="操作" width="180px">
          <template slot-scope="{row}">
            <el-button type="text" icon="el-icon-document" @click="toInfo(row.id)">商品清单</el-button>
            <el-dropdown class="handle-button">
              <span class="el-dropdown-link">
                操作<i class="el-icon-arrow-down el-icon--right" />
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item>
                  <el-button type="text" icon="el-icon-edit" @click="toUpdate(row.id)">修改</el-button>
                </el-dropdown-item>
                <el-dropdown-item v-permission="['sys:user:delete']" @click.native="toDelete(row.id)">
                  <el-button type="text" icon="el-icon-delete">删除</el-button>
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
    <!-- 添加弹窗 -->
    <el-dialog
      v-if="addDialog"
      title="添加套装"
      :visible.sync="addDialog"
      width="40%"
    >
      <pack-add @close="closeDialog" @after="getByPage" />
    </el-dialog>
    <!-- 添加弹窗结束 -->
    <!-- 修改弹窗 -->
    <el-dialog
      v-if="updateDialog"
      title="修改套装"
      :visible.sync="updateDialog"
      width="40%"
    >
      <pack-update :active-id="activeId" @close="closeDialog" @after="getByPage" />
    </el-dialog>
    <!-- 修改弹窗结束 -->
    <!-- 商品清单 -->
    <el-dialog
      v-if="infoDialog"
      title="商品清单"
      :visible.sync="infoDialog"
      width="70%"
    >
      <product-detail :active-id="activeId" />
    </el-dialog>
    <!-- 商品清单结束 -->
  </div>
</template>

<script>
import packApi from '@/api/shop/shop-pack.js'
import PackAdd from './pack-add.vue'
import PackUpdate from './pack-update.vue'
import ProductDetail from './product-detail.vue'
export default {
  components: {
    PackAdd,
    PackUpdate,
    ProductDetail
  },
  data() {
    return {
      // 分页对象
      page: {
        // 查询参数
        params: {},
        // 当前页
        pageNumber: 1,
        // 每页条数
        pageSize: 10
      },
      // 数据对象
      dataPage: {},
      // 添加弹窗
      addDialog: false,
      // 修改弹窗
      updateDialog: false,
      // 商品清单弹窗
      infoDialog: false,
      // 当前点击的套装ID
      activeId: null,
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
    // 添加
    toAdd() {
      this.addDialog = true
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
    },
    // 商品清单
    toInfo(id) {
      this.activeId = id
      this.infoDialog = true
    },
    // 删除
    toDelete(id) {
      this.$confirm('是否删除该套装?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        packApi.delete(id).then(res => {
          this.$message.success(res.msg)
          this.getByPage()
        })
      })
    },
    // 修改
    toUpdate(id) {
      this.activeId = id
      this.updateDialog = true
    },
    // 关闭所有弹窗
    closeDialog() {
      this.addDialog = false
      this.updateDialog = false
      this.infoDialog = false
    }
  }
}
</script>

<style>
</style>
