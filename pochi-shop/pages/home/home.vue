<template>
	<view class="page">
		<view class="head-info">
			<!-- 搜索 -->
			<view class="head-search">
				<view class="icon-info" @click="onCode">
					<text class="iconfont icon-saoyisao"></text>
					<!-- <image src="/static/xiaoxi_ico.png" mode=""></image> -->
				</view>
				<view class="search" @click="onSearch">
					<view class="icon">
						<image src="/static/fdj_ico.png" mode=""></image>
					</view>
					<view class="hint">
						<text class="max">搜索</text>
						<text class="min">热门内容</text>
					</view>
				</view>
			</view>
			<!-- 分类列表 -->
			<!-- <view class="classify-list">
				<view class="list" v-for="(item,index) in classList" :class="{'action':classifyShow==index}" @click="onClassify(item,index)"
				 :key="index">
					<text>{{item.name}}</text>
					<text class="line" v-show="classifyShow==index"></text>
				</view>
			</view> -->
		</view>
		<mescroll-body ref="mescrollRef" @down="downCallback" @up="upCallback" :down="downOption" :up="upOption" :top="0">
			<view class="main" v-show="classifyShow===0">
				<!-- banner -->
				<view class="banner">
					<swiper class="screen-swiper square-dot" indicator-dots="true" circular="true" autoplay="true" interval="5000"
					 duration="500">
						<swiper-item v-for="(item,index) in swiperList" @tap="clickBanner(item)" :key="index">
							<image :src="item.pic" mode="aspectFill"></image>
						</swiper-item>
					</swiper>
				</view>
				<!-- 菜单导航 -->
				<view class="menu-nav">
					<scroll-view scroll-x @scroll="ScrollMenu" class="nav-list">
						<view class="nav" ref="nav" :style="navList.length<=10?'flex-direction:row':''">
							<view class="list" v-for="(item,index) in navList" @click="onSkip('menu', item.id)" :key="item.id">
								<image :src="item.icon" mode=""></image>
								<text>{{item.name}}</text>
							</view>
						</view>
					</scroll-view>
					<view class="indicator" v-if="navList.length>10">
						<view class="plan">
							<view class="bar" :style="'left:'+slideNum+'%'"></view>
						</view>
					</view>
				</view>
				<!-- 通知 -->
				<view class="inform">
					<view class="inform-info">
						<view class="picture">
							<image src="/static/gg_ico.png" mode=""></image>
						</view>
						<view class="info">
							<swiper class="swiper" :circular="true" :vertical="true" :indicator-dots="false" :autoplay="true" :interval="3000"
							 :duration="1000">
								<swiper-item v-for="item in noticeList" :key="item.noticeId">
									<view class="swiper-item" @click="getNoticeInfo(item.noticeId)">
										<text class="one-omit">{{item.noticeTitle}}</text>
									</view>
								</swiper-item>
							</swiper>
						</view>
					</view>
				</view>
				<!-- 限时抢购，好货精选 -->
				<view class="new-product" v-if="SecKillProductList.length != 0">
					<view class="flash-sale">
						<view class="line"></view>
						<view class="flash-title">
							<view class="pictrue">
								<image src="/static/xsqg_title.png" mode=""></image>
							</view>
							<view style="padding-left: 100rpx;" v-if="flag">
								下一场秒杀开始还剩：
							</view>
							<view class="date-time" v-if="flag">
								<text class="time">{{day}}</text>
								<text class="da">:</text>
								<text class="time">{{hour}}</text>
								<text class="da">:</text>
								<text class="time">{{min}}</text>
								<text class="da">:</text>
								<text class="time">{{sec}}</text>
							</view>
							<view style="padding-left: 400rpx;font-size: 26rpx;color:#a09f9f;" v-if="!flag">
								秒杀进行中...
							</view>
						</view>
						<view class="goods-list">
							<view class="goods-list">
								<view v-for="item in SecKillProductList" :key="item.id" class="list" @click="toProductInfo(item.id)">
									<view class="pictrue">
										<image :src="item.productPic"></image>
									</view>
									<view class="price">
										<text class="selling-price">￥{{item.productPrice}}</text>
										<text class="original-price">￥{{item.productOldPrice}}</text>
									</view>
								</view>
							</view>
						</view>
					</view>
				</view>
				<!-- 今日上新 -->
				<view class="new-product">
					<view class="product-title">
						<view class="title">
							<image src="/static/hr_ico.png"></image>
							<text>今日上新</text>
						</view>
						<view class="describe">
							<text>今日上新商品是否有你心仪礼物</text>
						</view>
					</view>
					<view class="goods-list">
						<view v-for="item in newProductList" :key="item.id" class="list" @click="toProductInfo(item.id)">
							<view class="pictrue">
								<image :src="item.pic"></image>
							</view>
							<view class="price">
								<text class="selling-price">￥{{item.price}}</text>
								<!-- <text class="original-price">￥19</text> -->
							</view>
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
			</view>
		</mescroll-body>
		<ClassifyData v-show="classifyShow!=0"></ClassifyData>
		<!-- tabbar -->
		<TabBar :tabBarShow="0"></TabBar>
		<!-- 通知公告弹窗 -->
		<view class="cu-modal" :class="noticeDialog?'show':''">
			<view class="cu-dialog">
				<view class="cu-bar bg-white justify-end">
					<view class="content">{{notice.noticeTitle}}</view>
					<view class="action" @tap="noticeDialog = false">
						<text class="cuIcon-close text-red"></text>
					</view>
				</view>
				<view class="padding-xl">
					<rich-text :nodes="notice.noticeContent"></rich-text>
				</view>
			</view>
		</view>
		<!-- 通知公告弹窗结束 -->
	</view>
</template>

<script>
	import wxApi from '@/api/we-chat.js'
	import TabBar from '../../components/TabBar/TabBar.vue';
	import ClassifyData from '../../components/ClassifyData/ClassifyData.vue';
	// 引入mescroll-mixins.js
	import MescrollMixin from "@/components/mescroll-uni/mescroll-mixins.js";
	import bannerApi from '@/api/sys-banner.js'
	import noticeApi from '@/api/sys-notice.js'
	import categoryApi from '@/api/shop-product-category.js'
	import productApi from '@/api/shop-product.js'
	import shopSecKillApi from '@/api/shop-seckill.js'
	export default {
		mixins: [MescrollMixin], // 使用mixin
		components: {
			TabBar,
			ClassifyData,
		},
		data() {
			return {
				flag: true,
				mescroll: null, // mescroll实例对象 (此行可删,mixins已默认)
				// 下拉刷新的配置(可选, 绝大部分情况无需配置)
				downOption: {},
				// 上拉加载的配置(可选, 绝大部分情况无需配置)
				upOption: {
					use: false
				},
				// 轮播图
				swiperList: [],
				// 通知公告
				noticeList: [],
				// 控制通知公告弹窗展示
				noticeDialog: false,
				// 当前点击的通知公告
				notice: {},
				// 导航宫格
				navList: [],
				// 新品商品
				newProductList: [],
				// 推荐商品
				goodsList: [],
				slideNum: 0,
				classifyShow: 0,
				// 页面高度
				pageHeight: 500,
				SecKillProductList: [],
				nextSecKillTime: null,
				CountDown: 1800,
				day: 0,
				hour: 0,
				min: 0,
				sec: 0
			}
		},
		onReady() {
			uni.hideTabBar();
			// #ifdef MP
			uni.setNavigationBarTitle({
				title: '首页',
			})
			uni.setNavigationBarColor({
				frontColor: '#ffffff',
				backgroundColor: '#fe3b0f',
			})
			// #endif
		},
		created(){
			// 如果用户没有登陆成功说明要去注册
			if(uni.getStorageSync("loginflage") === true){
				uni.authorize({
				    scope: 'scope.userInfo',
				    success() {
				        uni.getUserInfo().then(res =>{
							const userInfo = res[1].userInfo
							// 获取到openId
							const openId = uni.getStorageSync('openId')
							userInfo.openId = openId
							wxApi.registerLogin(userInfo).then(res=>{
								// 把token设置到缓存中
								uni.setStorageSync('Authorization', res.data.token)
								wxApi.getLoginInfo().then(res=>{
									uni.setStorageSync('loginUser', res.data)
								})
								// 跳转到首页
								uni.navigateTo({
									url:'/pages/home/home'
								})
							})
							console.log(res[1].userInfo)
						})	
				    }
				})
			}	
		},
		onShow() {
			this.getAllShopSecKill()
		},
		onPageScroll(e) {
			let scrollTop = e.scrollTop;
			if (scrollTop > 0) {
				this.pageHeight = 210;
			} else {
				this.pageHeight = 500;
			}
		},
		onReachBottom() {
			console.log(12333);
		},
		methods: {
			// 初始化页面数据
			initData() {
				this.getBannerList()
				this.getNoticeList()
				this.getNavList()
				this.getNewProduct()
				this.getRecommendList()
			},
			// 查询所有的秒杀
			getAllShopSecKill(){
				shopSecKillApi.getAll().then(res => {
					this.SecKillProductList = res.data.all
					this.nextSecKillTime = res.data.nextSecKillTime
					if(this.nextSecKillTime!=null){
						this.flag=true
						const seconds = this.nextSecKillTime / 1000.0;
						this.CountDown = seconds
						this.CountDownData()
					}else{
						this.flag=false
					}
				})	
			},
			/**
			 * 倒计时
			 */
			CountDownData() {
				setTimeout(() => {
					if (this.CountDown <= 0) {
						this.getAllShopSecKill()
						return
					}
					this.CountDown--;
					this.day = parseInt(this.CountDown / (24 * 60 * 60))
					this.hour = parseInt(this.CountDown / (60 * 60) % 24);
					this.min = parseInt(this.CountDown / 60 % 60);
					this.sec = parseInt(this.CountDown % 60);	
					this.CountDownData();
				}, 1000)
			},
			// 查询轮播图
			getBannerList() {
				bannerApi.getBannerList().then(res => {
					this.swiperList = res.data
				})
			},
			// 查询通知公告列表
			getNoticeList() {
				noticeApi.getNoticeList().then(res => {
					this.noticeList = res.data
				})
			},
			// 查询通知详情
			getNoticeInfo(id) {
				noticeApi.get(id).then(res => {
					this.notice = res.data
					this.noticeDialog = true
				})
			},
			// 查询导航宫格
			getNavList() {
				categoryApi.getNavList().then(res => {
					this.navList = res.data
				})
			},
			// 查询新品推荐
			getNewProduct() {
				productApi.getNewProduct().then(res => {
					this.newProductList = res.data
				})
			},
			// 查看商品详情
			toProductInfo(id) {
				uni.navigateTo({
					url: `/pages/GoodsDetails/GoodsDetails?id=${id}`
				})
			},
			// 查询推荐
			getRecommendList() {
				productApi.getRecommendList().then(res => {
					this.goodsList = res.data
				})
			},
			// 点击轮播图
			clickBanner(item) {
				bannerApi.addClickCount(item.id)
				uni.navigateTo({
					url: item.url + '&bannerId=' + item.id
				})
			},
			/*下拉刷新的回调, 有三种处理方式:*/
			downCallback() {
				this.$nextTick(() => {
					this.initData()
					this.mescroll.endSuccess();
				})
			},
			/*上拉加载的回调*/
			upCallback(page) {
				setTimeout(() => {
					this.mescroll.endByPage(10, 20);
				}, 2000)
			},
			/**
			 * 菜单导航滚动
			 */
			ScrollMenu(e) {
				let scrollLeft = e.target.scrollLeft;
				const query = uni.createSelectorQuery().in(this);
				query.select('.nav').boundingClientRect(data => {
					let wid = e.target.scrollWidth - data.width - (data.left * 2 + 5);
					this.slideNum = (scrollLeft / wid * 300) / 2;
				}).exec();
			},
			/**
			 * 搜索点击
			 */
			onSearch() {
				uni.navigateTo({
					url: '/pages/search/search'
				})
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
			 * 分类点击
			 * @param {Object} item
			 * @param {Number} index
			 */
			onClassify(item, index) {
				this.classifyShow = index;
			},
			/**
			 * 跳转点击
			 * @param {String} type 跳转类型
			 */
			onSkip(type, id) {
				switch (type) {
					case 'mess':
						uni.navigateTo({
							url: '/pages/Message/Message'
						})
						break;
					case 'paycode':
						uni.navigateTo({
							url: '/pages/PaymentCode/PaymentCode'
						})
						break;
					case 'menu':
						uni.navigateTo({
							url: '/pages/SearchGoodsList/SearchGoodsList?categoryId=' + id
						})
						break;
					case 'inform':
						break;
					case 'flash':
						uni.navigateTo({
							url: '/pages/FlashSale/FlashSale'
						})
						break;
					case 'GoodChoice':
						uni.navigateTo({
							url: '/pages/GoodChoice/GoodChoice'
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
	};
</script>

<style scoped lang="scss">
	@import 'home.scss';
</style>