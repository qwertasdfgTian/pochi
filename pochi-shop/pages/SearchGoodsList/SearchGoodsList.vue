<template>
	<view class="page">
		<!-- 搜索 -->
		<view class="search-head">
			<view class="back" @click="onBack">
				<text></text>
			</view>
			<view class="search">
				<text class="iconfont icon-fadajing" @click="initData"></text>
				<input type="text" v-model="searchParam.keyword" placeholder="搜索商品"/>
			</view>
			<view class="cut" @click="isList = !isList">
				<text class="iconfont" :class="isList?'icon-shitu01':'icon-shitu02'"></text>
			</view>
		</view>
		<!-- 筛选 -->
		<view class="screen-info">
			<view class="screen-list">
				<view class="list" @click="handleSort(1)">
					<text :class="{'action':searchParam.sorted===1}" >综合</text>
					<text :class="{'action':searchParam.sorted===1}" class="iconfont icon-sanjiao icon_z"></text>
				</view>
				<view class="list" @click="handleSort(2)">
					<text :class="{'action':searchParam.sorted===2}">销量</text>
					<text :class="{'action':searchParam.sorted===2}" class="iconfont icon-sanjiao icon_z"></text>
				</view>
				<view class="list" @click="handleSort(3)">
					<text>价格</text>
					<view class="icon_j">
						<text :class="{'action':searchParam.sorted===3 && searchParam.sortType == 1}" class="iconfont icon-sanjiao up"></text>
						<text :class="{'action':searchParam.sorted===3 && searchParam.sortType == 2}" class="iconfont icon-sanjiao down"></text>
					</view>
				</view>
				<view class="list" @click="Drawer">
					<text>筛选</text>
					<text class="iconfont icon-shaixuan icon_s"></text>
				</view>
			</view>
		</view>
		<!-- 商品列表 -->
		<view class="goods-data">	
			<mescroll-body ref="mescrollRef" @init="mescrollInit" @down="downCallback" @up="upCallback" :down="downOption" :up="upOption"
			 :top="0">	
				<view class="goods-list">
					<view :class="isList?'list-view':'list-li'" v-for="(item,index) in goodsList" @click="onGoodsList(item.productId)" :key="index">
						<view class="thumb">
							<image :src="item.pic" mode="heightFix"></image>
						</view>
						<view class="item">
							<view class="title">
								<text class="two-omit">{{item.name}}</text>
							</view>
							<view class="price">
								<view class="retail-price">
									<text class="min">￥</text>
									<text class="max">{{item.price}}</text>
								</view>
							</view>
						</view>
					</view>
				</view>
				<view v-if="showBottom" style="text-align: center;line-height: 50rpx;">
					抱歉，没有找到商品额~
				</view>
			</mescroll-body>
		</view>
		<!-- 抽屉 -->
		<view class="cu-modal drawer-modal justify-end dialog-container" :class="{'show':isDrawer}" @click="isDrawer = false" style="position: fixed;">
			<view class="cu-dialog basis-lg" @click.stop="isDrawer = true">
				<view class="price-screen">
					<view class="title">
						<text>价格区间</text>
					</view>
					<view class="price-section">
						<input type="number" v-model="searchParam.priceStart" placeholder="最低价">
						<text></text>
						<input type="number" v-model="searchParam.priceEnd" placeholder="最高价">
					</view>
				</view>
				<view class="serve">
					<view class="title">
						<text>分类</text>
					</view>
					<view class="serve-list">
						<view :class="searchParam.categoryId === item.id?'action': ''" @click="searchByCategory(item.id)" class="list" v-for="(item, index) in categoryBrand.categoryList" :key="index">
							<text>{{item.name}}</text>
						</view>
					</view>
				</view>
				<view class="serve">
					<view class="title">
						<text>品牌</text>
					</view>
					<view class="serve-list">
						<view :class="searchParam.brandId === item.id?'action': ''" @click="searchByBrand(item.id)" class="list" v-for="(item, index) in categoryBrand.brandList" :key="index">
							<text>{{item.name}}</text>
						</view>
					</view>
				</view>
				
				
				<view class="operation-btn">
					<view class="btn" @click.stop="isDrawer = false">
						<text>取消</text>
					</view>
					<view @click="initData" class="btn action">
						<text>确认</text>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import productApi from '@/api/shop-product.js'
	import categoryApi from '@/api/shop-product-category.js'
	// 引入mescroll-mixins.js
	import MescrollMixin from "@/components/mescroll-uni/mescroll-mixins.js";
	export default {
		mixins: [MescrollMixin], // 使用mixin
		data() {
			return {
				showBottom: false,
				mescroll: null, // mescroll实例对象 (此行可删,mixins已默认)
				// 下拉刷新的配置(可选, 绝大部分情况无需配置)
				downOption: {},
				// 上拉加载的配置(可选, 绝大部分情况无需配置)
				upOption: {},
				// 列表视图切换
				isList: true,
				// 筛选弹窗
				isScreen: false,
				// 抽屉
				isDrawer: false,
				loadFlag: false,
				goodsList: [],
				// 右侧分类品牌
				categoryBrand: {},
				// 搜索用的分类ID
				searchCategoryId: null,
				// 搜索参数
				searchParam: {
					// 排序列
					sorted: 1,
					// 正序倒序排列，1正序，2倒序
					sortType: 2,
					// 关键字
					keyword: '',
					// 当前页
					pageNumber: 1,
					// 每页条数
					pageSize: 10
				},
				flag: true
			}
		},
		onLoad(params) {
			this.searchParam.keyword = decodeURIComponent(params.keyword || '');
			const categoryId = params.categoryId
			const brandId = params.brandId
			const categoryIdTemp = params.categoryIdTemp
			if(categoryId) {
				this.$set(this.searchParam, 'categoryId', categoryId)
				this.searchParam.categoryId = categoryId
				this.searchCategoryId = categoryId
			}else {
				this.searchCategoryId = categoryIdTemp
			}
			if(brandId) {
				this.$set(this.searchParam, 'brandId', brandId)
				this.searchParam.brandId = brandId
			}
			this.searchProduct()
			
			setTimeout(()=>{
				this.loadFlag = true
			}, 100)
		},
		methods: {
			// 点击加载筛选，只加载一次
			Drawer(){
				this.isDrawer = true
				// 加载右侧列表
				if(this.flag){
					if(this.searchCategoryId){
						categoryApi.getCategoryAndBrandById(this.searchCategoryId).then(res=>{
							this.categoryBrand = res.data
						})				
					}else {
						categoryApi.getCategoryAndBrandById(this.goodsList[0].categoryId).then(res=>{
							this.categoryBrand = res.data
						})
					}
					this.flag= false
				}	
			},
			// 根据分类查询
			searchByCategory(id) {
				this.$set(this.searchParam, 'categoryId', id)
			},
			// 根据品牌查询
			searchByBrand(id) {
				this.$set(this.searchParam, 'brandId', id)
			},
			// 处理排序
			handleSort(type) {
				this.searchParam.sorted = type
				switch(type) {
					case 1:
						this.searchParam.sortType = 2;
						break;
					case 2:
						this.searchParam.sortType = 2;
						break;
					case 3:
						if(this.searchParam.sortType === 1) {
							this.searchParam.sortType = 2;
						}else {
							this.searchParam.sortType = 1;
						}
						break;
				}
				this.initData()
			},
			// 初始化数据
			initData() {
				this.searchParam.pageNumber = 1
				this.goodsList = []
				this.searchProduct()
				uni.pageScrollTo({
					scrollTop: 0,
					duration: 200
				});
			},
			// 搜索商品
			searchProduct() {
				this.showBottom= false
				productApi.search(this.searchParam).then(res => {
					if (res.data.list) {
						this.goodsList.push(...res.data.list)
						this.isDrawer = false
					}
					if (res.data.list.length === 0){
						this.showBottom= true
					}
				})
			},
			/*下拉刷新的回调, 有三种处理方式:*/
			downCallback() {
				if(this.loadFlag) {
					console.log("下拉刷新")
					this.mescroll.endSuccess();
					this.initData()
				}
			},
			/*上拉加载的回调*/
			upCallback(page) {
				if(this.loadFlag) {
					console.log("上拉加载")
					this.mescroll.endByPage(10, 20);
					this.searchParam.pageNumber = this.searchParam.pageNumber + 1
					this.searchProduct()
				}
			},
			/**
			 * 返回点击
			 */
			onBack() {
				uni.navigateBack();
			},
			/**
			 * 商品列表点击
			 */
			onGoodsList(item, index) {
				uni.navigateTo({
					url: '/pages/GoodsDetails/GoodsDetails?id='+item,
					animationType: 'zoom-fade-out',
					animationDuration: 200
				})
			}
		}
	}
</script>

<style scoped lang="scss">
	@import 'SearchGoodsList.scss';
</style>
