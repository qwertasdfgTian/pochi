<template>
	<view class="page">
		<view class="logo">
			<image src="../../static/logo.png" mode=""></image>
		</view>
		<!-- 填写区 -->
		<view class="input-info">
			<view class="info">
				<input type="tel" maxlength="11" v-model="shopUserLoginDto.phone" placeholder="手机号">
				<view class="more"></view>
			</view>
			<view class="info" :style="isLoginWay?'display: none':''">
				<input :password='!isPassword' v-model="shopUserLoginDto.password" maxlength="26" placeholder="请输入密码">
				<view class="more">
					<text class="iconfont" :class="isPassword?'icon-eye-on':'icon-eye-off'" @click="isPassword = !isPassword"></text>
					<!-- <text class="mo">忘记密码</text> -->
				</view>
			</view>
		</view>
		<!-- 按钮 -->
		<view class="btn-info">
			<view class="btn" @click="isLogin?onLogin():''">
				<text>账号密码登录</text>
			</view>
		</view>
		<!-- 操作 -->
		<!-- <view class="operation" style="s;">
			<text @click="onRegister">手机号绑定</text>
		</view> -->
		<view class="other-ways">
			<text>其他登录方式</text>
		</view>
		<!-- 登录方式 -->
		<view class="login-way">
			<!-- uniapp自带的获取当前用户的信息 -->
			<button class="way" open-type="getUserInfo" @getuserinfo="getUserInfo">
				<image src="/static/wx_ico.png" mode=""></image>
				<text>微信登录</text>
			</button>
		</view>
	</view>
</template>

<script>
	import wxApi from '@/api/we-chat.js'
	import md5 from 'js-md5'
	export default {
		data() {
			return {
				isLogin: false,
				isLoginWay: false,
				isPassword: false,
				// 表单
				shopUserLoginDto: {
					phone: '',
					password: '',
				},
			};
		},
		methods: {
			/**
			 * 获取用户信息
			 */
			getUserInfo(detail) {
				const userInfo = detail.detail.userInfo
				// 获取到openId
				console.log(userInfo)
				const openId = uni.getStorageSync('openId')
				userInfo.openId = openId
				wxApi.registerLogin(userInfo).then(res=>{
					// 登录成功提示
					uni.showToast({
						title:'登录成功'
					})
					// 把token设置到缓存中
					uni.setStorageSync('Authorization', res.data.token)
					wxApi.getLoginInfo().then(res=>{
						uni.setStorageSync('loginUser', res.data)
					})
					// 跳转到首页
					uni.navigateTo({
						url:'/pages/home/home'
					})
				})
			},
			/**
			 * 登录切换
			 */
			// onLoginCut() {
			// 	this.isLoginWay = !this.isLoginWay;
			// 	// 账号密码
			// 	if (!this.isLoginWay) {
			// 		this.isLogin = this.form.password && this.form.phone ? true : false;
			// 	}
			// },
			/**
			 * 登录点击
			 */
			onLogin() {
				// 密码加密
				const user = {...this.shopUserLoginDto}
				user.password = md5(user.password)
				wxApi.wxLoginByPhone(user).then(res=>{
					// 登录成功提示
					uni.showToast({
						title:'登录成功'
					})
					// 把token设置到缓存中
					uni.setStorageSync('Authorization', res.data.token)
					wxApi.getLoginInfo().then(res=>{
						uni.setStorageSync('loginUser', res.data)
					})
					// 跳转到首页
					uni.navigateTo({
						url:'/pages/home/home'
					})
				})
			}
		},
		watch: {
			shopUserLoginDto: {
				handler(newValue, oldValue) {
					// 账号密码
					if (!this.isLoginWay) {
						this.isLogin = newValue.password && newValue.phone ? true : false;
					}
				},
				deep: true
			}
		}
	}
</script>

<style scoped lang="scss">
	@import 'login.scss';
	button {
		background-color: transparent;
		&::after {
			border: 0px;
		}
	}
</style>