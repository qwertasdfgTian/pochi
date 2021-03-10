import request from '@/utils/request'
const groupName = 'order'
export default {
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
   * 查询回显数据
   * @param {*} id
   */
  get(id) {
    return request({
      url: `/${groupName}/get/${id}`,
      method: 'get'
    })
  },
  /**
   * 查询历史数据
   * @param {*} id
   */
  getHistory(id) {
    return request({
      url: `/${groupName}/getHistory/${id}`,
      method: 'get'
    })
  },
  /**
   * 改变订单状态
   */
  changeOrderStatus(order) {
    return request({
      url: `/${groupName}/changeOrderStatus`,
      method: 'put',
      data: order
    })
  },
  orderPoint() {
    return request({
      url: `/${groupName}/orderPoint`,
      method: 'get'
    })
  },
  monthOrder() {
    return request({
      url: `/${groupName}/monthOrder`,
      method: 'get'
    })
  }
}
