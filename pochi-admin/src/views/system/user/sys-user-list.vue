<template>
  <div>
    <!-- 搜索栏开始 -->
    <div class="search-form">
      <el-form :inline="true" :model="page.params" size="small">
        <el-form-item>
          <el-input v-model="page.params.username" clearable placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="page.params.nickName" clearable placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="page.params.status" clearable placeholder="请选择启用状态">
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
    <!-- 搜索栏结束 -->
    <!-- 操作按钮组 -->
    <div v-permission="['sys:user:add']" class="button-group">
      <el-button type="primary" size="small" icon="el-icon-plus" @click="toAdd">添加</el-button>
    </div>
    <!-- 操作按钮组结束 -->
    <!-- 数据表格开始 -->
    <div class="data-table">
      <el-table v-loading="loading" header-row-class-name="pochi-table-header" :data="dataPage.list" stripe style="width: 100%">
        <el-table-column prop="username" label="账号" />
        <el-table-column prop="nickName" label="昵称" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="header" label="头像">
          <template slot-scope="{row}">
            <el-image
              style="width: 60px; height: 60px"
              :src="row.header"
              fit="fill"
              :preview-src-list="[row.header]"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" show-overflow-tooltip />
        <el-table-column prop="updateTime" label="更新时间" show-overflow-tooltip />
        <el-table-column prop="loginTime" label="登录时间" show-overflow-tooltip />
        <el-table-column prop="status" label="启用状态">
          <template slot-scope="{row}">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="changeStatus(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="note" label="备注" show-overflow-tooltip />
        <el-table-column label="操作">
          <template slot-scope="{row}">
            <el-button type="text" icon="el-icon-document" @click="toInfo(row.id)">详情</el-button>
            <el-dropdown class="handle-button">
              <span class="el-dropdown-link">
                操作<i class="el-icon-arrow-down el-icon--right" />
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item v-permission="['sys:user:update']">
                  <el-button type="text" icon="el-icon-edit" @click="toUpdate(row.id)">修改</el-button>
                </el-dropdown-item>
                <el-dropdown-item v-permission="['sys:user:delete']" @click.native="deleteById(row.id)">
                  <el-button type="text" icon="el-icon-delete">删除</el-button>
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
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
    <!-- 添加用户弹窗 -->
    <el-dialog
      v-if="addDialog"
      title="添加用户"
      :visible.sync="addDialog"
      width="45%"
    >
      <sysUserAdd @after="getByPage" @close="closeDialog" />
    </el-dialog>
    <!-- 添加用户弹窗结束 -->
    <!-- 修改弹窗 -->
    <el-dialog
      v-if="updateDialog"
      title="修改用户"
      :visible.sync="updateDialog"
      width="45%"
    >
      <sysUserUpdate :active-id="activeId" @after="getByPage" @close="closeDialog" />
    </el-dialog>
    <!-- 修改弹窗结束 -->
    <!-- 详情弹窗开始 -->
    <el-dialog
      v-if="infoDialog"
      title="用户详情"
      :visible.sync="infoDialog"
      width="40%"
    >
      <sysUserInfo :active-id="activeId" />
    </el-dialog>
    <!-- 详情弹窗结束 -->
  </div>
</template>

<script>
import sysUserApi from '@/api/system/sys-user'
import sysUserAdd from './sys-user-add'
import sysUserUpdate from './sys-user-update'
import sysUserInfo from './sys-user-info'
export default {
  components: {
    sysUserAdd,
    sysUserUpdate,
    sysUserInfo
  },
  data() {
    return {
      // 分页对象
      page: {
        // 分页传参
        params: {},
        pageNumber: 1,
        pageSize: 10
      },
      // 当前点击的用户
      activeId: '',
      // 控制添加用户弹窗展示
      addDialog: false,
      // 控制修改用户弹窗展示
      updateDialog: false,
      // 控制用户详情弹窗展示
      infoDialog: false,
      // 数据 page 对象
      dataPage: {},
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
      // 搜索时，要还原为第一页
      this.page.pageNumber = 1
      this.getByPage()
    },
    // 重置
    Reset() {
      this.page.pageNumber = 1
      this.page.params = {}
      this.getByPage()
    },
    // 每页条数变更时触发
    handleSizeChange(val) {
      this.page.pageSize = val
      this.getByPage()
    },
    // 当前页变更时触发
    handleCurrentChange(val) {
      this.page.pageNumber = val
      this.getByPage()
    },
    // 弹出添加弹窗
    toAdd() {
      this.addDialog = true
    },
    // 打开修改弹窗
    toUpdate(id) {
      this.activeId = id
      this.updateDialog = true
    },
    // 查看用户详情
    toInfo(id) {
      this.activeId = id
      this.infoDialog = true
    },
    // 分页查询
    getByPage() {
      this.loading = true
      sysUserApi.getByPage(this.page).then(res => {
        this.dataPage = res.data
      })
      this.loading = false
    },
    // 关闭弹窗
    closeDialog() {
      this.addDialog = false
      this.updateDialog = false
      this.infoDialog = false
    },
    // 根据id删除
    deleteById(id) {
      this.$confirm('是否删除该用户?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        sysUserApi.deleteById(id).then(res => {
          this.$message.success(res.msg)
          this.getByPage()
        })
      })
    },
    // 改变页面开关触发
    changeStatus(row) {
      const status = row.status
      if (status === 0) {
        // 禁用
        this.$confirm('是否禁用该用户，禁用后将无法登陆?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          sysUserApi.disableById(row.id).then(res => {
            this.$message.success(res.msg)
            this.getByPage()
          })
        }).catch(() => {
          row.status = 1
        })
      } else {
        // 启用
        this.$confirm('是否启用该用户?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'success'
        }).then(() => {
          sysUserApi.enableById(row.id).then(res => {
            this.$message.success(res.msg)
            this.getByPage()
          })
        }).catch(() => {
          row.status = 0
        })
      }
    }
  }
}
</script>
