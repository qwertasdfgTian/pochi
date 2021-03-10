import request from '@/utils/request'
const groupName = 'orderPay'
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
   * 根据商品订单号查询
   * @param {*} id
   */
  getByOrderId(id) {
    return request({
      url: `/${groupName}/getByOrderId/${id}`,
      method: 'get'
    })
  },
  /**
   * 删除
   */
  deleteById(id) {
    return request({
      url: `/${groupName}/delete/${id}`,
      method: 'delete'
    })
  }
}
