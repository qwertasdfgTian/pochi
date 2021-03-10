import request from '@/utils/request'
const groupName = 'shopPack'
export default {
	/**
	 * 添加
	 */
	save(sysUser) {
		return request({
			url: `/${groupName}/save`,
			method: 'post',
			data: sysUser
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
	 * 修改
	 */
	update(sysUser) {
		return request({
			url: `/${groupName}/update`,
			method: 'put',
			data: sysUser
		})
	},
	/**
	 * 删除
	 * @param {*} id
	 */
	delete(id) {
		return request({
			url: `/${groupName}/delete/${id}`,
			method: 'delete'
		})
	},
	/**
	 * 根据id查询
	 * @param {*} id
	 */
	get(id) {
		return request({
			url: `/${groupName}/get/${id}`,
			method: 'get'
		})
	},
	/**
	 * 根据商品ID查询
	 * @param {Object} id
	 */
	getByProductId(id) {
		return request({
			url: `/${groupName}/getByProductId/${id}`,
			method: 'get'
		})
	},
}
