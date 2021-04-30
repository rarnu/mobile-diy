<template>
  <div class="race-dialog">
    <el-dialog title="种族编辑" :modelValue="modelValue" width="80%" :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false">

      <el-form ref="form" :model="form" label-width="auto" size="small">
        <el-form-item label="类型">
          <el-input v-model="form.text" readonly></el-input>
        </el-form-item>
        <el-form-item label="种族">
          <el-select v-model="form.race" placeholder="请选择怪兽种族" style="width: 100%" clearable>
            <el-option v-for="item in raceList" :value="item"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="类型1">
          <el-select v-model="form.type1" placeholder="请选择怪兽类型" style="width: 100%" clearable>
            <el-option v-for="item in typeList" :value="item"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="类型2">
          <el-select v-model="form.type2" placeholder="请选择怪兽类型" style="width: 100%" clearable>
            <el-option v-for="item in typeList" :value="item"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="类型3">
          <el-select v-model="form.type3" placeholder="请选择怪兽类型" style="width: 100%" clearable>
            <el-option v-for="item in typeList" :value="item"></el-option>
          </el-select>
        </el-form-item>

      </el-form>

      <template #footer>
        <el-button plain size="medium" @click="closeDialog">关闭</el-button>
        <el-button type="primary" size="medium" @click="callbackRace">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
const monsterTypeList = require('@/assets/json/monster-type-list.json');

export default {
  name: "RaceDialog",
  props: ['modelValue', 'raceText', 'language'],
  mounted() {
    this.form.text = this.raceText;
    this.updateList();
  },
  updated() {
    this.updateList();
    this.updateSelect();
  },
  data() {
    return {
      raceList: [],
      typeList: [],
      form: {
        text: '',
        race: '',
        type1: '',
        type2: '',
        type3: ''
      }
    };
  },
  methods: {
    updateList() {
      let baseList = monsterTypeList[this.language];
      this.raceList = [
        baseList['spellcaster'],
        baseList['dragon'],
        baseList['zombie'],
        baseList['warrior'],
        baseList['beastWarrior'],
        baseList['beast'],
        baseList['wingedBeast'],
        baseList['fiend'],
        baseList['fairy'],
        baseList['insect'],
        baseList['dinosaur'],
        baseList['reptile'],
        baseList['fish'],
        baseList['seaSerpent'],
        baseList['aqua'],
        baseList['pyro'],
        baseList['thunder'],
        baseList['rock'],
        baseList['plant'],
        baseList['machine'],
        baseList['psychic'],
        baseList['divineBeast'],
        baseList['creatorGod'],
        baseList['wyrm'],
        baseList['cyberse']
      ];
      this.typeList = [
        baseList['normal'],
        baseList['effect'],
        baseList['ritual'],
        baseList['fusion'],
        baseList['synchro'],
        baseList['xyz'],
        baseList['toon'],
        baseList['spirit'],
        baseList['union'],
        baseList['gemini'],
        baseList['tuner'],
        baseList['flip'],
        baseList['pendulum'],
        baseList['spsummon'],
        baseList['link']
      ];
    },
    closeDialog() {
      this.resetForm('text');
      this.$emit('update:modelValue', false);
    },
    callbackRace() {
      this.$emit('transferRace', this.form.text);
      this.closeDialog();
    },
    getPureName(str) {
      return str.replace(/\[(.*?)\(.*?\)]/g, '$1');
    },
    updateSelect() {
      let arr = this.raceText.split('／');
      this.form.race = arr.length > 0 ? this.getPureName(arr[0]) : '';
      this.form.type1 = arr.length > 1 ? this.getPureName(arr[1]) : '';
      this.form.type2 = arr.length > 2 ? this.getPureName(arr[2]) : '';
      this.form.type3 = arr.length > 3 ? this.getPureName(arr[3]) : '';
    },
    textToKanjiKana(str) {
      switch (str) {
        case '通常': return '[通(つう)][常(じょう)]';
        case '効果': return '[効(こう)][果(か)]';
        case '儀式': return '[儀(ぎ)][式(しき)]';
        case '融合': return '[融(ゆう)][合(ごう)]';
        case '特殊召喚': return '[特(とく)][殊(しゅ)][召(しょう)][喚(かん)]';
        case '魔法使い族': return '[魔(ま)][法(ほう)][使(つか)]い[族(ぞく)]';
        case 'ドラゴン族': return 'ドラゴン[族(ぞく)]';
        case 'アンデット族': return 'アンデット[族(ぞく)]';
        case '戦士族': return '[戦(せん)][士(し)][族(ぞく)]';
        case '獣戦士族': return '[獣(じゅう)][戦(せん)][士(し)][族(ぞく)]';
        case '獣族': return '[獣(けもの)][族(ぞく)]';
        case '鳥獣族': return '[鳥(ちょう)][獣(じゅう)][族(ぞく)]';
        case '悪魔族': return '[悪(あく)][魔(ま)][族(ぞく)]';
        case '天使族': return '[天(てん)][使(し)][族(ぞく)]';
        case '昆虫族': return '[昆(こん)][虫(ちゅう)][族(ぞく)]';
        case '恐竜族': return '[恐(きょう)][竜(りゅう)][族(ぞく)]';
        case '爬虫類族': return '[爬(は)][虫(ちゅう)][類(るい)][族(ぞく)]';
        case '魚族': return '[魚(さかな)][族(ぞく)]';
        case '海竜族': return '[海(かい)][竜(りゅう)][族(ぞく)]';
        case '水族': return '[水(みず)][族(ぞく)]';
        case '炎族': return '[炎(ほのお)][族(ぞく)]';
        case '雷族': return '[雷(いかずち)][族(ぞく)]';
        case '岩石族': return '[岩(がん)][石(せき)][族(ぞく)]';
        case '植物族': return '[植(しょく)][物(ぶつ)][族(ぞく)]';
        case '機械族': return '[機(き)][械(かい)][族(ぞく)]';
        case 'サイキック族': return 'サイキック[族(ぞく)]';
        case '幻神獣族': return '[幻(げん)][神(しん)][獣(じゅう)][族(ぞく)]';
        case '創造神族': return '[創(そう)][造(ぞう)][神(しん)][族(ぞく)]';
        case '幻竜族': return '[幻(げん)][竜(りゅう)][族(ぞく)]';
        case 'サイバース族': return 'サイバース[族(ぞく)]';
        default: return str;
      }
    },
    updateRaceText() {
      // TODO: update race text
      const split = (this.language === 'jp' || this.language === 'tc') ? '／' : '/';
      let finalStr = '';
      let r = this.form.race;
      let t1 = this.form.type1;
      let t2 = this.form.type2;
      let t3 = this.form.type3;

      if (r !== '') {
        if (this.language === 'jp') {
          finalStr += this.textToKanjiKana(r);
        } else {
          finalStr += r;
        }
      }
      if (t1 !== '') {
        if (this.language === 'jp') {
          finalStr += split + this.textToKanjiKana(t1);
        } else {
          finalStr += split + t1;
        }
      }

      if (t2 !== '') {
        if (this.language === 'jp') {
          finalStr += split + this.textToKanjiKana(t2);
        } else {
          finalStr += split + t2;
        }
      }
      if (t3 !== '') {
        if (this.language === 'jp') {
          finalStr += split + this.textToKanjiKana(t3);
        } else {
          finalStr += split + t3;
        }
      }

      this.form.text = finalStr;
    }
  },
  watch: {
    'raceText'() {
      this.updateRaceText();
    },
    'form.race'() {
      this.updateRaceText();
    },
    'form.type1'() {
      this.updateRaceText();
    },
    'form.type2'() {
      this.updateRaceText();
    },
    'form.type3'() {
      this.updateRaceText();
    }

  }
}
</script>

<style lang="scss" scoped>
.race-dialog {

}
</style>