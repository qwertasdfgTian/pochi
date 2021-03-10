<template>
  <div>
    <el-form :model="shopBrand" label-width="80px" size="small">
      <el-row :gutter="20">
        <el-col :span="24" :offset="0">
          <el-form-item label="品牌名称">
            {{ shopBrand.name }}
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24" :offset="0">
          <el-form-item label="logo">
            <el-image
              style="width: 100px; height: 100px"
              :src="shopBrand.logo"
              :preview-src-list="[shopBrand.logo]"
              fit="fill"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24" :offset="0">
          <el-form-item label="商品分类">
            <el-tag v-for="item in shopBrand.categoryList" :key="item.id" class="category-info" type="info">{{ item.name }}</el-tag>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script>
import brandApi from '@/api/shop/shop-brand'
export default {
  props: {
    activeId: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      // 回显对象
      shopBrand: {}
    }
  },
  watch: {
    activeId: {
      immediate: true,
      handler: function(newVal, oldVal) {
        this.getById(newVal)
      }
    }
  },
  methods: {
    // 查询回显
    getById(id) {
      brandApi.get(id).then(res => {
        this.shopBrand = res.data
      })
    }
  }
}
</script>

<style scoped>
.category-info {
  margin-right: 10px;
  margin-bottom: 5px;
}
</style>
