import request from '@/utils/request'
const groupName = 'orderComment'
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
	searchCommentNum(id) {
		return request({
			url: `/${groupName}/searchCommentNum/${id}`,
			method: 'get'
		})
	}
}
