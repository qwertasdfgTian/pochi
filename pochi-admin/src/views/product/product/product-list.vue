<template>
  <div>
    <!-- 搜索框 -->
    <div class="search-form">
      <el-form :model="page.params" :inline="true" size="small">
        <el-form-item>
          <el-input v-model="page.params.name" clearable placeholder="商品名称" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="page.params.createBy" clearable placeholder="创建人" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="page.params.publishStatus" placeholder="上架" clearable>
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="page.params.newStatus" placeholder="新品" clearable>
            <el-option label="是" :value="1" />
            <el-option label="否" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="page.params.recommendStatus" placeholder="推荐" clearable>
            <el-option label="是" :value="1" />
            <el-option label="否" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select
            v-model="page.params.brandId"
            filterable
            remote
            :remote-method="getBrandList"
            placeholder="品牌"
            clearable
          >
            <el-option
              v-for="item in brandList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="search">查询</el-button>
          <el-button type="warning" icon="el-icon-refresh" @click="Reset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <!-- 搜索框结束 -->
    <!-- 操作按钮组 -->
    <div class="button-group">
      <el-button type="primary" size="small" icon="el-icon-plus" @click="toAdd">添加</el-button>
    </div>
    <!-- 操作按钮组结束 -->
    <div class="data-table">
      <el-table
        v-loading="loading"
        header-row-class-name="pochi-table-header"
        :data="dataPage.list"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="name" label="标题" width="200px" />
        <el-table-column prop="pic" align="center" width="170px" label="图片">
          <template slot-scope="{row}">
            <el-image :src="row.pic" style="width: 170px; height: 150px" fit="fill" :preview-src-list="[row.pic]" />
          </template>
        </el-table-column>
        <el-table-column prop="albumPicList" align="center" label="轮播图" width="170px">
          <template slot-scope="{row}">
            <div class="carousel">
              <el-carousel height="150px" indicator-position="none">
                <el-carousel-item v-for="item in row.albumPicList" :key="item">
                  <el-image style="width: 170px; height: 150px" :src="item" fit="fill" />
                </el-carousel-item>
              </el-carousel>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="80px" />
        <el-table-column prop="stock" label="库存" width="80px" />
        <el-table-column prop="lowStock" label="预警库存" width="100px" />
        <el-table-column prop="sale" label="销量" width="80px" />
        <el-table-column prop="commentNum" label="评论数" width="100px" />
        <el-table-column prop="point" label="购买积分" width="100px" />
        <el-table-column prop="category.name" label="分类" width="120px" />
        <el-table-column prop="brandName" label="品牌" width="120px" />
        <el-table-column prop="transFee" label="运费" width="100px" />
        <el-table-column prop="specs" label="规格" width="150px" />
        <el-table-column label="标签" width="120px">
          <template slot-scope="{row}">
            <div class="switch-group">
              <div class="switch-item">
                <span>上架：</span>
                <el-switch
                  v-model="row.publishStatus"
                  :active-value="1"
                  :inactive-value="0"
                  @change="changePublish(row)"
                />
              </div>
              <div class="switch-item">
                <span>新品：</span>
                <el-switch
                  v-model="row.newStatus"
                  :active-value="1"
                  :inactive-value="0"
                  @change="changeNews(row)"
                />
              </div>
              <div class="switch-item">
                <span>推荐：</span>
                <el-switch
                  v-model="row.recommendStatus"
                  :active-value="1"
                  :inactive-value="0"
                  @change="changeRecommend(row)"
                /></div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" />
        <el-table-column prop="createTime" label="创建时间" width="160px" />
        <el-table-column prop="createBy" label="创建人" width="120px" />
        <el-table-column prop="updateTime" label="修改时间" width="160px" />
        <el-table-column prop="updateBy" label="修改人" width="120px" />
        <el-table-column label="操作" fixed="right" width="160px">
          <template slot-scope="{row}">
            <el-button type="text" icon="el-icon-edit" @click="toUpdate(row.id)">修改</el-button>
            <el-button type="text" icon="el-icon-search" @click="getComment(row.id)">查看评论</el-button>
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
    <!-- 评论列表弹窗 -->
    <el-dialog
      title="商品评论"
      :visible.sync="commentDialog"
      width="80%"
    >
      <comment-list :product-id="activeId" />
    </el-dialog>
    <!-- 评论列表弹窗结束 -->
  </div>
</template>

<script>
import brandApi from '@/api/shop/shop-brand'
import productApi from '@/api/shop/shop-product'
import commentList from '@/views/order/comment/comment-list'
export default {
  components: {
    commentList
  },
  data() {
    return {
      // 分页对象
      page: {
        // 搜索对象
        params: {},
        // 当前页
        pageNumber: 1,
        // 每页条数
        pageSize: 10
      },
      // 品牌列表
      brandList: [],
      // 数据分页对象
      dataPage: {},
      loading: false,
      // 控制评论列表展示
      commentDialog: false,
      // 当前点击的商品ID
      activeId: null
    }
  },
  created() {
    this.getByPage()
  },
  methods: {
    // 查看评论
    getComment(id) {
      this.activeId = id
      this.commentDialog = true
    },
    // 重置
    Reset() {
      this.page.pageNumber = 1
      this.page.params = {}
      this.getByPage()
    },
    // 查询品牌
    getBrandList(name) {
      brandApi.getByName(name).then((res) => {
        this.brandList = res.data
      })
    },
    // 上架状态发生改变触发
    changePublish(item) {
      const status = item.publishStatus
      // 从1变成0
      if (status === 0) {
        this.$confirm('是否下架该商品?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          productApi.unPublish(item.id).then(res => {
            this.$message.success(res.msg)
          })
        }).catch(() => {
          // 还原回1
          item.publishStatus = 1
        })
      } else {
        // 从0 变成1
        this.$confirm('是否上架该商品?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'success'
        }).then(() => {
          productApi.publish(item.id).then(res => {
            this.$message.success(res.msg)
          })
        }).catch(() => {
          // 还原回0
          item.publishStatus = 0
        })
      }
    },
    // 新品
    changeNews(item) {
      const status = item.newStatus
      // 从1变成0
      if (status === 0) {
        this.$confirm('是否取消新品?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          productApi.old(item.id).then(res => {
            this.$message.success(res.msg)
          })
        }).catch(() => {
          // 还原回1
          item.newStatus = 1
        })
      } else {
        // 从0 变成1
        this.$confirm('是否把该商品变为新品?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'success'
        }).then(() => {
          productApi.news(item.id).then(res => {
            this.$message.success(res.msg)
          })
        }).catch(() => {
          // 还原回0
          item.newStatus = 0
        })
      }
    },
    // 是否推荐
    changeRecommend(item) {
      const status = item.recommendStatus
      // 从1变成0
      if (status === 0) {
        this.$confirm('是否取消推荐品?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          productApi.unRecommend(item.id).then(res => {
            this.$message.success(res.msg)
          })
        }).catch(() => {
          // 还原回1
          item.recommendStatus = 1
        })
      } else {
        // 从0 变成1
        this.$confirm('是否把该商品变为推荐品?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'success'
        }).then(() => {
          productApi.recommend(item.id).then(res => {
            this.$message.success(res.msg)
          })
        }).catch(() => {
          // 还原回0
          item.recommendStatus = 0
        })
      }
    },
    // 搜索
    search() {
      this.page.pageNumber = 1
      this.getByPage()
    },
    // 跳转到添加页
    toAdd() {
      this.$router.push('/product/add')
    },
    // 分页查询
    getByPage() {
      this.loading = true
      productApi.getByPage(this.page).then(res => {
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
    // 跳转到修改
    toUpdate(id) {
      this.$router.push('/product/update/' + id)
    },
    // 删除
    toDelete(id) {
      this.$confirm('是否删除该菜单?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        productApi.deleteById(id).then(res => {
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
