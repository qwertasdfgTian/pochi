<template>
  <div>
    <el-form ref="form" :model="sysNotice" :rules="rules" label-width="80px" size="small">
      <el-row :gutter="20">
        <el-col :span="24" :offset="0">
          <el-form-item label="标题" prop="noticeTitle">
            <el-input v-model="sysNotice.noticeTitle" clearable placeholder="请输入标题" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24" :offset="0">
          <el-form-item label="排序" prop="sorted">
            <el-input-number
              v-model="sysNotice.sorted"
              controls-position="right"
              style="width: 100%"
              placeholder="请输入排序值"
              :min="1"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24" :offset="0">
          <el-form-item label="公告内容" prop="noticeContentTemp">
            <markdown-editor ref="markdownEditor" v-model="sysNotice.noticeContentTemp" language="zh_CN" height="500px" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item>
        <el-button type="primary" @click="update">修改</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import sysNoticeApi from '@/api/system/sys-notice'
import MarkdownEditor from '@/components/MarkdownEditor'
export default {
  components: {
    MarkdownEditor
  },
  props: {
    activeId: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      // 表单对象
      sysNotice: {},
      // 表单校验对象
      rules: {
        noticeTitle: [
          { required: true, message: '请输入公告标题', trigger: 'blur' }
        ],
        sorted: [
          { required: true, message: '请输入排序编码', trigger: 'blur' }
        ],
        noticeContentTemp: [
          { required: true, message: '请输入公告内容', trigger: 'blur' }
        ]
      }
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
    // 更新
    update() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          // 判断，如果存在内容，就转成HTML
          if (this.sysNotice.noticeContentTemp) {
            this.sysNotice.noticeContent = this.$refs.markdownEditor.getHtml()
          }
          sysNoticeApi.update(this.sysNotice).then(res => {
            this.$message.success(res.msg)
            this.$emit('after')
            this.$emit('close')
          })
        }
      })
    },
    // 根据id查询
    getById(id) {
      sysNoticeApi.get(id).then(res => {
        this.sysNotice = res.data
        // 判断内容是否存在
        if (this.sysNotice.noticeContent) {
          this.$refs.markdownEditor.setHtml(this.sysNotice.noticeContent)
        }
      })
    }
  }
}
</script>

<style>
</style>
