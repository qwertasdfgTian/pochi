<template>
	<view class="page">
		<!-- 编辑 -->
		<!-- <view class="article-edit">
			<view class="edit" @click="isEdit = !isEdit">
				<text>{{isEdit?'完成':'编辑'}}</text>
			</view>
		</view> -->
		<!-- 文章列表 -->
		<uni-swipe-action>
			<view  class="article-list">
			<uni-swipe-action-item @click="showModal(item.productId)" :right-options="options" v-for="(item,index) in dataList" :key="index">
				<view class="list" @click="toProductInfo(item.productId)">
					<view class="check" :style="isEdit?'display: flex':'display: none'">
						<text class="iconfont icon-check"></text>
					</view>
					<view class="item">
						<view class="title">
							<text>{{item.productName}}</text>
						</view>
						<view class="like">
							<text>{{item.collectionCount}}人喜欢</text>
						</view>
					</view>
					<view class="thumb">
						<image :src="item.productPic" mode=""></image>
					</view>
				</view>
			</uni-swipe-action-item>
			</view>
		</uni-swipe-action>
		<view class="top" v-if="showBottom" style="text-align: center;line-height: 50px;">
			暂时没有收藏
		</view>
		<!-- 底部 -->
		<view class="footer-btn" :style="isEdit?'display: flex':'display: none'">
			<view class="btn">取消收藏</view>
		</view>
		<view class="cu-modal" :class="showDialog?'show':''">
			<view class="cu-dialog">
				<view class="cu-bar bg-white justify-end">
					<view class="content">取消收藏</view>
					<view class="action" @tap="showDialog = false">
						<text class="cuIcon-close text-red"></text>
					</view>
				</view>
				<view class="padding-xl">
					是否取消该商品收藏
				</view>
				<view class="cu-bar bg-white justify-end">
					<view class="action">
						<button class="cu-btn line-green text-green" @tap="showDialog = false">取消</button>
						<button class="cu-btn bg-green margin-left" @tap="removeCollection()">确定</button>
		
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import collectionApi from '@/api/shop-product-collection.js'
	import productApi from '@/api/shop-product.js'
	export default {
		data() {
			return {
				// 分页对象
				page: {
					// 当前页
					pageNumber: 1,
					// 每页条数
					pageSize: 10
				},
				// 滑动操作按钮组
				options: [{
					text: '删除',
					style: {
						backgroundColor: '#dd524d'
					}
				}],
				// 是否展示模态框
				showDialog: false,
				// 当前商品ID
				currentProductId: null,
				loadFlag: true,
				// 数据列表
				dataList: [],
				isEdit: false,
				showBottom: false
			};
		},
		onReachBottom() {
			if (this.loadFlag) {
				// 上拉加载
				this.page.pageNumber++
				this.getByPage()
			}
		},
		onShow() {
			this.getByPage()
		},
		methods: {
			// 查看商品详情
			toProductInfo(id) {
				uni.navigateTo({
					url: `/pages/GoodsDetails/GoodsDetails?id=${id}`
				})
			},
			// 分页查询收藏
			getByPage() {
				collectionApi.getByPage(this.page).then(res => {
					this.dataList.push(...res.data.list)
					if (res.data.totalCount === this.dataList.length) {
						this.loadFlag = false
					}
					if(res.data.totalCount === 0){
						this.showBottom = true
					}
				})
			},
			// 打开是否取消收藏弹窗
			showModal(productId) {
				this.currentProductId = productId
				this.showDialog = true
			},
			// 删除收藏仓
			removeCollection() {
				collectionApi.changeCollection({productId: this.currentProductId}).then(res=>{
					uni.showToast({
						title:'取消成功'
					})
					this.showDialog = false
					this.initData()
				})
			},
			// 初始化数据
			initData() {
				this.dataList = []
				this.page.pageNumber = 1
				this.getByPage()
			}
		}
	}
</script>

<style scoped lang="scss">
	@import 'ContentCollection.scss'
</style>
