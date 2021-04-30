<template>
    <span v-compress-text="compressParams">
        <template v-for="item in textList">
            <span v-if="typeof item === 'object'" :class="['ruby', specColor === '1' ? 'red' : '', specColor === '2' ? 'gold' : '', specColor === '3' ? 'silver': '', specColor === '4' ? 'blue' : '']">{{ item.ruby }}<span class="rt" v-compress-rt>{{ item.rt }}</span></span>
            <span v-else :class="['ruby', specColor === '1' ? 'red' : '', specColor === '2' ? 'gold' : '', specColor === '3' ? 'silver': '', specColor === '4' ? 'blue' : '']" :style="noCompress.includes(item) ? noCompressStyle : ''">{{ item }}</span>
        </template>
    </span>
</template>

<script>
export default {
  name: 'CompressText',
  props: ['text', 'width', 'height', 'language', 'fontLoading', 'autoSizeElement', 'specColor'],
  data() {
    return {
      noCompress: '●①②③④⑤⑥⑦⑧⑨⑩',
      textScale: 1.0
    }
  },
  computed: {
    compressParams() {
      return {
        specColor: this.specColor,
        width: this.width,
        height: this.height,
        language: this.language,
        autoSizeElement: this.autoSizeElement   // 英文语言下压缩到一定程度，字体缩小的元素
      };
    },
    textList() {
      let rep = this.language === 'sc' ? this.text.replace(/-/g, '－') : this.text;
      rep = rep.replace('\n', '\n\r');
      return rep.replace(new RegExp(`\\[(.*?)\\((.*?)\\)]|[${this.noCompress}]`, 'g'), s => `|${s}|`).split('|').filter(value => value).map(value => {
        if (/\[.*?\(.*?\)]/g.test(value)) {
          return {
            ruby: value.replace(/\[(.*?)\((.*?)\)]/g, '$1'),
            rt: value.replace(/\[(.*?)\((.*?)\)]/g, '$2')
          };
        }
        return value;
      });
    },
    noCompressStyle() {
      return {
        display: 'inline-block',
        transform: `scaleX(${1 / this.textScale})`,
        padding: `0 ${(1 - this.textScale) * 36}px`
      };
    }
  },
  watch: {
    fontLoading() {
      if (!this.fontLoading) {
        // 强制刷新
        this.$forceUpdate();
      }
    }
  },
  directives: {
    // 压缩或拉伸注音文字
    compressRt(el) {
      let ruby = el.parentNode;
      let rt = el;
      rt.classList.remove('justify');
      rt.style.paddingLeft = '0px';
      rt.style.paddingRight = '0px';
      ruby.style.margin = '';
      rt.style.transform = '';
      rt.style.left = '';

      let text = ruby.innerText.split('\n')[0];
      let rubyWidth = ruby.offsetWidth;
      let rtWidth = rt.offsetWidth;
      let rrv = rtWidth / rubyWidth;
      if (rrv < 0.9 && text.length > 1) {
        // 拉伸两端对齐
        rt.classList.add('justify');
        if (rrv < 0.25) {
          let pd = (rubyWidth - rtWidth) / 4;
          rt.style.paddingLeft = `${pd}px`;
          rt.style.paddingRight = `${pd}px`;
        } else if (rrv >= 0.25 && rrv < 0.35) {
          let pd = (rubyWidth - rtWidth) / 8;
          rt.style.paddingLeft = `${pd}px`;
          rt.style.paddingRight = `${pd}px`;
        } else if (rrv >= 0.35 && rrv < 0.45) {
          let pd = (rubyWidth - rtWidth) / 12;
          rt.style.paddingLeft = `${pd}px`;
          rt.style.paddingRight = `${pd}px`;
        }
      } else if (rtWidth > rubyWidth) {
        // 压缩
        if (rubyWidth / rtWidth < 0.6) {
          // 防止过度压缩，加宽ruby
          // 公式：(rubyWidth + widen) / rtWidth = 0.6
          let widen = 0.6 * rtWidth - rubyWidth;
          ruby.style.margin = `0 ${widen / 2}px`;
          rt.style.transform = `scaleX(${(rubyWidth + widen) / rtWidth})`;
          rt.style.left = `${-widen / 2}px`;
        } else {
          rt.style.transform = `scaleX(${rubyWidth / rtWidth})`;
        }
      } else {
        // 不变并居中
        rt.style.left = `${(rubyWidth - rtWidth) / 2}px`;
      }
    },
    // 压缩文本文字
    compressText(el, binding) {
      let that = binding.instance;
      let params = binding.value;
      if (params.width && params.height) {
        el.style.display = 'inline-block';
        el.style.width = `${params.width}px`;
        el.style.transform = '';
        el.style.transformOrigin = '0 0';

        let autoSizeElement = document.querySelector(params.autoSizeElement);
        autoSizeElement?.classList.remove('small-description');

        if (el.clientHeight > params.height) {
          // 用二分法获取最大的scale，精度0.01
          let scale = 0.5;
          let start = 0;
          let end = 1;
          // let time = 0;
          while (scale > 0) {
            // time++;
            scale = (start + end) / 2;
            el.style.width = `${params.width / scale}px`;
            el.style.transform = `scaleX(${scale})`;
            el.clientHeight > params.height ? end = scale : start = scale;
            if (el.clientHeight <= params.height && end - start <= /*0.01*/ 0.02) {
              // 如果是英文，灵摆和效果栏字体判断缩小
              if (params.language === 'en' && params.autoSizeElement && (scale < /*0.7*/ 0.5)) {
                // 防止死循环
                if (autoSizeElement?.classList.contains('small-description')) {
                  break;
                } else {
                  autoSizeElement?.classList.add('small-description');
                  scale = 0.5;
                  start = 0;
                  end = 1;
                }
              } else {
                break;
              }
            }
          }
          that.textScale = scale;
        } else {
          that.textScale = 1.0;
        }
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.ruby {
  position: relative;
  //white-space:pre-wrap;

  .rt {
    -webkit-text-stroke: 0 transparent;
    font-family: ygo-tip, sans-serif;
    font-size: 16px;
    font-weight: bold;
    position: absolute;
    left: 0;
    text-align: center;
    white-space: nowrap;
    letter-spacing: 0;
    text-indent: 0;
    transform-origin: 0 0;

    &.justify {
      text-align-last: justify;
      left: 5%;
      width: 90%;
    }
  }
}

.red {
  -webkit-background-clip: text;
  background-image: linear-gradient(#a31818, #ff5050, #a31818);
  color: transparent;
  display: inline-block;
  -webkit-text-stroke: 1px #800000;
}

.blue {
  -webkit-background-clip: text;
  background-image: linear-gradient(#8600FF, #CA8EFF, #96FED1, #8600FF);
  color: transparent;
  display: inline-block;
  -webkit-text-stroke: 1px #5B00AE;
}

.gold {
  -webkit-background-clip: text;
  background-image: linear-gradient(#DAA520, #FFFF00, #DAA520);
  color: transparent;
  display: inline-block;
  -webkit-text-stroke: 1px #A0522D;
}

.silver {
  -webkit-background-clip: text;
  background-image: linear-gradient(#C0C0C0, #F5F5F5, #C0C0C0);
  color: transparent;
  display: inline-block;
  -webkit-text-stroke: 1px #696969;
}

</style>
