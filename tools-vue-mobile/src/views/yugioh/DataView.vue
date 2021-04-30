<template>
  <div class="yugioh-container">
    <Page>
      <template #default>
        <div class="form-main">
          <el-form ref="form" :model="form" label-width="auto" size="small">
            <el-form-item label="文件">
              <el-row :gutter="10">
                <el-col :span="5">
                  <el-button type="primary" @click="newCard">新建</el-button>
                </el-col>
                <el-col :span="5">
                  <el-button type="primary" @click="importJson">打开</el-button>
                </el-col>
                <el-col :span="5">
                  <el-button type="primary" @click="exportJson">保存</el-button>
                </el-col>
                <el-col :span="5">
                  <el-button type="warning" @click="shareCardData">分享</el-button>
                </el-col>
              </el-row>
            </el-form-item>
            <el-form-item label="语言">
              <el-row :gutter="10">
                <el-col :span="18">
                  <el-select v-model="form.language" placeholder="请选择语言" @change="changeLanguage">
                    <el-option label="简体中文" value="sc"></el-option>
                    <el-option label="繁体中文" value="tc"></el-option>
                    <el-option label="日文" value="jp"></el-option>
                    <el-option label="英文" value="en"></el-option>
                    <el-option label="星光体" value="as"></el-option>
                    <el-option label="奥利哈钢" value="or"></el-option>
                  </el-select>
                </el-col>
                <el-col :span="6">
                  <el-button type="primary" style="width: 100%;" :loading="randomLoading" @click="getRandomCard">随机一卡</el-button>
                </el-col>
              </el-row>
            </el-form-item>
            <el-form-item label="卡名">
              <el-autocomplete v-model="form.name" :fetch-suggestions="fetchCardName" placeholder="请输入卡名" @select="selectCardName"></el-autocomplete>
            </el-form-item>
            <el-form-item label="注音">
              <el-button type="primary" :disabled="form.language !== 'jp'" @click="remoteKana">远程注音</el-button>
              <el-button type="success" @click="editTextKana">注音文本编辑</el-button>
            </el-form-item>
            <el-form-item label="颜色">
              <el-color-picker v-model="form.color"></el-color-picker>
              <span class="tip">（自动选择清空）</span>
            </el-form-item>
            <el-form-item label="类型">
              <el-radio-group v-model="form.type">
                <el-radio-button label="monster">怪兽</el-radio-button>
                <el-radio-button label="spell">魔法</el-radio-button>
                <el-radio-button label="trap">陷阱</el-radio-button>
                <el-radio-button label="pendulum">灵摆</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="属性" v-if="['monster','pendulum'].includes(form.type)">
              <el-radio-group v-model="form.attribute">
                <el-radio-button label="dark">暗</el-radio-button>
                <el-radio-button label="light">光</el-radio-button>
                <el-radio-button label="earth">地</el-radio-button>
                <el-radio-button label="water">水</el-radio-button>
                <el-radio-button label="fire">炎</el-radio-button>
                <el-radio-button label="wind">风</el-radio-button>
                <el-radio-button label="divine">神</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="图标" v-if="['spell','trap'].includes(form.type)">
              <el-select v-model="form.icon" placeholder="请选择图标" clearable>
                <el-option label="装备" value="equip"></el-option>
                <el-option label="场地" value="filed"></el-option>
                <el-option label="速攻" value="quick-play"></el-option>
                <el-option label="仪式" value="ritual"></el-option>
                <el-option label="永续" value="continuous"></el-option>
                <el-option label="反击" value="counter"></el-option>
                <el-option label="连接／通常" value="link-normal"></el-option>
                <el-option label="连接／装备" value="link-equip"></el-option>
                <el-option label="连接／场地" value="link-filed"></el-option>
                <el-option label="连接／速攻" value="link-quick-play"></el-option>
                <el-option label="连接／仪式" value="link-ritual"></el-option>
                <el-option label="连接／永续" value="link-continuous"></el-option>
                <el-option label="连接／反击" value="link-counter"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="图片">
              <el-button type="primary" @click="uploadImage">相册</el-button>
              <el-button type="primary" @click="takePhotoImage">拍照</el-button>
              <el-button style="margin-left: 10px" plain @click="deleteImage">删除</el-button>&nbsp;&nbsp;&nbsp;
              <el-image v-if="form.image" :src="form.image" :lazy="true" :style="imageStyle"/>
            </el-form-item>
            <el-form-item label="卡类" v-if="form.type==='monster'">
              <el-select v-model="form.cardType" placeholder="请选择卡类">
                <el-option label="通常" value="normal"></el-option>
                <el-option label="效果" value="effect"></el-option>
                <el-option label="仪式" value="ritual"></el-option>
                <el-option label="融合" value="fusion"></el-option>
                <el-option label="同调" value="synchro"></el-option>
                <el-option label="黑暗同调" value="darksync"></el-option>
                <el-option label="超量" value="xyz"></el-option>
                <el-option label="连接" value="link"></el-option>
                <el-option label="衍生物" value="token"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="灵摆" v-if="form.type==='pendulum'">
              <el-select v-model="form.pendulumType" placeholder="请选择灵摆">
                <el-option label="通常／灵摆" value="normal-pendulum"></el-option>
                <el-option label="效果／灵摆" value="effect-pendulum"></el-option>
                <el-option label="仪式／灵摆" value="ritual-pendulum"></el-option>
                <el-option label="融合／灵摆" value="fusion-pendulum"></el-option>
                <el-option label="同调／灵摆" value="synchro-pendulum"></el-option>
                <el-option label="黑暗同调／灵摆" value="darksync-pendulum"></el-option>
                <el-option label="超量／灵摆" value="xyz-pendulum"></el-option>
                <el-option label="链接／灵摆" value="link-pendulum"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="星级" v-if="showLevel || showMinusLevel">
              <el-input-number v-model="form.level" :min="0" :max="13" :precision="0"></el-input-number>
            </el-form-item>
            <el-form-item label="阶级" v-if="showRank">
              <el-input-number v-model="form.rank" :min="0" :max="13" :precision="0"></el-input-number>
            </el-form-item>

            <el-form-item label="刻度" v-if="form.type==='pendulum'">
              <el-input-number v-model="form.pendulumScale" :min="-1" :max="13" :precision="0"></el-input-number>
              <span class="tip">（? 输入 -1）</span>
            </el-form-item>
            <el-form-item label="灵摆效果" label-width="40px" v-if="form.type==='pendulum'">
              <el-input type="textarea" :autosize="{minRows: 3}" v-model="form.pendulumDescription"
                        placeholder="请输入灵摆效果" @input="inputPendulumDescription"></el-input>
            </el-form-item>
            <el-form-item label="种族" v-if="['monster','pendulum'].includes(form.type)">
              <el-row :gutter="10">
                <el-col :span="20">
                  <el-input v-model="form.monsterType" placeholder="请输入种族"></el-input>
                </el-col>
                <el-col :span="4">
                  <el-button type="primary" @click="raceDialog = true">输入</el-button>
                </el-col>
              </el-row>
            </el-form-item>
            <el-form-item label="ATK" v-if="['monster','pendulum'].includes(form.type)">
              <el-input-number v-model="form.atk" :min="-2" :max="999999" :precision="0"></el-input-number>
              <span class="tip">（? 输入 -1）（∞ 输入 -2）</span>
            </el-form-item>
            <el-form-item label="DEF" v-if="(form.type==='monster'&&form.cardType!=='link')||form.type==='pendulum'">
              <el-input-number v-model="form.def" :min="-2" :max="999999" :precision="0"></el-input-number>
              <span class="tip">（? 输入 -1）（∞ 输入 -2）</span>
            </el-form-item>
            <el-form-item label="箭头" v-if="(form.type==='monster'&& form.cardType==='link') || (form.type === 'pendulum' && form.pendulumType === 'link-pendulum') || ((form.type === 'spell' || form.type === 'trap') && form.icon.startsWith('link-'))">
              <div class="arrow-form">
                <div class="arrow-item" v-for="item in [8,1,2,7,9,3,6,5,4]" @click="toggleArrow(item)" :style="arrowItemStyle(item)">
                  <i class="fas fa-arrow-alt-up" v-if="item===1"></i>
                  <i class="fas fa-arrow-alt-up" style="transform: rotate(45deg)" v-if="item===2"></i>
                  <i class="fas fa-arrow-alt-right" v-if="item===3"></i>
                  <i class="fas fa-arrow-alt-right" style="transform: rotate(45deg)" v-if="item===4"></i>
                  <i class="fas fa-arrow-alt-down" v-if="item===5"></i>
                  <i class="fas fa-arrow-alt-down" style="transform: rotate(45deg)" v-if="item===6"></i>
                  <i class="fas fa-arrow-alt-left" v-if="item===7"></i>
                  <i class="fas fa-arrow-alt-left" style="transform: rotate(45deg)" v-if="item===8"></i>
                </div>
              </div>
            </el-form-item>
            <el-form-item label="效果">
              <el-switch v-model="form.firstLineCompress" active-text="首行压缩(只有一行时无效)"></el-switch>
              <el-input type="textarea" :autosize="{minRows: 3}" v-model="form.description" placeholder="请输入效果"></el-input>
            </el-form-item>
            <el-form-item label="卡包">
              <el-input v-model="form.package" placeholder="请输入卡包"></el-input>
            </el-form-item>
            <el-form-item label="密码">
              <div style="display: flex">
                <el-input v-model="form.password" placeholder="请输入密码"></el-input>
                <el-button style="margin-left: 10px" type="primary" :loading="searchLoading" @click="searchCardByPassword">搜索</el-button>
                <el-button type="primary" @click="randomPassword">随机</el-button>
              </div>
            </el-form-item>
            <el-form-item label="版权">
              <el-select v-model="form.copyright" placeholder="请选择版权" clearable>
                <el-option label="简体中文" value="sc"></el-option>
                <el-option label="日文" value="jp"></el-option>
                <el-option label="英文" value="en"></el-option>
              </el-select>
            </el-form-item>
            <el-row :gutter="10">
              <el-col :span="8">
                <el-form-item label="角标">
                  <el-switch v-model="form.laser"></el-switch>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="圆角">
                  <el-switch v-model="form.radius"></el-switch>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="卡背">
                  <el-switch v-model="form.cardBack"></el-switch>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="闪卡">
              <el-row :gutter="10">
                <el-col :span="8">
                  <el-switch v-model="form.flash0" active-text="背景"></el-switch>&nbsp;&nbsp;
                </el-col>
                <el-col :span="8">
                  <el-switch v-model="form.flash1" active-text="效果框"></el-switch>&nbsp;&nbsp;
                </el-col>
                <el-col :span="8">
                  <el-select v-model="form.redName">
                    <el-option label="无" value=""></el-option>
                    <el-option label="红碎" value="1"></el-option>
                    <el-option label="金字" value="2"></el-option>
                    <el-option label="银字" value="3"></el-option>
                    <el-option label="蓝碎" value="4"></el-option>
                  </el-select>
                </el-col>
              </el-row>
            </el-form-item>
            <el-form-item label="卡面">
              <el-radio-group v-model="form.flash2">
                <el-radio-button label="0">0</el-radio-button>
                <el-radio-button label="1">1</el-radio-button>
                <el-radio-button label="2">2</el-radio-button>
                <el-radio-button label="3">3</el-radio-button>
                <el-radio-button label="4">4</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-form>
        </div>

        <KanjiKanaDialog v-model="kanjiKanaDialog"></KanjiKanaDialog>
        <RaceDialog v-model="raceDialog" :raceText="form.monsterType" :language="form.language" @transferRace="getRace"></RaceDialog>
        <EffectDialog v-model="effectDialog"></EffectDialog>
      </template>

    </Page>
  </div>
</template>

<script>
import Page from '@/components/page/Page';
import KanjiKanaDialog from '@/views/yugioh/components/KanjiKanaDialog';
import RaceDialog from "@/views/yugioh/components/RaceDialog";
import EffectDialog from "@/views/yugioh/components/EffectDialog";
import loadImage from 'blueimp-load-image';
import scDemo from './sc/sc-demo';
import tcDemo from './tc/tc-demo';
import jpDemo from './jp/jp-demo';
import enDemo from './en/en-demo';
import asDemo from './as/as-demo';
import orDemo from './or/or-demo';

export default {

  name: 'DataView',
  components: {
    Page,
    EffectDialog,
    KanjiKanaDialog,
    RaceDialog
  },
  data() {
    return {
      searchLoading: false,
      randomLoading: false,
      form: {
        language: 'jp',
        name: '',
        color: '',
        type: 'monster',
        attribute: 'dark',
        icon: '',
        image: '',
        cardType: 'normal',
        pendulumType: 'normal-pendulum',
        level: 0,
        rank: 0,
        pendulumScale: 0,
        pendulumDescription: '',
        monsterType: '',
        atk: 0,
        def: 0,
        arrowList: [],
        description: '',
        package: '',
        password: '',
        copyright: '',
        laser: false,
        radius: false,
        cardBack: false,
        scale: 0.30,
        firstLineCompress: false,
        flash0: false,
        flash1: false,
        flash2: 0,
        redName: ''
      },
      lastDescriptionHeight: 300,   // 最后一行效果压缩高度
      kanjiKanaDialog: false,
      raceDialog: false,
      effectDialog: false
    };
  },
  created() {
    try {
      diy.initCard();
    } catch (e) {

    }
  },
  mounted() {
    window.assignCardData = this.assignCardData;
    window.getFormData = this.getFormData;
    window.setImage = this.setImage;

    document.fonts.ready.then();
  },
  methods: {
    shareCardData() {
      try {
        diy.openShareCard(this.cardName);
      } catch (e) {

      }
    },
    getFormData() {
      return this.form;
    },
    baseImage(path) {
      return require('@/assets/image' + path);
    },
    newCard() {
      Object.assign(this.form, jpDemo);
    },
    randomPassword() {
      let rand = '';
      for (let i = 0; i < 8; i++) {
        rand += Math.floor(Math.random() * 10);
      }
      this.form.password = rand;
    },
    async getRandomCard() {
      this.randomLoading = true;
      let res = await this.axios({
        method: 'get',
        url: '/yugioh/random-card',
        params: {
          lang: this.form.language
        }
      });
      let cardInfo = await this.parseYugiohCard(res.data.data, this.form.language);
      Object.assign(this.form, cardInfo);
      this.randomLoading = false;
    },
    importJson() {
      try {
        diy.openJsonFile();
      } catch (e) {

      }
    },
    exportJson() {
      let data = JSON.stringify(this.form);
      try {
        diy.saveJsonFile(data);
      } catch (e) {
      }
    },
    assignCardData(data) {
      Object.assign(this.form, data);
      this.refreshFont();
    },
    getRace(race) {
      // 从 Race 对话框回传过来的
      this.form.monsterType = race;
    },
    // 刷新字体
    refreshFont() {
      setTimeout(() => {
        document.fonts.ready.then();
      });
    },
    changeLanguage(value) {
      if (value === 'sc') {
        Object.assign(this.form, scDemo);
      } else if (value === 'tc') {
        Object.assign(this.form, tcDemo);
      } else if (value === 'jp') {
        Object.assign(this.form, jpDemo);
      } else if (value === 'en') {
        Object.assign(this.form, enDemo);
      } else if (value === 'as') {
        Object.assign(this.form, asDemo);
      } else if (value === 'or') {
        Object.assign(this.form, orDemo);
      }
      this.refreshFont();
    },
    setImage(imgData) {
      console.log('js: setImage: ' + imgData);
      this.form.image = imgData;
    },
    uploadImage() {
      try {
        diy.uploadImage();
      } catch (e) {
      }
    },
    takePhotoImage() {
      try {
        diy.takePhotoImage();
      } catch (e) {
      }
    },
    deleteImage() {
      this.form.image = '';
    },
    toggleArrow(item) {
      if (this.form.arrowList.includes(item)) {
        this.form.arrowList = this.form.arrowList.filter(value => value !== item);
      } else {
        this.form.arrowList.push(item);
      }
    },
    arrowItemStyle(item) {
      let border = '';
      let color = '';
      if (this.form.arrowList.includes(item)) {
        border = '1px solid darkorange';
        color = 'darkorange';
      }
      return {
        border: border,
        color: color,
        visibility: item === 9 ? 'hidden' : ''
      };
    },
    editTextKana() {
      try {
        diy.editTextKana();
      } catch (e) {

      }
    },
    remoteKana() {
      // 从远程服务器请求注音
      // 从远程服务器请求注音
      this.kanjiKanaAPI(this.cardName).then(kk => {
        if (kk) {
          this.form.name = kk;
        } else {
          this.form.name = this.kanjiToKana(this.cardName);
        }
      });
      if ((this.form.type === 'monster' && this.form.cardType === 'normal') || (this.form.type === 'pendulum' && this.form.pendulumType === 'normal-pendulum')) {
        this.normalKanjiKanaAPI(this.form.description).then(kk => {
          if (kk) {
            this.form.description = kk;
          } else {
            this.form.description = this.kanjiToKana(this.form.description);
          }
        });
      } else {
        this.effectKanjiKanaAPI(this.form.description).then(kk => {
          if (kk) {
            this.form.description = kk;
          } else {
            this.form.description = this.kanjiToKana(this.form.description);
          }
        });
      }
      this.effectKanjiKanaAPI(this.form.pendulumDescription).then(kk => {
        if (kk) {
          this.form.pendulumDescription = kk;
        } else {
          this.form.pendulumDescription = this.kanjiToKana(this.form.pendulumDescription);
        }
      });
    },
    fetchCardName(value, callback) {
      if (value) {
        this.axios({
          method: 'get',
          url: '/yugioh/card',
          params: {
            name: this.cardName,
            lang: this.form.language
          }
        }).then(res => {
          let data = res.data.data;
          data.forEach(value => {
            value.value = `${value.name}（${value.id}）`;
          });
          callback(data);
        });
      }
      callback([]);
    },
    selectCardName(value) {
      this.form.name = value.name;
      this.form.password = value.id;
      this.searchCardByPassword();
    },
    async parseData(data) {
      let cardInfo = await this.parseYugiohCard(data, this.form.language);
      Object.assign(this.form, cardInfo);
    },
    searchByPassword(p) {
      this.form.password = p;
      this.searchCardByPassword();
    },
    searchCardByPassword() {
      this.searchLoading = true;
      this.axios({
        method: 'get',
        url: '/yugioh/card/' + this.form.password,
        params: {
          lang: this.form.language
        }
      }).then(res => {
        this.parseData(res.data.data);
      }).finally(() => {
        this.searchLoading = false;
      });
    },
  },
  computed: {
    cardName() {
      return this.form.name.replace(/\[(.*?)\(.*?\)]/g, '$1');
    },
    showLevel() {
      let flag = false;
      if (this.form.type === 'monster') {
        flag = ['normal', 'effect', 'ritual', 'fusion', 'synchro', 'token'].includes(this.form.cardType);
      } else if (this.form.type === 'pendulum') {
        flag = ['normal-pendulum', 'effect-pendulum', 'ritual-pendulum', 'fusion-pendulum', 'synchro-pendulum'].includes(this.form.pendulumType);
      }
      return flag;
    },
    showRank() {
      let flag = false;
      if (this.form.type === 'monster') {
        flag = this.form.cardType === 'xyz';
      } else if (this.form.type === 'pendulum') {
        flag = this.form.pendulumType === 'xyz-pendulum';
      }
      return flag;
    },
    showMinusLevel() {
      let flag = false;
      if (this.form.type === 'monster') {
        flag = this.form.cardType === 'darksync';
      } else if (this.form.type === 'pendulum') {
        flag = this.form.pendulumType === 'darksync-pendulum';
      }
      return flag;
    },
    imageStyle() {
      return {
        position: 'absolute',
        top: '2px',
        width: '28px',
        height: '28px'
      };
    },
  },
  watch: {
    'form.package'() {
      if (this.form.language === 'jp') {
        this.form.package = this.form.package.replace('-EN', '-JP').replace('-SC', '-JP').replace('-TC', '-JP');
      } else if (this.form.language === 'sc') {
        this.form.package = this.form.package.replace('-EN', '-SC').replace('-JP', '-SC').replace('-TC', '-SC');
      } else if (this.form.language === 'tc') {
        this.form.package = this.form.package.replace('-EN', '-TC').replace('-SC', '-TC').replace('-JP', '-TC');
      } else if (this.form.language === 'en') {
        this.form.package = this.form.package.replace('-JP', '-EN').replace('-SC', '-EN').replace('-TC', '-EN');
      }
    },
    // 图片转base64
    'form.image'() {
      if (this.form.image && !this.form.image.startsWith('data:image')) {
        loadImage(this.form.image, {
          canvas: true,
          top: 0,
          aspectRatio: 1,
          crossOrigin: 'Anonymous'
        }).then(data => {
          this.form.image = data.image.toDataURL('image/png', 1);
        });
      }
    }
  }
};
</script>

<style lang="scss" scoped>
@import "./sc/sc";
@import "./tc/tc";
@import "./jp/jp";
@import "./en/en";
@import "./as/as";
@import "./or/or";

.yugioh-container {

  .yugioh-card {
    width: 1393px;
    height: 2031px;
    display: inline-flex;
    flex-wrap: wrap;
    vertical-align: top;
    position: relative;
    user-select: none;
    color: black;
    transform-origin: 0 0;
    overflow: hidden;

    .card-name {
      position: absolute;
      left: 116px;
      width: 1030px;
      max-height: 130px;

      ::v-deep(.ruby) {
        .rt {
          font-size: 18px;
          top: 3px;
        }
      }
    }

    .card-attribute {
      position: absolute;
      left: 1159px;
      top: 96px;
    }

    .card-level {
      position: absolute;
      top: 250px;

      .el-image {
        margin-left: 3px;
      }
    }

    .card-rank {
      position: absolute;
      top: 250px;

      .el-image {
        margin-right: 3px;
      }
    }

    .spell-trap-link {
      position: absolute;
      display: flex;
      align-items: center;

      ::v-deep(.ruby) {
        .rt {
          font-size: 18px;
          top: -2px;
        }
      }

      .el-image {
        display: flex;
        min-width: 72px;
      }
    }

    .spell-trap {
      position: absolute;
      display: flex;
      align-items: center;

      ::v-deep(.ruby) {
        .rt {
          font-size: 18px;
          top: -2px;
        }
      }

      .el-image {
        display: flex;
        min-width: 72px;
      }
    }

    .card-image {
      position: absolute;

      .el-image {
        width: 100%;
        height: 100%;

        .image-slot {
          display: flex;
          height: 100%;
          width: 100%;
          justify-content: center;
          align-items: center;
          font-size: 120px;
          color: $normal-color;
        }
      }
    }

    .card-mask {
      position: absolute;
      z-index: 10;
    }

    .left-pendulum {
      position: absolute;
      top: 1379px;
      left: 93px;
      width: 95px;
      font-family: ygo-atk-def, serif;
      font-size: 98px;
      text-align: center;
      letter-spacing: -10px;
      z-index: 20;
    }

    .right-pendulum {
      position: absolute;
      top: 1379px;
      right: 104px;
      width: 95px;
      font-family: ygo-atk-def, serif;
      font-size: 98px;
      text-align: center;
      letter-spacing: -10px;
      z-index: 20;
    }

    .pendulum-description {
      position: absolute;
      left: 221px;
      width: 950px;
      text-align: justify;
      z-index: 20;

      ::v-deep(.ruby) {
        .rt {
          font-size: 12px;
          top: -5px;
        }
      }
    }

    .card-package {
      position: absolute;
      font-family: ygo-password, serif;
      font-size: 40px;
      z-index: 20;
    }

    .link-arrow {
      .el-image {
        position: absolute;
        z-index: 20;
      }
    }

    .card-description {
      position: absolute;
      left: 109px;
      width: 1170px;
      text-align: justify;
      z-index: 20;

      .card-effect {
        white-space: nowrap;

        ::v-deep(.ruby) {
          .rt {
            font-size: 14px;
            top: -3px;
          }
        }
      }

      .description-info {
        ::v-deep(.ruby) {
          .rt {
            font-size: 12px;
            top: -5px;
          }
        }
      }
    }

    .atk-def-link {
      position: absolute;
      left: 109px;
      top: 1844px;
      z-index: 20;
    }

    .card-atk {
      position: absolute;
      right: 395px;
      top: 1845px;
      font-family: ygo-atk-def, serif;
      font-size: 61px;
      letter-spacing: 2px;
      z-index: 20;
    }

    .card-atk-infinate {
      position: absolute;
      right: 48px;
      top: -5px;
      font-family: ygo-jp, 楷体, serif;
      font-size: 61px;
    }


    .card-def {
      position: absolute;
      right: 124px;
      top: 1845px;
      font-family: ygo-atk-def, serif;
      font-size: 61px;
      letter-spacing: 2px;
      z-index: 20;
    }

    .card-def-infinate {
      position: absolute;
      right: 48px;
      top: -5px;
      font-family: ygo-jp, 楷体, serif;
      font-size: 61px;
    }


    .card-link {
      position: absolute;
      right: 116px;
      top: 1846px;
      font-family: ygo-link, serif;
      font-size: 54px;
      letter-spacing: 2px;
      z-index: 20;
    }

    .card-password {
      position: absolute;
      left: 66px;
      top: 1929px;
      font-family: ygo-password, serif;
      font-size: 40px;
      z-index: 20;
    }

    .card-copyright {
      position: absolute;
      top: 1936px;
      right: 141px;
      z-index: 20;
    }

    .card-laser {
      position: absolute;
      left: 1276px;
      top: 1913px;
      z-index: 20;
    }

    .card-flash-0 {
      position: absolute;
      top: 0;
      left: 0;
    }

    &.card-back {
      * {
        display: none;
      }
    }
  }

  .form-ydk {

  }

  .form-main {
    .font-loading {
      display: flex;
      align-items: center;
      color: $primary-color;
      margin-bottom: 20px;

      i {
        margin-right: 10px;
        font-size: 18px;
      }
    }

    .arrow-form {
      width: 130px;
      display: flex;
      flex-wrap: wrap;
      margin-right: -10px;
      margin-bottom: -10px;

      .arrow-item {
        width: 32px;
        height: 32px;
        margin-right: 10px;
        margin-bottom: 10px;
        border: 1px solid $border-color;
        border-radius: 4px;
        display: flex;
        justify-content: center;
        align-items: center;
        cursor: pointer;
        color: $placeholder-color;
        font-size: 18px;
      }
    }

    .button-group {
      .el-row {
        margin-top: -20px;

        .el-col {
          margin-top: 20px;

          ::v-deep(.el-upload) {
            width: 100%;
          }

          .el-button {
            width: 100%;
          }
        }
      }
    }
  }
}
</style>
