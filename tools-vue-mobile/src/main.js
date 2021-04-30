import {createApp} from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';
import ElementPlus from 'element-plus';
import 'element-plus/lib/theme-chalk/index.css';
import locale from 'element-plus/lib/locale/lang/zh-cn';
import 'normalize.css';
import '@/core/route-actived';
import axios from './core/axios';
import dayjs from 'dayjs';
import base from '@/plugins/base';
import yugiohPlugin from '@/plugins/yugioh-plugin';
import './assets/font/ygo-font.css';
import './assets/css/all.css';
import Kuroshiro from "kuroshiro";
import KuromojiAnalyzer from "kuroshiro-analyzer-kuromoji";

const app = createApp(App);

app.config.globalProperties.axios = axios;
app.config.globalProperties.dayjs = dayjs;
const kuroshiro = new Kuroshiro();
kuroshiro.init(new KuromojiAnalyzer({dictPath: './dict'}));
app.config.globalProperties.kuroshiro = kuroshiro;
app.config.globalProperties.kUtil = Kuroshiro.Util;

app.use(ElementPlus, {locale});
app.use(base);
app.use(yugiohPlugin);
app.use(router);
app.use(store);
const vm = app.mount('#app');

// 接口请求地址配置

// app.config.globalProperties.baseURL = 'https://tools.kooriookami.top/api';
app.config.globalProperties.baseURL = 'http://rarnu.xyz:9987/api';
app.config.globalProperties.rarnuURL = 'http://rarnu.xyz:9987/kk';
// app.config.globalProperties.rarnuURL = 'http://127.0.0.1:9987/kk'

export default vm;
