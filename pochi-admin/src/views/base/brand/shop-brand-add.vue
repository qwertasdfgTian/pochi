<template>
  <div>
    <el-form ref="form" :model="shopBrand" :rules="rules" label-width="80px" size="small">
      <el-row :gutter="20">
        <el-col :span="24" :offset="0">
          <el-form-item label="logo">
            <el-upload
              name="mf"
              class="avatar-uploader"
              :action="uploadUrl"
              :show-file-list="false"
              :headers="{Authorization: token}"
              :data="{dir: 'brand'}"
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
          <el-form-item label="品牌名称" prop="name">
            <el-input v-model="shopBrand.name" clearable placeholder="品牌名称" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12" :offset="0">
          <el-form-item label="排序" prop="sort">
            <el-input-number v-model="shopBrand.sort" controls-position="right" style="width: 100%" clearable placeholder="请输入排序编码" :min="1" />
          </el-form-item>
        </el-col>
        <el-col :span="12" :offset="0">
          <el-form-item label="显示">
            <el-radio-group v-model="shopBrand.showStatus">
              <el-radio :label="1">
                显示
              </el-radio>
              <el-radio :label="0">
                隐藏
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24" :offset="0">
          <el-form-item label="商品分类">
            <el-select v-model="shopBrand.categoryIds" style="width: 100%" multiple placeholder="请选择分类" clearable filterable>
              <el-option
                v-for="item in categoryList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item>
        <el-button type="primary" @click="add">添加</el-button>
      </el-form-item>
    </el-form>

  </div>
</template>

<script>
import categoryApi from '@/api/shop/shop-product-category'
import brandApi from '@/api/shop/shop-brand'
import { mapGetters } from 'vuex'
export default {
  data() {
    return {
      // 表单对象
      shopBrand: {},
      // 商品分类列表
      categoryList: [],
      // 上传图片回显
      imageUrl: null,
      // 上传图片路径
      uploadUrl: process.env.VUE_APP_UPLOAD_URL,
      // 表单校验对象
      rules: {
        name: [
          { required: true, message: '请输入品牌名称', trigger: 'blur' }
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
  created() {
    this.getAllCategory()
    this.shopBrand.showStatus = 1
  },
  methods: {
    // 上传成功回显
    handleAvatarSuccess(res, file) {
      this.imageUrl = res.data
      this.shopBrand.logo = this.imageUrl
    },
    // 查询所有二级商品分类
    getAllCategory() {
      categoryApi.getAllSecond().then(res => {
        this.categoryList = res.data
      })
    },
    // 添加
    add() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          brandApi.save(this.shopBrand).then(res => {
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
    width: 90px;
    height: 90px;
    line-height: 90px;
    text-align: center;
  }
  .avatar {
    width: 90px;
    height: 90px;
    display: block;
  }
</style>
