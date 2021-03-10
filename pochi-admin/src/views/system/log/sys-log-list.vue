<template>
  <div>
    <!-- 搜索表单 -->
    <div class="search-form">
      <el-form :model="searchForm" :inline="true" size="small">
        <el-form-item>
          <el-input v-model="searchForm.logUrl" clearable placeholder="请求地址" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="searchForm.logController" clearable placeholder="控制层" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="searchForm.logStatus" placeholder="选择状态" clearable>
            <el-option label="成功" value="1" />
            <el-option label="失败" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-date-picker
            v-model="searchForm.createdDate"
            type="datetimerange"
            range-separator="至"
            value-format="yyyy-MM-dd HH:mm:ss"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="search">查询</el-button>
          <el-button type="warning" icon="el-icon-refresh" @click="Reset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <!-- 搜索表单结束 -->
    <!-- 数据表格开始 -->
    <div class="data-table">
      <el-table
        v-loading="loading"
        header-row-class-name="pochi-table-header"
        :data="dataPage.list"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="logUrl" label="请求地址" />
        <el-table-column prop="logStatus" label="状态" width="80px">
          <template slot-scope="{row}">
            <el-tag v-if="row.logStatus === 1" type="success">成功</el-tag>
            <el-tag v-else type="danger">失败</el-tag>

          </template>
        </el-table-column>
        <el-table-column prop="logUa" label="浏览器UA标识" show-overflow-tooltip />
        <el-table-column prop="logController" label="控制层" />
        <el-table-column prop="logMethod" label="请求方式" width="80px" />
        <el-table-column prop="logTime" label="响应时间" width="80px" />
        <el-table-column prop="logIp" label="请求IP" width="120px" />
        <el-table-column prop="createdDate" label="创建时间" width="160px" />
        <el-table-column prop="createdBy" label="创建人" width="150px" />
        <el-table-column label="操作" width="150px">
          <template slot-scope="{row}">
            <el-button type="text" icon="el-icon-document" @click="toInfo(row.logId)">详情</el-button>
            <el-button type="text" icon="el-icon-delete" @click="toDelete(row.logId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <!-- 数据表格结束 -->
    <!-- 分页组件开始 -->
    <div class="pageable">
      <el-pagination
        :current-page="searchForm.pageNumber"
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
      title="日志详情"
      :visible.sync="infoDialog"
      width="40%"
    >
      <sys-log-info :active-id="activeId" />
    </el-dialog>

    <!-- 详情弹窗结束 -->
  </div>
</template>

<script>
import sysLogApi from '@/api/system/sys-log'
import sysLogInfo from './sys-log-info'
import SysLogInfo from './sys-log-info.vue'
export default {
  components: {
    sysLogInfo
  },
  data() {
    SysLogInfo
    return {
      // 查询表单
      searchForm: {
        // 当前页
        pageNumber: 1,
        // 每页条数
        pageSize: 10
      },
      // 数据分页对象
      dataPage: {},
      // 当前点击的日志ID
      activeId: '',
      // 控制详情页是否展示
      infoDialog: false,
      // 遮罩
      loading: false
    }
  },
  created() {
    this.getByPage()
  },
  methods: {
    Reset() {
      this.searchForm = { pageNumber: 1 }
      this.getByPage()
    },
    // 分页查询
    getByPage() {
      this.loading = true
      sysLogApi.getByPage(this.searchForm).then(res => {
        this.dataPage = res.data
      })
      this.loading = false
    },
    // 每页条数发生改变
    handleSizeChange(val) {
      this.searchForm.pageSize = val
      this.getByPage()
    },
    // 当前页发生改变
    handleCurrentChange(val) {
      this.searchForm.pageNumber = val
      this.getByPage()
    },
    // 搜索
    search() {
      this.searchForm.pageNumber = 1
      this.getByPage()
    },
    // 显示日志详情
    toInfo(id) {
      this.activeId = id
      this.infoDialog = true
    },
    // 删除日志
    toDelete(id) {
      this.$confirm('是否删除该日志?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        sysLogApi.deleteById(id).then(res => {
          this.$message.success(res.msg)
          this.getByPage()
        })
      })
    }
  }
}
</script>

<style>
</style>
