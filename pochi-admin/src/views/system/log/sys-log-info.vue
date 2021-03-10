<template>
  <div>
    <el-form label-width="80px" size="small">
      <el-row :gutter="20">
        <el-col :span="12" :offset="0">
          <el-form-item label="控制层">
            {{ sysLog.logController }}
          </el-form-item>
        </el-col>
        <el-col :span="12" :offset="0">
          <el-form-item label="请求IP">
            {{ sysLog.logIp }}
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8" :offset="0">
          <el-form-item label="请求路径">
            {{ sysLog.logUrl }}
          </el-form-item>
        </el-col>
        <el-col :span="8" :offset="0">
          <el-form-item label="状态">
            {{ sysLog.logStatus === 1? '成功': '失败' }}
          </el-form-item>
        </el-col>
        <el-col :span="8" :offset="0">
          <el-form-item label="请求方式">
            {{ sysLog.logMethod }}
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8" :offset="0">
          <el-form-item label="创建时间">
            {{ sysLog.createdDate }}
          </el-form-item>
        </el-col>
        <el-col :span="8" :offset="0">
          <el-form-item label="创建人">
            {{ sysLog.createdBy }}
          </el-form-item>
        </el-col>
        <el-col :span="8" :offset="0">
          <el-form-item label="响应时间">
            {{ sysLog.logTime }} 毫秒
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24" :offset="0">
          <el-form-item label="UA标识">
            {{ sysLog.logUa }}
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24" :offset="0">
          <el-form-item label="请求参数">
            <json-editor ref="jsonEditor" v-model="sysLog.logParamsJson" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24" :offset="0">
          <el-form-item label="响应结果">
            <json-editor ref="jsonEditor" v-model="sysLog.logResultJson" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script>
import sysLogApi from '@/api/system/sys-log'
import JsonEditor from '@/components/JsonEditor'
export default {
  components: {
    JsonEditor
  },
  props: {
    // 日志ID
    activeId: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      // 日志对象
      sysLog: {
        logParamsJson: '',
        logParamsResult: ''
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
    // 根据id查询
    getById(id) {
      sysLogApi.get(id).then(res => {
        this.sysLog = res.data
        // 判断参数和返回值是否存在，如果存在，转成JSON对象
        if (res.data.logParams) {
          this.sysLog.logParamsJson = JSON.parse(res.data.logParams)
        }
        if (res.data.logResult) {
          this.sysLog.logResultJson = JSON.parse(res.data.logResult)
        }
      })
    }
  }
}
</script>

<style>
.CodeMirror pre {
  line-height: 16px !important;
}
.json-editor {
  min-height: 200px;
  max-height: 500px;
  overflow-y: scroll;
}
</style>
