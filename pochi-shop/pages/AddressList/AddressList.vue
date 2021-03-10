<template>
	<view class="page">
		<!-- 地址列表 -->
		<view class="address-list">
			<view class="list" v-for="(item,index) in addressList" :key="index">
				<view @click="toBack(item)" class="name-phone">
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
	</view>
</template>

<script>
	import addressApi from '@/api/shop-user-address.js'
	export default {
		data() {
			return {
				// 地址列表
				addressList: [],
				// 跳转类型，1代表从订单跳转过来
				type: null,
				// 页面传进来的商品信息
				productArr: []
			};
		},
		onLoad(param) {
			this.type = param.type
			this.productArr = param.productArr
		},
		onShow() {
			this.getAddressList()
		},
		methods:{
			// 点击地址
			toBack(item) {
				if(this.type) {
					// uni.redirectTo({
					// 	url: `/pages/ConfirmOrder/ConfirmOrder?address=${JSON.stringify(item)}&productArr=${this.productArr}`
					// })
					uni.setStorageSync('Address', item)
					uni.navigateBack()
				}
			},
			// 查询地址列表
			getAddressList() {
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
			}
		}
	}
</script>

<style scoped lang="scss">
	@import 'AddressList.scss';
</style>
