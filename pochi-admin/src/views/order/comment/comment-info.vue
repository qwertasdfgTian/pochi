<template>
  <div>
    <el-form label-width="80px">
      <el-row :gutter="20">
        <el-col :span="24" :offset="0">
          <el-form-item label="头像">
            <el-image
              style="width: 50px; height: 50px"
              :src="currentComment.memberIcon"
              fit="fill"
              :preview-src-list="[currentComment.memberIcon]"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12" :offset="0">
          <el-form-item label="商品编号">
            {{ currentComment.productId }}
          </el-form-item>
        </el-col>
        <el-col :span="12" :offset="0">
          <el-form-item label="订单编号">
            {{ currentComment.orderId }}
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24" :offset="0">
          <el-form-item label="商品名称">
            {{ currentComment.productName }}
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12" :offset="0">
          <el-form-item label="评论用户">
            {{ currentComment.nickeName }}
          </el-form-item>
        </el-col>
        <el-col :span="12" :offset="0">
          <el-form-item label="评论时间">
            {{ currentComment.creatTime }}
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12" :offset="0">
          <el-form-item label="评论类型">
            <span v-if="currentComment.commentType === 1">普通评论</span>
            <span v-else-if="currentComment.commentType === 2">商家回复</span>
            <span v-else>追评</span>
          </el-form-item>
        </el-col>
        <el-col :span="12" :offset="0">
          <el-form-item label="评分">
            <el-rate v-model="currentComment.star" disabled :colors="colors" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24" :offset="0">
          <el-form-item label="图片列表">
            <el-image
              v-for="(item,index) in currentComment.picList"
              :key="index"
              style="margin:0 10px; width: 100px; height: 150px"
              :src="item"
              fit="fill"
              :preview-src-list="[item]"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24" :offset="0">
          <el-form-item label="评论内容">
            {{ currentComment.content }}
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script>
export default {
  props: {
    currentComment: {
      type: Object,
      default() {
        return {}
      }
    }
  },
  data() {
    return {
      // 评分颜色
      colors: ['#99A9BF', '#F7BA2A', '#FF9900']
    }
  }
}
</script>

<style>

</style>
