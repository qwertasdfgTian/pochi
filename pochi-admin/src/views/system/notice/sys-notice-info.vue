<template>
  <div>
    <el-form :model="sysNotice" label-width="80px" size="small">
      <el-form-item label="标题">
        {{ sysNotice.noticeTitle }}
      </el-form-item>
      <el-form-item label="排序">
        {{ sysNotice.sorted }}
      </el-form-item>
      <el-form-item label="创建人">
        {{ sysNotice.createdBy }}
      </el-form-item>
      <el-form-item label="创建时间">
        {{ sysNotice.createdTime }}
      </el-form-item>
      <el-form-item label="内容">
        <div v-html="sysNotice.noticeContent" />
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import sysNoticeApi from '@/api/system/sys-notice'
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
      sysNotice: {}
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
      sysNoticeApi.get(id).then(res => {
        this.sysNotice = res.data
      })
    }
  }
}
</script>

<style>

</style>
