<template>
		<view class="cu-modal bottom-modal" :class="{'show':isShow}" @click="hide">
		  <view class="cu-dialog">
		    <view class="modal-title">
					<text>优惠券</text>
				</view>
				<view class="tips">可领取优惠券</view>
				<view class="coupon-list">
					<view class="top" v-if="couponList.length===0" style="text-align: center;line-height: 50px;">
						没有可领取的优惠券
					</view>
					<view class="list" v-for="item in couponList" :key="item.id">
						<view class="coupon-price">
							<view class="discounts">
								<text class="min">￥</text>
								<text class="max">{{item.amount}}</text>
							</view>
							<view class="full-reduction">
								<text>满{{item.minPoint}}元减{{item.amount}}元</text>
							</view>
							<view class="jag"></view>
						</view>
						<view class="coupon-info">
							<view class="info-title">
								<view class="tag">
									<text v-if="item.couponType === 0">全场通用</text>
									<text v-else-if="item.couponType === 1">限品类券</text>
									<text v-else>限本商品</text>
								</view>
								<view class="title">
									<text>{{item.name}}</text>
								</view>
							</view>
							<view class="date-get">
								<view class="date">
									<text>{{String(item.startTime).split(' ')[0]}}-{{String(item.endTime).split(' ')[0]}}</text>
								</view>
								<view class="get" v-if="item.status === 1" @click="catchCoupon(item.id)">
									<text>点击领取</text>
								</view>
								<view class="get" v-else>
									<!-- <text></text> -->
								</view>
							</view>
						</view>
						<view class="use-status" v-if="item.status === 0">
							<text>已过期</text>
						</view>
						<view class="use-status" v-if="item.status === 999">
							<text>已领取</text>
						</view>
					</view>
				</view>
		  </view>
		</view>
</template>

<script>
	import couponApi from '@/api/shop-coupon.js'
	export default {
		data() {
			return {
				isShow: false,
				isPage: false,
			}
		},
		props: {
			// 优惠券列表
			couponList: {
				type: Array,
				default() {
					return {}
				}
			}
		},
		methods: {
			// 领取优惠券
			catchCoupon(id) {
				couponApi.catchCoupon({id}).then(res=>{
					uni.showToast({
						title:res.msg
					})
					setTimeout(()=>{
						this.$emit('refreshCoupon')
					}, 300)
				})
			},
			show(){
				this.isPage = true;
				setTimeout(() =>{
					this.isShow = true;
				},300)
			},
			hide(){
				this.isShow = false;
				setTimeout(() =>{
					this.isPage = false;
				},300)
			},
		},
	}
</script>

<style scoped lang="scss">
	@import 'GoodsCoupon.scss';
</style>
