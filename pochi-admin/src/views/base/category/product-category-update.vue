<template>
  <div>
    <el-form ref="form" :model="category" :rules="rules" label-width="80px" size="small">
      <el-row :gutter="20">
        <el-col :span="24" :offset="0">
          <el-form-item label="图标">
            <el-upload
              name="mf"
              class="avatar-uploader"
              :action="uploadUrl"
              :show-file-list="false"
              :headers="{Authorization: token}"
              :data="{dir: 'type'}"
              :on-success="handleAvatarSuccess"
            >
              <img v-if="imageUrl" :src="imageUrl" class="avatar">
              <i v-else class="el-icon-plus avatar-uploader-icon" />
            </el-upload>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24" :offset="0">
          <el-form-item label="上级分类">
            <treeselect
              v-model="category.parentId"
              :options="selectTree"
              :normalizer="normalizer"
              :show-count="true"
              placeholder="请选择上级分类"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12" :offset="0">
          <el-form-item label="分类名称" prop="name">
            <el-input v-model="category.name" placeholder="分类名称" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="12" :offset="0">
          <el-form-item label="排序" prop="sort">
            <el-input-number v-model="category.sort" controls-position="right" style="width: 100%" placeholder="分类名称" clearable :min="1" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12" :offset="0">
          <el-form-item label="分类层级" prop="level">
            <el-radio-group v-model="category.level">
              <el-radio :label="1">1级</el-radio>
              <el-radio :label="2">2级</el-radio>
              <el-radio :label="3">3级</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12" :offset="0">
          <el-form-item label="导航显示">
            <el-radio-group v-model="category.navStatus">
              <el-radio :label="1">显示</el-radio>
              <el-radio :label="0">隐藏</el-radio>
            </el-radio-group>
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
import categoryApi from '@/api/shop/shop-product-category'
import { mapGetters } from 'vuex'
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
export default {
  components: {
    Treeselect
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
      category: {},
      // 上传图片回显
      imageUrl: null,
      // 上传图片路径
      uploadUrl: process.env.VUE_APP_UPLOAD_URL,
      // 下拉选择
      selectTree: [],
      rules: {
        parentId: [
          { required: true, message: '请选择上级分类', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '请输入分类名称', trigger: 'blur' }
        ],
        sort: [
          { required: true, message: '请输入排序编码', trigger: 'blur' }
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
  created() {
    this.getSelectTree()
  },
  methods: {
    // 上传成功回显
    handleAvatarSuccess(res, file) {
      this.imageUrl = res.data
      this.category.icon = this.imageUrl
    },
    // 设置属性下拉框结构
    normalizer(node) {
      if (!node.children) {
        delete node.children
      }
      return {
        id: node.id,
        label: node.name,
        children: node.children
      }
    },
    // 修改
    update() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          categoryApi.update(this.category).then(res => {
            this.$message.success(res.msg)
            this.$emit('after')
            this.$emit('close')
          })
        }
      })
    },
    // 查询下拉选择树
    getSelectTree() {
      categoryApi.getSelectTree().then(res => {
        this.selectTree = []
        // 设置顶级菜单的父级选项
        const tree = { id: 0, name: '根菜单', children: [] }
        tree.children = res.data
        this.selectTree.push(tree)
      })
    },
    // 根据id查询
    getById(id) {
      categoryApi.get(id).then(res => {
        this.category = res.data
        this.imageUrl = this.category.icon
      })
    }
  }
}
</script>

<style>

</style>
