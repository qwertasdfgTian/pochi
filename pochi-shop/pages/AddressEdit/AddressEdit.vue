<template>
	<view class="page">
		<view class="address-input">
			<view class="list-input">
				<view class="title">
					<text>收货人</text>
				</view>
				<view class="content">
					<input v-model="address.name" type="text" placeholder="请填写收货人姓名">
				</view>
			</view>
			<view class="list-input">
				<view class="title">
					<text>手机号</text>
				</view>
				<view class="content">
					<input v-model="address.phoneNumber" type="tel" placeholder="请填写收货人手机号">
				</view>
			</view>
			<view class="list-input">
				<view class="title">
					<text>所在地区</text>
				</view>
				<view class="content">
					<input v-model="addressAddr" @focus="openSelect" type="tel" placeholder="省市区县/乡镇等">
				</view>
			</view>
			<view class="list-textarea">
				<view class="title">
					<text>详细地址</text>
				</view>
				<view class="content">
					<textarea v-model="address.detailAddress" type="tel" placeholder="街道/楼牌号等" />
				</view>
			</view>
		</view>
		<view class="tag-default">
			<view class="default-address">
				<view class="title">
					<text>默认地址</text>
				</view>
				<view class="switch-default">
					<switch v-model="defaultStatus" @change="changeDefault" class="red sm" color="#0077EE !important" :checked="defaultStatus"></switch>
				</view>
			</view>
		</view>
		<view class="footer-btn" @click="submit">
			<view class="btn">
				<text>保存</text>
			</view>
		</view>
		<simple-address ref="simpleAddress" :pickerValueDefault="cityPickerValueDefault" @onConfirm="confirmAddress" themeColor='#007AFF'></simple-address>
	</view>
</template>

<script>
	import addressApi from '@/api/shop-user-address.js'
	export default {
		data() {
			return {
				addressType: '2',
				// 地址 
				address: {
					province: '',
					city: '',
					region: ''
				},
				// 是否默认
				defaultStatus: true,
				// 省市县选择器默认选中
				cityPickerValueDefault: [0,0,1]
			};
		},
		onLoad(params) {
			this.address.defaultStatus = 1
			this.addressType = params.type||'2';
			uni.setNavigationBarTitle({
				title: this.addressType === '1' ? '编辑收货地址':'新建收货地址'
			})
			if(this.addressType === '1') {
				// 修改
				addressApi.get(params.id).then(res=>{
					this.address = res.data
					this.defaultStatus = this.address.defaultStatus === 1
				})
			}
		},
		computed:{
			addressAddr() {
				if(this.address.province) {
					return this.address.province + '-' + this.address.city + '-' + this.address.region
				}else {
					return ''
				}
			}
		},
		methods: {
			// 选中地址触发
			confirmAddress(e) {
				const addressArr = e.labelArr
				this.address.province = addressArr[0]
				this.address.city = addressArr[1]
				this.address.region = addressArr[2]
			},
			// 打开省市县选择器
			openSelect() {
				this.$refs.simpleAddress.open()
			},
			// 提交表单
			submit() {
				if(this.addressType === '1') {
					this.update()
				}else {
					this.save()
				}
			},
			// 修改默认地址
			changeDefault(e) {
				this.address.defaultStatus = e.target.value ? 1:0
			},
			// 修改地址
			update() {
				addressApi.update(this.address).then(res=>{
					uni.showToast({
						title:res.msg
					})
					setTimeout(()=>{
						uni.navigateBack()
					}, 500)
				})
			},
			// 添加地址
			save() {
				addressApi.save(this.address).then(res=>{
					uni.showToast({
						title:res.msg
					})
					setTimeout(()=>{
						uni.navigateBack()
					}, 500)
				})
			}
		}
	}
</script>

<style scoped lang="scss">
	@import 'AddressEdit.scss';
</style>
