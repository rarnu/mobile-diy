import {createStore} from 'vuex';

export default createStore({
    state: {
        collapse: false
    },
    mutations: {
        setCollapse(state) {
            state.collapse = !state.collapse;
        }
    },
    actions: {}
});
