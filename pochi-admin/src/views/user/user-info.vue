<template>
  <div>
    <el-form label-width="80px" size="small">
      <el-row class="image-container">
        <el-image :src="user.header" class="header-image" :preview-list="[user.header]" />
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8" :offset="0">
          <el-form-item label="账号">
            {{ user.phone }}
          </el-form-item>
        </el-col>
        <el-col :span="8" :offset="0">
          <el-form-item label="性别">
            {{ user.gender===1?'男':'女' }}
          </el-form-item>
        </el-col>
        <el-col :span="8" :offset="0">
          <el-form-item label="昵称">
            {{ user.nickname }}
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12" :offset="0">
          <el-form-item label="启用状态">
            {{ user.status===1?'启用':'封禁' }}
          </el-form-item>
        </el-col>
        <el-col :span="12" :offset="0">
          <el-form-item label="创建时间">
            {{ user.createTime }}
          </el-form-item>
        </el-col>
      </el-row>
      <!-- 统计信息 -->
      <div class="statistic">
        <el-row>
          <el-col :span="6" :offset="0">
            <el-form-item>
              <div slot="label">积分</div>
              {{ user.point }}
            </el-form-item>
          </el-col>
          <el-col :span="6" :offset="0">
            <el-form-item>
              <div slot="label">累计积分</div>
              {{ user.historyPoint }}
            </el-form-item>
          </el-col>
          <el-col v-if="user.shopUserStatistic" :span="6" :offset="0">
            <el-form-item>
              <div slot="label">累计消费</div>
              {{ user.shopUserStatistic.consumeAmount }}
            </el-form-item>
          </el-col>
          <el-col v-if="user.shopUserStatistic" :span="6" :offset="0">
            <el-form-item>
              <div slot="label">订单数</div>
              {{ user.shopUserStatistic.orderCount }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="user.shopUserStatistic">
          <el-col :span="6" :offset="0">
            <el-form-item>
              <div slot="label">优惠券数</div>
              {{ user.shopUserStatistic.couponCount }}
            </el-form-item>
          </el-col>
          <el-col :span="6" :offset="0">
            <el-form-item>
              <div slot="label">评价数</div>
              {{ user.shopUserStatistic.commentCount }}
            </el-form-item>
          </el-col>
          <el-col :span="6" :offset="0">
            <el-form-item>
              <div slot="label">退货次数</div>
              {{ user.shopUserStatistic.returnOrderCount }}
            </el-form-item>
          </el-col>
          <el-col :span="6" :offset="0">
            <el-form-item>
              <div slot="label">登录次数</div>
              {{ user.shopUserStatistic.loginCount }}
            </el-form-item>
          </el-col>
        </el-row>

      </div>
      <!-- 统计信息结束 -->
    </el-form>
  </div>
</template>

<script>
import userApi from '@/api/system/shop-user'
export default {
  props: {
    activeId: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      // 用户对象
      user: {}
    }
  },
  watch: {
    activeId: {
      immediate: true,
      handler: function() {
        this.getById()
      }
    }
  },
  methods: {
    // 根据ID查询
    getById() {
      userApi.get(this.activeId).then((res) => {
        this.$nextTick(() => {
          this.user = res.data
        })
      })
    }
  }
}
</script>

<style scoped>
.image-container {
  text-align: center;
}
.header-image {
  width: 100px;
  height: 100px;
  margin-bottom: 20px;
}

.statistic >>> .el-row {
  border-top: 1px solid #dcdfe6;
  border-bottom: 1px solid #dcdfe6;
  border-right: 1px solid #dcdfe6;
  margin-bottom: -1px;
}

.statistic >>> .el-col {
  border-left: 1px solid #dcdfe6;
}

.statistic >>> .el-form-item__label {
  border-right: 1px solid #dcdfe6;
  text-align: center;
  background-color: #f2f6fc;
}

.statistic >>> .el-form-item {
  line-height: 45px;
  margin-bottom: 0px;
}

.statistic >>> .el-form-item__content {
  text-align: center;
}

</style>
