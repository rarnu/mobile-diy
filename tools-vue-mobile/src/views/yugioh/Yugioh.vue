<template>
  <div class="yugioh-container">
    <Page>
      <template #default>
        <div class="yugioh-card" :class="cardClass" :style="cardStyle" ondragstart="return false">

          <div class="card-name" v-name-color="form.color">
            <CompressText :text="form.name" :fontLoading="fontLoading" :width="1030" :height="200" :specColor="form.redName" :language="form.language"></CompressText>
          </div>
          <div class="card-attribute">
            <el-image :src="attributeSrc"></el-image>
          </div>

          <div class="card-level" v-if="showLevel" :style="levelStyle">
            <el-image v-for="item in form.level" :src="baseImage('/level.png')"></el-image>
          </div>

          <div class="card-rank" v-if="showRank" :style="rankStyle">
            <el-image v-for="item in form.rank" :src="baseImage('/rank.png')"></el-image>
          </div>

          <div class="card-rank" v-if="showMinusLevel" :style="minusLevelStyle">
            <el-image v-for="item in form.level" :src="baseImage('/minus.png')"></el-image>
          </div>

          <div class="spell-trap-link" v-if="['spell','trap'].includes(form.type) && form.icon.startsWith('link-')">
            <span>{{ (form.language === 'as' || form.language === 'or') ? '' : form.language === 'en' ? '[' : '【' }}</span>
            <CompressText :text="spellTrapLinkName" :fontLoading="fontLoading"></CompressText>
            <el-image class="spell-trap-icon" v-if="['link-filed', 'link-quick-play', 'link-equip', 'link-continuous', 'link-ritual', 'link-counter'].includes(form.icon)" :src="baseImage(`/icon-${form.icon.replace('link-', '')}.png`)"></el-image>
            <span>{{ (form.language === 'as' || form.language === 'or') ? '' : form.language === 'en' ? ']' : '】' }}</span>
          </div>

          <div class="spell-trap" v-if="['spell','trap'].includes(form.type) && !form.icon.startsWith('link-')">
            <span>{{ (form.language === 'as' || form.language === 'or') ? '' : form.language === 'en' ? '[' : '【' }}</span>
            <CompressText :text="spellTrapName" :fontLoading="fontLoading"></CompressText>
            <el-image class="spell-trap-icon" v-if="['filed', 'quick-play', 'equip', 'continuous', 'ritual', 'counter'].includes(form.icon)" :src="baseImage(`/icon-${form.icon}.png`)"></el-image>
            <span>{{ (form.language === 'as' || form.language === 'or') ? '' : form.language === 'en' ? ']' : '】' }}</span>
          </div>

          <div class="card-image" v-if="form.image" :style="imageStyle">
            <el-image :src="form.image" :lazy="true">
              <template #placeholder>
                <div class="image-slot">
                  <i class="fal fa-spinner fa-pulse"></i>
                </div>
              </template>
              <template #error>
                <div class="image-slot">
                  <i class="fal fa-image"></i>
                </div>
              </template>
            </el-image>
          </div>

          <div class="card-mask" :style="maskStyle">
            <el-image v-if="form.type==='pendulum'" :src="baseImage('/card-mask-pendulum.png')" fit="cover"></el-image>
            <el-image v-else :src="baseImage('/card-mask.png')" fit="cover"></el-image>
          </div>

          <div class="left-pendulum" v-if="form.type==='pendulum'">
            <span v-if="form.pendulumScale >= 0">{{ form.pendulumScale }}</span>
            <span v-else-if="form.pendulumScale === -1">?</span>
          </div>

          <div class="right-pendulum" v-if="form.type==='pendulum'">
            <span v-if="form.pendulumScale >= 0">{{ form.pendulumScale }}</span>
            <span v-else-if="form.pendulumScale === -1">?</span>
          </div>

          <div class="pendulum-description" v-if="form.type==='pendulum'">
            <CompressText :text="form.pendulumDescription" :width="950" :height="230" :fontLoading="fontLoading"
                          :language="form.language" autoSizeElement=".pendulum-description"></CompressText>
          </div>

          <div class="card-package" :style="packageStyle">
            <span>{{ form.package }}</span>
          </div>

          <div class="link-arrow" v-if="(form.type==='monster'&&form.cardType==='link') || ((form.type === 'spell' || form.type === 'trap') && form.icon.startsWith('link-'))">
            <el-image :src="baseImage('/arrow-up-on.png')" style="top: 293px;left: 569px" v-show="form.arrowList.includes(1)"></el-image>
            <el-image :src="baseImage('/arrow-right-up-on.png')" style="top: 313px;left: 1141px" v-show="form.arrowList.includes(2)"></el-image>
            <el-image :src="baseImage('/arrow-right-on.png')" style="top: 774px;left: 1221px" v-show="form.arrowList.includes(3)"></el-image>
            <el-image :src="baseImage('/arrow-right-down-on.png')" style="top: 1347px;left: 1141px" v-show="form.arrowList.includes(4)"></el-image>
            <el-image :src="baseImage('/arrow-down-on.png')" style="top: 1427px;left: 569px" v-show="form.arrowList.includes(5)"></el-image>
            <el-image :src="baseImage('/arrow-left-down-on.png')" style="top: 1347px;left: 109px" v-show="form.arrowList.includes(6)"></el-image>
            <el-image :src="baseImage('/arrow-left-on.png')" style="top: 774px;left: 88px" v-show="form.arrowList.includes(7)"></el-image>
            <el-image :src="baseImage('/arrow-left-up-on.png')" style="top: 313px;left: 109px" v-show="form.arrowList.includes(8)"></el-image>
            <el-image :src="baseImage('/arrow-up-off.png')" style="top: 293px;left: 569px" v-show="!form.arrowList.includes(1)"></el-image>
            <el-image :src="baseImage('/arrow-right-up-off.png')" style="top: 313px;left: 1141px" v-show="!form.arrowList.includes(2)"></el-image>
            <el-image :src="baseImage('/arrow-right-off.png')" style="top: 774px;left: 1221px" v-show="!form.arrowList.includes(3)"></el-image>
            <el-image :src="baseImage('/arrow-right-down-off.png')" style="top: 1347px;left: 1141px" v-show="!form.arrowList.includes(4)"></el-image>
            <el-image :src="baseImage('/arrow-down-off.png')" style="top: 1427px;left: 569px" v-show="!form.arrowList.includes(5)"></el-image>
            <el-image :src="baseImage('/arrow-left-down-off.png')" style="top: 1347px;left: 109px" v-show="!form.arrowList.includes(6)"></el-image>
            <el-image :src="baseImage('/arrow-left-off.png')" style="top: 771px;left: 87px" v-show="!form.arrowList.includes(7)"></el-image>
            <el-image :src="baseImage('/arrow-left-up-off.png')" style="top: 313px;left: 109px" v-show="!form.arrowList.includes(8)"></el-image>
          </div>
          <div class="link-arrow" v-if="form.type === 'pendulum' && form.pendulumType === 'link-pendulum'">
            <el-image :src="baseImage('/arrow-p-board.png')" style="top: 320px; left:42px;"></el-image>
            <el-image :src="baseImage('/arrow-p-up.png')" style="top: 327px;left: 567px" v-show="form.arrowList.includes(1)"></el-image>
            <el-image :src="baseImage('/arrow-p-down.png')" style="top: 1240px;left: 567px" v-show="form.arrowList.includes(5)"></el-image>
            <el-image :src="baseImage('/arrow-p-left.png')" style="top: 715px;left: 54px" v-show="form.arrowList.includes(7)"></el-image>
            <el-image :src="baseImage('/arrow-p-right.png')" style="top: 715px;left: 1259px" v-show="form.arrowList.includes(3)"></el-image>
            <el-image :src="baseImage('/arrow-p-left-up.png')" style="top: 350px;left: 80px" v-show="form.arrowList.includes(8)"></el-image>
            <el-image :src="baseImage('/arrow-p-left-down.png')" style="top: 1180px;left: 80px" v-show="form.arrowList.includes(6)"></el-image>
            <el-image :src="baseImage('/arrow-p-right-up.png')" style="top: 350px;left: 1186px" v-show="form.arrowList.includes(2)"></el-image>
            <el-image :src="baseImage('/arrow-p-right-down.png')" style="top: 1180px;left: 1186px" v-show="form.arrowList.includes(4)"></el-image>
          </div>

          <div class="card-description" v-card-description>
            <div v-if="['monster','pendulum'].includes(form.type)" class="card-effect">
              <span>{{ (form.language === 'as' || form.language === 'or') ? '' : form.language === 'en' ? '[' : '【' }}</span>
              <CompressText :text="form.monsterType" :fontLoading="fontLoading"></CompressText>
              <span>{{ (form.language === 'as' || form.language === 'or') ? '' : form.language === 'en' ? ']' : '】' }}</span>
            </div>

            <div class="description-info" :style="descriptionStyle">
              <!-- 只有一行的情况，无论如何都压缩 -->
              <div v-if="form.description.split('\n').length === 1">
                <CompressText :text="form.description" :width="1170" :height="300" :fontLoading="fontLoading"
                              :language="form.language" autoSizeElement=".card-description"></CompressText>
              </div>
              <div v-else>
                <template v-for="(item,index) in form.description.split('\n')">
                  <!-- 处理第一行 -->
                  <div v-if="index === 0">
                    <!-- 如果第一行压缩 -->
                    <div v-if="form.firstLineCompress">
                      <CompressText :text="item" :width="1170" :height="70" :fontLoading="fontLoading"></CompressText>
                    </div>
                    <!-- 否则按其他行一样处理 -->
                    <div v-else>
                      <CompressText :text="item" :fontLoading="fontLoading"></CompressText>
                    </div>
                  </div>
                  <!--中间行不压缩-->
                  <div v-if="index > 0 && index < (form.description.split('\n').length - 1)">
                    <CompressText :text="item" :fontLoading="fontLoading"></CompressText>
                  </div>
                  <!--最后一行压缩-->
                  <div v-if="index !== 0 && index === (form.description.split('\n').length - 1)" class="last-description">
                    <CompressText :text="item" :width="1170" :height="lastDescriptionHeight" :fontLoading="fontLoading"
                                  :language="form.language" autoSizeElement=".card-description"></CompressText>
                  </div>
                  <!--item为空提供换行-->
                  <br v-if="!item">
                </template>
              </div>

            </div>
          </div>

          <div class="atk-def-link">
            <el-image :src="baseImage('/atk-def.svg')"
                      v-if="(form.type==='monster'&&form.cardType!=='link')||(form.type==='pendulum' && form.pendulumType !== 'link-pendulum')"></el-image>
            <el-image :src="baseImage('/atk-link.svg')" v-if="(form.type==='monster'&&form.cardType==='link') || (form.type === 'pendulum' && form.pendulumType === 'link-pendulum')"></el-image>
          </div>

          <div class="card-atk" v-if="['monster','pendulum'].includes(form.type)">
            <span v-if="form.atk >= 10000">
              <CompressText :text="`${form.atk}`" :width="400" :height="61"></CompressText>
            </span>

            <span v-if="form.atk >= 0 && form.atk <= 9999">{{ form.atk }}</span>
            <span v-else-if="form.atk === -1">?</span>
            <span v-else-if="form.atk === -2"><span class="card-atk-infinate"><b>∞</b></span></span>
          </div>

          <div class="card-def" v-if="(form.type==='monster'&&form.cardType!=='link')||(form.type==='pendulum' && form.pendulumType !== 'link-pendulum')">
            <span v-if="form.def >= 0">{{ form.def }}</span>
            <span v-else-if="form.def === -1">?</span>
            <span v-else-if="form.atk === -2"><span class="card-def-infinate"><b>∞</b></span></span>
          </div>

          <div class="card-link" v-if="(form.type==='monster'&&form.cardType==='link') || (form.type === 'pendulum' && form.pendulumType === 'link-pendulum')">
            <span>{{ form.arrowList.length }}</span>
          </div>

          <div class="card-password" :style="passwordStyle">
            <span>{{ form.password }}</span>
          </div>

          <div class="card-copyright" v-if="form.copyright">
            <el-image :src="copyrightSrc"></el-image>
          </div>

          <div class="card-laser" v-if="form.laser">
            <el-image :src="baseImage('/laser.png')"></el-image>
          </div>

          <div class="card-flash-0" v-if="form.flash0">
            <el-image :src="baseImage('/flash_bg.png')"></el-image>
          </div>

          <div class="card-flash-0" v-if="form.flash1 && form.type !== 'pendulum'">
            <el-image :src="baseImage('/flash_effect.png')"></el-image>
          </div>

          <div class="card-flash-0">
            <el-image :src="baseImage(`/flash_${form.flash2}.png`)"></el-image>
          </div>

        </div>

      </template>

    </Page>
  </div>
</template>

<script>
import Page from '@/components/page/Page';
import CompressText from '@/views/yugioh/components/CompressText';
import html2canvas from '@/assets/js/html2canvas';
import loadImage from 'blueimp-load-image';
import scDemo from './sc/sc-demo';
import tcDemo from './tc/tc-demo';
import jpDemo from './jp/jp-demo';
import enDemo from './en/en-demo';
import asDemo from './as/as-demo';
import orDemo from './or/or-demo';

export default {

  name: 'Yugioh',
  components: {
    Page,
    CompressText
  },
  data() {
    return {
      fontLoading: true,
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
      lastDescriptionHeight: 300   // 最后一行效果压缩高度
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
    window.setScale = this.setScale;
    window.setScaleEx = this.setScaleEx;
    window.exportImage = this.exportImage;

    document.fonts.ready.then(() => {
      this.fontLoading = false;
    });
  },
  methods: {
    setScale(w) {
      this.form.scale = w * 1.0 / 1393;
    },
    setScaleEx(s) {
      this.form.scale = s;
    },
    baseImage(path) {
      return require('@/assets/image' + path);
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
        this.fontLoading = true;
        document.fonts.ready.then(() => {
          this.fontLoading = false;
        });
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
    beforeUpload(file) {
      let flag = file.type.includes('image');
      if (flag) {
        loadImage(file, {
          canvas: true,
          top: 0,
          aspectRatio: 1
        }).then(data => {
          this.form.image = data.image.toDataURL('image/png', 1);
        });
      } else {
        this.$message.warning('请选择正确图片格式');
      }
      return false;
    },
    deleteImage() {
      this.form.image = '';
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
    // 获取最后一行效果的压缩高度
    getLastDescriptionHeight() {
      let lastDescription = document.querySelector('.last-description');
      if (lastDescription) {
        if (['monster', 'pendulum'].includes(this.form.type)) {
          this.lastDescriptionHeight = 330 - lastDescription.offsetTop;
        } else {
          this.lastDescriptionHeight = 380 - lastDescription.offsetTop;
        }
      } else {
        this.lastDescriptionHeight = 0;
      }
    },
    parseData(data) {
      let cardInfo = this.parseYugiohCard(data, this.form.language);
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
    exportImage() {
      let element = document.querySelector('.yugioh-card');
      html2canvas(element, {
        useCORS: true,
        backgroundColor: 'transparent',
        width: this.form.scale * 1393,
        height: this.form.scale * 2031,
      }).then(canvas => {
        let dataURL = canvas.toDataURL('image/png', 1);
        diy.receiveImageData(dataURL);
      });
    }
  },
  computed: {
    cardClass() {
      return `${this.form.language}-class ${this.form.cardBack ? 'card-back' : ''}`;
    },
    cardStyle() {
      let background;
      let cp = 'card';
      if (this.form.cardBack) {
        let u = this.baseImage('/card-back.png');
        background = `url(${u}) no-repeat center/cover`;
      } else if (this.form.type === 'monster') {
        let u = this.baseImage(`/${cp}-${this.form.cardType}.png`);
        background = `url(${u}) no-repeat center/cover`;
      } else if (this.form.type === 'pendulum') {
        let u = this.baseImage(`/${cp}-${this.form.pendulumType}.png`);
        background = `url(${u}) no-repeat center/cover`;
      } else {
        let u = this.baseImage(`/${cp}-${this.form.type}.png`);
        background = `url(${u}) no-repeat center/cover`;
      }
      return {
        transform: `scale(${this.form.scale})`,
        background: background,
        borderRadius: this.form.radius ? '24px' : '',
        marginRight: `${(this.form.scale - 1) * 1393}px`,
        marginBottom: `${(this.form.scale - 1) * 2031}px`
      };
    },
    attributeSrc() {
      let suffix = '';
      if (this.form.language === 'jp') {
        suffix = '-jp';
      } else if (this.form.language === 'en') {
        suffix = '-en';
      }
      if (['monster', 'pendulum'].includes(this.form.type)) {
        return this.baseImage(`/attribute-${this.form.attribute}${suffix}.png`);
      } else {
        return this.baseImage(`/attribute-${this.form.type}${suffix}.png`);
      }
    },
    spellTrapName() {
      let name = '';
      if (this.form.language === 'sc') {
        if (this.form.type === 'spell') {
          name = '魔法卡';
        } else if (this.form.type === 'trap') {
          name = '陷阱卡';
        }
      } else if (this.form.language === 'tc') {
        if (this.form.type === 'spell') {
          name = '魔法卡';
        } else if (this.form.type === 'trap') {
          name = '陷阱卡';
        }
      } else if (this.form.language === 'jp') {
        if (this.form.type === 'spell') {
          name = '[魔(ま)][法(ほう)]カード';
        } else if (this.form.type === 'trap') {
          name = '[罠(トラップ)]カード';
        }
      } else if (this.form.language === 'en') {
        if (this.form.type === 'spell') {
          name = 'Spell Card';
        } else if (this.form.type === 'trap') {
          name = 'Trap Card';
        }
      } else if (this.form.language === 'or') {
        if (this.form.type === 'spell') {
          name = 'fundsthc';
        } else if (this.form.type === 'trap') {
          name = 'fundgrun';
        }
      }
      return name;
    },
    spellTrapLinkName() {
      let name = '';
      if (this.form.language === 'sc') {
        name = '链接';
      } else if (this.form.language === 'tc') {
        name = '鏈接';
      } else if (this.form.language === 'jp' || this.form.language === 'as') {
        name = 'リンク';
      } else if (this.form.language === 'en') {
        name = 'Link';
      } else if (this.form.language === 'or') {
        name = 'LINK';
      }
      return name;
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
    levelStyle() {
      let right;
      if (this.form.level < 13) {
        right = '146px'
      } else {
        right = '100px'
      }
      return {
        right: right
      }
    },
    rankStyle() {
      let left;
      if (this.form.rank < 13) {
        left = '147px'
      } else {
        left = '100px'
      }
      return {
        left: left
      }
    },
    minusLevelStyle() {
      let left;
      if (this.form.level < 13) {
        left = '147px';
      } else {
        left = '100px';
      }
      return {
        left: left
      }
    },
    imageStyle() {
      let left, top, width, height;
      if (this.form.type === 'pendulum') {
        left = '94px';
        top = '365px';
        width = '1206px';
        height = '1204px';
      } else {
        left = '171px';
        top = '373px';
        width = '1053px';
        height = '1053px';
      }
      return {
        left: left,
        top: top,
        width: width,
        height: height
      };
    },
    maskStyle() {
      let left, top;
      if (this.form.type === 'pendulum') {
        left = '81px';
        top = '1254px';
      } else {
        left = '168px';
        top = '373px';
      }
      return {
        left: left,
        top: top
      };
    },
    packageStyle() {
      let top, left, right;
      if (this.form.type === 'pendulum') {
        top = '1854px';
        left = '116px';
      } else if (this.form.type === 'monster' && this.form.cardType === 'link') {
        top = '1455px';
        right = '252px';
      } else if ((this.form.type === 'spell' || this.form.type === 'trap') && this.form.icon.startsWith('link-')) {
        top = '1455px';
        right = '252px';
      } else {
        top = '1455px';
        right = '148px';
      }
      return {
        color: this.form.type === 'monster' && (this.form.cardType === 'xyz') ? 'white' : 'black',
        top: top,
        left: left,
        right: right
      };
    },
    descriptionStyle() {
      let fontFamily;
      if (this.form.language === 'en') {
        if ((this.form.type === 'monster' && this.form.cardType === 'normal') || (this.form.type === 'pendulum' && this.form.pendulumType === 'normal-pendulum')) {
          fontFamily = 'ygo-en-italic';
        }
      }
      return {
        fontFamily: fontFamily
      };
    },
    passwordStyle() {
      return {
        color: this.form.type === 'monster' && this.form.cardType === 'xyz' ? 'white' : 'black'
      };
    },
    copyrightSrc() {
      let color = this.form.type === 'monster' && this.form.cardType === 'xyz' ? 'white' : 'black';
      return this.baseImage(`/copyright-${this.form.copyright}-${color}.svg`);
    },
    cardName() {
      return this.form.name.replace(/\[(.*?)\(.*?\)]/g, '$1');
    }
  },
  directives: {
    nameColor(el, binding) {
      let that = binding.instance;
      // 文本和注音颜色分开控制
      let color = 'black';
      // 自动颜色
      if ((that.form.type === 'monster' && ['xyz', 'link', 'darksync'].includes(that.form.cardType)) || ['spell', 'trap'].includes(that.form.type) ||
          (that.form.type === 'pendulum' && ['xyz-pendulum', 'link-pendulum', 'darksync-pendulum'].includes(that.form.pendulumType))) {
        color = 'white';
      }
      el.style.color = binding.value || color;
      let rtList = el.querySelectorAll('.rt');
      rtList.forEach(rt => {
        rt.style.color = color;
      });
    },
    cardDescription(el, binding) {
      let that = binding.instance;
      that.getLastDescriptionHeight();
    }
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
