import request from '@/utils/request'
const groupName = 'orderPay'
export default {
  /**
   * 根据创建人查询本月累计消费
   */
  getConsumption() {
    return request({
      url: `/${groupName}/getConsumption`,
      method: 'get'
    })
  }
}
