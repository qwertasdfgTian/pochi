<template>
  <div>
    <!-- 添加按钮开始 -->
    <div class="button-group">
      <el-button type="primary" size="small" icon="el-icon-plus" @click="toAdd">添加</el-button>
    </div>
    <!-- 添加按钮结束 -->
    <!-- 数据表格开始 -->
    <div class="data-table">
      <el-table
        header-row-class-name="pochi-table-header"
        :data="treeList"
        stripe
        row-key="menuId"
        style="width: 100%"
      >
        <el-table-column prop="menuId" label="菜单编号" width="100px" />
        <el-table-column prop="menuName" label="名称" />
        <el-table-column prop="orderNum" label="排序" width="60px" />
        <el-table-column prop="routerPath" label="路由地址" show-overflow-tooltip />
        <el-table-column prop="componentUrl" label="组件路径" show-overflow-tooltip />
        <el-table-column prop="menuType" label="菜单类型" width="80px">
          <template slot-scope="{row}">
            <el-tag v-if="row.menuType === 1">目录</el-tag>
            <el-tag v-if="row.menuType === 2">菜单</el-tag>
            <el-tag v-if="row.menuType === 3">权限</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="visible" label="显示状态" width="80px">
          <template slot-scope="{row}">
            <el-tag v-if="row.visible === 1">显示</el-tag>
            <el-tag v-if="row.visible === 0" type="info">隐藏</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="启用状态" width="80px">
          <template slot-scope="{row}">
            <el-tag v-if="row.status === 1">启用</el-tag>
            <el-tag v-if="row.status === 0" type="info">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="permission" label="权限标识" show-overflow-tooltip />
        <el-table-column prop="icon" label="菜单图标">
          <template slot-scope="{row}">
            <div v-if="row.icon">
              <i v-if="row.icon.indexOf('el-icon') >= 0" :class="row.icon" />
              <svg-icon v-else :icon-class="row.icon" class-name="disabled" />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" show-overflow-tooltip />
        <el-table-column prop="createBy" label="创建人" width="120px" />
        <el-table-column prop="updateTime" label="修改时间" show-overflow-tooltip />
        <el-table-column prop="updateBy" label="修改人" width="120px" />
        <el-table-column label="操作" width="80px">
          <template slot-scope="{row}">
            <el-dropdown class="handle-button">
              <span class="el-dropdown-link">
                操作<i class="el-icon-arrow-down el-icon--right" />
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item @click.native="toUpdate(row.menuId)">
                  <el-button type="text" icon="el-icon-edit">修改</el-button>
                </el-dropdown-item>
                <el-dropdown-item @click.native="toDelete(row.menuId)">
                  <el-button type="text" icon="el-icon-delete">删除</el-button>
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <!-- 数据表格结束 -->
    <!-- 添加弹窗 -->
    <el-dialog
      title="添加菜单"
      :visible.sync="addDialog"
      width="45%"
    >
      <sys-menu-add v-if="addDialog" @after="getTreeList" @close="closeDialog" />
    </el-dialog>
    <!-- 添加弹窗结束 -->
    <!-- 修改弹窗 -->

    <el-dialog
      title="修改菜单"
      :visible.sync="updateDialog"
      width="45%"
    >
      <sys-menu-update v-if="updateDialog" :active-id="activeId" @after="getTreeList" @close="closeDialog" />
    </el-dialog>

    <!-- 修改弹窗结束 -->
  </div>
</template>

<script>
import sysMenuApi from '@/api/system/sys-menu'
import sysMenuAdd from './sys-menu-add'
import sysMenuUpdate from './sys-menu-update'
import SysMenuAdd from './sys-menu-add.vue'
export default {
  components: {
    sysMenuAdd,
    sysMenuUpdate
  },
  data() {
    SysMenuAdd
    return {
      // 树形结构
      treeList: [],
      // 控制添加弹窗显示
      addDialog: false,
      // 控制修改弹窗显示
      updateDialog: false,
      // 当前点击的菜单编号
      activeId: ''
    }
  },
  created() {
    this.getTreeList()
  },
  methods: {
    // 打开添加弹窗
    toAdd() {
      this.addDialog = true
    },
    // 打开编辑弹窗
    toUpdate(id) {
      this.activeId = id
      this.updateDialog = true
    },
    // 删除
    toDelete(id) {
      this.$confirm('是否删除该菜单?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        sysMenuApi.deleteById(id).then(res => {
          this.$message.success(res.msg)
          this.getTreeList()
        })
      })
    },
    // 关闭弹窗
    closeDialog() {
      this.addDialog = false
      this.updateDialog = false
    },
    // 树状查询
    getTreeList() {
      sysMenuApi.getTreeList().then(res => {
        this.treeList = res.data
      })
    }
  }
}
</script>

<style>
</style>
