import request from '@/utils/request'
const groupName = 'cartItem'
export default {
  /**
   * 添加
   */
  save(cartItem) {
    return request({
      url: `/${groupName}/save`,
      method: 'post',
      data: cartItem
    })
  },
  /**
   * 查询购物车数量
   */
  getProductCount() {
    return request({
      url: `/${groupName}/getProductCount`,
      method: 'get'
    })
  },
  /**
   * 分页查询
   */
  getByPage(page) { // 分页查询
  	return request({
  		url: `/${groupName}/getByPage`,
  		method: 'post',
  		data: page
  	})
  },
  /**
   * 批量删除
   * @param {Object} ids
   */
  deleteByIds(ids) {
	  return request({
	  	url: `/${groupName}/deleteByIds`,
	  	method: 'put',
	  	data: ids
	  })
  },
  /**
   * @param {Object} ids移入收藏
   */
  moveToCollection(ids) {
	  return request({
	  	url: `/${groupName}/moveToCollection`,
	  	method: 'post',
	  	data: ids
	  })
  }
}
