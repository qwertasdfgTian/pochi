<template>
	<view class="page">
		<!-- 编辑 -->
		<view class="article-edit">
			<view class="edit" @tap="clearHistory">
				<text>清空</text>
			</view>
		</view>
		<!-- 记录列表 -->
		<view class="record-data">
			<view class="record-list" v-for="(val, key,index) in dataObj" :key="key">
				<view class="record-date">
					<text>{{key}}</text>
				</view>
				<uni-swipe-action> 
					<uni-swipe-action-item v-for="(item,idx) in val" :key="idx" @click="removeHistory(item.productId)" :right-options="options" >
						<view class="goods-list">
							<view class="list" @click="toProductInfo(item.productId)">
								<view class="check" :style="isEdit?'display: flex':'display: none'">
									<text class="iconfont icon-check"></text>
								</view>
								<view class="thumb">
									<image :src="item.productPic" mode=""></image>
								</view>
								<view class="item">
									<view class="title">
										<text class="two-omit">{{item.productName}}</text>
									</view>
									<view class="price-more">
										<view class="price">￥{{item.productPrice}}</view>
									</view>
								</view>
							</view>
						</view>
					</uni-swipe-action-item>
				</uni-swipe-action>
			</view>
		</view>
		<view class="bottom" v-if="showBottom">
			没有更多了~
		</view>
		<view class="footer-btn" :style="isEdit?'display: flex':'display: none'">
			<view class="btn">删除</view>
		</view>
	</view> 
</template>

<script>
	import historyApi from '@/api/shop-prodict-history.js'
	export default {
		data() {
			return {
				// 分页对象
				page: {
					// 每页条数
					pageSize: 5,
					// 当前页
					pageNumber: 1
				},
				// 是否展示模态框
				showDialog: false,
				// 滑动操作按钮组
				options: [{
					text: '删除',
					style: {
						backgroundColor: '#dd524d'
					}
				}],
				// 是否展示禁止加载
				showBottom: false,
				// 列表对象
				dataObj: {},
				isEdit: false,
				dataArr: []
			};
		},
		onShow() {
			this.getByPage()
		},
		onReachBottom() {
			if(!this.showBottom) {
				//  上拉加载
				this.page.pageNumber++
				this.getByPage()
			}
		},
		methods: {
			// 打开是否删除历史
			showModal(productId) {
				this.currentProductId = productId
				this.showDialog = true
			},
			// 删除历史
			removeHistory(id) {
				historyApi.deleteHistoryById(id).then(res=>{
					uni.showToast({
						title:'删除成功'
					})
					this.getByPage()
				})
			},
			// 查看商品详情
			toProductInfo(id) {
				uni.navigateTo({
					url: `/pages/GoodsDetails/GoodsDetails?id=${id}`
				})
			},
			// 清空历史记录
			clearHistory() {
				historyApi.clearHistory().then(res=>{
					uni.showToast({
						title:'清空成功'
					})
					this.showBottom = true
					this.dataObj = {}
					this.getByPage()
				})
			},
			// 分页查询历史记录
			getByPage() {
				historyApi.getByPage(this.page).then(res=>{
					this.dataObj={}
					const dataMap = res.data
					const keys = Object.keys(dataMap)
					if(dataMap && keys && keys[0]) {
						keys.forEach(key => {
							this.dataArr = this.dataObj[key]
							console.log(this.dataArr)
							if(this.dataArr && this.dataArr[0]) {
								this.dataArr.push(...dataMap[key])
								this.$set(this.dataObj, key, this.dataArr)
							}else {
								this.$set(this.dataObj, key, dataMap[key])
							}
						})
					}else {
						this.showBottom = true
					}
				})
			}
		}
	}
</script>

<style scoped lang="scss">
	@import 'BrowsingHistory.scss';
</style>