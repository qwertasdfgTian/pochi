<template>
  <div>
    <el-form ref="form" :model="sysUser" :rules="rules" label-width="80px" size="small">
      <el-row :gutter="20">
        <el-col :span="24" :offset="0">
          <el-form-item label="头像">
            <el-upload
              class="avatar-uploader"
              name="mf"
              :action="uploadUrl"
              :show-file-list="false"
              :data="{dir: 'header'}"
              :headers="{Authorization: token}"
              :on-success="handleAvatarSuccess"
            >
              <img v-if="imageUrl" :src="imageUrl" class="avatar">
              <i v-else class="el-icon-plus avatar-uploader-icon" />
            </el-upload>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="账号">
            <el-input v-model="sysUser.username" readonly placeholder="请输入账号" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="角色">
            <el-select v-model="sysUser.sysRole.roleId" style="width: 100%" clearable placeholder="请选择角色">
              <el-option
                v-for="item in roleList"
                :key="item.roleId"
                :label="item.roleName"
                :value="item.roleId"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="昵称" prop="nickName">
            <el-input v-model="sysUser.nickName" placeholder="请输入昵称" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="sysUser.email" placeholder="请输入邮箱" clearable />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="备注">
            <el-input v-model="sysUser.note" :rows="2" type="textarea" placeholder="请输入备注" clearable />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item>
        <el-button type="primary" @click="update">修改</el-button>
        <el-button type="warning" @click="Reset">重置</el-button>
      </el-form-item>
    </el-form>

  </div>
</template>

<script>
import sysUserApi from '@/api/system/sys-user'
import sysRoleApi from '@/api/system/sys-role'
import { mapGetters } from 'vuex'
export default {
  props: {
    // 调用方传来的用户ID
    activeId: {
      type: String,
      default: null
    }
  },
  data() {
    // 自定义的邮箱和手机验证规则
    var checkEmail = (rule, value, callback) => {
      const regEmail = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
      if (regEmail.test(value)) {
        return callback()
      }
      callback(new Error('请输入合法的邮箱'))
    }
    return {
      // 表单数据
      sysUser: {
        // 角色信息
        sysRole: {}
      },
      // 文件上传路径
      uploadUrl: process.env.VUE_APP_UPLOAD_URL,
      // 图片回显地址
      imageUrl: null,
      // 角色下拉列表
      roleList: [],
      // 表单校验对象
      rules: {
        nickName: [
          { required: true, message: '请输入昵称', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { validator: checkEmail, trigger: 'blur' }
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
      // 该属性作用就是让监听器立即生效
      immediate: true,
      // 处理监听器的实际逻辑
      handler: function(newVal, oldVal) {
        this.getById(newVal)
      }
    }
  },
  created() {
    this.getAllRole()
  },
  methods: {
    Reset() {
      const name = this.sysUser.username
      this.sysUser = {
        id: this.activeId,
        username: name,
        // 角色信息
        sysRole: {
          roleId: undefined
        }
      }
    },
    // 根据id查询用户
    getById(id) {
      sysUserApi.get(id).then(res => {
        this.sysUser = res.data
        this.imageUrl = this.sysUser.header
      })
    },
    // 修改用户
    update() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          sysUserApi.update(this.sysUser).then(res => {
            this.$message.success(res.msg)
            // 关闭对话框，并刷新列表
            this.$emit('close')
            this.$emit('after')
          })
        }
      })
    },
    // 上传图片成功后的回调
    handleAvatarSuccess(res, file) {
      this.$message.success(res.msg)
      this.imageUrl = res.data
      this.sysUser.header = this.imageUrl
    },
    // 查询所有角色
    getAllRole() {
      sysRoleApi.getAll().then(res => {
        this.roleList = res.data
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
    width: 178px;
    height: 178px;
    line-height: 178px;
    text-align: center;
  }
  .avatar {
    width: 178px;
    height: 178px;
    display: block;
  }
</style>
