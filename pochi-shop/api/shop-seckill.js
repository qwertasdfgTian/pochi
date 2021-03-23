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
  },
  /**
   * 查询所有开始和未开始的秒杀
   */
  getAll() {
    return request({
      url: `/${groupName}/getAll`,
      method: 'get'
    })
  },
  /**
   * 查询是否是秒杀商品
   */
  getSecKill(id) {
    return request({
      url: `/${groupName}/getSecKill/${id}`,
      method: 'get'
    })
  },
  /**
   * 去秒杀
   */
  toSecKill(id) {
    return request({
      url: `/${groupName}/toSecKill/${id}`,
      method: 'get'
    })
  }
}
