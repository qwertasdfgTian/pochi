<template>
	<view class="page">
		<!-- 订单状态 -->
		<view class="order-status">
			<view class="status">
				<text class="iconfont icon-zhuyi"></text>
				<text>{{orderStatus[order.status]}}</text>
			</view>
			<view class="reason" v-if="order.status===0">
				<text>剩余{{min}}分{{sec}}秒</text>
			</view>
		</view>
		<!-- 收货地址 -->
		<view class="shipping-address">
			<view class="name-phone">
				<text class="iconfont icon-dingwei"></text>
				<text>{{order.receiverName}}</text>
				<text>{{order.receiverPhone}}</text>
			</view>
			<view class="address">
				<text>{{order.receiverProvince}}{{order.receiverCity}}{{order.receiverRegion}}{{order.receiverDetailAddress}}</text>
			</view>
		</view>
		<!-- 订单商品 -->
		<view class="order-goods">
			<view class="goods-list">
				<view class="list" v-for="(item,index) in order.itemList" :key="index" @click="toProductInfo(item.productId)">
					<view class="thumb">
						<image :src="item.productPic" mode=""></image>
					</view>
					<view class="item">
						<view class="title">
							<text class="one-omit">{{item.productName}}</text>
						</view>
						<view class="num-size">
							<text>数量：{{item.productQuantity}}</text>
						</view>
						<view class="price">
							<text>￥{{item.productPrice}}</text>
						</view>
						<!-- <view class="order-btn">
							<view class="btn" @click="onApplyAftersales">
								<text>申请售后</text>
							</view>
						</view> -->
					</view>
				</view>
			</view>
			<view class="contact">
				<text class="iconfont icon-kefu"></text>
				<text>联系客服</text>
			</view>
		</view>
		<!-- 订单信息 -->
		<view class="order-info">
			<view class="info-list">
				<view class="list">
					<view class="title">订单编号:</view>
					<view class="content">
						<text>{{order.id}}</text>
						<text class="btn">复制</text>
					</view>
				</view>
				<view class="list">
					<view class="title">下单时间:</view>
					<view class="content">
						<text>{{order.createTime}}</text>
					</view>
				</view>
				<view class="list">
					<view class="title">支付方式:</view>
					<view class="content">
						<text>支付宝支付</text>
					</view>
				</view>
				<view class="list">
					<view class="title">配送方式:</view>
					<view class="content">
						<text>普通快递</text>
					</view>
				</view>
				<view class="list" v-if="order.status >= 3">
					<view class="title">配送日期:</view>
					<view class="content">
						<text>{{order.deliveryTime}}</text>
					</view>
				</view>
			</view>
		</view>
		<!-- 订单明细 -->
		<view class="order-details">
			<view class="details-list">
				<view class="list">
					<view class="title">
						<text>商品总额</text>
					</view>
					<view class="price">
						<text>￥{{order.totalAmount}}</text>
					</view>
				</view>
				<view class="list">
					<view class="title">
						<text>运费</text>
					</view>
					<view class="price">
						<text>+￥{{order.freightAmount}}</text>
					</view>
				</view>
				<view class="list action">
					<view class="title">
						<text>实付款：</text>
					</view>
					<view class="price">
						<text>￥{{order.payAmount}}</text>
					</view>
				</view>
			</view>
		</view>
		<!-- 底部按钮 -->
		<view class="footer-btn">
			<view class="del" v-if="order.status >= 4" @click="toMove(order.id)">
				<text>删除订单</text>
			</view>
			<view class="btn">
				<text class="action" v-if="order.status === 0" @click="pay">确认付款</text>
				<text class="action" v-if="order.status === 3" @click="confirmSign">确认收货</text>
				<text class="action" v-if="order.status === 1" @click="toCancel">取消订单</text>
				<text class="action" v-if="order.status === 4" @click="onEvaluate">评价</text>
				<text class="action" v-if="order.status >= 4" @click="onBuy">再次购买</text>
				<text class="action" v-if="order.status === 2" @click="toReturn">申请退款</text>
			</view>
		</view>
		<view class="cu-modal" :class="showMove?'show':''">
			<view class="cu-dialog">
				<view class="cu-bar bg-white justify-end">
					<view class="content">删除订单</view>
					<view class="action" @tap="showMove = false">
						<text class="cuIcon-close text-red"></text>
					</view>
				</view>
				<view class="padding-xl">
					是否删除订单
				</view>
				<view class="cu-bar bg-white justify-end">
					<view class="action">
						<button class="cu-btn line-green text-green" @tap="showMove = false">取消</button>
						<button class="cu-btn bg-green margin-left" @tap="deleteOrder">确定</button>
					</view>
				</view>
			</view>
		</view>
		
		<view class="cu-modal" :class="showCancel?'show':''">
			<view class="cu-dialog">
				<view class="cu-bar bg-white justify-end">
					<view class="content">取消订单</view>
					<view class="action" @tap="showCancel = false">
						<text class="cuIcon-close text-red"></text>
					</view>
				</view>
				<view class="padding-xl">
					是否取消订单
				</view>
				<view class="cu-bar bg-white justify-end">
					<view class="action">
						<button class="cu-btn line-green text-green" @tap="showCancel = false">取消</button>
						<button class="cu-btn bg-green margin-left" @tap="cancelOrderById">确定</button>
					</view>
				</view>
			</view>
		</view>
		
		<view class="cu-modal" :class="showReturn?'show':''">
			<view class="cu-dialog">
				<view class="cu-bar bg-white justify-end">
					<view class="content">申请退款</view>
					<view class="action" @tap="showReturn = false">
						<text class="cuIcon-close text-red"></text>
					</view>
				</view>
				<view class="padding-xl">
					是否申请退款
				</view>
				<view class="cu-bar bg-white justify-end">
					<view class="action">
						<button class="cu-btn line-green text-green" @tap="showReturn = false">取消</button>
						<button class="cu-btn bg-green margin-left" @tap="returnOrderById">确定</button>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import orderApi from '@/api/shop-order.js'
	import productApi from '@/api/shop-product.js'
	export default {
		data() {
			return {
				CountDown: 1800,
				// 订单对象
				order: {},
				// 订单ID
				orderId: null,
				day: 0,
				hour: 0,
				min: 0,
				sec: 0,
				// 订单状态
				orderStatus: {
					0:'待付款',
					1:'待确认',
					2:'待发货',
					3:'待签收',
					4:'待评价',
					5:'已完成',
					6:'已失效',
					7:'已关闭'
				},
				// 弹窗显示状态
				showMove: false,
				showCancel: false,
				showReturn: false,
				// 订单Id
				orderId:'',
				timer:null
			};
		},
		onLoad(param) {
			this.orderId = param.id
			this.getById()
			this.CountDownData();
		},
		methods:{
			/**
			 * 倒计时
			 */
			CountDownData() {
				setTimeout(() => {
					this.CountDown--;
					this.day = parseInt(this.CountDown / (24 * 60 * 60))
					this.hour = parseInt(this.CountDown / (60 * 60) % 24);
					this.min = parseInt(this.CountDown / 60 % 60);
					this.sec = parseInt(this.CountDown % 60);
					if (this.CountDown <= 0) {
						uni.showToast({
							icon: 'none',
							title:'订单已过期'
						})
						return
					}
					this.CountDownData();
				}, 1000)
			},
			onBuy(){
				uni.navigateTo({
					url: `/pages/GoodsDetails/GoodsDetails?id=${this.order.itemList[0].productId}`
				})
			},
			/**
			 * 评价点击
			 */
			onEvaluate() {
				uni.navigateTo({
					url: '/pages/MyEvaluatePush/MyEvaluatePush?itemList='+JSON.stringify(this.order.itemList)
				})
				this.getMyOrder()
			},
			toReturn() {
				this.showReturn = true
			},
			returnOrderById() {
				orderApi.cancelOrderById(this.orderId).then(res => {
					uni.requestSubscribeMessage({
						tmplIds: ['DRRjCAJnwYdqTD84dFDYIPephHQr9NrIDj3ttyWHIwU'],	
						success: (response)=> {
							console.log("我进来发消息了")
							uni.showToast({
							  title: res.msg
							})
							this.showReturn = false
							this.getMyOrder()
						}		
					})	
				})
				console.log("申请退款成功")
			},
			cancelOrderById() {
				orderApi.cancelOrderById(this.orderId).then(res => {
					uni.showToast({
					  title:'取消订单成功'
					})
					this.showCancel = false
					this.getMyOrder()
				})
				console.log("取消订单成功")
			},
			toCancel() {
				this.showCancel = true
			},
			// 查看商品详情
			toProductInfo(id) {
				uni.navigateTo({
					url: `/pages/GoodsDetails/GoodsDetails?id=${id}`
				})
			},
			// 询问是否移入收藏
			toMove(id) {
				this.showMove = true
				this.orderId = id
			},
			deleteOrder() {
				orderApi.deleteOrderById(this.orderId).then(res => {
					uni.showToast({
					  title:'删除成功'
					})
					this.showMove = false
					this.getMyOrder()
				})
				console.log("删除订单")
				setTimeout(()=>{
					uni.navigateBack()
				}, 300)
			},
			// 根据id查询
			getById() {
				orderApi.get(this.orderId).then(res=>{
					this.order = res.data
					if(res.data!=null){
						const creatTime = res.data.createTime
						orderApi.orderRemainingTime(creatTime).then(r => {
							if(res.data){
								const seconds = res.data / 1000.0;
								this.CountDown = seconds
							}else {
								this.CountDown = 1
							}
						})
					}else{
						this.CountDown=1800
					}
				})
			},
			// 确认收货
			confirmSign() {
				orderApi.receiveById(this.orderId).then(res=>{
					uni.showToast({
						title:res.msg
					})
					setTimeout(()=>{
						uni.navigateBack()
					}, 300)
				})
			},
			// 支付
			pay() {
				uni.redirectTo({
					url: `/pages/CashierDesk/CashierDesk?id=${this.order.id}&amount=${this.order.payAmount}`,
				})
			},
			/**
			 * 售后点击
			 */
			onApplyAftersales(){
				uni.navigateTo({
					url: '/pages/AfterSaleType/AfterSaleType',
				})
			}
		}
	}
</script>

<style scoped lang="scss">
	@import 'OrderDetails.scss';
</style>
