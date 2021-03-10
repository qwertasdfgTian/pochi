import request from '@/utils/request'
const groupName = 'sysMenu'
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
   * 根据角色查询
   */
  getRoleSelectMenu(id) {
    return request({
      url: `/${groupName}/getRoleSelectMenu/${id}`,
      method: 'get'
    })
  },
  /**
   * 树形查询菜单
   */
  getTreeList() {
    return request({
      url: `/${groupName}/getTreeList`,
      methods: 'get'
    })
  },
  /**
   * 查询用户的路由
   */
  getRouters() {
    return request({
      url: `/${groupName}/getRouters`,
      method: 'get'
    })
  }
}
