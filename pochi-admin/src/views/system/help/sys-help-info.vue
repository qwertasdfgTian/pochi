<template>
  <div>
    <el-form :model="sysHelp" label-width="80px" size="small">
      <el-form-item label="标题">
        {{ sysHelp.title }}
      </el-form-item>
      <el-form-item label="创建人">
        {{ sysHelp.createdBy }}
      </el-form-item>
      <el-form-item label="创建时间">
        {{ sysHelp.createdTime }}
      </el-form-item>
      <el-form-item label="修改人">
        {{ sysHelp.updateBy }}
      </el-form-item>
      <el-form-item label="修改时间">
        {{ sysHelp.updateTime }}
      </el-form-item>
      <el-form-item label="内容">
        {{ sysHelp.content }}
      </el-form-item>
    </el-form>

  </div>
</template>

<script>
import sysHelpApi from '@/api/system/sys-help'
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
      sysHelp: {}
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
      sysHelpApi.get(id).then(res => {
        this.sysHelp = res.data
      })
    }
  }
}
</script>

<style>

</style>
