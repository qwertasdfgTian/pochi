<template>
  <view class="page">
    <!--评价筛选-->
    <view class="evaluate-filtrate">
      <view class="filtrate-list">
        <view class="list" @click="changeType(999)">
          <text :class="flag===999? 'action':''" v-if="sumNum<=999">全部{{sumNum}}</text>
		  <text :class="flag===999? 'action':''" v-else>全部999+</text>
        </view>
        <view class="list" @click="changeType(4)">
          <text :class="flag===4? 'action':''" v-if="goodNum<=999">好评{{goodNum}}</text>
		  <text :class="flag===4? 'action':''" v-else>好评999+</text>
        </view>
        <view class="list" @click="changeType(3)">
			<text :class="flag===3? 'action':''" v-if="middleNum<=999">中评{{middleNum}}</text>
			<text :class="flag===3? 'action':''" v-else>中评999+</text>
        </view>
        <view class="list" @click="changeType(2)">
			<text :class="flag===2? 'action':''" v-if="failNum<=999">差评{{failNum}}</text>
			<text :class="flag===2? 'action':''" v-else>差评999+</text>
        </view>
      </view>
    </view>
    <view class="evaluate-data">
		<view class="top" v-if="commentDataPage.totalCount===0" style="text-align: center;line-height: 50px;">
			暂时没有相关评价
		</view>
      <view class="evaluate-list">
        <view class="list" v-for="(comment, ci) in dataList" :key="ci">
          <view class="user-info">
            <view class="thumb">
              <image :src="comment.memberIcon" mode=""></image>
            </view>
            <view class="nickname-grade">
              <view class="nickname">
                <text>{{comment.nickName}}</text>
              </view>
              <view class="grade">
                <view class="star">
				  <text v-for="star in comment.star" :key="star" class="cuIcon-favorfill lg text-gray"></text>
                </view>
                <view class="date">
                  <text>{{comment.createTime}}</text>
                </view>
              </view>
            </view>
          </view>
          <view class="content">
            <view class="character">
              <text class="two-omit">{{comment.content}}</text>
            </view>
            <view class="attr">
              <text>{{comment.productName}}</text>
            </view>
            <view class="thumb-list">
              <!-- <view class="video-info" @click="isVideoShow = true">
                <image src="/static/img/goods_thumb_09.png" class="picture"></image>
                <view class="shade">
                  <text class="iconfont icon-bofang"></text>
                </view>
              </view> -->
              <view class="list" v-for="(pic, pi) in comment.picList"
                    @click="onPreview(pic,pi)"
                    :key="pi">
                <image :src="pic"></image>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>
    <!--视频播放-->
    <view class="video-win" @click="onCloseVideo">
      <view class="cu-modal" :class="{'show':isVideoShow}">
        <view class="cu-dialog" @click.stop>
          <view class="bg-img" v-show="isVideoShow">
            <video id="evaluate-video" src="http://wxsnsdy.tc.qq.com/105/20210/snsdyvideodownload?filekey=30280201010421301f0201690402534804102ca905ce620b1241b726bc41dcff44e00204012882540400&bizid=1023&hy=SH&fileparam=302c020101042530230204136ffd93020457e3c4ff02024ef202031e8d7f02030f42400204045a320a0201000400"></video>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
	import commentApi from '@/api/shop-order-comment.js'
export default {
  data() {
    return {
		// 判断点击的评论分类
	  flag: 999,
      isVideoShow: false,
      urls: [
        '/static/img/yf_02.jpg',
        '/static/img/wen_01.jpg',
        '/static/img/class_01.jpg',
        '/static/img/yf_01.png'
      ],
      videoContext: '',
	  commentDataPage: {},
	  // 当前商品的id
	  productId: '',
	  // 评价分页查询对象
	  commentPage: {
	  	// 当前页
	  	pageNumber: 1,
	  	// 每页条数
	  	pageSize: 10,
	  	// 查询参数
	  	params: {
	  	}
	  },
	  sumNum: 0,
	  goodNum: 0,
	  middleNum: 0,
	  failNum: 0,
	  loadFlag: true,
	  dataList: []
    };
  },
  onReady: function (res) {
    this.videoContext = uni.createVideoContext('evaluate-video')
  },
  onLoad(params){
	this.productId = params
	this.searchComment(999)
	this.searchCommentNum()
  },
  onShow() {
  },
  methods:{
	  onReachBottom() {
	  	if (this.loadFlag) {
	  		// 上拉加载
	  		this.commentPage.pageNumber++
	  		this.searchComment(this.flag)
	  	}
	  },
	  changeType(start) {
		 if(start === 999){
		 	this.flag = 999
		 }
		 if(start === 4){
		 	this.flag = 4
		 }
		 if(start === 3){
		 	this.flag = 3
		 }
		 if(start === 2){
		 	this.flag = 2
		 } 
		 this.dataList=[]
		 this.searchComment(start)
	  },
	  // 查询商品评价
	  searchComment(start) {
		this.commentPage.params.productId = this.productId.productId
		this.commentPage.params.star = start
		commentApi.getByPage(this.commentPage).then(res=>{
			this.dataList.push(...res.data.list)
			this.commentDataPage = res.data
			this.sumNum = this.commentDataPage.totalCount
			if (res.data.totalCount === this.dataList.length) {
				this.loadFlag = false
			}
		}) 
	  },
	  // 查询各种评价的数量
	  searchCommentNum() {
		 const productId = this.productId.productId
		 commentApi.searchCommentNum(productId).then(res=>{
			this.goodNum = res.data.goodNum
			this.middleNum = res.data.middleNum
			this.failNum = res.data.failNum
		 })  
	  },
    /**
     * 图片点击预览
     * @param item
     * @param index
     */
    onPreview(item,index){
      uni.previewImage({
        urls: this.urls,
        indicator: 'number',
        current: index,
        longPressActions: {
          itemList: ['发送给朋友', '保存图片', '收藏'],
          success: function(data) {
            console.log('选中了第' + (data.tapIndex + 1) + '个按钮,第' + (data.index + 1) + '张图片');
          },
          fail: function(err) {
            console.log(err.errMsg);
          }
        }
      });
    },
    /**
     * 关闭视频点击
     */
    onCloseVideo(){
      this.videoContext.pause();
      this.isVideoShow = false;
    }
  }
}
</script>

<style scoped lang="scss">
@import "GoodsEvaluateList.scss";
</style>
