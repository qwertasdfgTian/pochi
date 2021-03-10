<template>
  <div>
    <!-- 搜索栏 -->
    <div class="search-form">
      <el-form :model="page.params" :inline="true" size="small">
        <el-form-item>
          <el-input v-model="page.params.name" clearable placeholder="品牌名称" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="search">查询</el-button>
          <el-button type="warning" icon="el-icon-refresh" @click="Reset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <!-- 搜索栏结束 -->
    <!-- 添加按钮开始 -->
    <div class="button-group">
      <el-button type="primary" size="small" icon="el-icon-plus" @click="toAdd">添加</el-button>
    </div>
    <!-- 添加按钮结束 -->
    <!-- 列表页 -->
    <div class="data-table">
      <el-table
        v-loading="loading"
        header-row-class-name="pochi-table-header"
        :data="dataPage.list"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="name" label="品牌名称" />
        <el-table-column prop="showStatus" label="是否显示">
          <template slot-scope="{row}">
            <el-tag v-if="row.showStatus === 1" type="success">是</el-tag>
            <el-tag v-else type="info">否</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" />
        <el-table-column prop="logo" label="logo">
          <template slot-scope="{row}">
            <el-image
              style="width: 50px; height: 50px"
              :src="row.logo"
              :preview-src-list="[row.logo]"
              fit="fill"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="{row}">
            <el-button type="text" icon="el-icon-document" @click="toInfo(row.id)">详情</el-button>
            <el-dropdown class="handle-button">
              <span class="el-dropdown-link">
                操作<i class="el-icon-arrow-down el-icon--right" />
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item @click.native="toUpdate(row.id)">
                  <el-button type="text" icon="el-icon-edit">修改</el-button>
                </el-dropdown-item>
                <el-dropdown-item @click.native="toDelete(row.id)">
                  <el-button type="text" icon="el-icon-delete">删除</el-button>
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <!-- 列表页结束 -->
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
      title="添加品牌"
      :visible.sync="addDialog"
      width="40%"
    >
      <shop-brand-add @after="getByPage" @close="closeDialog" />
    </el-dialog>
    <!-- 添加弹窗结束 -->
    <!-- 修改弹窗 -->
    <el-dialog
      v-if="updateDialog"
      title="修改品牌"
      :visible.sync="updateDialog"
      width="40%"
    >
      <shop-brand-update :active-id="activeId" @after="getByPage" @close="closeDialog" />
    </el-dialog>
    <!-- 修改弹窗结束 -->
    <!-- 详情弹窗 -->
    <el-dialog
      v-if="infoDialog"
      title="详情品牌"
      :visible.sync="infoDialog"
      width="40%"
    >
      <shop-brand-info :active-id="activeId" />
    </el-dialog>
    <!-- 详情弹窗结束 -->
  </div>
</template>

<script>
import brandApi from '@/api/shop/shop-brand'
import shopBrandAdd from './shop-brand-add'
import shopBrandUpdate from './shop-brand-update'
import shopBrandInfo from './shop-brand-info'
export default {
  components: {
    shopBrandAdd,
    shopBrandUpdate,
    shopBrandInfo
  },
  data() {
    return {
      // 分页对象
      page: {
        // 参数对象
        params: {},
        // 当前页
        pageNumber: 1,
        // 每页条数
        pageSize: 10
      },
      // 数据分页对象
      dataPage: {},
      // 控制添加弹窗
      addDialog: false,
      // 控制修改弹窗
      updateDialog: false,
      // 控制详情弹窗
      infoDialog: false,
      // 当前点击的品牌ID
      activeId: null,
      // 遮罩
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
    // 搜索
    search() {
      this.page.pageNumber = 1
      this.getByPage()
    },
    // 每页显示条数改变触发
    handleSizeChange(val) {
      this.page.pageSize = val
      this.getByPage()
    },
    // 当前页改变时触发
    handleCurrentChange(val) {
      this.page.pageNumber = val
      this.getByPage()
    },
    // 打开添加弹窗
    toAdd() {
      this.addDialog = true
    },
    // 分页查询
    getByPage() {
      this.loading = true
      brandApi.getByPage(this.page).then(res => {
        this.dataPage = res.data
      })
      this.loading = false
    },
    // 查看详情
    toInfo(id) {
      this.activeId = id
      this.infoDialog = true
    },
    // 打开修改弹窗
    toUpdate(id) {
      this.activeId = id
      this.updateDialog = true
    },
    // 删除
    toDelete(id) {
      this.$confirm('是否删除该品牌?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        brandApi.deleteById(id).then(res => {
          this.$message.success(res.msg)
          this.getByPage()
        })
      })
    },
    // 关闭弹窗
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
