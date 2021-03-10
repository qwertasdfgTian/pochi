<template>
  <div>
    <!-- 搜索栏开始 -->
    <div class="search-form">
      <el-form :model="page.params" :inline="true" size="small">
        <el-form-item>
          <el-input v-model="page.params.noticeTitle" clearable placeholder="公告标题" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="page.params.enabled" placeholder="启用状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用 " :value="0" />
          </el-select>
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
    <!-- 数据表格开始 -->
    <div class="data-table">
      <el-table
        v-loading="loading"
        header-row-class-name="pochi-table-header"
        :data="dataPage.list"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="noticeTitle" label="通知标题" />
        <el-table-column prop="enabled" label="启用">
          <template slot-scope="{row}">
            <el-switch
              v-model="row.enabled"
              :active-value="1"
              :inactive-value="0"
              @change="changeEnabled(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createdBy" label="创建人" />
        <el-table-column prop="createdTime" label="创建时间" />
        <el-table-column prop="sorted" label="排序" />
        <el-table-column label="操作">
          <template slot-scope="{row}">
            <el-button type="text" icon="el-icon-document" @click="toInfo(row.noticeId)">详情</el-button>
            <el-dropdown class="handle-button">
              <span class="el-dropdown-link">
                操作<i class="el-icon-arrow-down el-icon--right" />
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item @click.native="toUpdate(row.noticeId)">
                  <el-button type="text" icon="el-icon-edit">修改</el-button>
                </el-dropdown-item>
                <el-dropdown-item @click.native="toDelete(row.noticeId)">
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
      title="添加公告"
      :visible.sync="addDialog"
      width="50%"
    >
      <sys-notice-add @after="getByPage" @close="closeDialog" />
    </el-dialog>
    <!-- 添加弹窗结束 -->
    <!-- 修改弹窗 -->
    <el-dialog
      v-if="updateDialog"
      title="修改公告"
      :visible.sync="updateDialog"
      width="50%"
    >
      <sys-notice-update :active-id="activeId" @after="getByPage" @close="closeDialog" />
    </el-dialog>
    <!-- 修改弹窗结束 -->
    <!-- 详情弹窗 -->
    <el-dialog
      v-if="infoDialog"
      title="公告详情"
      :visible.sync="infoDialog"
      width="50%"
    >
      <sys-notice-info :active-id="activeId" />
    </el-dialog>

    <!-- 详情弹窗结束 -->
  </div>
</template>

<script>
import sysNoticeApi from '@/api/system/sys-notice'
import sysNoticeAdd from './sys-notice-add'
import sysNoticeUpdate from './sys-notice-update'
import sysNoticeInfo from './sys-notice-info'
export default {
  components: {
    sysNoticeAdd,
    sysNoticeUpdate,
    sysNoticeInfo
  },
  data() {
    return {
      // 查询分页对象
      page: {
        // 传参
        params: {},
        // 每页条数
        pageSize: 10,
        // 当前页
        pageNumber: 1
      },
      // 控制添加弹窗展示
      addDialog: false,
      // 控制修改弹窗展示
      updateDialog: false,
      // 控制详情弹窗展示
      infoDialog: false,
      // 数据分页对象
      dataPage: {},
      // 当前点击的通知编号
      activeId: null,
      // 遮罩
      loading: false
    }
  },
  created() {
    this.getByPage()
  },
  methods: {
    // 搜索
    search() {
      this.pageNumber = 1
      this.getByPage()
    },
    // 重置
    Reset() {
      this.page.pageNumber = 1
      this.page.params = {}
      this.getByPage()
    },
    // 启用状态改变
    changeEnabled(row) {
      // 如果状态变成了1
      if (row.enabled === 1) {
        this.$confirm('是否启用?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'success'
        }).then(() => {
          sysNoticeApi.enableById(row.noticeId).then(res => {
            this.$message.success(res.msg)
            this.getByPage()
          })
        }).catch(() => {
        // 还原回禁用状态
          row.enabled = 0
        })
      } else {
        this.$confirm('是否禁用?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          sysNoticeApi.disableById(row.noticeId).then(res => {
            this.$message.success(res.msg)
            this.getByPage()
          })
        }).catch(() => {
        // 还原回启用状态
          row.enabled = 1
        })
      }
    },
    // 分页查询
    getByPage() {
      this.loading = true
      sysNoticeApi.getByPage(this.page).then(res => {
        this.dataPage = res.data
      })
      this.loading = false
    },
    // 打开添加弹窗
    toAdd() {
      this.addDialog = true
    },
    // 打开修改弹窗
    toUpdate(id) {
      this.updateDialog = true
      this.activeId = id
    },
    // 打开详情弹窗
    toInfo(id) {
      this.activeId = id
      this.infoDialog = true
    },
    // 删除
    toDelete(id) {
      this.$confirm('是否删除该通知?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        sysNoticeApi.deleteById(id).then(res => {
          this.$message.success(res.msg)
          this.getByPage()
        })
      })
    },
    // 每页条数改变时触发
    handleSizeChange(val) {
      this.page.pageSize = val
      this.getByPage()
    },
    // 当前页改变时触发
    handleCurrentChange(val) {
      this.page.pageNumber = val
      this.getByPage()
    },
    // 关闭所有弹窗
    closeDialog() {
      this.addDialog = false
      this.updateDialog = false
    }
  }
}
</script>

<style>
</style>
