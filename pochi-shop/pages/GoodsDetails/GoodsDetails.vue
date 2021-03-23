<template>
	<view @click="isMore = false">
		<view class="goods-head" :style="'background:rgba(255,255,255,' + PageScrollTop / 100 + ')'">
			<!-- 返回 -->
			<view class="back" @click="onBack">
				<view class="back-one" :class="{ action: PageScrollTop > 120 }">
					<text></text>
				</view>
			</view>
			<!-- tab切换 -->
			<view class="head-tab" v-if="PageScrollTop > 120">
				<view class="tab" :class="{'action':TabShow===0}" @click="onTab(0)">
					<text>商品</text>
					<text class="line"></text>
				</view>
				<view class="tab" :class="{'action':TabShow===1}" @click="onTab(1)">
					<text>评价</text>
					<text class="line"></text>
				</view>
				<view class="tab" :class="{'action':TabShow===2}" @click="onTab(2)">
					<text>详情</text>
					<text class="line"></text>
				</view>
			</view>
			<!-- 分享更多 -->
			<view class="share-more">
				<view class="share-more-one" :class="{ action: PageScrollTop > 120 }">
					<view class="list">
						<text class="iconfont icon-share"></text>
					</view>
					<view class="list" @click.stop="isMore = !isMore">
						<text class="iconfont icon-diandian"></text>
					</view>
				</view>
				<view class="mroe-list" v-show="isMore">
					<!-- <navigator class="list">
						<view class="icon">
							<text class="iconfont icon-xiaoxi"></text>
						</view>
						<view class="title">
							<text>消息</text>
						</view>
					</navigator> -->
					<navigator open-type="switchTab" url="/pages/home/home" class="list">
						<view class="icon">
							<text class="iconfont icon-home"></text>
						</view>
						<view class="title">
							<text>首页</text>
						</view>
					</navigator>
					<navigator class="list" url="/pages/ContentCollection/ContentCollection">
						<view class="icon">
							<text class="iconfont icon-guanzhu"></text>
						</view>
						<view class="title">
							<text>我的收藏</text>
						</view>
					</navigator>
					<navigator class="list" url="/pages/BrowsingHistory/BrowsingHistory">
						<view class="icon">
							<text class="iconfont icon-zuji"></text>
						</view>
						<view class="title">
							<text>浏览记录</text>
						</view>
					</navigator>
				</view>
			</view>
		</view>
		<!-- banner，标题 -->
		<view class="banner-title">
			<!-- banner -->
			<view class="banner">
				<swiper class="screen-swiper round-dot" indicator-dots="true" circular="true" autoplay="true" interval="5000"
				 duration="500">
					<swiper-item v-for="(item, index) in product.albumPicList" :key="index">
						<image :src="item" mode="aspectFill"></image>
					</swiper-item>
				</swiper>
			</view>
			<!-- 价格 -->
			<view class="price-info" v-if="!(secKillflag)">
				<view class="price">
					<text class="min">￥</text>
					<text class="max">{{String(product.price).split('.')[0]}}</text>
					<text class="min">.{{String(product.price).split('.')[1]?String(product.price).split('.')[1]:'00'}}</text>
				</view>
				<view class="info">
					<view class="list" @click="onDepreciate">
						<text class="iconfont icon-jiangjia"></text>
						<text>降价通知</text>
					</view>
					<view class="list" @click="changeCollection">
						<text class="iconfont" :class="AttentionShow===0?'icon-guanzhu-off':'icon-guanzhu-on action'"></text>
						<text>{{ AttentionShow === 0 ? '收藏' : '已收藏' }}</text>
					</view>
				</view>
			</view>
				<!-- 限时抢购 -->
				<view class="flash-price" v-if="secKillflag">
					<view class="price-item">
						<view class="icon-item">
							<text class="iconfont icon-flash-sale"></text>
						</view>
						<view class="price">
							<view class="current-price">
								<text class="min">￥</text>
								<text class="max">{{String(shopSeckillPrice).split('.')[0]}}</text>
								<text class="min">.{{String(shopSeckillPrice).split('.')[1]?String(shopSeckillPrice).split('.')[1]:'00'}}</text>
							</view>
							<view class="original-price">
								<text>￥{{product.price}}</text>
							</view>
						</view>
						<view class="tag">
							<text class="iconfont icon-flash-naozhong"></text>
						</view>
					</view>
					<view class="time-item">
						<view class="title" v-if="startflag">
							<text>距结束还剩：</text>
						</view>
						<view class="title" v-if="!startflag">
							<text>距开始还剩：</text>
						</view>
						<view class="time">
							<text class="num">{{day}}</text>
							<text class="dot">:</text>
							<text class="num">{{hour}}</text>
							<text class="dot">:</text>
							<text class="num">{{min}}</text>
							<text class="dot">:</text>
							<text class="num">{{sec}}</text>
						</view>
					</view>
				</view>
			
			<!-- 标题 -->
			<view class="goods-title">
				<text>{{product.name}}</text>
			</view>
			<!-- 开通会员 -->
			<view class="dredge-vip">
				<view class="title">
					<text class="iconfont icon-vip"></text>
					<text>
						开通年卡会员预计估算优惠
						<text class="col">15.37</text>
						元
					</text>
				</view>
				<view class="dredge">
					<text>立即</text>
					<text>开通</text>
				</view>
			</view>
		</view>
		<!-- 优惠积分 -->
		<view class="goods-discounts">
			<view class="list">
				<view class="title">积分</view>
				<view class="content">
					<text>购买本商品可获得{{product.point}}积分</text>
				</view>
			</view>
			<view class="list" @click="$refs['GoodsServe'].show()">
				<view class="title">服务</view>
				<view class="content">
					<view class="serve">
						<text class="iconfont icon-baozheng"></text>
						<text>退款保证</text>
					</view>
					<view class="serve">
						<text class="iconfont icon-baozheng"></text>
						<text>物流配送</text>
					</view>
				</view>
				<view class="more">
					<text class="iconfont icon-more"></text>
				</view>
			</view>
			<view class="list" @click="showCoupon">
				<view class="title">领券</view>
				<view class="content">
					<view v-for="item in couponList" :key="item.id" class="coupon-list">
						<view>满{{item.minPoint}}减{{item.amount}}</view>
					</view>
				</view>
				<view class="more">
					<text class="iconfont icon-more"></text>
				</view>
			</view>
		</view>
		<!-- 属性规格 -->
		<view class="goods-discounts">
			<view class="list" @click="$refs['GoodsAttr'].show(1)">
				<view class="title">已选</view>
				<view class="content">
					<text>{{choseProductName}}</text>
				</view>
				<view class="more">
					<text class="iconfont icon-more"></text>
				</view>
			</view>
			<view class="list">
				<view class="title">送至</view>
				<view class="content">
					<view class="serve">
						<text class="iconfont icon-dingwei"></text>
						<text v-if="address!=null">{{address.province}}-{{address.city}}-{{address.region}}-{{address.detailAddress}}</text>
						<text v-else>暂无收货地址</text>
					</view>
				</view>
				<view class="more">
					<text class="iconfont icon-more" @click="show"></text>
				</view>
			</view>
			<view class="list">
				<view class="title">运费</view>
				<view class="content">
					<text>￥{{product.transFee}}</text>
				</view>
				<view class="more">
					<!-- <text class="iconfont icon-more"></text> -->
				</view>
			</view>
		</view>
		<!-- 评价 -->
		<view class="evaluate-data" ref="evaluate">
			<view class="title-more" @click="onEvaluate">
				<view class="title">
					<text>评价</text>
					<text class="num" v-if="commentDataPage.totalCount <= 999">{{commentDataPage.totalCount}}</text>
					<text class="num" v-else>999+</text>
				</view>
				<view class="more">
					<text class="iconfont icon-more"></text>
				</view>
			</view>
			<view class="top" v-if="commentDataPage.totalCount===0" style="text-align: center;line-height: 50px;">
				暂时没有相关评价
			</view>
			<view class="evaluate-list" v-for="(comment, ci) in commentDataPage.list" :key="ci">
				<view class="user-info">
					<view class="thumb">
						<image :src="comment.memberIcon" mode=""></image>
					</view>
					<view class="nickname-grade">
						<view class="nickname">
							<text>{{comment.nickName}}</text>
						</view>
						<view class="grade">
							<text v-for="star in comment.star" :key="star" class="cuIcon-favorfill lg text-gray"></text>
						</view>
					</view>
				</view>
				<view class="content">
					<view class="character">
						<text class="two-omit">{{comment.content}}</text>
					</view>
					<view class="thumb-list">
						<view class="list" v-for="(pic, pi) in comment.picList" :key="pi">
							<image :src="pic" mode=""></image>
						</view>
					</view>
				</view>
				<u-line color="red" />
				<view class="look-all" @click="onEvaluate" v-if="ci===1">
					<text>查看全部评价</text>
				</view>
			</view>
		</view>
		<!-- 排行榜 -->
		<view class="ranking-list">
			<view class="ranking-title">
				<view class="title">排行榜</view>
			</view>
			<view class="goods-list">
				<view class="list" v-for="(item, index) in rankList" :key="index">
					<view class="thumb">
						<image :src="item.pic"></image>
					</view>
					<view class="title">
						<text class="two-omit">{{item.name}}</text>
					</view>
					<view class="price">
						<text>￥{{item.price}}</text>
					</view>
				</view>
			</view>
		</view>
		<!-- 商品介绍 -->
		<view v-if="product.productComment" class="products-introduction" ref="products">
			<view class="title">
				<text>商品介绍</text>
			</view>
			<view class="content" v-html="product.productComment"></view>
		</view>
		<view class="products-introduction" ref="products">
			<view class="title">
				<text>商品详情</text>
			</view>
			<view class="content" v-html="product.productContent"></view>
		</view>
		<!-- 底部 -->
		<view class="page-footer">
			<view class="footer-fn">
				<view class="list">
					<text class="iconfont icon-kefu"></text>
					<text>联系客服</text>
				</view>
				<view class="list cu-avatar xl radius" @click="onToCart">
					<text class="iconfont icon-cart"></text>
					<text>购物车</text>
					<view class="cu-tag badge" v-if="productCount > 99">99+</view>
					<view class="cu-tag badge" v-else-if="productCount > 1">{{productCount}}</view>
					<view class="cu-tag badge" v-else-if="productCount === 1"></view>
				</view>
			</view>
			<view class="footer-buy">
				<view class="cart-add" @click="$refs['GoodsAttr'].show(2)">
					<text>加入购物车</text>
				</view>
				<view class="buy-at" @click="$refs['GoodsAttr'].show(3)" v-if="!startflag">
					<text>立即购买</text>
				</view>
				<view class="buy-at" v-if="startflag" @click="toSecKill()">
					<text>立即秒杀</text>
				</view>
			</view>
		</view>
		<!-- 服务弹窗 -->
		<goods-serve ref="GoodsServe"></goods-serve>
		<!-- 优惠券 -->
		<goods-coupon ref="GoodsCoupon" :coupon-list="couponList"></goods-coupon>
		<!-- 属性规格 -->
		<goods-attr ref="GoodsAttr" :pack-list="packList" v-on:pChoseProduct="choseProduct"></goods-attr>
		<!-- 添加地址 -->
		<popup-layer class="tanchu" ref="popupRef" @closeCallBack="close" :direction="'top'">
		  	<!-- 地址列表 -->
		  	<view class="address-list">
		  		<view class="list" v-for="(item,index) in addressList" :key="index" @click="choseAddress(item)">
		  			<view class="name-phone">
		  				<view class="name">
		  					<text class="one-omit">{{item.name}}</text>
		  				</view>
		  				<view class="phone">
		  					<text>{{item.phoneNumber}}</text>
		  					<text class="tag" v-if="item.defaultStatus === 1">默认</text>
		  				</view>
		  			</view>
		  			<view class="address-edit">
		  				<view class="address">
		  					<text>{{item.province+item.city+item.region+item.detailAddress}}</text>
		  				</view>
		  				<view class="edit" @click.stop="onAddressEdit(1, item.id)">
		  					<text class="iconfont icon-edit1"></text>
		  				</view>
		  			</view>
		  		</view>
		  	</view>
		  	<!-- 添加地址 -->
		  	<view class="add-address">
		  		<view class="btn" @click="onAddressEdit(2)">
		  			<text>新建收货地址</text>
		  		</view>
		  	</view>
		</popup-layer>	
	</view>
</template>

<script>
	import popupLayer from '@/components/popup-layer/popup-layer.vue';
	import GoodsServe from '../../components/GoodsServe/GoodsServe.vue';
	import GoodsCoupon from '../../components/GoodsCoupon/GoodsCoupon.vue';
	import GoodsAttr from '../../components/GoodsAttr/GoodsAttr.vue';
	import productApi from '@/api/shop-product.js'
	import couponApi from '@/api/shop-coupon.js'
	import packApi from '@/api/shop-pack.js'
	import cartApi from '@/api/shop-cart-item.js'
	import historyApi from '@/api/shop-prodict-history.js'
	import collectionApi from '@/api/shop-product-collection.js'
	import commentApi from '@/api/shop-order-comment.js'
	import addressApi from '@/api/shop-user-address.js'
	import shopSecKillApi from '@/api/shop-seckill.js'
	export default {
		components: {
			GoodsServe,
			GoodsCoupon,
			GoodsAttr,
			popupLayer
		},
		data() {
			return {
				// 地址列表
				addressList: [],
				addflag: false,
				// 当前商品ID
				productId: '',
				// 商品信息
				product: {},
				// 优惠券列表
				couponList: [],
				// 套装列表
				packList: [],
				// 排行榜
				rankList: [],
				// 评价分页查询对象
				commentPage: {
					// 当前页
					pageNumber: 1,
					// 每页条数
					pageSize: 10,
					// 查询参数
					params: {
					}
				},
				// 购物车商品数量
				productCount: 0,
				TabShow: 0,
				isMore: false,
				AttentionShow: 0,
				PageScrollTop: 0,
				type: 0,
				commentDataPage: {},
				address: null,
				choseProductName: '',
				secKillflag: false,
				startflag: false,
				CountDown: 1800,
				day: 0,
				hour: 0,
				min: 0,
				sec: 0,
				shopSeckillPrice: ''
			};
		},
		onShow() {
			// 加载收货地址
			this.getAddressList()
			this.getAddressList1()
		},
		onLoad(params) {
			console.log(params)
			this.type = 0;
			this.productId = params.id
			this.getProductInfo()
			this.getProductCoupon() 
			this.getProductPackList()
			this.getRankByProduct()
			this.getProductCount()
			this.saveHistory()
			this.getCollection()
			this.getComment()
			this.getSecKill()
		},
		onPageScroll(e) {
			this.PageScrollTop = e.scrollTop;
		},
		methods: {
			// 去秒杀
			toSecKill() {
				shopSecKillApi.toSecKill(this.productId).then(res=>{
					uni.showToast({
					  icon:'none',
					  title:res.msg
					})
				})
			},
			getSecKill() {
				shopSecKillApi.getSecKill(this.productId).then(res=>{
					if(res.data==null){
						this.secKillflag = false
						this.startflag = false
					}
					if(res.data.beginSecKillTime!=null){
						// 标记是秒杀的商品
						this.secKillflag = true
						const seconds = res.data.beginSecKillTime / 1000.0;
						this.CountDown = seconds
						this.CountDownData()
					}
					if(res.data.endSecKillTime!=null){
						// 标记是秒杀的商品
						this.secKillflag = true
						// 标记可以秒杀
						this.startflag = true
						const seconds = res.data.endSecKillTime / 1000.0;
						this.CountDown = seconds
						this.CountDownData()
					}
					if(res.data.shopSeckill!=null){
						this.shopSeckillPrice=res.data.shopSeckill.productPrice
					}
				})
			},
			/**
			 * 倒计时
			 */
			CountDownData() {
				setTimeout(() => {
					if (this.CountDown <= 0) {
						if(this.startflag==true){
							uni.showToast({
								icon: 'none',
								title:'秒杀已结束'
							})
						}
						this.getSecKill()
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
			choseAddress(item) {
				uni.setStorageSync('Address', item)
				this.$refs.popupRef.close()// 或者 boolShow = false
				this.address = item
			},
			// 查询地址列表
			getAddressList1() {
				addressApi.getUserAddress().then(res=>{
					this.addressList = res.data
				})
			},
			/**
			 * 编辑地址点击
			 */
			onAddressEdit(type, id){
				uni.navigateTo({
					url: `/pages/AddressEdit/AddressEdit?type=${type}&id=${id}`,
				})
			},
			close(){
				console.log("退出去了")
			},
			show(){
				this.$refs.popupRef.show() // 或者 boolShow = rue
			},
			// 已选商品
			choseProduct(productname) {
				// 加载已选商品
				this.choseProductName = productname
				// 重新加载购物车数量
				this.getProductCount()
			},
			// 加载收货地址
			getAddressList() {
				addressApi.getUserAddress().then(res => {
					const addressArr = res.data
					if (addressArr && addressArr[0]) {
						this.address = addressArr[0]
						// 设置到缓存里面
						uni.setStorageSync('Address', this.address)
					}
				})
			},
			// 查询商品评价
			getComment() {
				this.commentPage.params.productId = this.productId
				this.commentPage.params.star = 1000
				commentApi.getByPage(this.commentPage).then(res=>{
					this.commentDataPage = res.data
				})
			},
			// 打开商品优惠券组件
			showCoupon(){
				this.$refs['GoodsCoupon'].show()
				this.getProductCoupon()
			},
			// 查询商品详情
			getProductInfo() {
				productApi.get(this.productId).then(res=>{
					if(res.data.productContent) {
						res.data.productContent = res.data.productContent.replace(/\<img/gi, '<img style="max-width:100%;height:auto"')
					}
					this.product = res.data
					if(this.product.specs!=null){
						this.choseProductName = this.product.specs +"，1个"
					}else{
						this.choseProductName = this.product.name +"，1个"
					}
				})
			},
			// 查询商品优惠券
			getProductCoupon() {
				couponApi.getProductCoupon(this.productId).then(res=>{
					this.couponList = res.data
				})
			},
			// 查询商品套装
			getProductPackList() {
				packApi.getByProductId(this.productId).then(res=>{
					this.packList = res.data
				})
			},
			// 查询排行榜
			getRankByProduct() {
				productApi.getRankByProduct(this.productId).then(res=>{
					this.rankList = res.data
				})
			},
			// 查询购物车商品数
			getProductCount() {
				cartApi.getProductCount().then(res=>{
					this.productCount = res.data
				})
			},
			// 保存历史记录
			saveHistory() {
				historyApi.save({productId: this.productId})
			},
			// 切换收藏状态
			changeCollection() {
				collectionApi.changeCollection({productId: this.productId}).then(res=>{
					if (this.AttentionShow === 0) {
						this.AttentionShow = 1;
						uni.showToast({
							title: '收藏成功',
							icon: 'none'
						})
					} else {
						this.AttentionShow = 0;
						uni.showToast({
							title: '取消成功',
							icon: 'none'
						})
					}
				})
			},
			getCollection(){
				collectionApi.getByProductId(this.productId).then(res=>{
					if(res.data) {
						this.AttentionShow = 1
					}else {
						this.AttentionShow = 0
					}
				})
			},
			/**
			 * 返回
			 */
			onBack() {
				console.log('aaaa')
				uni.navigateBack();
				console.log('bbb')
			},
			/**
			 * tab
			 */
			onTab(type) {
				this.TabShow = type;
				switch (type) {
					case 0:
						uni.pageScrollTo({
							scrollTop: 0,
							duration: 300
						});
						break;
					case 1:
						uni.createSelectorQuery().select(".evaluate-data").boundingClientRect((data) => { //data - 各种参数
							uni.pageScrollTo({
								scrollTop: this.PageScrollTop + data.top - 50,
								duration: 300
							});
						}).exec()
						break;
					case 2:
						uni.createSelectorQuery().select(".products-introduction").boundingClientRect((data) => { //data - 各种参数
							uni.pageScrollTo({
								scrollTop: this.PageScrollTop + data.top - 50,
								duration: 300
							});
						}).exec()
						break;
				}
			},
			/**
			 * 去购物车
			 */
			onToCart() {
				uni.switchTab({
					url: '/pages/cart/cart'
				})
			},
			/**
			 * 降价通知点击
			 */
			onDepreciate() {
				uni.showToast({
					title: '降价通知提醒成功',
					icon: 'success'
				})
			},
			/**
			 * 评价点击
			 */
			onEvaluate() {
				uni.navigateTo({
					url: '/pages/GoodsEvaluateList/GoodsEvaluateList?productId=' + this.product.id
				})
			}
		}
	};
</script>

<style scoped lang="scss">
	@import 'GoodsDetails.scss';
</style>