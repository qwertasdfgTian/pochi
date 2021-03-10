<template>
  <div>

    <el-card class="product-card" shadow="never">
      <!-- 步骤条 -->
      <el-steps align-center :active="activeStep" finish-status="success">
        <el-step title="商品信息" />
        <el-step title="商品营销" />
        <el-step title="商品详情" />
        <el-step title="商品关联" />
      </el-steps>
      <!-- 步骤条结束 -->
      <!-- 商品表单 -->
      <el-form ref="form" :model="shopProduct" :rules="rules" class="form" label-width="80px" size="small">
        <!-- 商品详情 -->
        <div v-show="activeStep===0" class="product-info">
          <el-row :gutter="20">
            <el-col :span="12" :offset="0">
              <el-form-item label="商品分类">
                <el-cascader
                  v-model="shopProduct.categoryIds"
                  clearable
                  filterable
                  placeholder="选择分类后才能选择对应品牌"
                  style="width: 100%"
                  :options="categoryTree"
                  :props="{ expandTrigger: 'hover', value: 'id', label: 'name' }"
                  @change="changeCategory"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12" :offset="0">
              <el-form-item label="商品品牌" prop="brandId">
                <el-select v-model="shopProduct.brandId" style="width: 100%" placeholder="选择品牌" clearable filterable>
                  <el-option
                    v-for="item in brandList"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id"
                  />
                </el-select>

              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24" :offset="0">
              <el-form-item label="商品标题" prop="name">
                <el-input v-model="shopProduct.name" placeholder="商品标题" clearable />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24" :offset="0">
              <el-form-item label="副标题 ">
                <el-input v-model="shopProduct.subTitle" placeholder="副标题" clearable />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12" :offset="0">
              <el-form-item label="库存" prop="stock">
                <el-input-number v-model="shopProduct.stock" style="width: 100%" controls-position="right" :min="1" placeholder="库存" clearable />
              </el-form-item>
            </el-col>
            <el-col :span="12" :offset="0">
              <el-form-item label="警戒库存">
                <el-input-number v-model="shopProduct.lowStock" :max="shopProduct.stock" style="width: 100%" controls-position="right" :min="1" placeholder="警戒库存" clearable />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12" :offset="0">
              <el-form-item label="排序" prop="sort">
                <el-input-number v-model="shopProduct.sort" style="width: 100%" controls-position="right" :min="1" placeholder="排序" clearable />
              </el-form-item>
            </el-col>
            <el-col :span="12" :offset="0">
              <el-form-item label="规格" prop="specs">
                <el-input v-model="shopProduct.specs" placeholder="规格" clearable />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24" :offset="0">
              <el-form-item label="商品描述" prop="productComment">
                <el-input v-model="shopProduct.productComment" type="textarea" :rows="4" placeholder="商品描述" clearable />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item>
            <el-button type="primary" @click="activeStep++">下一步，填写商品营销</el-button>
          </el-form-item>
        </div>
        <!-- 商品详情结束 -->
        <!-- 商品营销 -->
        <div v-show="activeStep === 1" class="product-info">
          <el-row :gutter="20">
            <el-col :span="24" :offset="0">
              <el-form-item label="价格" prop="price">
                <el-input-number v-model="shopProduct.price" style="width: 100%" :min="0" controls-position="right" placeholder="价格（单位/元）" clearable />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24" :offset="0">
              <el-form-item label="上架">
                <el-switch
                  v-model="shopProduct.publishStatus"
                  :active-value="1"
                  :inactive-value="0"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24" :offset="0">
              <el-form-item label="新品">
                <el-switch
                  v-model="shopProduct.newStatus"
                  :active-value="1"
                  :inactive-value="0"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24" :offset="0">
              <el-form-item label="推荐">
                <el-switch
                  v-model="shopProduct.recommendStatus"
                  :active-value="1"
                  :inactive-value="0"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12" :offset="0">
              <el-form-item label="积分">
                <el-input-number v-model="shopProduct.point" :min="0" style="width: 100%" controls-position="right" placeholder="积分" clearable />
              </el-form-item>
            </el-col>
            <el-col :span="12" :offset="0">
              <el-form-item label="运费">
                <el-input-number v-model="shopProduct.transFee" :min="0" style="width: 100%" controls-position="right" placeholder="运费（单位/元）" clearable />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item>
            <el-button @click="activeStep--">上一步，填写商品信息</el-button>
            <el-button type="primary" @click="activeStep++">下一步，填写商品详情</el-button>
          </el-form-item>
        </div>
        <!-- 商品营销结束 -->
        <!-- 商品详情开始 -->
        <div v-show="activeStep === 2" class="product-info">
          <el-row :gutter="20">
            <el-col :span="24" :offset="0">
              <el-form-item label="商品图片">
                <el-upload
                  name="mf"
                  class="avatar-uploader"
                  :action="uploadUrl"
                  :show-file-list="false"
                  :data="{dir: 'product'}"
                  :headers="{Authorization: token}"
                  :on-success="handleAvatarSuccess"
                >
                  <img v-if="imageUrl" :src="imageUrl" class="avatar">
                  <i v-else class="el-icon-plus avatar-uploader-icon" />
                </el-upload>
                <div class="danger-text">
                  请上传封面图片，该图片用于 列表页展示
                </div>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24" :offset="0">
              <el-form-item label="轮播图">
                <el-upload
                  name="mf"
                  class="avatar-uploader"
                  :action="uploadUrl"
                  list-type="picture-card"
                  :data="{dir: 'product'}"
                  :file-list="albumPicList"
                  :headers="{Authorization: token}"
                  :on-success="handleBannerSuccess"
                  :on-remove="handleRemove"
                >
                  <i class="el-icon-plus" />
                </el-upload>
                <div class="danger-text">
                  请上传商品详情轮播图，如果不上传，默认采用上面的图片展示
                </div>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="24" :offset="0">
              <el-form-item label="商品详情">
                <div class="danger-text">
                  商品详情建议采用上传多图的方式。文字过多可能会影响加载效果
                </div>
                <tinymce ref="content" v-model="shopProduct.productContent" :height="500" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item>
            <el-button @click="activeStep--">上一步，填写商品营销</el-button>
            <el-button type="primary" @click="activeStep++">下一步，填写商品关联</el-button>
          </el-form-item>
        </div>
        <!-- 商品详情结束 -->
        <!-- 商品关联 -->
        <div v-show="activeStep === 3" class="product-info">
          <el-row :gutter="20">
            <el-col :span="24" :offset="0">
              <el-form-item label="关联套装">
                <el-button type="primary" @click="selectPack">选择套装</el-button>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24" :offset="0">
              <el-form-item>
                <el-table
                  header-row-class-name="pochi-table-header"
                  :data="shopProduct.shopPackList"
                  stripe
                  border
                  style="width: 100%"
                >
                  <el-table-column prop="id" label="套装编号" />
                  <el-table-column prop="name" label="名称" />
                  <el-table-column prop="productCount" label="商品数" />
                  <el-table-column prop="createTime" label="创建时间" />
                  <el-table-column prop="createBy" label="创建人" />
                </el-table>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24" :offset="0">
              <el-form-item label="套装详情">
                <!-- <el-button type="primary" @click="selectProduct">选择商品</el-button> -->
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24" :offset="0">
              <el-form-item>
                <el-table
                  header-row-class-name="pochi-table-header"
                  :data="shopProduct.productList"
                  stripe
                  border
                  style="width: 100%"
                >
                  <el-table-column prop="name" label="名称" />
                  <el-table-column prop="price" label="价格" />
                  <el-table-column prop="stock" label="库存" />
                  <el-table-column prop="brandName" label="品牌" />
                </el-table>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item>
            <el-button @click="activeStep--">上一步，填写商品营销</el-button>
            <el-button type="primary" @click="update">提交</el-button>
          </el-form-item>
        </div>
        <!-- 商品关联结束 -->
      </el-form>
      <!-- 商品表单结束 -->
      <!-- 商品选择弹窗 -->
      <el-dialog
        append-to-body
        title="选择商品"
        :visible.sync="selectProductDialog"
        width="70%"
      >
        <select-product :default-select="shopProduct.productList" @selectSubmit="selectProductSubmit" />
      </el-dialog>

      <!-- 商品选择弹窗结束 -->
      <!-- 选择套装 -->
      <el-dialog
        title="选择套装"
        :visible.sync="selectPackDialog"
        width="70%"
        append-to-body
      >
        <selectPack @selectSubmit="selectPackSubmit" />
      </el-dialog>

      <!-- 选择套装结束 -->
    </el-card>
  </div>
</template>

<script>
import selectProduct from '@/components/SelectProduct'
import selectPack from './select-pack'
import categoryApi from '@/api/shop/shop-product-category'
import brandApi from '@/api/shop/shop-brand'
import productApi from '@/api/shop/shop-product'
import Tinymce from '@/components/Tinymce'
import { mapGetters } from 'vuex'
export default {
  components: { Tinymce, selectProduct, selectPack },
  data() {
    return {
      // 当前选中的步骤条
      activeStep: 0,
      // 表单对象
      shopProduct: {
        // 商品列表
        productList: [],
        // 套装
        shopPack: {},
        // 临时的套装列表，这里只有一个元素
        shopPackList: []
      },
      // 商品分类树
      categoryTree: [],
      // 品牌数组
      brandList: [],
      // 文件上传路径
      uploadUrl: process.env.VUE_APP_UPLOAD_URL,
      // 图片回显地址
      imageUrl: null,
      // 存放临时的轮播图
      albumPicList: [],
      // 商品选择弹窗
      selectProductDialog: false,
      // 套装选择弹窗
      selectPackDialog: false,
      flag: true,
      // 表单校验对象
      rules: {
        brandId: [
          { required: true, message: '请选择商品品牌', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '请输入标题', trigger: 'blur' }
        ],
        stock: [
          { required: true, message: '请输入库存', trigger: 'blur' }
        ],
        sort: [
          { required: true, message: '请输入排序编码', trigger: 'blur' }
        ],
        specs: [
          { required: true, message: '请输入规格', trigger: 'blur' }
        ],
        price: [
          { required: true, message: '请输入价格', trigger: 'blur' }
        ],
        productContent: [
          { required: true, message: '请输入商品详情', trigger: 'blur' }
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
    // 加载商品信息
    const id = this.$route.params.id
    this.getCatgortTree()
    this.getUpdate(id)
  },
  methods: {
    // 根据套装编号查询
    getByPackCode(id) {
      productApi.getProductDetailList(id).then(res => {
        this.shopProduct.productList = res.data
      })
    },
    // 选择商品
    selectProduct() {
      this.selectProductDialog = true
    },
    // 选择套装
    selectPack() {
      this.selectPackDialog = true
    },
    // 提交选中商品触发
    selectProductSubmit(val) {
      this.selectProductDialog = false
      this.shopProduct.productList = val
    },
    // 提交选中套装触发
    selectPackSubmit(val) {
      this.selectPackDialog = false
      this.shopProduct.shopPack = val
      this.shopProduct.shopPackList = [val]
      this.getByPackCode(val.id)
    },
    // 根据id查询回显
    getUpdate(id) {
      productApi.getUpdate(id).then(res => {
        this.shopProduct = res.data
        this.flag = false
        if (this.shopProduct.shopPack) {
          this.shopProduct.shopPackList = [this.shopProduct.shopPack]
        }
        if (this.shopProduct.pic) {
          this.imageUrl = this.shopProduct.pic
        }
        // 回显轮播图
        const albumPicList = this.shopProduct.albumPicList
        if (albumPicList && albumPicList[0]) {
          albumPicList.forEach(e => this.albumPicList.push({ url: e }))
        }
        this.changeCategory(this.shopProduct.categoryIds)
        // 如果在对话框中，可能会报content是undefined的错误
        // 这个时候，只需要使用 nextTick就可以了
        // 在这里面就可以保证这段代码一定在组件创建完毕后执行
        // 这就意味着，content一定存在
        this.$nextTick(() => {
          // 回显富文本
          this.$refs.content.setContent(this.shopProduct.productContent)
        })
      })
    },
    // 查询分类树
    getCatgortTree() {
      categoryApi.getTree().then(res => {
        this.categoryTree = res.data
      })
    },
    // 根据分类ID查询商品品牌列表
    getBrandByCategoryId() {
      brandApi.getByCategoryId(this.shopProduct.categoryId).then(res => {
        this.brandList = res.data
      })
    },
    // 选中的分类发生变化时触发
    changeCategory(item) {
      if (this.flag) {
        this.$set(this.shopProduct, 'brandId', null)
      }
      if (!item || !item[0]) {
        this.brandList = []
        this.$set(this.shopProduct, 'brandId', null)
        this.$set(this.shopProduct, 'categoryId', null)
        return
      }
      // 取出最后一个分类ID
      const leafCategory = item[item.length - 2]
      // 赋值给商品分类ID
      this.$set(this.shopProduct, 'categoryId', leafCategory)
      // 加载品牌
      this.getBrandByCategoryId()
      this.flag = true
    },
    // 上传图片成功后的回调
    handleAvatarSuccess(res, file) {
      this.$message.success(res.msg)
      this.imageUrl = res.data
      this.shopProduct.pic = this.imageUrl
    },
    // 删除轮播图触发
    handleRemove(file, fileList) {
      // 根据uid去删除
      this.albumPicList.splice(
        this.albumPicList.findIndex(e => e.uid === file.uid),
        1
      )
    },
    // 上传轮播图触发
    handleBannerSuccess(res, file) {
      this.albumPicList.push({
        uid: file.uid,
        url: res.data
      })
    },
    // 提交表单
    update() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          if (this.imageUrl != null) {
            const albumPicList = []
            this.albumPicList.forEach(e => albumPicList.push(e.url))
            this.shopProduct.albumPicList = albumPicList
            productApi.update(this.shopProduct).then(res => {
              this.$message.success(res.msg)
              this.$router.push('/product/list')
            })
          } else {
            this.$message.error('请上传商品图片')
          }
        } else {
          this.$message.error('请返回上一步检查，必填项不能为空')
        }
      })
    }
  }
}
</script>

<style scoped>
  .product-card {
    width: 800px;
    margin: auto;
    padding: 20px;
  }
  .form {
    margin-top: 50px;
  }
  .danger-text {
    color: red;
  }
</style>

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
    height: 300px;
    line-height: 300px;
    text-align: center;
  }
  .avatar {
    width: 178px;
    height: 300px;
    display: block;
  }
</style>
