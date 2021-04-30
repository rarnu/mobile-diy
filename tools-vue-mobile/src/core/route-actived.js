import router from '../router';
import {ElMessage} from 'element-plus';

// 启用路由拦截
router.beforeEach((to, from, next) => {
    if (!router.resolve(to.path).matched.length) {
        ElMessage.warning('页面不存在');
        next(from.path);
    } else {
        setDocumentTitle(to);
        next();
    }
});

// 设置文档标题
function setDocumentTitle(to) {
    document.title = `${to.meta.title}`;
}
