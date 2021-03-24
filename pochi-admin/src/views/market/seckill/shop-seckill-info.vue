<template>
  <div>
    <el-form ref="form" :model="shopSecKill" label-width="80px" size="small">
      <el-row>
        <el-col :span="24" class="header-container">
          <el-image
            style="width: 100px; height: 100px"
            :src="shopSecKill.productPic"
            fit="fill"
          />
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="活动名称">
            {{ shopSecKill.name }}
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="商品名称">
            {{ shopSecKill.productName }}
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="商品价格">
            ￥{{ shopSecKill.productPrice }}
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="商品库存">
            {{ shopSecKill.stock }}
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="逾期时间">
            {{ shopSecKill.cancelTime }}分钟
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="创建时间">
            {{ shopSecKill.createTime }}
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="更新时间">
            {{ shopSecKill.updateTime }}
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="活动时间">
            {{ shopSecKill.beginTime }} 至 {{ shopSecKill.endTime }}
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

  </div>
</template>

<script>
import secKillApi from '@/api/market/shop-seckill'
export default {
  props: {
    activeId: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      // 回显用户信息对象
      shopSecKill: {}
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
    // 根据id查询
    getById(id) {
      secKillApi.get(id).then(res => {
        this.shopSecKill = res.data
      })
    }
  }
}
</script>

<style scoped>
.header-container {
  text-align: center;
  margin-bottom: 15px;
}
</style>
