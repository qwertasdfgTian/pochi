<template>
	<view class="page">
		<view class="head">
			<!-- #ifdef APP-PLUS -->
			<view class="title">购物车</view>
			<!-- #endif -->
			<view class="edit" @click="isEdit = !isEdit">
				<text>{{isEdit?'完成':'编辑'}}</text>
			</view>
		</view>
		<!-- 购物车列表 -->
		<mescroll-body ref="mescrollRef" @down="downCallback" @up="upCallback" :down="downOption" :up="upOption" :top="0">
			<view class="cart-list">
				<view class="list" v-for="(item, index) in goodsList" :key="index">
					<view class="check" @click="changeCheck(item)">
						<text class="iconfont icon-checked" v-if="item.check"></text>
						<text class="iconfont icon-check" v-else></text>
					</view>
					<view class="goods" >
						<view class="thumb" @click="toProductInfo(item.productId)">
							<image :src="item.productPic" mode=""></image>
						</view>
						<view class="item">
							<view class="title" @click="toProductInfo(item.productId)">
								<text class="two-omit">{{item.productName}}</text>
							</view>
							<view class="price-num">
								<view class="price" @click="toProductInfo(item.productId)">
									<text class="min">￥</text>
									<text class="max">{{item.price}}</text>
								</view>
								<view class="num">
									<view class="add" @click="changeQuantity(item, -1)">
										<text class="iconfont icon-jian"></text>
									</view>
									<view class="number">
										<text>{{item.quantity}}</text>
									</view>
									<view class="add" @click="changeQuantity(item, 1)">
										<text class="iconfont icon-jia"></text>
									</view>
								</view>
							</view>
						</view>
					</view>
				</view>
			</view>
			<view class="top" v-if="showBottom" style="text-align: center;line-height: 50px;">
				空空如也
			</view>
			<!-- 为你推荐 -->
			<view class="recommend-info">
				<view class="recommend-title">
					<view class="title">
						<image src="/static/wntj_title.png" mode=""></image>
					</view>
				</view>
				<view class="goods-list">
					<view class="list" v-for="(item,index) in recommendGoods" @click="toProductInfo(item.id)" :key="index">
						<view class="pictrue">
							<image :src="item.pic" mode="heightFix"></image>
						</view>
						<view class="title-tag">
							<view class="tag">
								{{item.name}}
							</view>
						</view>
						<view class="price-info">
							<view class="user-price">
								<text class="min">￥</text>
								<text class="max">{{item.price}}</text>
							</view>
							<!-- <view class="vip-price">
								<image src="/static/vip_ico.png"></image>
								<text>￥{{(item.price * 0.9).toFixed(2)}}</text>
							</view> -->
						</view>
					</view>
				</view>
			</view>
			<!-- 结算 -->
			<view class="close-account">
				<view class="check-total">
					<view class="total" style="padding-left: 40rpx;">
						<text>合计：</text>
						<text class="price">￥{{totalMoney}}</text>
					</view>
				</view>
				<view class="account">
					<view class="btn-calculate" @click="toPay" v-if="!isEdit">
						<text>去结算</text>
					</view>
					<view class="btn-del" v-else>
						<text class="attention" @click="toMove">移入关注</text>
						<text class="del" @click="toDelete">删除</text>
					</view>
				</view>
			</view>
		</mescroll-body>
		<!-- tabbar -->
		<TabBar :tabBarShow="3"></TabBar>
		
		<view class="cu-modal" :class="showDelete?'show':''">
			<view class="cu-dialog">
				<view class="cu-bar bg-white justify-end">
					<view class="content">移除商品</view>
					<view class="action" @tap="showDelete = false">
						<text class="cuIcon-close text-red"></text>
					</view>
				</view>
				<view class="padding-xl">
					是否将商品从购物车中移除
				</view>
				<view class="cu-bar bg-white justify-end">
					<view class="action">
						<button class="cu-btn line-green text-green" @tap="showDelete = false">取消</button>
						<button class="cu-btn bg-green margin-left" @tap="deleteByIds">确定</button>
					</view>
				</view>
			</view>
		</view>
		
		<view class="cu-modal" :class="showMove?'show':''">
			<view class="cu-dialog">
				<view class="cu-bar bg-white justify-end">
					<view class="content">移入收藏</view>
					<view class="action" @tap="showMove = false">
						<text class="cuIcon-close text-red"></text>
					</view>
				</view>
				<view class="padding-xl">
					是否将商品从购物车移入到收藏中
				</view>
				<view class="cu-bar bg-white justify-end">
					<view class="action">
						<button class="cu-btn line-green text-green" @tap="showMove = false">取消</button>
						<button class="cu-btn bg-green margin-left" @tap="moveToCollection">确定</button>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import TabBar from '../../components/TabBar/TabBar.vue';
	// 引入mescroll-mixins.js
	import MescrollMixin from "@/components/mescroll-uni/mescroll-mixins.js";
	import cartApi from '@/api/shop-cart-item.js'
	import productApi from '@/api/shop-product.js'
	export default {
		mixins: [MescrollMixin], // 使用mixin
		components: {
			TabBar,
		},
		data() {
			return {
				// 分页对象
				page: {
					// 当前页
					pageNumber: 1,
					// 每页条数
					pageSize: 20
				},
				showBottom: false,
				// 购物车商品列表
				goodsList: [],
				// 选中的总条数
				totalCount: 0,
				// 删除商品弹窗
				showDelete: false,
				// 移入收藏弹窗
				showMove: false,
				// 推荐商品
				recommendGoods: [],
				mescroll: null, // mescroll实例对象 (此行可删,mixins已默认)
				// 下拉刷新的配置(可选, 绝大部分情况无需配置)
				downOption: {},
				// 上拉加载的配置(可选, 绝大部分情况无需配置)
				upOption: {
					use: false,
					toTop: {
						src: '',
					}
				},
				isEdit: false,
			};
		},
		computed:{
			totalMoney() {
				const checkList = this.goodsList.filter(e=>e.check)
				let money = 0
				checkList.forEach(e=>{
					money += e.quantity * e.price
				})
				return money.toFixed(2)
			}
		},
		onReady() {
			uni.hideTabBar()
		},
		onShow() {
			this.initData()
		},
		methods: {
			// 查看商品详情
			toProductInfo(id) {
				uni.navigateTo({
					url: `/pages/GoodsDetails/GoodsDetails?id=${id}`
				})
			},
			// 去结算
			toPay() {
				const checkGoods = []
				this.goodsList.forEach(e=>{
					if(e.check) {
						checkGoods.push({cartId: e.id,productId: e.productId, count: e.quantity})
					}
				})
				if(checkGoods[0]) {
					uni.navigateTo({
						url: '/pages/ConfirmOrder/ConfirmOrder?productArr=' + JSON.stringify(checkGoods)
					})
				}
			},
			// 分页查询
			getByPage() {
				cartApi.getByPage(this.page).then(res=>{
					this.goodsList = res.data
					if(this.goodsList.length === 0){
						this.showBottom = true
					}
				})
			},
			// 选中
			changeCheck(item) {
				this.$set(item, 'check', !item.check)
				if(item.check) {
					this.totalCount += item.quantity
				}else {
					this.totalCount -= item.quantity
				}
			},
			// 查询推荐
			getRecommendList() {
				productApi.getRecommendList().then(res=>{
					this.recommendGoods = res.data
				})
			},
			// 改变数量
			changeQuantity(item, num) {
				this.$set(item, 'quantity', item.quantity + num)
				if(item.quantity <= 0){
				    item.quantity = 1
				    uni.showToast({
					  icon:'none',
					  title:'最少购买1个商品'
				    })
				}
			    this.totalCount += item.quantity	
			},
			// 移入收藏
			moveToCollection() {
				const ids = []
				this.goodsList.forEach(e=>{
					if(e.check) {
						ids.push(e.id)
					}
				})
				cartApi.moveToCollection(ids).then(res=>{
					uni.showToast({
						title:res.msg
					})
					this.initData()
				})
			},
			// 批量删除
			deleteByIds() {
				const ids = []
				this.goodsList.forEach(e=>{
					if(e.check) {
						ids.push(e.id)
					}
				})
				cartApi.deleteByIds(ids).then(res=>{
					uni.showToast({
						title:res.msg
					})
					this.initData()
				})
			},
			// 询问是否移入收藏
			toMove() {
				this.showMove = true
			},
			// 询问是否删除
			toDelete() {
				this.showDelete = true
			},
			// 初始化数据
			initData() {
				this.goodsList = []
				this.totalCount = 0
				this.page.pageNumber = 1
				this.showMove = false
				this.showDelete = false
				this.showBottom = false
				this.getByPage()
				this.isEdit = false
				this.getRecommendList()
			},
			/*下拉刷新的回调, 有三种处理方式:*/
			downCallback() {
				this.mescroll.endSuccess();
			},
			/*上拉加载的回调*/
			upCallback(page) {
				setTimeout(() => {
					this.mescroll.endByPage(10, 20);
				}, 2000)
			},
			/**
			 * 跳转点击
			 * @param {String} type 跳转类型
			 */
			onSkip(type) {
				switch (type) {
					case 'classify':
						uni.navigateTo({
							url: '/pages/SearchGoodsList/SearchGoodsList',
						})
						break;
					case 'goods':
						uni.navigateTo({
							url: '/pages/GoodsDetails/GoodsDetails',
							animationType: 'zoom-fade-out',
							animationDuration: 200
						})
						break;
				}
			}
		}
	}
</script>

<style scoped lang="scss">
	@import 'cart.scss';
</style>