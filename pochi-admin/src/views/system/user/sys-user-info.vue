<template>
  <div>
    <el-form ref="form" :model="sysUser" label-width="80px" size="small">
      <el-row>
        <el-col :span="24" class="header-container">
          <el-image
            style="width: 100px; height: 100px"
            :src="sysUser.header"
            fit="fill"
          />
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="账号">
            {{ sysUser.username }}
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="角色">
            {{ sysUser.sysRole.roleName }}
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="昵称">
            {{ sysUser.nickName }}
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="邮箱">
            {{ sysUser.email }}
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="启用状态">
            {{ sysUser.status === 1 ? '启用': '未启用' }}
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="创建时间">
            {{ sysUser.createTime }}
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="更新时间">
            {{ sysUser.updateTime }}
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="最后登录">
            {{ sysUser.loginTime }}
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="备注">
            {{ sysUser.note }}
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

  </div>
</template>

<script>
import sysUserApi from '@/api/system/sys-user'
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
      sysUser: {
        // 角色信息
        sysRole: {}
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
      sysUserApi.get(id).then(res => {
        this.sysUser = res.data
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
