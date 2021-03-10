<template>
  <div>
    <el-form ref="form" :model="sysHelp" :rules="rules" label-width="80px" size="small">
      <el-form-item label="标题" prop="title">
        <el-input v-model="sysHelp.title" placeholder="请输入标题" clearable />
      </el-form-item>
      <el-form-item label="内容" prop="content">
        <tinymce v-model="sysHelp.content" :height="300" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="add">添加</el-button>
      </el-form-item>
    </el-form>

  </div>
</template>

<script>
import sysHelpApi from '@/api/system/sys-help'
import Tinymce from '@/components/Tinymce'
export default {
  components: { Tinymce },
  data() {
    return {
      // 表单对象
      sysHelp: {},
      // 表单校验对象
      rules: {
        title: [
          { required: true, message: '请输入标题', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入内容', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    // 添加
    add() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          sysHelpApi.save(this.sysHelp).then(res => {
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
