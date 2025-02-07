/*
* 专门用于存储用户状态信息的
* */
import {defineStore} from "pinia";

export const defineUser=defineStore(
  "loginUser",
  {
    state: () => (
      // 用户信息
      {
        uid: 0,
        username: ""
      }
    ),
    getters: {  // 用于获取state中的数据
      getUid(){
        return this.uid
      },
      getUsername(){
        return this.username
      }
    },
    actions: {
      setuid(uid){
        this.uid=uid
      },
      setusername(username){
        this.username=username
      }
    }
  }
)
