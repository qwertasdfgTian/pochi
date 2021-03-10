import request from '@/utils/request'
const groupName = 'brand'
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
   * 分页
   */
  getByPage(page) {
    return request({
      url: `/${groupName}/getByPage`,
      method: 'post',
      data: page
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
  },
  /**
   * 根据id查询
   */
  get(id) {
    return request({
      url: `/${groupName}/get/${id}`,
      method: 'get'
    })
  },
  /**
   * 根据分类id查询
   */
  getByCategoryId(id) {
    return request({
      url: `/${groupName}/getByCategoryId/${id}`,
      method: 'get'
    })
  },
  /**
   * 根据id查询回显
   */
  getUpdate(id) {
    return request({
      url: `/${groupName}/getUpdate/${id}`,
      method: 'get'
    })
  },
  /**
   * 根据名称查询
   */
  getByName(name) {
    return request({
      url: `/${groupName}/getByName/${name}`,
      method: 'get'
    })
  }
}
