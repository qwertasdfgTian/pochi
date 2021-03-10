<template>
  <div>
    <el-form ref="form" :model="sysRole" :rules="rules" label-width="80px" size="small">
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="角色名" prop="roleName">
            <el-input v-model="sysRole.roleName" placeholder="请输入角色名" clearable />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="排序" prop="roleSort">
            <el-input-number v-model="sysRole.roleSort" style="width: 100%" controls-position="right" :min="1" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="权限">
            <el-tree
              ref="authTree"
              :props="{label: 'menuName'}"
              :data="menuTree"
              show-checkbox
              :render-after-expand="false"
              node-key="menuId"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item>
        <el-button type="primary" @click="add">添加</el-button>
        <el-button type="warning" @click="Reset">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import sysRoleApi from '@/api/system/sys-role'
import sysMenuApi from '@/api/system/sys-menu'
export default {
  data() {
    return {
      // 表单对象
      sysRole: {},
      // 菜单树
      menuTree: [],
      // 表单校验对象
      rules: {
        roleName: [
          { required: true, message: '角色名不能为空', trigger: 'blur' }
        ],
        roleSort: [
          { required: true, message: '排序码不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getTreeList()
  },
  methods: {
    Reset() {
      this.sysRole = {}
      this.$nextTick(() => {
        this.$refs.authTree.setCheckedKeys([])
      })
    },
    // 添加角色
    add() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          const checkKeys = this.$refs.authTree.getCheckedKeys()
          const halfCheckKeys = this.$refs.authTree.getHalfCheckedKeys()
          // 合并选中的数组和半选中的数组
          checkKeys.push(...halfCheckKeys)
          this.sysRole.authIds = checkKeys
          sysRoleApi.save(this.sysRole).then(res => {
            this.$message.success(res.msg)
            this.$emit('after')
            this.$emit('close')
          })
          this.sysRole = {}
        }
      })
    },
    // 树状查询
    getTreeList() {
      sysMenuApi.getTreeList().then(res => {
        this.menuTree = res.data
      })
    }
  }
}
</script>

<style>
</style>
