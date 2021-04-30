<template>
  <div class="kanji-kana-dialog">
    <el-dialog title="一键注音" :modelValue="modelValue" width="90%" :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false">
      <el-form ref="form" :model="form" label-position="top" size="small">
        <el-form-item label="注音只支持 OCG 常用语" prop="text">
          <el-input type="textarea" :autosize="{minRows: 3}" v-model="form.text" placeholder="请输入文本"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button plain size="medium" @click="closeDialog">关闭</el-button>
        <el-button type="primary" size="medium" @click="addKana">注音</el-button>
        <el-button type="primary" size="medium" @click="remoteAddKana">远程注音</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'KanjiKanaDialog',
  props: ['modelValue'],
  data() {
    return {
      form: {
        text: ''
      }
    };
  },
  created() {
  },
  methods: {
    closeDialog() {
      this.resetForm('form');
      this.$emit('update:modelValue', false);
    },
    addKana() {
      this.form.text = this.kanjiToKana(this.form.text);
    },
    remoteAddKana() {
      try {
        this.form.text = diy.remoteKana(this.form.text);
      } catch (e) {

      }
    }
  }
};
</script>

<style lang="scss" scoped>
.kanji-kana-dialog {

}
</style>
