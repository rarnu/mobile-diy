import vm from '@/main';

const kanjiKanaMap = require('@/assets/json/kanji-kana.json');
const monsterTypeList = require('@/assets/json/monster-type-list.json');

export default {
    install(app, options) {

        // 接口请求地址配置
        app.config.globalProperties.baseURL = 'http://yugioh.vip:9800/api';
        // app.config.globalProperties.baseURL = 'http://0.0.0.0:9800/api';

        // 解析游戏王卡片
        app.config.globalProperties.parseYugiohCard = async function (data, lang, kk = true) {
            let card = {
                name: parseName(data),
                type: parseType(data),
                attribute: parseAttribute(data),
                icon: parseIcon(data),
                image: parseImage(data),
                cardType: parseCardType(data),
                pendulumType: parsePendulumType(data),
                level: parseLevelRank(data),
                rank: parseLevelRank(data),
                pendulumScale: parsePendulumScale(data),
                pendulumDescription: parsePendulumDescription(data),
                monsterType: parseMonsterType(data, lang),
                atk: parseAtk(data),
                def: parseDef(data),
                arrowList: parseArrowList(data),
                description: parseDescription(data, lang),
                firstLineCompress: parseFirstLineCompress(data),
                package: parsePackage(data),
                password: parsePassword(data)
            };
            if (lang === 'jp' && kk) {
                // 添加注音
                let kk = await vm.kanjiKanaAPI(card.name);
                if (kk) {
                    card.name = kk;
                } else {
                    card.name = vm.kanjiToKana(card.name);
                }
                let pekk = await vm.effectKanjiKanaAPI(card.pendulumDescription);
                if (pekk) {
                    card.pendulumDescription = pekk;
                } else {
                    card.pendulumDescription = vm.kanjiToKana(card.pendulumDescription)
                }
                card.monsterType = vm.kanjiToKana(card.monsterType);
                let efkk = ((card.type === 'monster' && card.cardType === 'normal') || (card.type === 'pendulum' && card.pendulumType === 'normal-pendulum')) ? await vm.normalKanjiKanaAPI(card.description) : await vm.effectKanjiKanaAPI(card.description);
                if (efkk) {
                    card.description = efkk;
                } else {
                    card.description = vm.kanjiToKana(card.description);
                }
            }
            return card;
        };
        // 添加假名
        app.config.globalProperties.kanjiToKana = function (text = '') {
            if (text === '') return '';
            // 重新排序kanjiKanaMap，最长key的放在最前
            let kanjiKanaReg = new RegExp(Object.keys(kanjiKanaMap).sort((a, b) => b.length - a.length).join('|'), 'g');
            return text.replace(/\[.*?\(.*?\)]/g, s => `|${s}|`).split('|').filter(value => value).map(value => {
                if (!/\[.*?\(.*?\)]/g.test(value)) {
                    return value.replace(kanjiKanaReg, s => kanjiKanaMap[s]);
                }
                return value;
            }).join('');
        };
        // 卡名注音 API
        app.config.globalProperties.kanjiKanaAPI = async function (text = '') {
            if (text === '') return '';
            try {
                let json = await vm.axios.post('/kanjikana/name', JSON.stringify({name: text}));
                let d = json.data;
                if (!d.data) {
                    return null;
                }
                return d.data;
            } catch (e) {
                return null;
            }
        };
        // 效果注音
        app.config.globalProperties.effectKanjiKanaAPI = async function (text = '') {
            if (text === '') return '';
            try {
                let json = await vm.axios.post('/kanjikana/effect', JSON.stringify({name: text}));
                let d = json.data;
                if (!d.data) {
                    return null;
                }
                return d.data;
            } catch (e) {
                return null;
            }
        };
        // 常规文本注音
        app.config.globalProperties.normalKanjiKanaAPI = async function (text = '') {
            if (text === '') return '';
            try {
                let json = await vm.axios.post( '/kanjikana/text', JSON.stringify({name: text}));
                let d = json.data;
                if (!d.data) {
                    return null;
                }
                return d.data;
            } catch (e) {
                return null;
            }
        };

        app.config.globalProperties.setGlobalServer = function (api = '') {
            vm.baseURL = api;
        }
    }
};

// 英文字母全角转半角
function characterToHalf(value) {
    // 全角A：65313，半角A：65
    // 全角Z：65338，半角Z：90
    // 全角a：65345，半角a：97
    // 全角z：65370，半角z：122
    // 全角-半角=65248
    let charList = Array.from(value).map(char => {
        let code = char.charCodeAt();
        if (char === '　') {
            return ' ';
        } else if (char === '﹒') {
            return '·';
        } else if (['＠', '．', '＆', '？', '！'].includes(char) || (code >= 65313 && code <= 65338) || (code >= 65345 && code <= 65370)) {
            return String.fromCharCode(code - 65248);
        }
        return char;
    });
    let text = charList.join('');
    // 日文卡名中的数字转半角
    text = text.replace(/「.*?」/g, s => numberToHalf(s));
    return text;
}

// 数字转半角
function numberToHalf(value) {
    let charList = Array.from(value).map(char => {
        let code = char.charCodeAt();
        if (code >= 65296 && code <= 65305) {
            return String.fromCharCode(code - 65248);
        }
        return char;
    });
    return charList.join('');
}

function parseName(data) {
    // ユベル-Fullwidth wordwrap|ダス・エクストレーム・トラウリヒ・ドラッヘ}}
    let n = data.name;
    if(n.indexOf('Fullwidth wordwrap|') !== -1) {
        n = n.replace('Fullwidth wordwrap|', '').replace('}}', '');
    }
    return n;
    // let name = characterToHalf(data.name);
    // 名字的数字要转半角
    // name = numberToHalf(name);
    // return name;
}

function parseName2(str = '') {
    let name = characterToHalf(str);
    // 名字的数字要转半角
    name = numberToHalf(name);
    // 蛋疼，特殊处理下
    name = name.replaceAll('Ɐ', '∀').replaceAll('´', '’');
    return name;
}

// 解析type
function parseType(data) {
    if (data.type & 0x1000000) {
        return 'pendulum';
    } else if (data.type & 0x4) {
        return 'trap';
    } else if (data.type & 0x2) {
        return 'spell';
    } else if (data.type & 0x1) {
        return 'monster';
    } else {
        return 'monster';
    }
}

// 解析attribute
function parseAttribute(data) {
    if (data.attribute & 0x40) {
        return 'divine';
    } else if (data.attribute & 0x20) {
        return 'dark';
    } else if (data.attribute & 0x10) {
        return 'light';
    } else if (data.attribute & 0x8) {
        return 'wind';
    } else if (data.attribute & 0x4) {
        return 'fire';
    } else if (data.attribute & 0x2) {
        return 'water';
    } else if (data.attribute & 0x1) {
        return 'earth';
    } else {
        return 'light';
    }
}

function parseIcon(data) {
    if (data.type & 0x100000) {
        return 'counter';
    } else if (data.type & 0x80000) {
        return 'filed';
    } else if (data.type & 0x40000) {
        return 'equip';
    } else if (data.type & 0x20000) {
        return 'continuous';
    } else if (data.type & 0x10000) {
        return 'quick-play';
    } else if (data.type & 0x80) {
        return 'ritual';
    } else {
        return '';
    }
}

function parseImage(data) {
    return `https://storage.googleapis.com/ygoprodeck.com/pics_artgame/${data.id}.jpg`;
}

function parseCardType(data) {
    if (data.type & 0x4000000) {
        return 'link';
    } else if (data.type & 0x800000) {
        return 'xyz';
    } else if (data.type & 0x4000) {
        return 'token';
    } else if (data.type & 0x2000) {
        return 'synchro';
    } else if (data.type & 0x80) {
        return 'ritual';
    } else if (data.type & 0x40) {
        return 'fusion';
    } else if (data.type & 0x20) {
        return 'effect';
    } else if (data.type & 0x10) {
        return 'normal';
    } else {
        return 'normal';
    }
}

function parsePendulumType(data) {
    if (parseType(data) === 'pendulum') {
        if (data.type & 0x800000) {
            return 'xyz-pendulum';
        } else if (data.type & 0x2000) {
            return 'synchro-pendulum';
        } else if (data.type & 0x80) {
            return 'ritual-pendulum';
        } else if (data.type & 0x40) {
            return 'fusion-pendulum';
        } else if (data.type & 0x20) {
            return 'effect-pendulum';
        } else if (data.type & 0x10) {
            return 'normal-pendulum';
        } else {
            return 'normal-pendulum';
        }
    } else {
        return 'normal-pendulum';
    }
}

function parseLevelRank(data) {
    let number = parseInt(data.level.toString(16).substr(-1), 16);
    if (number <= 13) {
        let pass = parsePassword(data);
        if (['01686814',  // 奥特玛雅·卓尔金
            '90884403', // 究极幻神 奥特美特尔·比希巴尔金
            '65305468', // 未来No.0 未来皇 霍普
            '43490025', // 未来No.0 未来皇 霍普-未来斩
            '26973555', // 未来No.0 未来龙皇 霍普
            '52653092' // 闪光No.0 希望之异热同心
        ].includes(pass.trim())) {
            return 0;
        }
        return number;
    } else {
        return 1;
    }
}

function parsePendulumScale(data) {
    if (parseType(data) === 'pendulum') {
        let list = data.desc.split(/【.*?】/);
        return parseInt(list?.[0].replace(/[^\d]/g, ''));
    } else {
        return 0;
    }
}

function parsePendulumDescription(data) {
    if (parseType(data) === 'pendulum') {
        let description = characterToHalf(data.desc);
        let list = description.replace(/\r/g, '\n').replace(/\n\n/g, '\n').split(/【.*?】/);
        return list?.[1].replace(/\d+→|\n/g, '').trim();
    } else {
        return '';
    }
}

function parseMonsterType(data, lang) {
    let list = [];
    // 先判断种族
    if (data.race & 0x1000000) {
        list.push('cyberse');
    } else if (data.race & 0x800000) {
        list.push('wyrm');
    } else if (data.race & 0x400000) {
        list.push('creatorGod');
    } else if (data.race & 0x200000) {
        list.push('divineBeast');
    } else if (data.race & 0x100000) {
        list.push('psychic');
    } else if (data.race & 0x80000) {
        list.push('reptile');
    } else if (data.race & 0x40000) {
        list.push('seaSerpent');
    } else if (data.race & 0x20000) {
        list.push('fish');
    } else if (data.race & 0x10000) {
        list.push('dinosaur');
    } else if (data.race & 0x8000) {
        list.push('beastWarrior');
    } else if (data.race & 0x4000) {
        list.push('beast');
    } else if (data.race & 0x2000) {
        list.push('dragon');
    } else if (data.race & 0x1000) {
        list.push('thunder');
    } else if (data.race & 0x800) {
        list.push('insect');
    } else if (data.race & 0x400) {
        list.push('plant');
    } else if (data.race & 0x200) {
        list.push('wingedBeast');
    } else if (data.race & 0x100) {
        list.push('rock');
    } else if (data.race & 0x80) {
        list.push('pyro');
    } else if (data.race & 0x40) {
        list.push('aqua');
    } else if (data.race & 0x20) {
        list.push('machine');
    } else if (data.race & 0x10) {
        list.push('zombie');
    } else if (data.race & 0x8) {
        list.push('fiend');
    } else if (data.race & 0x4) {
        list.push('fairy');
    } else if (data.race & 0x2) {
        list.push('spellcaster');
    } else if (data.race & 0x1) {
        list.push('warrior');
    }
    // 判断其他,顺序根据实卡做调整
    if (data.type & 0x2000000) {
        list.push('spsummon');
    }
    if (data.type & 0x4000000) {
        list.push('link');
    } else if (data.type & 0x1000000) {
        list.push('pendulum');
    } else if (data.type & 0x800000) {
        list.push('xyz');
    } else if (data.type & 0x2000) {
        list.push('synchro');
    } else if (data.type & 0x80) {
        list.push('ritual');
    } else if (data.type & 0x40) {
        list.push('fusion');
    }
    if (data.type & 0x400000) {
        list.push('toon');
    }
    if (data.type & 0x200000) {
        list.push('flip');
    }
    if (data.type & 0x1000) {
        list.push('tuner');
    }
    if (data.type & 0x800) {
        list.push('gemini');
    }
    if (data.type & 0x400) {
        list.push('union');
    }
    if (data.type & 0x200) {
        list.push('spirit');
    }
    if (data.type & 0x20) {
        list.push('effect');
    } else if (data.type & 0x10) {
        list.push('normal');
    }
    list = list.map(value => monsterTypeList[lang][value]).filter(value => value);
    if (['sc', 'en'].includes(lang)) {
        return list.join('/');
    } else if (['tc', 'jp'].includes(lang)) {
        return list.join('／');
    } else {
        return list.join('/');
    }
}

function parseAtk(data) {
    if (data.atk === -2) {
        return -1;
    } else {
        return data.atk;
    }
}

function parseDef(data) {
    if (parseCardType(data) !== 'link') {
        if (data.def === -2) {
            return -1;
        } else {
            return data.def;
        }
    } else {
        return 0;
    }
}

function parseArrowList(data) {
    let arrowList = [];
    if (parseCardType(data) === 'link') {
        if (data.def & 0x100) {
            arrowList.push(2);  // ↗
        }
        if (data.def & 0x80) {
            arrowList.push(1);  // ↑
        }
        if (data.def & 0x40) {
            arrowList.push(8);  // ↖
        }
        if (data.def & 0x20) {
            arrowList.push(3);  // →
        }
        if (data.def & 0x8) {
            arrowList.push(7);  // ←
        }
        if (data.def & 0x4) {
            arrowList.push(4);  // ↘
        }
        if (data.def & 0x2) {
            arrowList.push(5);  // ↓
        }
        if (data.def & 0x1) {
            arrowList.push(6);  // ↙
        }
    }
    return arrowList;
}

function parseDescription(data, lang) {
    let description = characterToHalf(data.desc);
    description = description.replace(/\r/g, '\n').replace(/\n\n/g, '\n');
    if (parseType(data) === 'pendulum') {
        let list = description.split(/【.*?】/);
        description = list?.[2].replace(/\n/g, '').trim();
    } else {
        // 融合、同调、超量、连接、衍生物保留一个换行
        if (['fusion', 'synchro', 'xyz', 'link', 'token'].includes(parseCardType(data))) {
            let lfCount = 0;
            let charList = Array.from(description).map(char => {
                if (char === '\n') {
                    if (lfCount > 0) {
                        return '';
                    }
                    lfCount++;
                }
                return char;
            });
            description = charList.join('');
            if (lang === 'sc' || lang === 'tc') {
                let pass = parsePassword(data);
                if (['01686814', // 奥特玛雅·卓尔金
                    '90884403', // 究极幻神 奥特美特尔·比希巴尔金
                    '58293343', // 暴君爆风龙
                    '22804644', // 死亡病毒龙
                    '84687358', // 反射镜力龙
                    '44373896', // 破坏轮龙
                    '83743222', // 女神的圣弓-矢月
                    '10960419', // 时间魔术锤
                    '19747827', // 真红眼黑龙剑
                    '46354113' // 火箭赫谟炮
                ].includes(pass.trim())) {
                    description = description.replace(/\n/g, '');
                }
            }
        } else {
            // 先去除换行
            if (description.indexOf('●') === -1) {
                // 没有圆点，去除换行
                description = description.replace(/\n/g, '');
            } else {
                if (lang === 'sc' || lang === 'tc') {
                    description = description.replace(/\n/g, '');
                    description = description.replace(/●/g, '\n●');
                } else if (lang !== 'jp') {
                    // 不是日文，去除换行
                    description = description.replace(/\n/g, '');
                }
            }
        }
    }
    return description;
}

function parseFirstLineCompress(data) {
    return ['monster', 'pendulum'].includes(parseType(data)) && ['fusion', 'synchro', 'xyz', 'link'].includes(parseCardType(data));
}

function parsePackage(data) {
    return data.setid.split(',').reverse()[0];
}

function parsePassword(data) {
    let password = data.id.toString();
    if (password.length < 8) {
        password = '0'.repeat(8 - password.length) + password;
    }
    return password;
}
