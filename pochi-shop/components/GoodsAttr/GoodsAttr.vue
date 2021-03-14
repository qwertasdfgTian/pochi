<template>
	<view class="cu-modal bottom-modal" :class="{'show':isShow}" @click="hide">
	  <view class="cu-dialog">
			<view class="goods-data">
				<view class="thumb">
					<image :src="currentItem.pic" mode=""></image>
				</view>
				<view class="item">
					<view class="title">
						<text></text>
					</view>
					<view class="price">
						<text class="min">￥</text>
						<text class="max">{{String(currentItem.price).split('.')[0]}}</text>
						<text class="min">.{{String(currentItem.price).split('.')[1]?String(currentItem.price).split('.')[1]:'00'}}</text>
					</view>
					<view class="inventory">
						<text>库存：{{currentItem.stock}}</text>
					</view>
				</view>
			</view>
			<view class="attr-size">
				<view class="attr-list">
					<view class="title">
						<text>选择套装 </text>
					</view>
					<view class="size-list">
						<div class="list" v-for="(item,idx) in packList" 
						:class="{'action':currentIndex === idx}"
						@click.stop="onAttrSize(idx)" :key="idx">
							<text>{{item.specName}}</text>
						</div>
					</view>
				</view>
				<view class="attr-number" @click.stop="onStop">
					<view class="tit">数量</view>
					<view class="number">
						<text class="iconfont icon-jian" @click="changeQuantity(item, -1)"></text>
						<input type="tel" v-model="productCount">
						<text class="iconfont icon-jia" @click="changeQuantity(item, 1)"></text>
					</view>
				</view>
			</view>
			<view class="attr-btn">
				<view class="add-cart" v-if="BuyType === 1" @click="addCart()">加入购物车</view>
				<view class="add-buy" v-if="BuyType === 1" @click="onConfirm(BuyType)">立即购买</view>
				<view class="confirm" v-if="BuyType === 3" @click="onConfirm(BuyType)">确定</view>
				<view class="confirm" v-if="BuyType === 2" @click="addCart()">确定</view>
			</view>
		</view>
	</view>
</template>

<script>
	import cartApi from '@/api/shop-cart-item.js'
	export default {
		props: {
			// 套装列表
			packList: {
				type: Array,
				default() {
					return {}
				}
			}
		},
		data() {
			return {
				isShow: false,
				// 当前选中的索引
				currentIndex: 0,
				// 用户选择的商品数
				productCount: 1,
				AttrIndex: 0,
				SizeIndex: 0,
				// 购买类型
				BuyType: 0,
			};
		},
		computed:{
			currentItem() {
				return this.packList[this.currentIndex]
			}
		},
		methods:{
			// 改变数量
			changeQuantity(item, num) {
				this.productCount = this.productCount + num
				if(this.productCount <= 0){
				    this.productCount = 1
				    uni.showToast({
					  icon:'none',
					  title:'最少购买1个商品'
				    })
				}
				this.$emit("pChoseProduct",this.currentItem.specName+"，"+this.productCount+"个")
			},
			// 加入购物车
			addCart() {
				cartApi.save({productId: this.currentItem.productId, quantity: this.productCount}).then(res=>{
					uni.showToast({
						title:res.msg
					})
					this.hide()
				})
				this.$emit("pChoseProduct",this.currentItem.specName+"，"+this.productCount+"个")
			},
			/**
			 * 显示 
			 * @param {Number} type 1 点击选择 2 加入购物 3 立即购买
			 */
			show(type){
				this.BuyType = type;
				this.isShow = true;
			},
			hide(){
				this.isShow = false;
			},
			onStop(){
				
			},
			/**
			 * 属性选择点击
			 */
			onAttrSize(idx){
				this.currentIndex = idx
				this.$emit("pChoseProduct",this.currentItem.specName+"，"+this.productCount+"个")
			},
			/**
			 * 确认点击
			 */
			onConfirm(type){
				// 取出当前商品的信息，传递到订单页
				const productId = this.currentItem.productId
				const count = this.productCount
				const productArr = [
					{productId, count}
				]
				
				uni.navigateTo({
					url: '/pages/ConfirmOrder/ConfirmOrder?productArr=' + JSON.stringify(productArr)
				})
			}
		}
	}
</script>

<style scoped lang="scss">
	@import 'GoodsAttr.scss';
</style>
