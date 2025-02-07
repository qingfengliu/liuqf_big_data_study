<script setup>
import {defineUser} from '../store/useStore.js'
import {defindSchedule} from '../store/scheduleStore.js'
import router from "../router/router.js";

let user=defineUser()
let schedule=defindSchedule()

function loginout(){
  user.name=''
  schedule.scheduleList=[]
  router.push('/login')
}

</script>
<template>
  <div>
    <h1 class="ht">欢迎使用日程管理系统</h1>
    <div>
      <div class="optionDiv" v-if="user.name==''">
        <router-link to="/login">
          <button class="b1s">登录</button>
        </router-link>
        <router-link to="/regist">
          <button class="b1s">注册</button>
        </router-link>
      </div>
      <div class="optionDiv" v-else>
        欢迎{{user.name}}登录
        <button class="b1b" @click="loginout()">退出登录</button>
        <router-link to="/showSchedule">
          <button class="b1b">查看我的日程</button>
        </router-link>
      </div>
      <br>
    </div>
  </div>
</template>
<style scoped>
.ht{
  text-align: center;
  color: cadetblue;
  font-family: 幼圆;
}
.b1s{
  border: 2px solid powderblue;
  border-radius: 4px;
  width:60px;
  background-color: antiquewhite;
}
.b1b{
  border: 2px solid powderblue;
  border-radius: 4px;
  width:100px;
  background-color: antiquewhite;
}
.optionDiv{
  width: 400px;
  float: right;
}
</style>
