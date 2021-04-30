<template>
  <div class="about-dialog-container">
    <el-dialog title="关于游戏王卡片制作器 3.3.0" :modelValue="modelValue" width="80%" :before-close="closeDialog">
      <div class="about-main">
        <p>本软件遵从 MIT 开源协议</p>
        <p>开源地址:</p>
        <p>
          &nbsp;&nbsp;&nbsp;&nbsp;制卡器 JS 版: &nbsp;<el-button type="text" @click="toGithubRarnu">https://github.com/rarnu/tools-vue</el-button>
        </p>
        <p>

          &nbsp;&nbsp;&nbsp;&nbsp;Electron 桌面端: &nbsp;<el-button type="text" @click="toGithubRarnu">github.com/rarnu/electron-diy</el-button>
        </p>
        <p><b>依赖的开源项目</b></p>
        <p>&nbsp;&nbsp;
          <el-button type="text" @click="toToolsvue">tools-vue</el-button>
          一个使用vue开发的网页版工具包
        </p>
        <p>&nbsp;&nbsp;
          <el-button type="text" @click="toElectron">electron</el-button>
          使用 JavaScript，HTML 和 CSS 构建跨平台的桌面应用程序
        </p>
        <p><b>依赖的闭源后端</b></p>
        <p>&nbsp;&nbsp;
          <el-button type="text">tools.kooriookami.top/api</el-button>
          提供各语言的卡片效果数据
        </p>
        <p>&nbsp;&nbsp;
          <el-button type="text">developer.baidu.com</el-button>
          提供卡片效果的翻译 API
        </p>
      </div>
      <template #footer>
        <el-button plain size="medium" @click="closeDialog">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'AboutDialog',
  props: ['modelValue'],
  methods: {
    closeDialog() {
      this.$emit('update:modelValue', false);
    },
    toToolsvue() {
      open('https://github.com/kooriookami/tools-vue');
    },
    toElectron() {
      open('https://www.electronjs.org/');
    },
    toGithubRarnu() {
      open('hhttps://github.com/rarnu/electron-diy');
    }
  }
};
</script>

<style lang="scss" scoped>
.about-dialog-container {
  .about-main {
    margin: -10px 0;

    p {
      line-height: 1.7;
      margin: 10px 0;

      .el-button {
        padding: 0;
        min-height: auto;
      }
    }
  }
}
</style>
