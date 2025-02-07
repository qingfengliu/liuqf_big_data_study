/*
* 专门用于存储日程信息的
* */

import {defineStore} from "pinia";

export const defindSchedule=defineStore(
  "scheduleList",
  {
    state: () => ({
      itemList:[
      ]
    }),
    getters: {
    },
    actions: {
    }
  }
)
