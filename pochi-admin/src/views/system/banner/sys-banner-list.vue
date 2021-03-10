<template>
  <div>
    <!-- 搜索表单开始 -->
    <div class="search-form">
      <el-form ref="form" :model="page.params" :inline="true" size="small">
        <el-form-item>
          <el-select v-model="page.params.status" placeholder="启用状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
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
        style="width: 100%"
      >
        <el-table-column prop="name" label="名称" width="120px" />
        <el-table-column prop="pic" label="图片" width="230px" align="center">
          <template slot-scope="{row}">
            <el-image
              :src="row.pic"
              fit="fill"
            />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80px">
          <template slot-scope="{row}">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="changeStatus(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="url" label="链接地址" />
        <el-table-column prop="clickCount" label="点击数" width="70px" />
        <el-table-column prop="orderCount" label="下单数" width="70px" />
        <el-table-column prop="sort" label="排序" width="60px" />
        <el-table-column prop="createTime" label="创建时间" show-overflow-tooltip />
        <el-table-column prop="createBy" label="创建人" width="100px" />
        <el-table-column prop="updateTime" label="修改时间" show-overflow-tooltip />
        <el-table-column prop="updateBy" label="修改人" width="100px" />
        <el-table-column label="操作" width="160px">
          <template slot-scope="{row}">
            <el-button type="text" icon="el-icon-edit" @click="toUpdate(row.id)">修改</el-button>
            <el-button type="text" icon="el-icon-delete" @click="toDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
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
    <!-- 数据表格结束 -->
    <!-- 添加弹窗 -->
    <el-dialog
      v-if="addDialog"
      title="添加轮播图"
      :visible.sync="addDialog"
      width="30%"
    >
      <sys-banner-add @after="getByPage" @close="closeDialog" />
    </el-dialog>

    <!-- 添加弹窗结束 -->
    <!-- 修改弹窗 -->
    <el-dialog
      v-if="updateDialog"
      title="修改轮播图"
      :visible.sync="updateDialog"
      width="30%"
    >
      <sys-banner-update :active-id="activeId" @after="getByPage" @close="closeDialog" />
    </el-dialog>

    <!-- 修改弹窗结束 -->
  </div>
</template>

<script>
import sysBannerAPi from '@/api/system/sys-banner'
import sysBannerAdd from './sys-banner-add'
import sysBannerUpdate from './sys-banner-update'
export default {
  components: {
    sysBannerAdd,
    sysBannerUpdate
  },
  data() {
    return {
      // 查询分页对象
      page: {
        // 查询条件
        params: {},
        // 当前页
        pageNumber: 1,
        // 每页条数
        pageSize: 10
      },
      // 控制添加弹窗显示
      addDialog: false,
      // 控制修改弹窗显示
      updateDialog: false,
      // 数据分页对象
      dataPage: {},
      // 当前点击的ID
      activeId: '',
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
    // 每页条数发生改变触发
    handleSizeChange(val) {
      this.page.pageSize = val
      this.getByPage()
    },
    // 当前页发生改变时触发
    handleCurrentChange(val) {
      this.page.pageNumber = val
      this.getByPage()
    },
    // 添加
    toAdd() {
      this.addDialog = true
    },
    // 修改
    toUpdate(id) {
      this.activeId = id
      this.updateDialog = true
    },
    // 改变状态
    changeStatus(row) {
      if (row.status === 0) {
        this.$confirm('是否禁用该图片?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          sysBannerAPi.disableById(row.id).then(res => {
            this.$message.success(res.msg)
            this.getByPage()
          })
        }).catch(() => {
          row.status = 1
        })
      } else {
        this.$confirm('是否启用该图片?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'success'
        }).then(() => {
          sysBannerAPi.enableById(row.id).then(res => {
            this.$message.success(res.msg)
            this.getByPage()
          })
        }).catch(() => {
          row.status = 0
        })
      }
    },
    // 删除
    toDelete(id) {
      this.$confirm('是否删除该图片?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        sysBannerAPi.deleteById(id).then(res => {
          this.$message.success(res.msg)
          this.getByPage()
        })
      })
    },
    // 关闭弹窗
    closeDialog() {
      this.addDialog = false
      this.updateDialog = false
    },
    // 分页查询
    getByPage() {
      this.loading = true
      if (this.page.params.status === '') {
        this.page.params.status = null
      }
      sysBannerAPi.getByPage(this.page).then(res => {
        this.dataPage = res.data
      })
      this.loading = false
    }
  }
}
</script>

<style>
</style>
