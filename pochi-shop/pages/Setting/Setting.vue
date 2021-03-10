<template>
	<view class="page">
		<!-- 用户信息 -->
		<view class="user-info">
			<view class="user-data" @click="onUserInfo">
				<view class="portrait-nickname">
					<view class="portrait">
						<image :src="user.header" mode=""></image>
					</view>
					<view class="nickname">
						<text>{{user.nickName}}</text>
					</view>
				</view>
				<view class="more">
					<text class="iconfont icon-more"></text>
				</view>
			</view>
			<view class="address" @click="onAddress">
				<view class="title">
					<text>地址管理</text>
				</view>
				<view class="more">
					<text class="iconfont icon-more"></text>
				</view>
			</view>
		</view>
		<!-- 设置列表 -->
		<view class="setting-list">
			<view class="list" @click="onPhone">
				<view class="title">
					<text>账户绑定</text>
				</view>
				<view class="more">
					<text class="iconfont icon-more"></text>
				</view>
			</view>
		</view>
		<!-- 设置列表 -->
		<view class="setting-list">
			<!-- #ifndef H5 -->
				<view class="list" @click="onSetting('common')">
					<view class="title">
						<text>通用</text>
					</view>
					<view class="more-content">
						<text class="content">清除本地缓存等</text>
						<text class="iconfont icon-more more"></text>
					</view>
				</view>
			<!-- #endif -->
			<view class="list" @click="onSetting('about')">
				<view class="title">
					<text>关于我们</text>
				</view>
				<view class="more-content">
					<text class="content"></text>
					<text class="iconfont icon-more more"></text>
				</view>
			</view>
		</view>
		<!-- 退出 -->
		<view class="quit-login" @click="changeUser">
			<text>切换账号</text>
		</view>
		<!-- 提示框 -->
		<DialogBox ref="DialogBox"></DialogBox>
	</view>
</template>

<script>
	import wxApi from '@/api/we-chat.js'
	export default {
		data() {
			return {
				user:{}
			};
		},
		onShow() {
			this.user = uni.getStorageSync('loginUser')
		},
		methods:{
			/**
			 * 用户切换点击
			 */
			changeUser(){
				uni.navigateTo({
					url: '/pages/login/login'
				})
			},
			/**
			 * 用户信息点击
			 */
			onUserInfo(){
				uni.navigateTo({
					url: '/pages/Information/Information'
				})
			},
			/**
			 * 地址点击
			 */
			onAddress(){
				uni.navigateTo({
					url: '/pages/AddressList/AddressList',
				})
			},
			onPhone(){
				uni.navigateTo({
					url:'/pages/register/register'
				})
			},
			/**
			 * 设置列表点击
			 * @param {String} type
			 */
			onSetting(type){
				switch(type) {
					case 'account':
						uni.navigateTo({
							url: '/pages/AccountSecurity/AccountSecurity'
						})
						break;
					case 'pay':
						uni.navigateTo({
							url: '/pages/PaymentPassword/PaymentPassword'
						})
						break;
					case 'invoice':
						uni.navigateTo({
							url: '/pages/InvoiceList/InvoiceList'
						})
						break;
					case 'vip':
						uni.navigateTo({
							url: '/pages/MyMemberInterest/MyMemberInterest'
						})
						break;
					case 'common':
						uni.navigateTo({
							url: '/pages/SettingCommon/SettingCommon'
						})
						break;
					case 'about':
						uni.navigateTo({
							url: '/pages/AboutUs/AboutUs'
						})
						break;
				}
			}
		}
	}
</script>

<style scoped lang="scss">
	@import 'Setting.scss';
</style>
