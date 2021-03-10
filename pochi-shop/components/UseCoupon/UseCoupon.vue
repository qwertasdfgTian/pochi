<template>
	<view class="page-total">
		<view class="cu-modal bottom-modal" :class="{ show: isShow }">
			<view class="cu-dialog">
				<view class="coupon-title">
					<view class="title">优惠券</view>
					<view class="explain">使用说明</view>
				</view>
				<view class="coupon-tab">
					<view class="tab" :class="{'action':TabShow===0}" @click.stop="onTab(0)">
						<text>可用优惠券（{{couponList.length}}）</text>
						<text class="line"></text>
					</view>
					<!-- <view class="tab" :class="{'action':TabShow===1}" @click.stop="onTab(1)">
						<text>不可用优惠券（1）</text>
						<text class="line"></text>
					</view> -->
				</view>
				<!-- 优惠券数据 -->
				<view class="coupon-data">
					<view class="top" v-if="couponList.length===0" style="text-align: center;line-height: 50px;">
						没有可用优惠券
					</view>
					<view class="coupon-list">
						<view class="list" :class="{'forbidden':TabShow === 1}" v-for="(item,index) in couponList" :key="index">
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
								<view class="check" @click="changeCheck(item)" v-show="TabShow === 0">
									<view class="iconfont icon-checked" v-if="item.check"></view>
									<view class="iconfont icon-check" v-else></view>
								</view>
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
									<!-- <view class="get">
										<text>点击领取</text>
									</view> -->
								</view>
							</view>
						</view>
					</view>
				</view>
				<!--确认 -->
				<view class="cpupon-confirm">
					<view class="confirm" @click.stop="onConfirm">确定</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				isShow: false,
				TabShow: 0,
				// 当前选中的优惠券
				currentCoupon: null
			};
		},
		props: {
			// 优惠券列表
			couponList: {
				type: Array,
				default () {
					return {}
				}
			}
		},
		methods: {			// 选中
			changeCheck(item) {
				this.couponList.forEach(e=>{
					if(e.check) {
						this.$set(e, 'check', false)
					}
				})
				this.$set(item, 'check', true)
				this.currentCoupon = item
			},
			show() {
				this.isShow = true;
			},
			hide() {
				this.isShow = false;
			},
			/**
			 * tab 点击
			 */
			onTab(index) {
				this.TabShow = index;
			},
			/**
			 * 确认点击
			 */
			onConfirm() {
				this.hide();
				this.$emit('checkCoupon', this.currentCoupon)
			}
		}
	}
</script>

<style scoped lang="scss">
	@import 'UseCoupon.scss';
</style>
