<template>
  <div>
    <el-form ref="form" :model="sysMenu" label-width="80px" :rules="rules" size="small">
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="上级菜单" prop="parentId">
            <treeselect
              v-model="sysMenu.parentId"
              :options="menuOptions"
              :normalizer="normalizer"
              :show-count="true"
              placeholder="请选择上级菜单"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="菜单类型">
            <el-radio-group v-model="sysMenu.menuType">
              <el-radio :label="1">目录</el-radio>
              <el-radio :label="2">菜单</el-radio>
              <el-radio :label="3">权限</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="sysMenu.menuType != 3" :gutter="20">
        <el-col :span="24">
          <el-form-item label="菜单图标">
            <select-icon @checkIcon="checkIcon" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="sysMenu.menuType === 2" :gutter="20">
        <el-col :span="24">
          <el-form-item label="组件路径" prop="componentUrl">
            <el-input v-model="sysMenu.componentUrl" clearable />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="菜单名称" prop="menuName">
            <el-input v-model="sysMenu.menuName" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="显示排序" prop="orderNum">
            <el-input-number v-model="sysMenu.orderNum" clearable controls-position="right" style="width: 100%" :min="1" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col v-if="sysMenu.menuType != 3" :span="12">
          <el-form-item label="路由地址" prop="routerPath">
            <el-input v-model="sysMenu.routerPath" clearable />
          </el-form-item>
        </el-col>
        <el-col v-if="sysMenu.menuType != 1" :span="12">
          <el-form-item label="权限标识" prop="permission">
            <el-input v-model="sysMenu.permission" clearable />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col v-if="sysMenu.menuType != 3" :span="12">
          <el-form-item label="显示状态">
            <el-radio-group v-model="sysMenu.visible">
              <el-radio :label="1">显示</el-radio>
              <el-radio :label="0">隐藏</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="启用状态">
            <el-radio-group v-model="sysMenu.status">
              <el-radio :label="1">启用</el-radio>
              <el-radio :label="0">禁用</el-radio>
            </el-radio-group>
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
import sysMenuApi from '@/api/system/sys-menu'
import SelectIcon from '@/components/SelectIcon'
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
export default {
  components: {
    Treeselect,
    SelectIcon
  },
  data() {
    return {
      // 表单对象
      sysMenu: {
        // 菜单类型，1目录，2菜单，3权限
        menuType: 1,
        visible: 1,
        status: 1
      },
      // 菜单树结构
      menuOptions: [],
      // 表单校验对象
      rules: {
        permission: [
          { required: true, message: '权限标识不能为空', trigger: 'blur' }
        ],
        routerPath: [
          { required: true, message: '路由地址不能为空', trigger: 'blur' }
        ],
        orderNum: [
          { required: true, message: '排序号不能为空', trigger: 'blur' }
        ],
        menuName: [
          { required: true, message: '菜单名称不能为空', trigger: 'blur' }
        ],
        componentUrl: [
          { required: true, message: '组件路径不能为空', trigger: 'blur' }
        ],
        parentId: [
          { required: true, message: '上级菜单不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getTreeSelect()
  },
  methods: {
    Reset() {
      const ico = this.sysMenu.icon
      this.sysMenu = {
        // 菜单类型，1目录，2菜单，3权限
        menuType: 1,
        visible: 1,
        status: 1,
        icon: ico
      }
    },
    // 添加
    add() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          sysMenuApi.save(this.sysMenu).then(res => {
            this.$message.success(res.msg)
            this.$emit('after')
            this.$emit('close')
          })
        }
      })
    },
    // 选中图标的回调
    checkIcon(icon) {
      this.$set(this.sysMenu, 'icon', icon)
    },
    // 设置属性下拉框结构
    normalizer(node) {
      if (!node.children) {
        delete node.children
      }
      return {
        id: node.menuId,
        label: node.menuName,
        children: node.children
      }
    },
    // 加载树形数据
    getTreeSelect() {
      sysMenuApi.getTreeList().then(res => {
        // 给menuoptions清空
        this.menuOptions = []
        // 设置顶级菜单的父级选项
        const menu = { menuId: 0, menuName: '根菜单', children: [] }
        menu.children = res.data
        this.menuOptions.push(menu)
      })
    }
  }
}
</script>

<style>
.vue-treeselect__control {
  border: 1px solid #dcdfe6 !important;
}
</style>
