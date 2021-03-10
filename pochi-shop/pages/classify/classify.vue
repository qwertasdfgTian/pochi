<template>
	<view class="page" ref="page">
		<!-- 搜索 -->
		<view class="search-index">
			<!-- #ifndef H5 -->
			<view class="icon" @click="onCode">
				<text class="iconfont icon-saoyisao"></text>
			</view>
			<!-- #endif -->
			<!-- #ifdef  H5 -->
			<view class="icon" @click="onPayCode">
				<text class="iconfont icon-fukuanma"></text>
			</view>
			<!-- #endif -->
			<view class="search">
				<view class="iconfont icon-fadajing"></view>
				<input type="text" placeholder="输入搜索内容" />
			</view>
			<view class="icon">
				<text class="iconfont icon-xiaoxi"></text>
			</view>
		</view>
		<!-- 分类数据 -->
		<scroll-view scroll-x class="classify-scroll bg-white nav">
			<view class="flex text-center">
				<view class="cu-item flex-sub" :class="index==TabCur?'text-orange cur':''" v-for="(item,index) in categoryTree"
				 :key="index" @tap="tabSelect" :data-id="index">
					{{item.name}}
				</view>
			</view>
		</scroll-view>
		<view class="classify-data" :style="'height:'+height+'px'">
			<view class="classify-one">
				<scroll-view scroll-y class="classify-list">
					<view @tap="tabLeftChange" class="list" :class="leftCur === index ? 'action': ''" v-for="(item,index) in leftCategory"
					 :key="index"  :data-id="index">
						<text>{{item.name}}</text>
					</view>
				</scroll-view>
			</view>
			<view class="classify-two-three">
				<scroll-view scroll-y class="scroll">
					<view class="classify-two">
						<view class="two-name">
							<view class="name">类目商品</view>
						</view>
						<view class="classify-three">
							<view class="list" @click="toSearchByCategory(item.id)" v-for="(item, index) in mainCategory" :key="index">
								<image :src="item.icon"></image>
								<text>{{item.name}}</text>
							</view>
						</view>
					</view>
					<view class="classify-two">
						<view class="two-name">
							<view class="name">品牌推荐</view>
						</view>
						<view class="classify-three">
							<view class="list" @click="toSearchByBrand(item.id)" v-for="(item, index) in brandList" :key="index">
								<image :src="item.logo"></image>
								<text>{{item.name}}</text>
							</view>
						</view>
					</view>
				</scroll-view>
			</view>
		</view>
		<!-- tabbar -->
		<TabBar :tabBarShow="1"></TabBar>
	</view>
</template>

<script>
	import TabBar from '../../components/TabBar/TabBar.vue';
	import categoryApi from '@/api/shop-product-category.js'
	import brandApi from '@/api/shop-brand.js'
	export default {
		components: {
			TabBar,
		},
		data() {
			return {
				// 分类树
				categoryTree: [],
				// 品牌列表
				brandList: [],
				// 左侧分类列表
				leftCategory: [],
				// 中间分类
				mainCategory: [],
				// 控制顶部选中
				TabCur: null,
				// 左侧控制选中
				leftCur: null,
				height: 0,
			};
		},
		onReady() {
			setTimeout(() => {
				uni.hideTabBar()
			}, 100)
			let info = uni.createSelectorQuery().select(".page");
			info.boundingClientRect((data) => { //data - 各种参数
				console.log(data.height);
				this.height = data.height - 100;
				// #ifdef APP-PLUS 
				this.height = data.height - 130;
				// #endif 

			}).exec()
		},
		watch: {
			TabCur: function(newVal, oldVal) {
				if (newVal || newVal === 0) {
					// 根据newVal去找到对应的左侧菜单
					this.leftCategory = this.categoryTree[newVal].children
					// 判断一下左侧的激活状态是否为0
					if (this.leftCur === 0) {
						this.mainCategory = this.leftCategory[0].children
						// 获取分类ID
						const categoryId = this.leftCategory[0].id
						this.getBrandByCategory(categoryId)
					} else {
						this.leftCur = 0
					}
				}
			},
			leftCur: function(newVal, oldVal) {
				if (newVal || newVal === 0) {
					this.mainCategory = this.leftCategory[newVal].children
					// 获取分类ID
					const categoryId = this.leftCategory[newVal].id
					this.getBrandByCategory(categoryId)
				}
			}
		},
		onShow() {
			this.getCategory()
		},
		methods: {
			// 携带品牌参数跳转到搜索
			toSearchByBrand(id) {
				const category = this.mainCategory[0]
				uni.navigateTo({
					url: `/pages/SearchGoodsList/SearchGoodsList?categoryIdTemp=${category.id}&brandId=${id}`
				})
			},
			// 跳转到搜索
			toSearchByCategory(id) {
				uni.navigateTo({
					url: '/pages/SearchGoodsList/SearchGoodsList?categoryId=' + id
				})
			},
			// 查询分类树
			getCategory() {
				categoryApi.getTree().then(res => {
					this.categoryTree = res.data
					this.TabCur = 0
				})
			},
			// 根据分类ID查询品牌
			getBrandByCategory(categoryId) {
				brandApi.getByCategoryId(categoryId).then(res => {
					this.brandList = res.data
				})
			},
			// 顶部导航栏点击触发
			tabSelect(e) {
				this.TabCur = e.currentTarget.dataset.id;
			},
			// 左侧导航点击触发
			tabLeftChange(e) {
				console.log(e)
				this.leftCur = e.currentTarget.dataset.id;
			},
			/**
			 * 扫一扫点击
			 */
			onCode() {
				// 只允许通过相机扫码
				uni.scanCode({
					onlyFromCamera: true,
					success: function(res) {
						console.log('条码类型：' + res.scanType);
						console.log('条码内容：' + res.result);
					}
				});
			},
			/**
			 * 付款码点击
			 */
			onPayCode() {
				uni.navigateTo({
					url: '/pages/PaymentCode/PaymentCode'
				})
			}
		}
	}
</script>

<style scoped lang="scss">
	@import 'classify.scss'
</style>
