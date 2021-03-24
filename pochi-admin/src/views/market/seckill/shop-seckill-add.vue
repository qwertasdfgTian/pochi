<template>
  <div>
    <el-form ref="form" :model="shopSecKill" :rules="rules" label-width="100px" size="small">
      <el-row :gutter="20">
        <el-col :span="24" :offset="0">
          <el-form-item label="活动名称" prop="name">
            <el-input v-model="shopSecKill.name" clearable placeholder="请输入名称" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="活动商品">
        <el-button type="primary" @click="selectProduct">选择秒杀商品</el-button>
      </el-form-item>
      <el-row :gutter="20">
        <el-col :span="24" :offset="0">
          <el-form-item label="商品图片">
            <el-image
              style="width: 60px; height: 60px"
              :src="shopSecKill.productPic"
              fit="fill"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24" :offset="0">
          <el-form-item label="商品名称" prop="productName">
            <el-input v-model="shopSecKill.productName" :readonly="true" :min="0" style="width: 100%" controls-position="right" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24" :offset="0">
          <el-form-item label="活动时间" prop="endTime">
            <el-date-picker
              v-model="timeRange"
              style="width: 100%"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始日期"
              value-format="yyyy-MM-dd HH:mm:ss"
              end-placeholder="结束日期"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12" :offset="0">
          <el-form-item label="价格设置" prop="productPrice">
            <el-input-number v-model="shopSecKill.productPrice" :min="0" style="width: 100%" controls-position="right" />
          </el-form-item>
        </el-col>
        <el-col :span="12" :offset="0">
          <el-form-item label="库存设置" prop="stock">
            <el-input-number v-model="shopSecKill.stock" :min="1" style="width: 100%" controls-position="right" clearable />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24" :offset="0">
          <el-form-item label="订单取消" prop="cancelTime">
            买家 <el-input-number v-model="shopSecKill.cancelTime" :min="5" style="width: 50%" controls-position="right" clearable placeholder="请设置订单取消时间" /> 分钟内未付款，自动取消订单
          </el-form-item>
        </el-col>
      </el-row>
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
      <chose-product :default-select="shopProduct" @selectProduct="selectProductSubmit" />
    </el-dialog>

    <!-- 商品选择弹窗结束 -->
  </div>
</template>

<script>
import choseProduct from '@/components/ChoseProduct'
import secKillApi from '@/api/market/shop-seckill'
export default {
  components: {
    choseProduct
  },
  data() {
    return {
      // 表单对象
      shopSecKill: {},
      // 有效期
      timeRange: [],
      shopProduct: {
        id: '',
        name: '',
        pic: '',
        price: null,
        brandName: '',
        categoryId: ''
      },
      // 商品选择弹窗
      selectDialog: false,
      // 表单校验对象
      rules: {
        name: [
          { required: true, message: '请输入活动名称', trigger: 'blur' }
        ],
        productName: [
          { required: true, message: '请选择商品', trigger: 'blur' }
        ],
        productPrice: [
          { required: true, message: '请输入价格', trigger: 'blur' }
        ],
        stock: [
          { required: true, message: '请输入库存', trigger: 'blur' }
        ],
        endTime: [
          { required: true, message: '请选择秒杀活动的日期范围', trigger: 'blur' }
        ],
        cancelTime: [
          { required: true, message: '请输入订单取消时间', trigger: 'blur' }
        ],
        quota: [
          { required: true, message: '请输入限额', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    timeRange: {
      immediate: true,
      deep: true,
      handler: function(newVal) {
        if (newVal && newVal[0]) {
          this.shopSecKill.beginTime = newVal[0]
          this.shopSecKill.endTime = newVal[1]
          console.log(this.shopSecKill.endTime)
        } else {
          this.shopSecKill.beginTime = null
          this.shopSecKill.endTime = null
        }
      }
    }
  },
  created() {
  },
  methods: {
    // 提交选中商品触发
    selectProductSubmit(val) {
      this.selectDialog = false
      this.shopProduct = val
      this.shopSecKill.productName = this.shopProduct.name
      this.shopSecKill.productPic = this.shopProduct.pic
      this.shopSecKill.productOldPrice = this.shopProduct.price
      this.shopSecKill.productId = this.shopProduct.id
      this.shopSecKill.brandName = this.shopProduct.brandName
      this.shopSecKill.categoryId = this.shopProduct.categoryId
    },
    // 选择商品
    selectProduct() {
      this.selectDialog = true
    },
    // 添加
    add() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          this.shopSecKill.cancelTime = this.shopSecKill.cancelTime + ''
          console.log(this.shopSecKill)
          secKillApi.save(this.shopSecKill).then(res => {
            this.$message.success(res.msg)
            this.$emit('after')
            this.$emit('close')
          })
        }
      })
    }
  }
}
</script>

<style>

</style>
