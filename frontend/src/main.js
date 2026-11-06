import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import mitt from 'mitt'
import App from './App.vue'
import router from './router'
import './plugins/iconfont.css'
import '@/assets/css/story-style.css'

export const eventBus = mitt()

const app = createApp(App)
for (const [name, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(name, component)
}
app.use(router)
app.use(ElementPlus, { locale: zhCn })
app.mount('#app')
