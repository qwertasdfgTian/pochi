import request from '@/utils/request'
const groupName = 'order'
export default {
	/**
	 * 创建订单
	 */
	createOrder(order) {
		return request({
			url: `/${groupName}/createOrder`,
			method: 'post',
			data: order
		})
	},
	/**
	 * 根据ID查询
	 * @param {Object} order
	 */
	get(id) {
		return request({
			url: `/${groupName}/get/${id}`,
			method: 'get'
		})
	},
	/**
	 * 确认收货
	 * @param {Object} order
	 */
	receiveById(id) {
		return request({
			url: `/${groupName}/receiveById/${id}`,
			method: 'put'
		})
	},
	/**
	 * 查询我的订单
	 */
	getMyOrder(order) {
		return request({
			url: `/${groupName}/getMyOrder`,
			method: 'post',
			data: order
		})
	},
	/**
	 * 创建支付订单
	 */
	createPayOrder(order) {
		return request({
			url: `/${groupName}/createPayOrder`,
			method: 'post',
			data: order
		})
	},
	// 查询订单的支付状态
	queryOrderPayOrderId(orderId) {
		return request({
			url: `/${groupName}/queryOrderPayOrderId/${orderId}`,
			method: 'get'
		})
	},
	/**
	 * 根据创建人查询各种订单的数量
	 */
	getOrderTypeNum() {
	  return request({
	    url: `/${groupName}/getOrderTypeNum`,
	    method: 'get'
	  })
	},
	deleteOrderById(orderId) {
		return request({
			url: `/${groupName}/deleteOrderById/${orderId}`,
			method: 'delete'
		})
	},
	cancelOrderById(orderId) {
		return request({
			url: `/${groupName}/cancelOrderById/${orderId}`,
			method: 'delete'
		})
	},
	orderRemainingTime(createTime) {
		return request({
			url: `/${groupName}/orderRemainingTime/${createTime}`,
			method: 'get'
		})
	}
}
