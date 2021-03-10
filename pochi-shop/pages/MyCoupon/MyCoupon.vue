<template>
	<view class="page">
		<!-- 优惠券tab -->
		<view class="coupon-tab">
			<view class="tab" :class="{'action':TabShow===0}" @click="onCouponTab(0)">
				<text>未使用</text>
				<text class="line"></text>
			</view>
			<view class="tab" :class="{'action':TabShow===1}" @click="onCouponTab(1)">
				<text>已使用</text>
				<text class="line"></text>
			</view>
			<view class="tab" :class="{'action':TabShow===2}" @click="onCouponTab(2)">
				<text>已过期</text>
				<text class="line"></text>
			</view>
		</view>
		<!-- 优惠券列表 -->
		<view class="coupon-list">
			<view class="top" v-if="MyBothCouponVo.expiredCoupon.length===0 && TabShow===2" style="text-align: center;line-height: 50px;">
				暂时没有发现优惠券~
			</view>
			<view class="top" v-if="MyBothCouponVo.notUsedCoupon.length===0 && TabShow===0" style="text-align: center;line-height: 50px;">
				暂时没有发现优惠券~
			</view>
			<view class="top" v-if="MyBothCouponVo.usedCoupon.length===0 && TabShow===1" style="text-align: center;line-height: 50px;">
				暂时没有发现优惠券~
			</view>
			
			<view class="list" v-if="TabShow===0" v-for="(item,index) in MyBothCouponVo.notUsedCoupon" :key="index">
				<view class="list-data" :class="{'coupon-lose':TabShow!=0}">
					<view class="coupon-price">
						<view class="discounts">
							<text class="min">￥</text>
							<text class="max">{{item.amount}}</text>
						</view>
						<view class="full-reduction"><text>满{{item.minPoint}}元减{{item.amount}}元</text></view>
						<view class="jag"></view>
					</view>
					<view class="coupon-info">
						<view class="info-title">
							<view class="tag" v-if="item.couponType===1"><text>限品类券</text></view>
							<view class="tag" v-if="item.couponType===2"><text>限特定商品</text></view>
							<view class="tag" v-if="item.couponType===0"><text>全场通用</text></view>
						</view>
						<view class="date-get">
							<view class="date"><text>{{item.startTime.split(" ")[0]}}-{{item.endTime.split(" ")[0]}}</text></view>
							<view class="get" @click="onCouponUse(item.id)" v-if="TabShow===0">
								<text>立即使用</text>
							</view>
						</view>
						<view class="describe-title" @click="isDes=!isDes">
							<text v-if="item.couponType===1">限品类：尽可购买特定分类的商品抵用</text>
							<text v-if="item.couponType===2">限特定商品：尽可购买指定的商品抵用</text>
							<text v-if="item.couponType===0">全场通用：可购买所有商品抵用</text>
						</view>
					</view>
				</view>
				<view class="use-status" v-if="TabShow != 0">
					<text v-if="TabShow === 1">已使用</text>
					<text v-else-if="TabShow === 2">已过期</text>
				</view>	
			</view>
			
			<view class="list" v-if="TabShow===1" v-for="(item,index) in MyBothCouponVo.usedCoupon" :key="index">
				<view class="list-data" :class="{'coupon-lose':TabShow!=0}">
					<view class="coupon-price">
						<view class="discounts">
							<text class="min">￥</text>
							<text class="max">{{item.amount}}</text>
						</view>
						<view class="full-reduction"><text>满{{item.minPoint}}元减{{item.amount}}元</text></view>
						<view class="jag"></view>
					</view>
					<view class="coupon-info">
						<view class="info-title">
							<view class="tag" v-if="item.couponType===1"><text>限品类券</text></view>
							<view class="tag" v-if="item.couponType===2"><text>限特定商品</text></view>
							<view class="tag" v-if="item.couponType===0"><text>全场通用</text></view>
						</view>
						<view class="date-get">
							<view class="date"><text>{{item.startTime.split(" ")[0]}}-{{item.endTime.split(" ")[0]}}</text></view>
							<view class="get" @click="onCouponUse" v-if="TabShow===0">
								<text>立即使用</text>
							</view>
						</view>
						<view class="describe-title" @click="isDes=!isDes">
							<text v-if="item.couponType===1">限品类：尽可购买特定分类的商品抵用</text>
							<text v-if="item.couponType===2">限特定商品：尽可购买指定的商品抵用</text>
							<text v-if="item.couponType===0">全场通用：可购买所有商品抵用</text>
						</view>
					</view>
				</view>
				<view class="use-status" v-if="TabShow != 0">
					<text v-if="TabShow === 1">已使用</text>
					<text v-else-if="TabShow === 2">已过期</text>
				</view>	
			</view>
			
			<view class="list" v-if="TabShow===2" v-for="(item,index) in MyBothCouponVo.expiredCoupon" :key="index">	
				<view class="list-data" :class="{'coupon-lose':TabShow!=0}">
					<view class="coupon-price">
						<view class="discounts">
							<text class="min">￥</text>
							<text class="max">{{item.amount}}</text>
						</view>
						<view class="full-reduction"><text>满{{item.minPoint}}元减{{item.amount}}元</text></view>
						<view class="jag"></view>
					</view>
					<view class="coupon-info">
						<view class="info-title">
							<view class="tag" v-if="item.couponType===1"><text>限品类券</text></view>
							<view class="tag" v-if="item.couponType===2"><text>限特定商品</text></view>
							<view class="tag" v-if="item.couponType===0"><text>全场通用</text></view>
						</view>
						<view class="date-get">
							<view class="date"><text>{{item.startTime.split(" ")[0]}}-{{item.endTime.split(" ")[0]}}</text></view>
							<view class="get" @click="onCouponUse" v-if="TabShow===0">
								<text>立即使用</text>
							</view>
						</view>
						<view class="describe-title" @click="isDes=!isDes">
							<text v-if="item.couponType===1">限品类：尽可购买特定分类的商品抵用</text>
							<text v-if="item.couponType===2">限特定商品：尽可购买指定的商品抵用</text>
							<text v-if="item.couponType===0">全场通用：可购买所有商品抵用</text>
						</view>
					</view>
				</view>
				<view class="use-status" v-if="TabShow != 0">
					<text v-if="TabShow === 1">已使用</text>
					<text v-else-if="TabShow === 2">已过期</text>
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
			TabShow: 0,
			isDes: false,
			MyBothCouponVo:{
				// 过期优惠券列表
				expiredCoupon:[],
				// 未使用优惠券列表
				notUsedCoupon:[],
				// 已使用优惠券列表
				usedCoupon:[]
			},
			map:{}
		};
	},
	onShow() {
		this.getAllProductCoupon()
	},
	methods:{
		// 查询所有的我的优惠券
		getAllProductCoupon(){
			couponApi.getAllProductCoupon().then(res=>{
				this.MyBothCouponVo = res.data
			})
		},
		/**
		 * 优惠券tab点击
		 * @param {Number} type
		 */
		onCouponTab(type){
			this.TabShow = type;
		},
		/**
		 * 去使用点击
		 */
		onCouponUse(id){
			uni.navigateTo({
				url: '/pages/SearchGoodsList/SearchGoodsList'
			})
		}
	}
};
</script>

<style scoped lang="scss">
@import 'MyCoupon.scss';
</style>
