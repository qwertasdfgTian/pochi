<template>
  <div>
    <!-- 搜索框 -->
    <div class="search-form">
      <el-form :model="page.params" :inline="true" size="small">
        <el-form-item>
          <el-input v-model="page.params.name" clearable placeholder="商品名称" />
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
          <el-button type="success" icon="el-icon-check" @click="handleSelect">确认添加</el-button>
        </el-form-item>
      </el-form>
    </div>
    <!-- 搜索框结束 -->
    <div class="data-table">
      <el-table
        ref="selectTable"
        v-loading="loading"
        header-row-class-name="pochi-table-header"
        :data="dataPage.list"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column
          type="selection"
          width="55"
        />
        <el-table-column prop="name" label="标题" />
        <el-table-column prop="price" label="价格" />
        <el-table-column prop="stock" label="库存" />
        <el-table-column prop="sale" label="销量" />
        <el-table-column prop="category.name" label="分类" />
        <el-table-column prop="brandName" label="品牌" />
        <el-table-column label="标签">
          <template slot-scope="{row}">
            <div class="switch-group">
              <div class="switch-item">
                <span>上架：</span>
                <span>
                  <el-tag v-if="row.publishStatus===1" type="success">是</el-tag>
                  <el-tag v-else type="info">否</el-tag>
                </span>
              </div>
              <div class="switch-item">
                <span>新品：</span>
                <span>
                  <el-tag v-if="row.newStatus===1" type="success">是</el-tag>
                  <el-tag v-else type="info">否</el-tag>
                </span>
              </div>
              <div class="switch-item">
                <span>推荐：</span>
                <span>
                  <el-tag v-if="row.recommendStatus===1" type="success">是</el-tag>
                  <el-tag v-else type="info">否</el-tag>
                </span>
              </div>
            </div>
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
  </div>
</template>

<script>
import brandApi from '@/api/shop/shop-brand'
import productApi from '@/api/shop/shop-product'
export default {
  props: {
    // 默认选中
    defaultSelect: {
      type: Array,
      default: null
    }
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
      dataPage: {
        // 表格数据
        list: []
      },
      // 当前选中行的数据
      selectVal: [],
      loading: false
    }
  },
  watch: {
    defaultSelect: {
      immediate: true,
      handler: function(newVal, oldVal) {
        this.setSelect()
      }
    }
  },
  created() {
    this.getByPage()
  },
  methods: {
    // 选中
    handleSelect() {
      if (this.selectVal && this.selectVal.length > 0) {
        const arr = []
        this.selectVal.forEach(e => {
          if (e) {
            arr.push(e)
          }
        })
        console.log(arr)
        this.$emit('selectSubmit', arr)
      } else {
        this.$emit('selectSubmit', [])
      }
    },
    // 回显选中状态
    setSelect() {
      // 取消选择状态
      this.$nextTick(() => {
        this.$refs.selectTable.clearSelection()
        if (this.defaultSelect && this.defaultSelect[0]) {
          this.defaultSelect.forEach(e => {
          // 找到e在dataPage.list中的位置
            const index = this.dataPage.list.findIndex(item => item.id === e.id)
            // 设置回显，第二个参数如果不设置，可能会导致数据被取消选中
            this.$refs.selectTable.toggleRowSelection(this.dataPage.list[index], true)
          })
        }
      })
    },
    // 选中触发
    handleSelectionChange(val) {
      this.selectVal = val
    },
    // 查询品牌
    getBrandList(name) {
      brandApi.getByName(name).then((res) => {
        this.brandList = res.data
      })
    },
    // 搜索
    search() {
      this.page.pageNumber = 1
      this.getByPage()
    },
    // 分页查询
    getByPage() {
      this.loading = true
      productApi.getByPageHasNotPack(this.page).then(res => {
        this.dataPage = res.data
        this.setSelect()
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
    }
  }
}
</script>

<style>
</style>
