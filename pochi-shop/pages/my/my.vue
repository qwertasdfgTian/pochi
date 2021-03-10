<template>
	<view class="page">
		<view class="my-top">
			<!-- head -->
			<view class="head" :style="'background-color: rgba(255,255,255,'+(scrollTop/50)+');'">
				<view class="portrait">
					<image v-show="scrollTop>20" src="//img11.360buyimg.com/jdphoto/s40x40_jfs/t1/25255/18/10701/1678/5c89f892E78c04688/684d63c0d68e39b1.png"></image>
				</view>
				<view class="title">
					<text v-show="scrollTop>20">我的</text>
				</view>
				<view class="setting-mess">
					<view class="setting" @click="onSetting">
						<text class="iconfont icon-setting" :style="scrollTop>20?'color:#333333':''"></text>
					</view>
				</view>
			</view>
			<!-- 用户信息 -->
			<view class="user-info" v-if="user.id">
				<view class="portrait">
					<image :src="user.header"></image>
				</view>
				<view class="info">
					<view class="nickname">
						<text>{{user.nickName}}</text>
					</view>
					<view class="rank">
						<image src="/static/rank.png"></image>
						<text>v1</text>
					</view>
				</view>
			</view>
			<view class="user-info" v-else @click="onUserInfo">
				<view class="portrait">
					<image src="http://img2.imgtn.bdimg.com/it/u=1039075865,3371165857&fm=26&gp=0.jpg"></image>
				</view>
				<view class="info">
					<view class="nickname">
						<text>登录/注册</text>
					</view>
				</view>
			</view>
			<!-- 关注区 -->
			<view class="focus-area">
				<view class="list" @click="onCollect('content')">
					<view class="num">
						<text>{{collectionSum}}</text>
					</view>
					<view class="title">
						<text>商品关注</text>
					</view>
				</view>
				<view class="list" @click="onCollect('content')">
					<view class="num">
						<text>{{collectionSum}}</text>
					</view>
					<view class="title">
						<text>喜欢的内容</text>
					</view>
				</view>
				<view class="list" @click="onCollect('record')">
					<view class="num">
						<text>{{historySum}}</text>
					</view>
					<view class="title">
						<text>浏览记录</text>
					</view>
				</view>
			</view>
			<!-- 会员 -->
			<view class="vip-info" @click="onMmeberVip">
				<view class="vip">
					<text>超级会员</text>
					<text class="line"></text>
				</view>
				<view class="vip-explain">
					<text>超级会员一年预计可省99元</text>
				</view>
				<view class="vip-btn">
					<text>立即查看</text>
				</view>
			</view>
		</view>
		<!-- 订单信息 -->
		<view class="order-info">
			<view class="list" @click="onSkipOrder(0)">
				<view class="icon">
					<text class="iconfont icon-daifukuan"></text>
					<text class="num" v-if="orderTypeNum.waitpaynum > 0">{{orderTypeNum.waitpaynum}}</text>
				</view>
				<view class="title">
					<text>待付款</text>
				</view>
			</view>
			<view class="list" @click="onSkipOrder(2)">
				<view class="icon">
					<text class="iconfont icon-daifahuo"></text>
					<text class="num" v-if="orderTypeNum.waitsendnum > 0">{{orderTypeNum.waitsendnum}}</text>
				</view>
				<view class="title">
					<text>待发货</text>
				</view>
			</view>
			<view class="list" @click="onSkipOrder(3)">
				<view class="icon">
					<text class="iconfont icon-daishouhuo"></text>
					<text class="num" v-if="orderTypeNum.alreadysendnum > 0">{{orderTypeNum.alreadysendnum}}</text>
				</view>
				<view class="title">
					<text>待收货</text>
				</view>
			</view>
			<view class="list" @click="onSkipOrder(4)">
				<view class="icon">
					<text class="iconfont icon-daipingjia"></text>
					<text class="num" v-if="orderTypeNum.alreadysignnum > 0">{{orderTypeNum.alreadysignnum}}</text>
				</view>
				<view class="title">
					<text>待评价</text>
				</view>
			</view>
			<!-- <view class="list" @click="onSkipOrder(5)">
				<view class="icon">
					<text class="iconfont icon-tuikuan"></text>
				</view>
				<view class="title">
					<text>退换</text>
				</view>
			</view> -->
		</view>
		<!-- 钱包 -->
		<view class="wallet-info">
			<view class="list">
				<view class="icon">
					<text class="number">{{myWalletVo.myPoint}}</text>
				</view>
				<view class="title">
					<text>积分</text>
				</view>
			</view>
			<view class="list" @click="onWallet('coupon')">
				<view class="icon">
					<text class="number">{{myWalletVo.couponCount}}</text>
				</view>
				<view class="title">
					<text>优惠券</text>
				</view>
			</view>
			<view class="list">
				<view class="icon">
					<text class="number" v-if="consumption!=null">￥{{consumption}}</text>
					<text class="number" v-else>￥0</text>
				</view>
				<view class="title">
					<text>本月累计消费</text>
				</view>
			</view>
			<view class="list">
				<view class="icon">
					<text class="iconfont icon-qianbao"></text>
				</view>
				<view class="title">
					<text class="action">我的钱包</text>
				</view>
			</view>
		</view>
		<!-- 为你推荐 -->
		<view class="recommend-info">
			<view class="recommend-title">
				<view class="title">
					<image src="/static/wntj_title.png" mode=""></image>
				</view>
			</view>
			<view class="goods-list">
				<view class="list" v-for="(item,index) in goodsList" @click="toProductInfo(item.id)" :key="index">
					<view class="pictrue">
						<image :src="item.pic" mode="heightFix"></image>
					</view>
					<view class="title-tag">
						<view class="tag">
							<text v-if="item.is_goods === 1">特价</text>
							{{item.name}}
						</view>
					</view>
					<view class="price-info">
						<view class="user-price">
							<text class="min">￥</text>
							<text class="max">{{item.price}}</text>
						</view>
					</view>
				</view>
			</view>
		</view>
		<!-- tabbar -->
		<TabBar :tabBarShow="4"></TabBar>
	</view>
</template>

<script>
	import TabBar from '../../components/TabBar/TabBar.vue';
	import productApi from '@/api/shop-product.js'
	import shopUserApi from '@/api/shop-user.js'
	import shopProductHistoryApi from '@/api/shop-prodict-history.js'
	import shopProductCollectionApi from '@/api/shop-product-collection.js'
	import shopOrderApi from '@/api/shop-order.js'
	import shopOrderPayApi from '@/api/shop-order-pay.js'
	export default {
		components: {
			TabBar,
		},
		data() {
			return {
				scrollTop: 0,
				// 当前登录用户
				user: {},
				isHotline: false,
				goodsList: [],
				// 我的钱包
				myWalletVo: {
					myPoint: 0,
					couponCount: 0
				},
				// 历史数量
				historySum: 0,
				// 收藏数量
				collectionSum: 0,
				// 本月累计消费
				consumption: 0.00,
				// 订单数量
				orderTypeNum: {}
			};
		},
		onShow() {
			this.user = uni.getStorageSync('loginUser')
			this.getMyWallet();
			this.getSumCollection();
			this.getConsumption();
			this.getOrderTypeNum();
			this.getSumHistory()
		},
		onLoad() {	
			console.log(this.user)
			this.getRecommendList()		
		},
		onReady() {
			uni.hideTabBar();	
		},
		onPageScroll(e) {
			this.scrollTop = e.scrollTop;
		},
		methods: {
			// 查看商品详情
			toProductInfo(id) {
				uni.navigateTo({
					url: `/pages/GoodsDetails/GoodsDetails?id=${id}`
				})
			},
			// 查询我的钱包
			getMyWallet() {
				shopUserApi.getMyWallet().then(res => {
					this.myWalletVo = res.data
				})
			},
			// 查询收藏总数
			getSumCollection() {
				shopProductCollectionApi.getSumCollection().then(res => {
					this.collectionSum = res.data
				})
			},
		    // 查询历史记录总数
			getSumHistory() {
				shopProductHistoryApi.getSumHistory().then(res => {
					this.historySum = res.data
				})
			},
			// 查询本月累计消费
			getConsumption() {
				shopOrderPayApi.getConsumption().then(res => {
					this.consumption = res.data
				})
			},
			// 查询各种状态的订单数量
			getOrderTypeNum() {
				shopOrderApi.getOrderTypeNum().then(res => {
					this.orderTypeNum = res.data
				})
			},
			// 查询推荐
			getRecommendList() {
				productApi.getRecommendList().then(res => {
					this.goodsList = res.data
				})
			},
			// 测试方法
			requestMessage() {
				uni.requestSubscribeMessage({
					tmplIds: ['mR8UIyXR7PAEvE9n-zJpPHVV5it3-5qtRECfSOfXvAQ'],
					compete: (res)=>{
						console.log(res)
					}
				})
			},
			/**
			 * 关注跳转
			 */
			onCollect(type) {
				switch (type) {
					case 'goods':
						uni.navigateTo({
							url: '/pages/GoodsOn/GoodsOn'
						})
						break;
					case 'content':
						uni.navigateTo({
							url: '/pages/ContentCollection/ContentCollection'
						})
						break;
					case 'record':
						uni.navigateTo({
							url: '/pages/BrowsingHistory/BrowsingHistory'
						})
						break;
				}
			},
			/**
			 * 订单
			 */
			onSkipOrder(type) {
				if (type === 5) {
					uni.navigateTo({
						url: '/pages/AfterSalesOrder/AfterSalesOrder',
					})
					return;
				}
				uni.navigateTo({
					url: '/pages/MyOrderList/MyOrderList?type=' + type,
				})
			},
			/**
			 * 钱包跳转点击
			 */
			onWallet(type) {
				switch (type) {
					case 'integral':
						uni.navigateTo({
							url: '/pages/IntegralDetails/IntegralDetails',
						})
						break;
					case 'coupon':
						uni.navigateTo({
							url: '/pages/MyCoupon/MyCoupon',
						})
						break;
					case 'wallet':
						uni.navigateTo({
							url: '/pages/MyWallet/MyWallet',
						})
						break;
					case 'SignIn':
						uni.navigateTo({
							url: '/pages/SignIn/SignIn',
						})
						break;
					case 'payment':
						uni.navigateTo({
							url: '/pages/PaymentCode/PaymentCode',
						})
						break;
				}
			},
			/**
			 * 我的服务点击
			 */
			onServer(type) {
				switch (type) {
					case 'feedback':
						uni.navigateTo({
							url: '/pages/Feedback/Feedback'
						})
						break;
					case 'serve':
						this.isHotline = true;
						break;
				}
			},
			/**
			 * 设置点击
			 */
			onSetting() {
				uni.navigateTo({
					url: '/pages/Setting/Setting'
				})
			},
			/**
			 * 消息点击
			 */
			onMessage() {
				uni.navigateTo({
					url: '/pages/Message/Message'
				})
			},
			/**
			 * 会员点击
			 */
			onMmeberVip() {
				uni.navigateTo({
					url: '/pages/MembersOpened/MembersOpened',
				})
			},
			/**
			 * 跳转点击
			 * @param {String} type 跳转类型
			 */
			onSkip(type) {
				switch (type) {
					case 'goods':
						uni.navigateTo({
							url: '/pages/GoodsDetails/GoodsDetails',
							animationType: 'zoom-fade-out',
							animationDuration: 200
						})
						break;
				}
			},
			/**
			 * 用户信息点击
			 * @param {Number} type
			 */
			onUserInfo() {
				uni.navigateTo({
					url: '/pages/login/login'
				})
			}
		}
	}
</script>

<style scoped lang="scss">
	@import 'my.scss';
</style>
