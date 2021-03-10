<template>
  <div>
    <!-- 添加按钮开始 -->
    <div class="button-group">
      <el-button type="primary" size="small" icon="el-icon-plus" @click="toAdd">添加</el-button>
    </div>
    <!-- 添加按钮结束 -->
    <!-- 数据表格 -->
    <div class="data-table">
      <el-table
        header-row-class-name="pochi-table-header"
        :data="treeList"
        stripe
        row-key="id"
        style="width: 100%"
      >
        <el-table-column prop="id" label="编号" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="level" label="级别" />
        <el-table-column prop="navStatus" label="导航栏显示">
          <template slot-scope="{row}">
            <el-tag v-if="row.navStatus === 1" type="success">是</el-tag>
            <el-tag v-else type="info">否</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" />
        <el-table-column prop="icon" label="图标">
          <template slot-scope="{row}">
            <el-image
              style="width: 50px; height: 50px"
              :src="row.icon"
              fit="fill"
              :preview-src-list="[row.icon]"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160px">
          <template slot-scope="{row}">
            <el-button type="text" icon="el-icon-edit" @click="toUpdate(row.id)">修改</el-button>
            <el-button type="text" icon="el-icon-delete" @click="toDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <!-- 数据表格结束 -->
    <!-- 添加弹窗开始 -->
    <el-dialog
      v-if="addDialog"
      title="添加分类"
      :visible.sync="addDialog"
      width="45%"
    >
      <product-category-add @after="getTree" @close="closeDialog" />
    </el-dialog>
    <!-- 添加弹窗结束 -->
    <!-- 修改弹窗开始 -->
    <el-dialog
      v-if="updateDialog"
      title="修改分类"
      :visible.sync="updateDialog"
      width="45%"
    >
      <product-category-update :active-id="activeId" @after="getTree" @close="closeDialog" />
    </el-dialog>
    <!-- 修改窗结束 -->
  </div>
</template>

<script>
import categoryApi from '@/api/shop/shop-product-category'
import productCategoryAdd from './product-category-add'
import productCategoryUpdate from './product-category-update'
export default {
  components: {
    productCategoryAdd,
    productCategoryUpdate
  },
  data() {
    return {
      // 树形列表
      treeList: [],
      // 控制添加弹窗展示
      addDialog: false,
      // 控制修改弹窗展示
      updateDialog: false,
      // 当前点击的分类ID
      activeId: null
    }
  },
  created() {
    this.getTree()
  },
  methods: {
    // 添加
    toAdd() {
      this.addDialog = true
    },
    // 修改
    toUpdate(id) {
      this.activeId = id
      this.updateDialog = true
    },
    // 关闭弹窗
    closeDialog() {
      this.addDialog = false
      this.updateDialog = false
    },
    // 删除
    toDelete(id) {
      this.$confirm('是否删除该分类?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        categoryApi.deleteById(id).then(res => {
          this.$message.success(res.msg)
          this.getTree()
        })
      })
    },
    // 树形查询
    getTree() {
      categoryApi.getTree().then(res => {
        this.treeList = res.data
      })
    }
  }
}
</script>

<style>
</style>
