import {createRouter, createWebHashHistory} from 'vue-router';

const routes = [
    {
        path: '/',
        name: 'app',
        component: () => import(/* webpackChunkName: "app-layout" */ '../layout/app-layout/AppLayout.vue'),
        redirect: '/yugioh',
        children: [
            {
                path: 'yugioh',
                name: 'yugioh',
                component: () => import(/* webpackChunkName: "yugioh" */ '../views/yugioh/Yugioh.vue'),
                meta: {
                    title: '游戏王卡片制作器'
                }
            },
            {
                path: 'dataview',
                name: 'dataview',
                component: () => import(/* webpackChunkName: "yugioh" */ '../views/yugioh/DataView.vue'),
                meta: {
                    title: '数据设定'
                }
            }
        ]
    }
];

const router = createRouter({
    history: createWebHashHistory(),
    routes
});

export default router;
