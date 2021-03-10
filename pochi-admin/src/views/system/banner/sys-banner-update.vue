<template>
  <div>
    <el-form ref="form" :model="sysBanner" :rules="rules" label-width="80px" size="small">
      <el-row>
        <el-col :span="12" :offset="0">
          <el-form-item label="名称" prop="name">
            <el-input v-model="sysBanner.name" placeholder="请输入名称" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="12" :offset="0">
          <el-form-item label="排序" prop="sort">
            <el-input-number v-model="sysBanner.sort" controls-position="right" style="width: 100%" :min="1" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24" :offset="0">
          <el-form-item label="跳转路径" prop="url">
            <el-input v-model="sysBanner.url" placeholder="请输入跳转路径" clearable />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24" :offset="0">
          <el-form-item label="图片">
            <el-upload
              name="mf"
              class="avatar-uploader"
              :action="uploadUrl"
              :show-file-list="false"
              :data="{dir: 'banner'}"
              :headers="{Authorization: token}"
              :on-success="handleAvatarSuccess"
            >
              <img v-if="imageUrl" :src="imageUrl" class="avatar">
              <i v-else class="el-icon-plus avatar-uploader-icon" />
            </el-upload>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24" :offset="0">
          <el-form-item label="备注">
            <el-input v-model="sysBanner.note" placeholder="备注" type="textarea" :rows="2" clearable />
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
import { mapGetters } from 'vuex'
import sysBannerApi from '@/api/system/sys-banner'
export default {
  props: {
    // 父组件传递的轮播图ID
    activeId: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      // 表单对象
      sysBanner: {},
      // 图片上传路径
      uploadUrl: process.env.VUE_APP_UPLOAD_URL,
      // 用于回显的图片路径
      imageUrl: null,
      // 表单校验对象
      rules: {
        name: [
          { required: true, message: '请输入轮播图名', trigger: 'blur' }
        ],
        sort: [
          { required: true, message: '请输入排序编码', trigger: 'blur' }
        ],
        url: [
          { required: true, message: '请输入跳转路径', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    ...mapGetters([
      'token'
    ])
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
    // 上传成功的回调
    handleAvatarSuccess(res, file) {
      this.$message.success(res.msg)
      this.imageUrl = res.data
      this.sysBanner.pic = this.imageUrl
    },
    // 根据ID查询
    getById(id) {
      sysBannerApi.get(id).then(res => {
        this.sysBanner = res.data
        this.imageUrl = this.sysBanner.pic
      })
    },
    // 修改
    update() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          sysBannerApi.update(this.sysBanner).then(res => {
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
  .avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
  .avatar-uploader .el-upload:hover {
    border-color: #409EFF;
  }
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 300px;
    height: 150px;
    line-height: 150px;
    text-align: center;
  }
  .avatar {
    width: 300px;
    height: 150px;
    display: block;
  }
</style>
