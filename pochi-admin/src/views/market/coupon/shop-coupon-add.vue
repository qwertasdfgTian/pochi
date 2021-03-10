<template>
  <div>
    <el-form ref="form" :model="shopCoupon" :rules="rules" label-width="100px" size="small">
      <el-row :gutter="20">
        <el-col :span="12" :offset="0">
          <el-form-item label="优惠券名称" prop="name">
            <el-input v-model="shopCoupon.name" clearable placeholder="请输入名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12" :offset="0">
          <el-form-item label="面值" prop="amount">
            <el-input-number v-model="shopCoupon.amount" :min="0" style="width: 100%" controls-position="right" clearable placeholder="请输入面值" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12" :offset="0">
          <el-form-item label="发行量" prop="publishCount">
            <el-input-number v-model="shopCoupon.publishCount" :min="0" style="width: 100%" controls-position="right" clearable placeholder="请输入发行量" />
          </el-form-item>
        </el-col>
        <el-col :span="12" :offset="0">
          <el-form-item label="使用门槛" prop="minPoint">
            <el-input-number v-model="shopCoupon.minPoint" :min="0" style="width: 100%" controls-position="right" clearable placeholder="请输入使用门槛" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24" :offset="0">
          <el-form-item label="有效期" prop="endTime">
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
        <el-col :span="24" :offset="0">
          <el-form-item label="优惠券类型" prop="couponType">
            <el-radio-group v-model="shopCoupon.couponType">
              <el-radio-button :label="0">全场通用</el-radio-button>
              <el-radio-button :label="1">指定分类</el-radio-button>
              <el-radio-button :label="2">指定商品</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24" :offset="0">
          <!-- 指定分类 -->
          <div v-if="shopCoupon.couponType === 1" class="select-category">
            <el-form-item>
              <el-form label-width="100px" :inline="true" size="small">
                <el-form-item label="选择分类">
                  <el-cascader
                    v-model="currentCategory"
                    clearable
                    filterable
                    placeholder="选择分类"
                    style="width: 100%"
                    :options="categoryList"
                    :props="{ expandTrigger: 'hover', value: 'id', label: 'name' }"
                  />
                </el-form-item>
                <el-form-item>
                  <el-button type="success" icon="el-icon-check" @click="addCategory">确定添加分类</el-button>
                </el-form-item>
              </el-form>
              <el-table
                header-row-class-name="pochi-table-header"
                :data="shopCoupon.categoryList"
                stripe
                border
                style="width: 100%"
              >
                <el-table-column prop="name" label="分类名称" />
                <el-table-column label="操作">
                  <template slot-scope="{row}">
                    <el-button type="text" icon="el-icon-delete" @click="deleteCategory(row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-form-item>
          </div>
          <!-- 指定分类结束 -->
          <!-- 指定商品 -->
          <div v-if="shopCoupon.couponType === 2" class="select-product">
            <el-form label-width="100px" size="small">
              <el-form-item>
                <el-button type="primary" @click="selectProduct">选择商品</el-button>
              </el-form-item>
              <el-form-item>
                <el-table
                  header-row-class-name="pochi-table-header"
                  :data="shopCoupon.productList"
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
            </el-form>

          </div>
          <!-- 指定商品结束 -->
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
      <select-product :default-select="shopCoupon.productList" @selectSubmit="selectSubmit" />
    </el-dialog>

    <!-- 商品选择弹窗结束 -->
  </div>
</template>

<script>
import categoryApi from '@/api/shop/shop-product-category'
import selectProduct from '@/components/SelectProduct'
import couponApi from '@/api/market/shop-coupon'
export default {
  components: {
    selectProduct
  },
  data() {
    return {
      // 表单对象
      shopCoupon: {
        // 分类集合
        categoryList: [],
        // 商品集合
        productList: []
      },
      // 有效期
      timeRange: [],
      // 分类列表
      categoryList: [],
      // 当前选择的分类
      currentCategory: {},
      // 商品选择弹窗
      selectDialog: false,
      // 表单校验对象
      rules: {
        name: [
          { required: true, message: '请输入优惠券名称', trigger: 'blur' }
        ],
        amount: [
          { required: true, message: '请输入优惠券面值', trigger: 'blur' }
        ],
        publishCount: [
          { required: true, message: '请输入优惠券发行数量', trigger: 'blur' }
        ],
        minPoint: [
          { required: true, message: '请输入优惠券使用门槛', trigger: 'blur' }
        ],
        endTime: [
          { required: true, message: '请选择优惠券使用日期范围', trigger: 'blur' }
        ],
        couponType: [
          { required: true, message: '请选择优惠券类型', trigger: 'blur' }
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
          this.shopCoupon.startTime = newVal[0]
          this.shopCoupon.endTime = newVal[1]
        } else {
          this.shopCoupon.startTime = null
          this.shopCoupon.endTime = null
        }
      }
    }
  },
  created() {
    this.getCategoryTree()
  },
  methods: {
    // 查询分类树
    getCategoryTree() {
      categoryApi.getTree().then(res => {
        this.categoryList = res.data
      })
    },
    // 提交选中商品触发
    selectSubmit(val) {
      this.selectDialog = false
      this.shopCoupon.productList = val
    },
    // 选择商品
    selectProduct() {
      this.selectDialog = true
    },
    // 添加
    add() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          couponApi.save(this.shopCoupon).then(res => {
            this.$message.success(res.msg)
            this.$emit('after')
            this.$emit('close')
          })
        }
      })
    },
    // 删除选中的商品
    deleteProduct(id) {
      this.shopCoupon.productList.splice(
        this.shopCoupon.productList.findIndex(e => e.id === id),
        1
      )
    },
    // 添加分类
    addCategory() {
      const firstId = this.currentCategory[0]
      const secondId = this.currentCategory[1]
      const thirdId = this.currentCategory[2]
      // 根据第一个下标找到一级分类
      let name = ''
      this.categoryList.forEach(e => {
        if (e.id === firstId) {
          name += e.name
          // 获取子节点，根据第二个下标找到二级分类
          e.children.forEach(c1 => {
            if (c1.id === secondId) {
              name += ' > ' + c1.name
              // 获取子节点，根据第三个下标找到三级分类
              c1.children.forEach(c2 => {
                if (c2.id === thirdId) {
                  name += ' > ' + c2.name
                  c2.name = name
                  // 找到了
                  this.shopCoupon.categoryList.push(c2)
                  this.currentCategory = []
                  return
                }
              })
            }
          })
        }
      })
    },
    // 删除分类
    deleteCategory(row) {
      this.shopCoupon.categoryList.splice(
        this.shopCoupon.categoryList.findIndex(e => e.id === row.id),
        1
      )
    }
  }
}
</script>

<style>

</style>
