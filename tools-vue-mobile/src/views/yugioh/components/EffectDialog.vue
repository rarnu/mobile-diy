<template>
  <div class="effect-dialog">
    <el-dialog title="效果编辑" :modelValue="modelValue" width="90%" :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false">

      <el-form ref="form" :model="form" label-width="auto" size="small">
        <el-form-item label="中文效果">
          <el-input type="textarea" :autosize="{minRows: 5}" v-model="form.text" placeholder="请输入中文效果"></el-input>
        </el-form-item>
        <el-form-item label="效果翻译">
          <el-button type="primary" @click="translate">翻译 (将中文效果翻译为日文并注音)</el-button>
        </el-form-item>
        <el-form-item label="日文效果">
          <el-input type="textarea" :autosize="{minRows: 5}" v-model="form.textjp" placeholder="请输入日文效果"></el-input>
        </el-form-item>
        <el-form-item label="首行效果">
          <el-row :gutter="10">
            <el-col :span="7">
              <el-select v-model="form.firstLineType" placeholder="请选择首行的类型" style="width: 100%;" clearable @change="firstLineTypeChange">
                <el-option v-for="item in firstLineTypes" :value="item"></el-option>
              </el-select>
            </el-col>
            <el-col :span="10">
              <el-select v-model="form.firstLineValue" placeholder="请选择首行的值" style="width: 100%;" clearable>
                <el-option v-for="item in firstLineValues" :value="item"></el-option>
              </el-select>
            </el-col>
            <el-col :span="3">
              <el-button type="primary" @click="writeFirstLine">写入</el-button>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button plain size="medium" @click="closeDialog">关闭</el-button>
        <el-button type="primary" size="medium" @click="remoteAddKana">远程注音</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
const effectList = require('@/assets/json/effect-text.json');
const cardTypeList = require('@/assets/json/cardtype-text.json');

export default {
  name: "EffectDialog",
  props: ['modelValue'],
  data() {
    return {
      effectKeys: [],
      effectSequences: [],
      firstLineTypes: [],
      firstLineValues: [],
      form: {
        text: '',
        textjp: '',
        effectKey: '',
        effectjp: '',
        firstLineType: '',
        firstLineValue: ''
      }
    };
  },
  created() {
    try {
      ipcRenderer.on('translate-reply', (event, args) => {
        if (!args.texts) {
          this.$message.warning('翻译失败，请稍后重试');
          return;
        }
        let str = '';
        args.texts.forEach((item) => {
          str += this.kanjiToKana(item) + '\n';
        });
        this.form.textjp += str;
      });
    } catch (err) {

    }
  },
  mounted() {
    this.effectKeys = Object.keys(effectList);
    this.firstLineTypes = Object.keys(cardTypeList);
  },
  methods: {
    remoteAddKana() {
      try {
        this.form.textjp = diy.remoteKana(this.form.textjp);
      } catch (e) {

      }
    },
    writeEffect() {
      const text = effectList[this.form.effectKey][this.form.effectjp];
      this.form.textjp += text;
    },
    writeFirstLine() {
      const text = cardTypeList[this.form.firstLineType][this.form.firstLineValue];
      this.form.textjp += text;
    },
    effectKeyChange() {
      this.effectSequences = Object.keys(effectList[this.form.effectKey]);
    },
    firstLineTypeChange() {
      this.firstLineValues = Object.keys(cardTypeList[this.form.firstLineType]);
    },
    closeDialog() {
      this.resetForm('text');
      this.$emit('update:modelValue', false);
    },
    translate() {
      if (this.form.text.trim() === '') return;
      try {
        let t = diy.remoteTranslate(this.form.text);
        let k = this.kanjiToKana(t);
        this.form.textjp += k;
      } catch (e) {
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.effect-dialog {

}
</style>