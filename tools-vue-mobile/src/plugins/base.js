import vm from '@/main';
import mime from 'mime';

export default {
    install(app, options) {
        app.config.globalProperties.config = require('@/assets/json/config.json');
        //重置表单
        app.config.globalProperties.resetForm = function (formName) {
            if (this.$refs[formName]) {
                this.$refs[formName].resetFields();
            }
        };
        app.config.globalProperties.timeFilter = function (value) {
            if (value) {
                return vm.dayjs(value).format('YYYY-MM-DD HH:mm:ss');
            }
        };

        app.config.globalProperties.dataURLtoBlob = function (dataURL) {
            let arr = dataURL.split(',');
            let mime = arr[0].match(/:(.*?);/)[1];
            let bstr = atob(arr[1]);
            let n = bstr.length;
            let u8arr = new Uint8Array(n);
            while (n--) {
                u8arr[n] = bstr.charCodeAt(n);
            }
            return new Blob([u8arr], {type: mime});
        };

        app.config.globalProperties.downloadBlob = function (blob, fileName) {
            if (!fileName) {
                fileName = new Date().getTime();
            }
            let a = document.createElement('a');
            let extension = mime.getExtension(blob.type);
            a.download = `${fileName}.${extension}`;
            a.href = URL.createObjectURL(blob);
            a.click();
        };
    }
};
