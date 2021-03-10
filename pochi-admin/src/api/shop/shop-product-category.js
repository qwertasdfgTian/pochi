import request from '@/utils/request'
const groupName = 'shopProductCategory'
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
   * 树形查询
   */
  getTree() {
    return request({
      url: `/${groupName}/getTree`,
      method: 'get'
    })
  },
  /**
   * 获取树形下拉框
   */
  getSelectTree() {
    return request({
      url: `/${groupName}/getSelectTree`,
      method: 'get'
    })
  },
  /**
   * 获取所有二级分类
   */
  getAllSecond() {
    return request({
      url: `/${groupName}/getAllSecond`,
      method: 'get'
    })
  }
}
