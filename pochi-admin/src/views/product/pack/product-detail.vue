<template>
  <div>
    <!-- 操作按钮组结束 -->
    <div class="data-table">
      <el-table
        header-row-class-name="pochi-table-header"
        :data="list"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="name" label="标题" />
        <el-table-column prop="price" label="价格" />
        <el-table-column prop="sale" label="销量" />
        <el-table-column prop="stock" label="库存" />
        <el-table-column prop="lowStock" label="预警库存" />
        <el-table-column prop="brandName" label="品牌" />
        <el-table-column prop="specs" label="规格" />
      </el-table>
    </div>
  </div>
</template>

<script>
import productApi from '@/api/shop/shop-product'
export default {
  props: {
    activeId: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      // 商品清单
      list: []
    }
  },
  watch: {
    activeId: {
      immediate: true,
      handler: function(newVal) {
        this.getByPackCode(newVal)
      }
    }
  },
  methods: {
    // 根据套装编号查询
    getByPackCode(id) {
      productApi.getProductDetailList(id).then(res => {
        this.list = res.data
      })
    }
  }
}
</script>

<style>

</style>
