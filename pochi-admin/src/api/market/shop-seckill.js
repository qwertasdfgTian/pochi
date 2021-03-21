import request from '@/utils/request'
const groupName = 'shopSecKill'
export default {
  /**
   * 添加
   */
  save(shopSecKill) {
    return request({
      url: `/${groupName}/save`,
      method: 'post',
      data: shopSecKill
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
   * 结束活动
   */
  down(id) {
    return request({
      url: `/${groupName}/down/${id}`,
      method: 'put'
    })
  }
}
