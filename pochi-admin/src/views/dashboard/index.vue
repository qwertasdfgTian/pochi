<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="16" :offset="0">
        <el-card shadow="never">
          <div slot="header">
            <span>月订单统计</span>
          </div>
          <div id="monthOrder" />
        </el-card>
      </el-col>
      <el-col :span="8" :offset="0">
        <el-card shadow="never">
          <div slot="header">
            <span>订单状态统计</span>
          </div>
          <div id="pointOrder" />
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="24" :offset="0">
        <el-card shadow="never">
          <div slot="header">
            <span>用户统计</span>
          </div>
          <div id="user" />
        </el-card>

      </el-col>
    </el-row>

  </div>
</template>

<script>
import { Chart } from '@antv/g2'
import orderApi from '@/api/shop/shop-order'
import userApi from '@/api/system/shop-user'

export default {
  name: 'Dashboard',
  data() {
    return {
      // 月订单表格
      monthOrderChart: null,
      // 订单状态表格
      statusOrderChart: null
    }
  },
  mounted() {
    this.initMonthOrderCharts()
    this.getMonthOrder()
    this.initStatusOrderCharts()
    this.getStatusOrder()
    userApi.getTopStatistic().then(res => {
      const chart = new Chart({
        container: 'user',
        autoFit: true,
        height: 500
      })

      chart.data(res.data)
      chart.scale('count', {
        nice: true
      })
      chart.tooltip({
        showMarkers: false,
        shared: true
      })

      chart
        .interval()
        .position('userName*count')
        .color('name')
        .adjust([
          {
            type: 'dodge',
            marginRatio: 0
          }
        ])

      chart.interaction('active-region')

      chart.render()
    })
    // const data = [
    //   { name: 'London', 月份: 'Jan.', 月均降雨量: 18.9 },
    //   { name: 'London', 月份: 'Feb.', 月均降雨量: 28.8 },
    //   { name: 'London', 月份: 'Mar.', 月均降雨量: 39.3 },
    //   { name: 'London', 月份: 'Apr.', 月均降雨量: 81.4 },
    //   { name: 'London', 月份: 'May', 月均降雨量: 47 },
    //   { name: 'London', 月份: 'Jun.', 月均降雨量: 20.3 },
    //   { name: 'London', 月份: 'Jul.', 月均降雨量: 24 },
    //   { name: 'London', 月份: 'Aug.', 月均降雨量: 35.6 },
    //   { name: 'Berlin', 月份: 'Jan.', 月均降雨量: 12.4 },
    //   { name: 'Berlin', 月份: 'Feb.', 月均降雨量: 23.2 },
    //   { name: 'Berlin', 月份: 'Mar.', 月均降雨量: 34.5 },
    //   { name: 'Berlin', 月份: 'Apr.', 月均降雨量: 99.7 },
    //   { name: 'Berlin', 月份: 'May', 月均降雨量: 52.6 },
    //   { name: 'Berlin', 月份: 'Jun.', 月均降雨量: 35.5 },
    //   { name: 'Berlin', 月份: 'Jul.', 月均降雨量: 37.4 },
    //   { name: 'Berlin', 月份: 'Aug.', 月均降雨量: 42.4 }
    // ]
  },
  methods: {
    // 填充状态订单数据
    getStatusOrder() {
      orderApi.orderPoint().then(res => {
        const data = res.data
        this.statusOrderChart.data(data)
        this.statusOrderChart.render()
      })
    },
    // 填充月订单数据
    getMonthOrder() {
      orderApi.monthOrder().then(res => {
        const data = res.data
        this.monthOrderChart.data(data)
        // 计算总单量
        let count = 0
        data.forEach(e => {
          count += e.count
        })
        // 辅助文本
        this.monthOrderChart
          .annotation()
          .text({
            position: ['50%', '50%'],
            content: '总订单',
            style: {
              fontSize: 14,
              fill: '#8c8c8c',
              textAlign: 'center'
            },
            offsetY: -20
          })
          .text({
            position: ['50%', '50%'],
            content: count,
            style: {
              fontSize: 20,
              fill: '#8c8c8c',
              textAlign: 'center'
            },
            offsetX: -10,
            offsetY: 20
          })
          .text({
            position: ['50%', '50%'],
            content: '单',
            style: {
              fontSize: 14,
              fill: '#8c8c8c',
              textAlign: 'center'
            },
            offsetY: 20,
            offsetX: 20
          })

        this.monthOrderChart.render()
      })
    },
    // 初始化订单状态表格
    initStatusOrderCharts() {
      const chart = new Chart({
        container: 'pointOrder',
        autoFit: true,
        height: 300
      })
      chart.scale('count', {
        formatter: (val) => {
          return val
        }
      })
      chart.coordinate('theta', {
        radius: 0.75,
        innerRadius: 0.6
      })
      chart.tooltip({
        showTitle: false,
        showMarkers: false,
        itemTpl: '<li class="g2-tooltip-list-item"><span style="background-color:{color};" class="g2-tooltip-marker"></span>{name}: {value}</li>'
      })
      chart
        .interval()
        .adjust('stack')
        .position('count')
        .color('statusMsg')
        .label('count', (count) => {
          return {
            content: (data) => {
              return `${data.statusMsg}: ${count}`
            }
          }
        })
        .tooltip('statusMsg*count', (item, percent) => {
          return {
            name: item,
            value: percent
          }
        })

      chart.interaction('element-active')
      this.statusOrderChart = chart
    },
    // 初始化月订单表格
    initMonthOrderCharts() {
      const chart = new Chart({
        container: 'monthOrder',
        autoFit: true,
        height: 300
      })
      chart.scale({
        year: {
          range: [0, 1]
        },
        value: {
          min: 0,
          nice: true
        }
      })
      chart.tooltip({
        showCrosshairs: true, // 展示 Tooltip 辅助线
        shared: true
      })

      chart.line().position('day*count').label('count')
      chart.point().position('day*count')
      this.monthOrderChart = chart
    }
  }
}
</script>
