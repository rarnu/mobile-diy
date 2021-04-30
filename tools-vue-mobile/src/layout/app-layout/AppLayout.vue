<template>
    <div class="app-layout-container">
        <el-container>
            <el-main>
                <router-view></router-view>
            </el-main>
        </el-container>
    </div>
</template>

<script>
    import {mapState} from 'vuex';

    export default {
        name: 'AppLayout',
        components: {
        },
        mounted() {
            document.onkeydown = this.disableKey;
            document.onkeypress = this.disableKey;
            addEventListener('resize', this.updateScrollbar);
        },
        unmounted() {
            removeEventListener('resize', this.updateScrollbar);
        },
        methods: {
            updateScrollbar() {
                // this.$refs['left-scrollbar'].update();
            },
            // 屏蔽某些键
            disableKey(e) {
                if (e.key === 'Backspace' && !['text', 'textarea', 'password'].includes(e.target.type)) {
                    return false;
                }
            }
        },
        computed: {
            ...mapState(['collapse']),
            leftStyle() {
                return {
                    marginLeft: this.collapse ? '-200px' : '0'
                };
            }
        }
    };
</script>

<style lang="scss" scoped>
    .app-layout-container {
        .el-header {
            border-bottom: 1px solid $border-color;
        }

        .el-container {
            height: calc(100vh);

            .left-aside {
                border-right: 1px solid $border-color;
                transition: all 0.3s;
            }

            .el-main {
                padding: 0;
            }
        }
    }
</style>
