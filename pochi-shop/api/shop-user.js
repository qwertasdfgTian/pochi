import request from '@/utils/request'
const groupName = 'user'
export default {
  /**
   * 根据创建人查询我的钱包
   */
  getMyWallet() {
    return request({
      url: `/${groupName}/getMyWallet`,
      method: 'get'
    })
  },
  /**
   * 根据创建人修改信息
   */
  update(userDto) {
    return request({
      url: `/${groupName}/update`,
      method: 'post',
	  data: userDto
    })
  }
}
