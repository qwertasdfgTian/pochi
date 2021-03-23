<template>
	<view class="page">
		<view class="head-back">
			<view class="back" @click="onBack">
				<text></text>
			</view>
			<view class="title">
				<text>我的订单</text>
			</view>
		</view>
		<!-- 订单tab -->
		<view class="order-tab">
			<view class="tab" :class="{'action':orderStatus==999}" @click="onOrderTab(999)">
				<text>全部</text>
				<text class="line"></text>
			</view>
			<view class="tab" :class="{'action':orderStatus==0}" @click="onOrderTab(0)">
				<text>待付款</text>
				<text class="line"></text>
			</view>
			<view class="tab" :class="{'action':orderStatus==2}" @click="onOrderTab(2)">
				<text>待发货</text>
				<text class="line"></text>
			</view>
			<view class="tab" :class="{'action':orderStatus==3}" @click="onOrderTab(3)">
				<text>待收货</text>
				<text class="line"></text>
			</view>
			<view class="tab" :class="{'action':orderStatus==4}" @click="onOrderTab(4)">
				<text>待评价</text>
				<text class="line"></text>
			</view>
		</view>
		<!-- 订单列表 -->
		<view class="order-list">
			<view class="list" v-for="(item,index) in dataPage.list" :key="index">
				<view class="title-status">
					<view class="title">
						<text>下单时间：{{item.createTime}}</text>
					</view>
					<view class="status">
						<text>{{orderType[item.status]}}</text>
						<text class="iconfont icon-laji del" @click="toMove(item.id)" v-if="item.status >= 4"></text>
					</view>
				</view>
				<view class="goods-list">
					<view class="goods" v-for="(product, i) in item.itemList" :key="i" @click="onOrderList(item.id)">
						<view class="thumb">
							<image :src="product.productPic" mode=""></image>
						</view>
						<view class="item">
							<view class="goods-name">
								<text class="two-omit">{{product.productName}}</text>
							</view>
							<view class="goods-price">
								<text class="min">￥</text>
								<text class="max">{{String(product.productPrice).split('.')[0]}}</text>
								<text class="min">.{{String(product.productPrice).split('.')[1]?String(product.productPrice).split('.')[1]:'00'}}</text>
							</view>
						</view>
					</view>
				</view>
				<view class="status-btn">
					<view class="btn" @click="toCancel(item.id)" v-if="item.status === 1">
						<text>取消订单</text>
					</view>
					<view v-if="item.status === 4" class="btn" @click.stop="onEvaluate(item)">
						<text>评价</text>
					</view>
					<view v-if="item.status >= 4" class="btn action" @click.stop="onBuy(item)">
						<text>再次购买</text>
					</view>
					<view v-if="item.status === 2" class="btn action" @click.stop="toReturn(item.id)">
						<text>申请退款</text>
					</view>
					<view v-if="item.status === 3" class="btn action" @click.stop="receiveById(item.id)">
						<text>确认收货</text>
					</view>
					<view v-if="item.status === 0" class="btn action" @click.stop="pay(item)">
						<text>确认付款</text>
					</view>
				</view>
			</view>
		</view>
		<view class="top" v-if="showBottom" style="text-align: center;line-height: 50px;">
			暂时没有相关的订单
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
	export default {
		data() {
			return {
				// 订单Id
				orderId:'',
				showBottom: false,
				orderStatus: 0,
				// 分页查询参数
				page: {
					// 当前页
					pageNumber: 1,
					// 每页条数
					pageSize: 20,
					// 查询参数
					params: {}
				},
				// 弹窗显示状态
				showMove: false,
				showCancel: false,
				showReturn: false,
				// 订单状态
				orderStatus: '999',
				// 数据分页对象
				dataPage: {
					list: []
				},
				orderType:['待付款','待确认','待发货','已发货','已签收','已完成','无效订单','已关闭'],
			};
		},
		watch: {
			orderStatus: {
				immediate: true,
				handler: function() {
					if(this.orderStatus !== 999) {
						this.page.params.status = this.orderStatus
					}else {
						this.page.params.status = null
					}
					this.getMyOrder()
				}
			}
		},
		onShow() {
			this.getMyOrder()
		},
		onLoad(params) {
			let type = params.type
			if(!type) {
				type = 999
			}
			this.orderStatus = type;
		},
		methods: {
			// 再次购买
			onBuy(item){
				uni.navigateTo({
					url: `/pages/GoodsDetails/GoodsDetails?id=${item.itemList[0].productId}`
				})
			},
			pay(item){
				uni.redirectTo({
					url: `/pages/CashierDesk/CashierDesk?id=${item.id}&amount=${item.payAmount}`,
				})
				// uni.navigateTo({
				// 	url: '/pages/OrderDetails/OrderDetails?id='+item.id,
				// })
			},
			// 确认收货
			receiveById(id) {
				orderApi.receiveById(id).then(res => {
					uni.showToast({
					  title:'确认收货成功'
					})
					this.getMyOrder()
				})
			},
			// 询问是否取消订单
			toCancel(id) {
				this.showCancel = true
				this.orderId = id
			},
			toReturn(id) {
				this.showReturn = true
				this.orderId = id
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
			// 询问是否删除
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
			},
			// 查询我的订单
			getMyOrder() {
				orderApi.getMyOrder(this.page).then(res => {
					this.showBottom = false
					this.dataPage = res.data
					if(res.data.totalCount === 0){
						this.showBottom = true
					}
				})
			},
			/**
			 * 返回点击
			 */
			onBack() {
				uni.navigateBack();
			},
			/**
			 * 订单tab点击
			 */
			onOrderTab(type) {
				this.orderStatus = type;
				// #ifdef H5
				uni.redirectTo({
					url: '/pages/MyOrderList/MyOrderList?type=' + type,
				})
				//#endif
			},
			/**
			 * 订单列表点击
			 */
			onOrderList(id) {
				uni.navigateTo({
					url: '/pages/OrderDetails/OrderDetails?id='+id,
				})
			},
			/**
			 * 评价点击
			 */
			onEvaluate(item) {
				uni.navigateTo({
					url: '/pages/MyEvaluatePush/MyEvaluatePush?itemList='+JSON.stringify(item.itemList)
				})
				this.getMyOrder()
			}
		}
	}
</script>

<style scoped lang="scss">
	@import 'MyOrderList.scss';
</style>
