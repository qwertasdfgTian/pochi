<template>
	<view class="content">
		<image class="logo" src="/static/logo.png"></image>
		<view class="text-area">
			<text class="title">{{title}}</text>
		</view>
		<vus-layer></vus-layer>
	</view>
</template>

<script>
	import wxApi from '@/api/we-chat.js'
	export default {
		data() {
			return {
				title: '喵喵宠物商城'
			}
		},
		onLoad() {
			uni.login({
				provider:'weixin',
				success: (r) => {
					const code = r.code
					wxApi.loginByCode(code).then(res=>{
						// 如果有token字段，说明登录成功，否则用户不存在
						if(res.data.token) {
							// 把token存到本地缓存
							uni.setStorageSync('Authorization', res.data.token)
							wxApi.getLoginInfo().then(res=>{
								uni.setStorageSync('loginUser', res.data)
							})
						}else {
							// 把openId存到缓存中 
							uni.setStorageSync('openId', res.data.openId)
							uni.setStorageSync('loginflage', true)
						}
						// 跳转到首页
						uni.switchTab({
							url:'/pages/home/home'
						})
					})
				}
			})
		},
		methods: {
		}
	}
</script>

<style>
	.content {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}

	.logo {
		height: 200rpx;
		width: 200rpx;
		margin-top: 200rpx;
		margin-left: auto;
		margin-right: auto;
		margin-bottom: 50rpx;
	}

	.text-area {
		display: flex;
		justify-content: center;
	}

	.title {
		font-size: 36rpx;
		color: #8f8f94;
	}
</style>
