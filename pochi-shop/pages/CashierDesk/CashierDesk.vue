<template>
	<view class="page">
		<view class="price-count-down">
			<view class="price">
				<text class="min">￥</text>
				<text class="max">{{String(payAmount).split('.')[0]}}</text>
				<text class="min">.{{String(payAmount).split('.')[1]?String(payAmount).split('.')[1]:'00'}}</text>
			</view>
			<view class="count-down">
				<view class="title">支付剩余时间</view>
				<view class="count">
					<text class="time">{{hour}}</text>
					<text class="dot">:</text>
					<text class="time">{{min}}</text>
					<text class="dot">:</text>
					<text class="time">{{sec}}</text>
				</view>
			</view>
		</view>
		<!-- 支付方式列表 -->
		<view class="pay-way">
			<view class="pay-list">
				<view class="list" v-for="(item,index) in PayList" @click="onPayWay(item,index)" :key="index">
					<view class="pay-type">
						<image :src="item.icon" mode=""></image>
						<text>{{item.name}}</text>
					</view>
					<view class="check">
						<text class="iconfont" :class="PayWay === index ? 'icon-checked action':'icon-check'"></text>
					</view>
				</view>
			</view>
		</view>
		<!-- 支付宝支付的弹窗 -->
		<popup-layer class="tanchu" ref="popupRef" @closeCallBack="close" :direction="'top'">
		  <view class="qrimg" style="text-align: center;padding-top: 20px;padding-bottom: 30px;">
		      <tki-qrcode cid="qrcode2" :icon="icon" ref="qrcode2" :val="val" :size="size" :onval="onval" :loadMake="loadMake" :usingComponents="true" />
		  </view>
		  <view style="text-align: center;padding-bottom: 100px;font-family:Georgia;font-size:20px;">请使用支付宝扫码支付</view>
		</popup-layer>
	    <!-- 支付宝弹窗结束 -->
		<view class="pay-submit">
			<view class="submit" @click="onSubmit">支付宝支付￥{{payAmount}}</view>
		</view>
	</view>
</template>

<script>
	import orderApi from '@/api/shop-order.js'
	import tkiQrcode from "@/components/tki-qrcode/tki-qrcode.vue"
	import popupLayer from '@/components/popup-layer/popup-layer.vue';
	export default {
		components: {tkiQrcode,popupLayer},
		data() {
			return {
				PayList: [{
					icon: '/static/zfb_pay.png',
					name: '支付宝支付',
				}],
				timer:null,
				// 订单金额
				payAmount: null,
				// 订单号
				orderId: null,
				PayWay: 0,
				PayPirce: `微信支付￥299.00`,
				CountDown: 1800,
				day: 0,
				hour: 0,
				min: 0,
				sec: 0,
				val: '', // 要生成的二维码值
				size: 300, // 二维码大小
				unit: 'upx', // 单位
				background: '#b4e9e2', // 背景色
				foreground: '#309286', // 前景色
				pdground: '#32dbc6', // 角标色
				icon: '/static/zfb_pay.png', // 二维码图标
				iconsize: 40, // 二维码图标大小
				lv: 3, // 二维码容错级别 ， 一般不用设置，默认就行
				onval: true, // val值变化时自动重新生成二维码
				loadMake: true, // 组件加载完成后自动生成二维码
				src: '' // 二维码生成后的图片地址或base64
			};
		},
		onLoad(param) {
			// 取出订单号和金额
			this.orderId = param.id
			this.payAmount = param.amount
			// 查询是否存在该订单
			this.get()
			this.CountDownData();
		},
		methods: {
			get(){
				orderApi.get(this.orderId).then(r => {
					if(r.data!=null){
						const creatTime = r.data.createTime
						orderApi.orderRemainingTime(this.orderId).then(res => {
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
			show(){
				this.$refs.popupRef.show() // 或者 boolShow = rue
			},
			close(){
				clearInterval(this.timer)
				console.log("退出去了")
			},
			closeAll() {
				this.$refs.popupRef.close()// 或者 boolShow = false
			},
			/**
			 * 支付方式切换点击
			 */
			onPayWay(item, index) {
				this.PayWay = index;
				this.PayPirce = `${item.name}￥299.00`
			},
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
			/**
			 * 支付点击
			 */
			onSubmit() {
				
				uni.requestSubscribeMessage({
					tmplIds: ['mRURJLmDVL2imCDEbp6vwyLPJQ_pjdvlRf4aCnk0MnU'],	
					success: (response)=> {
						console.log("我进来发消息了")
						// 调用支付接口
						orderApi.createPayOrder({
							id: this.orderId
						}).then(res => {
							if(res.data.payUrl){
								// 打开支付宝二维码弹窗
								this.val = res.data.payUrl
								console.log(this.val)
								this.show()
								this.timer = setInterval(() => {
									console.log("我是定时器")	
									orderApi.queryOrderPayOrderId(res.data.orderId).then(r => {
										if (r.data.status === 1) { // 说明订单状态为支付成功
										  // 清空定时器
										  clearInterval(this.timer)
										  uni.showToast({
										  	title:'支付成功'
										  })
										  this.closeAll()
										  uni.redirectTo({
										     url: `/pages/PayResult/PayResult?amount=${this.payAmount}`,
										  })
										}
									}).catch(() => {
									   // 清空定时器
									   clearInterval(this.timer)
								  })
								}, 2000);
							}else {
								uni.showToast({
									title: res.msg
								})
							}
							
						})		
						
					}
				})			
			}
		}
	}
</script>

<style scoped lang="scss">
	@import 'CashierDesk.scss';
</style>
