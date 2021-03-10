import { asyncRoutes, constantRoutes } from '@/router'
import Layout from '@/layout'
import sysMenuApi from '@/api/system/sys-menu'

/**
 * 构造懒加载的路由
 * @param {} view
 */
export const loadView = (view) => {
  return (resolve) => require([`@/views/${view}`], resolve)
}

/**
 * Filter asynchronous routing tables by recursion
 * @param routes asyncRoutes
 * @param roles
 */
export function filterAsyncRoutes(routes) {
  const res = []

  routes.forEach(route => {
    const tmp = { ...route }
    if (tmp.component) {
      // 如果component存在，就处理component
      if (tmp.component === 'Layout') {
        // 字符串的Layout，处理成Layout组件
        tmp.component = Layout
      } else {
        tmp.component = loadView(route.component)
      }
    }
    if (tmp.children && tmp.children[0]) {
      tmp.children = filterAsyncRoutes(tmp.children)
    }
    res.push(tmp)
  })

  return res
}

const state = {
  routes: [],
  addRoutes: []
}

const mutations = {
  SET_ROUTES: (state, routes) => {
    state.addRoutes = routes
    state.routes = constantRoutes.concat(routes)
  }
}

const actions = {
  generateRoutes({ commit }) {
    return new Promise(resolve => {
      sysMenuApi.getRouters().then(res => {
        console.log(asyncRoutes, res.data)
        const accessedRoutes = filterAsyncRoutes(res.data)
        commit('SET_ROUTES', accessedRoutes)
        resolve(accessedRoutes)
      })
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
