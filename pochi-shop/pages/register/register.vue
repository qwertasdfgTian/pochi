<template>
  <view class="page">
    <!-- 导航栏 -->
	<scroll-view scroll-x class="bg-white nav">
		<view class="flex text-center">
			<view class="cu-item flex-sub" :class="1==TabCur?'text-orange cur':''" @tap="tabSelect" :data-id="1">
				绑定已有账号
			</view>
			<view class="cu-item flex-sub" :class="2==TabCur?'text-orange cur':''" @tap="tabSelect" :data-id="2">
				注册并绑定新账号
			</view>
		</view>
	</scroll-view>
	<!-- 导航栏结束 -->
    <!-- 填写区 -->
    <view class="input-info">
      <view class="info">
        <input type="tel" v-model="form.phone" maxlength="11" placeholder="请输入账号">
        <view class="more">

        </view>
      </view>
      <view class="info">
        <input :password='!isPassword' maxlength="26" v-model="form.password" placeholder="请输入密码">
        <view class="more">
          <text class="iconfont" :class="isPassword?'icon-eye-on':'icon-eye-off'" @click="isPassword = !isPassword"></text>
        </view>
      </view>
    </view>
    <!-- 按钮 -->
    <view class="btn-info">
      <view class="btn" :style="isRegister?'opacity:1':'opacity:0.4'" @click="isRegister?onRegister():''">
        <text>绑定</text>
      </view>
    </view>
    <!-- 操作 -->
    <view class="operation">
      <text></text>
      <text @click="onLogin">已有账号?登录</text>
    </view>
  </view>
</template>

<script>
	import wxApi from '@/api/we-chat.js'
	import md5 from 'js-md5'
export default {
  data() {
    return {
      isPassword: false,
      isRegister: true,
	  // 当前导航栏
	  TabCur: 1,
      // 表单
      form:{
        phone: '',
        password: '',
      },
    };
  },
  methods:{
    onLogin(){
      uni.redirectTo({
        url: '/pages/login/login'
      })
    },
	// 切换导航栏
	tabSelect(e) {
		this.TabCur = e.currentTarget.dataset.id;
	},
    /**
     * 注册点击
     */
    onRegister(){
		// 设置绑定类型，1是绑定现有账号，2是绑定新账号
		this.form.bindType = this.TabCur
		// 密码加密
		const user = {...this.form}
		user.password = md5(user.password)
		wxApi.bindUser(user).then(res=>{
			uni.showToast({
				title:res.msg
			})
			wxApi.getLoginInfo().then(res=>{
				uni.setStorageSync('loginUser', res.data)
			})
			// 跳转到首页
			uni.switchTab({
				url:'/pages/home/home'
			})
		})
    }
  },
  watch:{
    form:{
      handler(newValue, oldValue) {
        if(newValue.phone && newValue.password){
          this.isRegister = true;
        }else{
          this.isRegister = false;
        }
      },
      deep: true
    }
  }
}
</script>

<style scoped lang="scss">
@import 'register.scss';
</style>
