import {createRouter,createWebHashHistory} from 'vue-router'
import Login from '../components/Login.vue'
import Regist from '../components/Register.vue'
import ShowSchedule from '../components/ShowSchedule.vue'

import pinia from '../pinia.js'
import {defineUser} from '../store/useStore.js'

let sysUser=defineUser(pinia)




const router = createRouter({
    history: createWebHashHistory(),
    routes: [
      {
        path:"/",
        redirect:"/showschedule"
      },
      {
        path:"/showschedule",
        component:ShowSchedule
      },
      {
        path:"/login",
        component:Login
      },
      {
        path:"/regist",
        component:Regist
      }
    ]
})

//拦截ShowSchedule路由，判断是否登录
router.beforeEach((to,from,next)=>{

  if(to.path=="/showschedule"){
    console.log("进入ShowSchedule路由")
    console.log(sysUser.username)
    if(sysUser.username=="" || sysUser.username==null){
      //未登录,回到登录页
      next("/login")
    }else{
      next()
    }
  }else{
    next()
  }
})
export default router
