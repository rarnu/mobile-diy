import axios from "axios";

export default {
    getCardNameList(list, language, callback) {
        let cardSet = Array.from(new Set(list));
        let reqSet = cardSet.map(e => axios.get(`/yugioh/card/${e}?lang=${language}`));
        axios.all(reqSet).then(axios.spread((...result) => {
            let arr = [];
            result.forEach(e => {
                arr.push({id: `${e.data.data.id}`, name: e.data.data.name, exported: false, count: this.getCardCount(list, `${e.data.data.id}`)});
            });
            callback(arr);
        }));
    },

    getCardCount(list, id) {
        let count = 0;
        for (let i = 0; i < list.length; i++) {
            if (parseInt(list[i]) === parseInt(id)) {
                count++;
            }
        }
        return count;
    }
}