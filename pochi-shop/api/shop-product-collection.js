import request from '@/utils/request'
const groupName = 'collection'
export default {
	/**
	 * 切换收藏状态
	 */
	changeCollection(collection) {
		return request({
			url: `/${groupName}/changeCollection`,
			method: 'post',
			data: collection
		})
	},
	/**
	 * 分页查询
	 * @param {分页查询} page
	 */
	getByPage(page) { // 分页查询
		return request({
			url: `/${groupName}/getByPage`,
			method: 'post',
			data: page
		})
	},
	/**
	 * 根据商品ID查询
	 * @param {*} id
	 */
	getByProductId(id) {
		return request({
			url: `/${groupName}/getByProductId/${id}`,
			method: 'get'
		})
	},
	/**
	 * 根据创建人查询收藏商品总数
	 */
	getSumCollection() {
	  return request({
	    url: `/${groupName}/getSumCollection`,
	    method: 'get'
	  })
	}
}
