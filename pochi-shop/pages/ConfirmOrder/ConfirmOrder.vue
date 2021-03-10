<template>
	<view class="page">
		<!-- 地址 -->
		<view class="address-data">
			<view v-if="address" class="address-list" @click="onSkip(1)">
				<view class="list">
					<text>{{address.province}}{{address.city}}{{address.region}}</text>
				</view>
				<view class="list">
					<text class="address">{{address.detailAddress}}</text>
				</view>
				<view class="list">
					<text>{{address.name}}</text>
					<text>{{address.phoneNumber}}</text>
				</view>
				<view class="list">
					<text class="tips">(如果快递不方便接收，您可以选择暂时寄存服务)</text>
				</view>
			</view>
			<view v-else class="address-list" @click="onSkip()">
				<view class="list">
					<text class="tips">(如果快递不方便接收，您可以选择暂时寄存服务)</text>
				</view>
			</view>
			<view class="bar">

			</view>
		</view>
		<!-- 商品 -->
		<view class="goods-data">
			<view class="goods-title">
				<text>商品信息</text>
			</view>
			<view class="goods-list">
				<view class="list" v-for="(item,index) in productList" :key="index">
					<view class="thumb">
						<image :src="item.pic" mode=""></image>
					</view>
					<view class="item">
						<view class="title">
							<text class="name one-omit">{{item.name}}</text>
						</view>
						<view class="price-number">
							<view class="price">
								<text class="min">￥</text>
								<text class="max">{{String(item.price).split('.')[0]}}</text>
								<text class="min">.{{String(item.price).split('.')[1]?String(item.price).split('.')[1]:'00'}}</text>
							</view>
							<view class="number">
								<text>x {{item.stock}}</text>
							</view>
						</view>
					</view>
				</view>
			</view>
			<view class="delivery">
				<div class="list">
					<view class="title">配送</view>
					<view class="content">
						<text>快递运输</text>
						<text class="iconfont icon-more"></text>
					</view>
				</div>
				<div class="list">
					<view class="title">运费险</view>
					<view class="content">
						<text>￥10.00</text>
						<text class="iconfont icon-check"></text>
					</view>
				</div>
				<div class="list">
					<view class="title">留言</view>
					<view class="content">
						<input v-model="note" type="text" placeholder="选填,建议先和商家沟通确认">
					</view>
				</div>
			</view>
		</view>
		<!-- 优惠 -->
		<view class="discounts-data">
			<view class="discounts">
				<div class="list" @click="$refs['InvoiceInfo'].show()">
					<view class="title">发票</view>
					<view class="content">
						<text>不开发票</text>
						<text class="iconfont icon-more"></text>
					</view>
				</div>
				<div class="list" @click="$refs['UseCoupon'].show()">
					<view class="title">优惠券</view>
					<view class="content">
						<text v-if="couponList.length <= 0">无可用</text>
						<text v-else-if="!coupon">请选择优惠券</text>
						<text v-else>已减免￥{{coupon.amount}}</text>
						<text class="iconfont icon-more"></text>
					</view>
				</div>
				<div class="list">
					<view class="title">积分</view>
					<view class="content">
						<text>共300，满1000可用</text>
						<!-- <text class="iconfont icon-more"></text> -->
					</view>
				</div>
			</view>
		</view>
		<!-- 订单金额 -->
		<view class="order-price">
			<view class="price-list">
				<view class="list">
					<view class="title">
						<text>商品金额</text>
					</view>
					<view class="price">
						<text>￥{{productAmount}}</text>
					</view>
				</view>
				<view class="list">
					<view class="title">
						<text>优惠券减免</text>
					</view>
					<view class="price">
						<text class="highlight">-￥{{couponAmount}}</text>
					</view>
				</view>
			</view>
		</view>
		<!-- 地址提示 -->
		<view class="address-tips" :style="scrollTop >= 100 ? '':'display:none'">
			<text>{{address.province}}{{address.city}}{{address.region}}{{address.detailAddress}}</text>
		</view>
		<!-- 底部合计提交 -->
		<view class="footer-submit">
			<view class="price">
				<text class="min">￥</text>
				<text class="max">{{String(totalAmount).split('.')[0]}}</text>
				<text class="min">.{{String(totalAmount).split('.')[1]?String(totalAmount).split('.')[1]:'00'}}</text>
			</view>
			<view class="submit" @click="onSubmit">
				<text>提交订单</text>
			</view>
		</view>
		<!-- 发票 -->
		<invoice-info ref="InvoiceInfo"></invoice-info>
		<!-- 优惠券 -->
		<use-coupon ref="UseCoupon" @check-coupon="checkCoupon" :coupon-list="couponList"></use-coupon>
	</view>
</template>

<script>
	import InvoiceInfo from '../../components/InvoiceInfo/InvoiceInfo.vue';
	import UseCoupon from '../../components/UseCoupon/UseCoupon.vue'
	import productApi from '@/api/shop-product.js'
	import addressApi from '@/api/shop-user-address.js'
	import orderApi from '@/api/shop-order.js'
	import couponApi from '@/api/shop-coupon.js'
	export default {
		components: {
			// 发票
			InvoiceInfo,
			// 优惠券
			UseCoupon,
		},
		onShow() {
			this.address = uni.getStorageSync('Address')
		},
		onLoad(param) {
			this.paramArr = JSON.parse(param.productArr)
			const tempAddress = param.address
			console.log(tempAddress)
			if (tempAddress) {
				this.address = JSON.parse(tempAddress)
			} else {
				// this.getAddressList()
				this.address = uni.getStorageSync('Address')
			}
			this.getProductByIds()
			this.getProductCoupon()
		},
		computed: {
			// 商品金额
			productAmount() {
				if (this.productList && this.productList[0]) {
					let price = 0
					this.productList.forEach(e=>{
						price = price + (e.price * e.stock)
					})
					return price
				} else {
					return 0
				}
			},
			// 优惠券减免
			couponAmount() {
				if(this.coupon) {
					return this.coupon.amount
				}else {
					return 0
				}
			},
			// 总金额
			totalAmount() {
				return (this.productAmount - this.couponAmount).toFixed(2)
			}
		},
		data() {
			return {
				// 上一个页面传递来的数组
				paramArr: [],
				// 商品列表
				productList: [],
				// 优惠券列表
				couponList: [],
				// 收货地址
				address: {},
				// 备注
				note: '',
				// 优惠券
				coupon: null,
				scrollTop: 0,
			};
		},
		onPageScroll(e) {
			this.scrollTop = e.scrollTop;
		},
		methods: {
			// 选中优惠券触发
			checkCoupon(coupon) {
				this.coupon = coupon
			},
			// 加载收货地址
			getAddressList() {
				addressApi.getUserAddress().then(res => {
					const addressArr = res.data
					if (addressArr && addressArr[0]) {
						this.address = addressArr[0]
					}
				})
			},
			// 查询商品优惠券
			getProductCoupon() {
				couponApi.getProductCoupon(this.paramArr[0].productId).then(res=>{
					// this.couponList = res.data
					// 查询用户领取的优惠券
					let sumPrice = 0
					this.productList.forEach(e => {
						sumPrice = sumPrice + e.price
					})
					console.log(sumPrice)
					res.data.forEach(e => {
						if(e.status === 999 && e.minPoint <= sumPrice){
							this.couponList.push(e)
						}			
					})
				})
			},
			// 根据id集合回显商品信息
			getProductByIds() {
				const productIds = []
				this.paramArr.forEach(e => {
					productIds.push(e.productId)
				})
				productApi.getByIds(productIds).then(res => {
					res.data.forEach(e => {
						const index = this.paramArr.findIndex(p => p.productId === e.id)
						e.stock = this.paramArr[index].count
						e.cartId = this.paramArr[index].cartId
					})
					this.productList = res.data
				})
			},
			/**
			 * 提交订单
			 */
			onSubmit() {
				const productList = this.productList.map(e=>{
					return {productId: e.id, count: e.stock, cartId: e.cartId}
				})
				// 构造参数
				const order = {
					productList: productList,
					addressId: this.address.id,
					note: this.note,
					//couponId: this.coupon.id
				}
				orderApi.createOrder(order).then(res=>{
					uni.showToast({
						title:res.msg
					})
					setTimeout(()=>{
						uni.redirectTo({
							url: `/pages/CashierDesk/CashierDesk?id=${res.data.id}&amount=${res.data.payAmount}`,
						})
					}, 300)
				})
				
			},
			/**
			 * 跳转点击
			 * @param {String} type 跳转类型
			 */
			onSkip(type) {
				uni.navigateTo({
					url: `/pages/AddressList/AddressList?type=${type}&productArr=${JSON.stringify(this.paramArr)}`,
				})
			}
		}
	}
</script>

<style scoped lang="scss">
	@import 'ConfirmOrder.scss';
</style>
