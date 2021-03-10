import request from '@/utils/request'
const groupName = 'shopCoupon'
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
   * 根据id查询使用记录
   * @param {*} id
   */
  getHistoryList(id) {
    return request({
      url: `/${groupName}/getHistoryList/${id}`,
      method: 'get'
    })
  },
  /**
   * 下架
   */
  down(id) {
    return request({
      url: `/${groupName}/down/${id}`,
      method: 'put'
    })
  }
}
