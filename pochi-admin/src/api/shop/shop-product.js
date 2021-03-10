import request from '@/utils/request'
const groupName = 'product'
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
   * 分页查询没有套装的商品
   * @param {分页查询} page
   */
  getByPageHasNotPack(page) { // 分页查询
    return request({
      url: `/${groupName}/getByPageHasNotPack`,
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
   * 查询回显数据
   * @param {*} id
   */
  getUpdate(id) {
    return request({
      url: `/${groupName}/getUpdate/${id}`,
      method: 'get'
    })
  },
  /**
   * 查询商品清单
   * @param {*} id
   */
  getProductDetailList(id) {
    return request({
      url: `/${groupName}/getProductDetailList/${id}`,
      method: 'get'
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
   * 上架
   */
  publish(id) {
    return request({
      url: `/${groupName}/publish/${id}`,
      method: 'put'
    })
  },
  /**
   * 下架
   */
  unPublish(id) {
    return request({
      url: `/${groupName}/unPublish/${id}`,
      method: 'put'
    })
  },
  /**
   * 新品
   */
  news(id) {
    return request({
      url: `/${groupName}/news/${id}`,
      method: 'put'
    })
  },
  /**
   * 非新品
   */
  old(id) {
    return request({
      url: `/${groupName}/old/${id}`,
      method: 'put'
    })
  },
  /**
   * 推荐
   */
  recommend(id) {
    return request({
      url: `/${groupName}/recommend/${id}`,
      method: 'put'
    })
  },
  /**
   * 不推荐
   */
  unRecommend(id) {
    return request({
      url: `/${groupName}/unRecommend/${id}`,
      method: 'put'
    })
  }
}
