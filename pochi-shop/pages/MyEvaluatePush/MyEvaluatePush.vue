<template>
	<view class="page">
		<div class="comment-list" v-for="(item, index) in commentList" :key="index">
			<view class="evaluate-goods">
				<view class="list">
					<view class="picture">
						<image :src="item.productPic"></image>
					</view>
					<view class="item">
						<view class="title">
							<text>商品评价</text>
						</view>
						<view class="star-list">
							<view class="star">
								<text @tap="changeStar(item, 1)" class="cuIcon-favorfill lg text-gray" :class="item.star >= 1?'ac':''"></text>
								<text @tap="changeStar(item, 2)" class="cuIcon-favorfill lg text-gray" :class="item.star >= 2?'ac':''"></text>
								<text @tap="changeStar(item, 3)" class="cuIcon-favorfill lg text-gray" :class="item.star >= 3?'ac':''"></text>
								<text @tap="changeStar(item, 4)" class="cuIcon-favorfill lg text-gray" :class="item.star >= 4?'ac':''"></text>
								<text @tap="changeStar(item, 5)" class="cuIcon-favorfill lg text-gray" :class="item.star >= 5?'ac':''"></text>
							</view>
							<view class="hint">
								<text>满意</text>
							</view>
						</view>
					</view>
				</view>
			</view>
			<!--填写-->
			<view class="input-info">
				<view class="input-title">
					<text>分享你的使用体验吧</text>
				</view>
				<view class="input-text">
					<textarea v-model="item.content" placeholder="感觉怎么？跟大家分享一下吧~"></textarea>
					<view class="record-text">
						<text>已写</text>
						<text class="ac">12</text>
						<text>个字</text>
					</view>
				</view>
				<view class="anonymous">
					<radio class="radio" :checked="isChecked" color="red" style="transform:scale(0.7)"></radio>
					<text>匿名评价</text>
				</view>
			</view>
			<!--上传图片-->
			<view class="upload-img">
				<view class="img-title">
					<text>上传图片</text>
				</view>
				<view class="img-list">
					<view class="list up-img" @click="toUpload(item)">
						<image src="/static/up_img.png"></image>
					</view>
					<view class="list" v-for="(img, index) in item.picList" :key="index">
						<image :src="img"></image>
						<text class="iconfont icon-close1" @tap="closeImg(item, img)"></text>
					</view>
				</view>
			</view>
		</div>
		<!--提交-->
		<view class="submit-btn" @click="submitComment">
			<view class="btn">
				<text>提交</text>
			</view>
		</view>
	</view>
</template>

<script>
	import commentApi from '@/api/shop-order-comment.js'
	export default {
		data() {
			return {
				isChecked: false,
				// 评论列表
				commentList: []
			};
		},
		onLoad(param) {
			const itemList = JSON.parse(param.itemList)
			const commentList = itemList.map(e => {
				return {
					orderItemId: e.id,
					orderId: e.orderId,
					star: 5,
					productPic: e.productPic,
					picList: []
				}
			})
			this.commentList = commentList
		},
		methods: {
			// 点击评分
			changeStar(item, star) {
				console.log(item, star)
				item.star = star
			},
			// 删除图片
			closeImg(item, url) {
				item.picList.splice(
					item.picList.findIndex(e => e === url), 1
				)
			},
			// 提交评价
			submitComment() {
				commentApi.save(this.commentList).then(res=>{
					uni.showToast({
						title:res.msg
					})
					setTimeout(()=>{
						uni.navigateBack()
					},300)
				})
			},
			// 上传图片
			toUpload(item) {
				uni.chooseImage({
					count: 3,
					success: (res) => {
						const fileUrls = res.tempFilePaths
						if (fileUrls.length + item.picList.length > 3) {
							uni.showToast({
								icon: 'none',
								title:"只能选择三张图片发表"
							})
						} else {
							fileUrls.forEach(fileUrl => {
								uni.uploadFile({
									url: 'http://localhost:8080/upload/uploadFile',
									filePath: fileUrl,
									name: 'mf',
									header: {
										Authorization: uni.getStorageSync('Authorization')
									},
									formData: {
										dir: 'comment'
									},
									success: (res) => {
										const url = JSON.parse(res.data).data
										item.picList.push(url)
									},
									fail: () => {
										console.log('上传失败')
									}
								})
							})
						}
					}
				})
			}
		}
	}
</script>

<style scoped lang="scss">
	@import "MyEvaluatePush.scss";
</style>
