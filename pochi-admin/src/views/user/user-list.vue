<template>
  <div>
    <!-- 搜索栏开始 -->
    <div class="search-form">
      <el-form :inline="true" :model="page.params" size="small">
        <el-form-item>
          <el-input v-model="page.params.phone" clearable placeholder="手机号" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="page.params.nickname" clearable placeholder="昵称" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="page.params.status" placeholder="状态" clearable>
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
    <!-- 数据表格开始 -->
    <el-table
      v-loading="loading"
      header-row-class-name="pochi-table-header"
      :data="dataPage.list"
      stripe
      style="width: 100%"
    >
      <el-table-column prop="phone" label="手机号" width="120px" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="header" label="头像" width="70px">
        <template slot-scope="{row}">
          <el-image
            style="width: 50px; height: 50px"
            :src="row.header"
            fit="fill"
            :preview-src-list="[row.header]"
          />
        </template>
      </el-table-column>
      <el-table-column prop="gender" label="性别" width="70px">
        <template slot-scope="{row}">
          {{ row.gender===1?'男':'女' }}
        </template>
      </el-table-column>
      <el-table-column prop="point" label="积分" width="120px" />
      <el-table-column prop="historyPoint" label="历史积分" width="120px" />
      <el-table-column prop="status" label="启用状态" width="100px">
        <template slot-scope="{row}">
          <el-switch
            v-model="row.status"
            :active-value="1"
            :inactive-value="0"
            @change="changeStatus(row)"
          />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180px" />
      <el-table-column prop="updateTime" label="修改时间" width="180px" />
      <el-table-column prop="note" label="个性签名" />
      <el-table-column label="操作" width="170px">
        <template slot-scope="{row}">
          <el-button type="text" icon="el-icon-document" @click="toInfo(row.id)">详情</el-button>
          <el-dropdown class="handle-button">
            <span class="el-dropdown-link">
              操作<i class="el-icon-arrow-down el-icon--right" />
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item @click.native="deleteById(row.id)">
                <el-button type="text" icon="el-icon-delete">删除</el-button>
              </el-dropdown-item>
              <el-dropdown-item @click.native="toUserOrder(row.phone)">
                <el-button type="text" icon="el-icon-delete">查看订单</el-button>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>
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
      v-if="infoDialog"
      title="用户详情"
      :visible.sync="infoDialog"
      width="45%"
    >
      <user-info :active-id="activeId" />
    </el-dialog>
    <!-- 详情弹窗结束 -->
    <!-- 详情弹窗 -->
    <el-dialog
      v-if="orderDialog"
      title="用户订单"
      :visible.sync="orderDialog"
      width="85%"
    >
      <order-list :user-id="activePhone" />
    </el-dialog>
    <!-- 详情弹窗结束 -->
  </div>
</template>

<script>
import userApi from '@/api/system/shop-user'
import UserInfo from './user-info'
import orderList from '@/views/order/order/order-list'
export default {
  components: {
    UserInfo,
    orderList
  },
  data() {
    return {
      // 搜索分页对象
      page: {
        // 当前页
        pageNumber: 1,
        // 每页条数
        pageSize: 20,
        // 查询参数
        params: {}
      },
      // 数据分页对象
      dataPage: {},
      // 当前点击ID
      activeId: null,
      // 控制详情弹窗展示
      infoDialog: false,
      // 控制订单弹窗展示
      orderDialog: false,
      // 当前点击的手机号
      activePhone: null,
      // 遮罩
      loading: false
    }
  },
  created() {
    this.getByPage()
  },
  methods: {
    // 删除
    deleteById(id) {
      this.$confirm('是否删除该会员用户?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        userApi.deleteById(id).then(res => {
          this.$message.success(res.msg)
          this.getByPage()
        })
      })
    },
    // 重置
    Reset() {
      this.page.pageNumber = 1
      this.page.params = {}
      this.getByPage()
    },
    // 查看用户订单
    toUserOrder(phone) {
      this.activePhone = phone
      this.orderDialog = true
    },
    // 改变启用状态
    changeStatus(item) {
      const status = item.status
      if (status === 0) {
        // 禁用
        this.$confirm('是否禁用该用户，禁用后将无法登陆?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          userApi.disableById(item.id).then(res => {
            this.$message.success(res.msg)
            this.getByPage()
          })
        }).catch(() => {
          item.status = 1
        })
      } else {
        // 启用
        this.$confirm('是否启用该用户?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'success'
        }).then(() => {
          userApi.enableById(item.id).then(res => {
            this.$message.success(res.msg)
            this.getByPage()
          })
        }).catch(() => {
          item.status = 0
        })
      }
    },
    // 打开详情弹窗
    toInfo(id) {
      this.activeId = id
      this.infoDialog = true
    },
    // 当前页发生改变触发
    handleCurrentChange(val) {
      this.page.pageNumber = val
      this.getByPage()
    },
    // 每页条数发生改变
    handleSizeChange(val) {
      this.page.pageSize = val
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
      if (this.page.params.status === '') {
        this.page.params.status = null
      }
      userApi.getByPage(this.page).then(res => {
        this.dataPage = res.data
      })
      this.loading = false
    }
  }
}
</script>

<style>
</style>
