<template>
	<view class="page">
		<!-- 用户信息列表 -->
		<view class="user-list">
			<view class="list" style="height: 160rpx;" @click="uploadPictures">
				<view class="title">
					<text>头像</text>
				</view>
				<view class="more-content">
					<image :src="image" mode=""></image>
					<image v-for="(item,index) in images" :key="index" :src="item"></image>
					<text class="iconfont icon-more more"></text>
				</view>
			</view>
			<view class="list" @click="onNickname">
				<view class="title">
					<text>昵称</text>
				</view>
				<view class="more-content">
					<text class="content">{{user.nickName}}</text>
					<text class="iconfont icon-more more"></text>
				</view>
			</view>
			<view class="list">
				<view class="title">
					<text>性别</text>
				</view>
				<view class="more-content">
					<text class="content">{{sexText}}</text>
					<text class="iconfont icon-more more"></text>
				</view>
				<view class="picker">
					<picker @change="sexPickerChange" :value="sexIndex" :range="sexArray">
						<view class="uni-input" style="height: 100rpx;">{{sexText}}</view>
					</picker>
				</view>
			</view>
			<view class="list">
				<view class="title">
					<text>出生日期</text>
				</view>
				<view class="more-content">
					<text class="content" v-if="user.birthday!=null">{{birthday}}</text>
					<text class="content" v-else>{{birthday}}</text>
					<text class="iconfont icon-more more"></text>
				</view>
				<view class="picker">
					<picker @change="birthdayPickerChange" mode="date" :value="birthdayDate" :start="startDate" :end="endDate">
						<view class="uni-input" style="height: 100rpx;">{{birthdayDate}}</view>
					</picker>
				</view>
			</view>
		</view>
		<!-- 提示框 -->
		<DialogBox ref="DialogBox"></DialogBox>
	</view>
</template>

<script>
	import shopUserApi from '@/api/shop-user.js'
	export default {
		data() {
			const currentDate = this.getDate({
					format: true
			})
			return {
				// 性别
				sexArray: ['男','女','保密'],
				sexIndex: 0,
				sexText: '保密',
				// 生日
				birthdayDate: currentDate,
				startDate: this.getDate('start'),
				endDate: this.getDate('end'),
				birthday: '2020-02-02',
				DialogBox: {},
				// 昵称
				nickname: '爱跳舞的汤姆猫',
				user:{},
				userDto:{
					sex: null,
					birthday: '',
					nickName: '',
					header: ''
				},
				image:''
			};
		},
		onShow() {
			this.user = uni.getStorageSync('loginUser')
			this.image = this.user.header
			this.birthday = this.user.birthday
			if(this.user.sex===1){
				this.sexIndex = 0
				this.sexText = '男'
			}
			if(this.user.sex===2){
				this.sexIndex = 1
				this.sexText = '女'
			}
			if(this.user.sex===3){
				this.sexIndex = 2
				this.sexText = '保密'
			}
		},
		methods:{
			// 上传图片
			uploadPictures() {
				uni.chooseImage({
					count:1,
					success:res => {
						console.log(res)
						// 将选择的本地图片赋值给data中定义的images变量
						this.userDto.header = res.tempFilePaths[0];
						this.image = this.userDto.header
						this.update(this.userDto)
						console.log(this.images)
					}
				})
			},
			update(userDto){
				console.log(userDto)
				shopUserApi.update(userDto).then(res =>{
					uni.setStorageSync('loginUser', res.data)
				})
			},
			/**
			 * 性别
			 * @param {Object} e
			 */
			sexPickerChange(e){
				this.sexIndex = e.detail.value;
				this.sexText = this.sexArray[this.sexIndex];
				if(this.sexText==='男'){	
					this.userDto.sex=1
				}
				if(this.sexText==='女'){
					this.userDto.sex=2
				}
				if(this.sexText==='保密'){
					this.userDto.sex=3
				}
				this.update(this.userDto)
			},
			/**
			 * 生日
			 * @param {Object} e
			 */
			birthdayPickerChange(e){
				this.birthday = e.detail.value;
				this.userDto.birthday=this.birthday
				this.update(this.userDto)
			},
			/**
			 * 获取日期
			 * @param {Object} type
			 */
			getDate(type) {
				const date = new Date();
				let year = date.getFullYear();
				let month = date.getMonth() + 1;
				let day = date.getDate();

				if (type === 'start') {
						year = year - 60;
				} else if (type === 'end') {
						year = year + 2;
				}
				month = month > 9 ? month : '0' + month;;
				day = day > 9 ? day : '0' + day;
				return `${year}-${month}-${day}`;
			},
			/**
			 * 昵称点击
			 */
			onNickname(){
				this.$refs['DialogBox'].confirm({
					title: '更改昵称',
					placeholder: '请输入修改的昵称',
					value: this.user.nickName,
					DialogType: 'input',
					animation: 0
				}).then((res)=>{
					this.nickname = res.value;
					this.userDto.nickName=this.nickname
					this.user.nickName=this.nickname
					this.update(this.userDto)
					console.log(this.userDto)
				})
			}
		}
	}
</script>

<style scoped lang="scss">
	@import 'Information.scss';
</style>
