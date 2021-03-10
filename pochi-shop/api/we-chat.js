import request from '@/utils/request'
const groupName = 'wx'
export default {
	/**
	 * 根据code登录
	 */
	loginByCode(code) {
		return request({
			url: `/${groupName}/wxLogin/${code}`,
			method: 'get'
		})
	},
	/**
	 * 注册登录
	 * @param {Object} user
	 */
	registerLogin(user) {
		return request({
			url: `/${groupName}/registerLogin`,
			method: 'post',
			data: user
		})
	},
	/**
	 * 绑定手机号
	 */
	bindUser(user) {
		return request({
			url: `/${groupName}/bindUser`,
			method: 'post',
			data: user
		})
	},
	// 获取登录用户
	getLoginInfo() {
		return request({
			url: `/${groupName}/info`,
			method: 'get'
		})
	},
	/**
	 * 手机号登录
	 * @param {Object} shopUserLoginDto
	 */
	wxLoginByPhone(shopUserLoginDto) {
		return request({
			url: `/${groupName}/wxLoginByPhone`,
			method: 'post',
			data: shopUserLoginDto
		})
	},
}
