import request from '@/utils/request'
const groupName = 'history'
export default {
  /**
   * 添加
   */
  save(history) {
    return request({
      url: `/${groupName}/save`,
      method: 'post',
      data: history
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
   * 根据id删除
   */
  deleteHistoryById(id) {
	return request({
	  url: `/${groupName}/deleteHistoryById/${id}`,
	  method: 'delete'
	})  
  },
  /**
   * 清空历史
   */
  clearHistory() {
    return request({
      url: `/${groupName}/clearHistory`,
      method: 'delete'
    })
  },
  /**
   * 根据创建人查询浏览记录总条数
   */
  getSumHistory() {
    return request({
      url: `/${groupName}/getSumHistory`,
      method: 'get'
    })
  }
}
