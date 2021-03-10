<template>
  <div>
    <!-- 搜索表单 -->
    <div class="search-form">
      <el-form ref="form" :model="page.params" label-width="80px" :inline="true" size="small">
        <el-form-item>
          <el-input v-model="page.params.roleName" placeholder="角色名" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="search">搜索</el-button>
          <el-button type="warning" icon="el-icon-refresh" @click="Reset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <!-- 搜索表单结束 -->
    <!-- 按钮组 -->
    <div class="button-group">
      <el-button type="primary" size="small" icon="el-icon-plus" @click="toAdd">添加</el-button>
    </div>
    <!-- 按钮组结束 -->

    <!-- 表格开始 -->
    <div class="data-table">
      <el-table
        v-loading="loading"
        header-row-class-name="pochi-table-header"
        :data="dataPage.list"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="roleId" label="角色编号" />
        <el-table-column prop="roleName" label="角色名" />
        <el-table-column prop="roleSort" label="排序" />
        <el-table-column prop="createBy" label="创建人" />
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column prop="updateBy" label="更新人" />
        <el-table-column prop="updateTime" label="更新时间" />
        <el-table-column label="操作">
          <template slot-scope="{row}">
            <el-dropdown class="handle-button">
              <span class="el-dropdown-link">
                操作<i class="el-icon-arrow-down el-icon--right" />
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item>
                  <el-button type="text" icon="el-icon-edit" @click="toUpdate(row.roleId)">修改</el-button>
                </el-dropdown-item>
                <el-dropdown-item @click.native="deleteById(row.roleId)">
                  <el-button type="text" icon="el-icon-delete">删除</el-button>
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <!-- 表格结束 -->

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
    <el-dialog v-if="addDialog" title="添加角色" :visible.sync="addDialog" width="30%">
      <sysRoleAdd @after="getByPage" @close="closeDialog" />
    </el-dialog>
    <!-- 添加弹窗结束 -->
    <!-- 修改弹窗 -->
    <el-dialog v-if="updateDialog" title="修改角色" :visible.sync="updateDialog" width="30%">
      <sysRoleUpdate :active-id="activeId" @after="getByPage" @close="closeDialog" />
    </el-dialog>
    <!-- 修改弹窗结束 -->
  </div>
</template>

<script>
import sysRoleAdd from './sys-role-add'
import sysRoleUpdate from './sys-role-update'
import sysRoleApi from '@/api/system/sys-role'
export default {
  components: {
    sysRoleAdd,
    sysRoleUpdate
  },
  data() {
    return {
      // 分页对象
      page: {
        // 搜索条件
        params: {},
        // 当前页
        pageNumber: 1,
        // 每页条数
        pageSize: 10
      },
      // 当前点击的角色ID
      activeId: '',
      // 数据显示分页对象
      dataPage: {},
      // 控制添加弹窗展示
      addDialog: false,
      // 控制修改弹窗展示
      updateDialog: false,
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
      // 页数回归第一页
      this.page.pageNumber = 1
      this.getByPage()
    },
    // 重置
    Reset() {
      this.page.pageNumber = 1
      this.page.params = {}
      this.getByPage()
    },
    // 打开添加弹窗
    toAdd() {
      this.addDialog = true
    },
    // 打开修改弹窗
    toUpdate(id) {
      this.activeId = id
      this.updateDialog = true
    },
    // 根据id删除
    deleteById(id) {
      this.$confirm('是否删除该角色?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        sysRoleApi.deleteById(id).then(res => {
          this.$message.success(res.msg)
          this.getByPage()
        })
      })
    },
    // 分页查询
    getByPage() {
      this.loading = true
      sysRoleApi.getByPage(this.page).then(res => {
        this.dataPage = res.data
      })
      this.loading = false
    },
    // 每页条数发生改变
    handleSizeChange(val) {
      this.page.pageSize = val
      this.getByPage()
    },
    // 关闭弹窗
    closeDialog() {
      this.addDialog = false
      this.updateDialog = false
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
