<template>
  <div>
    <el-form ref="form" :model="shopPack" :rules="rules" label-width="80px" size="small">
      <el-form-item label="套装名称" prop="name">
        <el-input v-model="shopPack.name" clearable placeholder="套装名称" />
      </el-form-item>
      <el-form-item label="套装商品">
        <el-button type="primary" @click="selectProduct">选择商品</el-button>
      </el-form-item>
      <el-form-item>
        <el-table
          header-row-class-name="pochi-table-header"
          :data="shopPack.productList"
          stripe
          border
          style="width: 100%"
        >
          <el-table-column prop="name" label="名称" />
          <el-table-column prop="price" label="价格" />
          <el-table-column prop="stock" label="库存" />
          <el-table-column prop="brandName" label="品牌" />
          <el-table-column label="操作">
            <template slot-scope="{row}">
              <el-button type="text" icon="el-icon-delete" @click="deleteProduct(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="add">添加</el-button>
      </el-form-item>
    </el-form>
    <!-- 商品选择弹窗 -->
    <el-dialog
      append-to-body
      title="选择商品"
      :visible.sync="selectDialog"
      width="70%"
    >
      <select-product :default-select="shopPack.productList" @selectSubmit="selectSubmit" />
    </el-dialog>

    <!-- 商品选择弹窗结束 -->
  </div>
</template>

<script>
import selectProduct from '@/components/SelectProduct'
import packApi from '@/api/shop/shop-pack'
export default {
  components: {
    selectProduct
  },
  data() {
    return {
      // 表单对象
      shopPack: {
        // 用户选中的商品列表
        productList: []
      },
      // 商品选择弹窗
      selectDialog: false,
      // 表单校验对象
      rules: {
        name: [
          { required: true, message: '请输入套装名称', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    // 添加
    add() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          packApi.save(this.shopPack).then(res => {
            this.$message.success(res.msg)
            this.$emit('close')
            this.$emit('after')
          })
        }
      })
    },
    // 选择商品
    selectProduct() {
      this.selectDialog = true
    },
    // 提交选中商品触发
    selectSubmit(val) {
      this.selectDialog = false
      this.shopPack.productList = val
    },
    // 删除选中的商品
    deleteProduct(id) {
      this.shopPack.productList.splice(
        this.shopPack.productList.findIndex(e => e.id === id),
        1
      )
    }
  }
}
</script>

<style>
</style>
