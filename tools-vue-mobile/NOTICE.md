# html2canvas
如果使用包管理器添加依赖，请在源码 6200 行左右，自行修改原始代码
```js
CanvasRenderer.prototype.renderTextWithLetterSpacing = function (text, letterSpacing) {
    var _this = this;
    if (letterSpacing === 0) {
        this.ctx.fillText(text.text, text.bounds.left, text.bounds.top + text.bounds.height);
    }
    // ...
};
```
修改为
```js
CanvasRenderer.prototype.renderTextWithLetterSpacing = function (text, letterSpacing) {
    var _this = this;
    if (navigator.userAgent.indexOf('Firefox') === -1){
        // 非火狐浏览器加这个
        this.ctx.textBaseline = 'ideographic';
    }
    if (letterSpacing === 0) {
        this.ctx.fillText(text.text, text.bounds.left, text.bounds.top + text.bounds.height);
    }
    // ...
};
```