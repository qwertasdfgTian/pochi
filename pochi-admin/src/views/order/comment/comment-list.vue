<template>
  <div>
    <div class="data-table">
      <el-table
        v-loading="loading"
        header-row-class-name="pochi-table-header"
        :data="dataPage.list"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="productId" label="商品编号" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="nickName" label="评论人" />
        <el-table-column prop="memberIcon" label="用户头像">
          <template slot-scope="{row}">
            <el-image
              style="width: 50px; height: 50px"
              :src="row.memberIcon"
              fit="fill"
              :preview-src-list="[row.memberIcon]"
            />
          </template>
        </el-table-column>
        <el-table-column prop="orderId" label="订单编号" />
        <el-table-column prop="commentType" label="评论类型" />
        <el-table-column prop="star" label="星数">
          <template slot-scope="{row}">
            <el-rate v-model="row.star" disabled :colors="colors" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="评论时间" />
        <el-table-column label="操作" fixed="right" width="170px">
          <template slot-scope="{row}">
            <el-button type="text" icon="el-icon-document" @click="toInfo(row)">详情</el-button>
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
    <!-- 详情弹窗开始 -->
    <el-dialog
      title="评论详情"
      :visible.sync="infoDialog"
      append-to-body
      width="50%"
    >
      <comment-info :current-comment="currentComment" />
    </el-dialog>
    <!-- 详情弹窗结束 -->
  </div>
</template>

<script>
import commentApi from '@/api/shop/shop-order-comment'
import commentInfo from './comment-info'
export default {
  components: {
    commentInfo
  },
  props: {
    productId: {
      type: String,
      default: null
    },
    orderId: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      // 分页查询对象
      page: {
        // 当前页
        pageNumber: 1,
        // 每页条数
        pageSize: 10,
        // 查询参数
        params: {}
      },
      colors: ['#99A9BF', '#F7BA2A', '#FF9900'],
      // 数据分页对象
      dataPage: {},
      // 遮罩
      loading: false,
      // 详情弹窗展示
      infoDialog: false,
      // 当前点击的评论
      currentComment: null
    }
  },
  watch: {
    productId: {
      immediate: true,
      handler: function() {
        if (this.productId) {
          this.page.params.productId = this.productId
          this.getByPage()
        }
      }
    },
    orderId: {
      immediate: true,
      handler: function() {
        if (this.orderId) {
          this.page.params.orderId = this.orderId
          this.getByPage()
        }
      }
    }
  },
  methods: {
    toInfo(item) {
      this.infoDialog = true
      this.currentComment = item
    },
    // 分页查询
    getByPage() {
      this.loading = true
      commentApi.getByPage(this.page).then((res) => {
        this.dataPage = res.data
      })
      this.loading = false
    },
    // 当前页发生改变
    handleCurrentChange(val) {
      this.page.pageNumber = val
      this.getByPage()
    },
    // 每页条数发生改变
    handleSizeChange(val) {
      this.page.pageSize = val
      this.getByPage()
    }
  }
}
</script>

<style>
</style>
