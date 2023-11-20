<template>
    <div class="back">
        <el-button type="primary" plain round @click="goBack">
            <el-icon style="vertical-align: middle">
                <Back />
            </el-icon>
            <span style="vertical-align: middle"> 返回 </span>
        </el-button>
    </div>
    <el-collapse v-model="activeName" accordion class="content">
        <el-collapse-item v-for="(activity, index) in activities" :key="activity.id" :title="formatActivityTitle(activity)"
            :name="index" @click="loadGoodsForActivity(activity.id)">

            <el-space wrap>
                <el-card v-for="good in goods" :key="good.id" class="box-card" style="width: 250px">
                    <template #header>
                        <div class="card-header">
                            <b>{{ good.commodityName }}</b>
                            <el-button class="button" text type="primary" plain @click="entergoods(good.id)">进入详情页
                            </el-button>
                        </div>
                    </template>
                    <div class="picture">
                        <img :src="good.imageUrl" :style="{ maxHeight: '200px', maxWidth: '200px' }" />
                    </div>
                    {{ good.price }} 元 &nbsp {{ good.categoryName }}
                    <div class="textarea">{{ good.introduction }}</div>
                </el-card>
            </el-space>
            <!-- {{ activity.description }} -->
        </el-collapse-item>
    </el-collapse>
</template>

<script>
export default {
    data() {
        return {
            activeName: '1',
            activities: [],
            goods: [],
        };
    },
    mounted() {
        const token = localStorage.getItem('token')
        //获取活动信息
        this.$axios
            .get(
                '/merchant/getActivitiesNow',
                {
                    headers: {
                        token: `${token}`
                    }
                }
            )
            .then((response) => {
                this.activities = response.data.data
            })
            .catch((error) => {
                console.log(error)
            })
    },
    methods: {
        goBack() {
            this.$router.push({ name: 'home' })
        },
        formatActivityTitle(activity) {
            return `持续天数:${activity.holdingDays} 参加种类:${activity.commodityCategories} 满${activity.x}减${activity.y} 资金池:${activity.funds} 注册资金>${activity.fundsLimit} 月销量>${activity.monthlySalesCountLimit} 月销售额>${activity.monthlySalesMoneyLimit}`;
        },
        loadGoodsForActivity(activityId) {
            const token = localStorage.getItem('token')
            // 清空商品列表
            this.goods = [];
            // 加载指定活动下的商品列表
            this.$axios
                .post("/user/showCommoditiesInOneActivity",
                    { id: activityId },
                    {
                        headers: { token: `${token}` }
                    }
                )
                .then((response) => {
                    this.goods = response.data.data
                })
                .catch((error) => {
                    console.log(error);
                });
        },
        entergoods(goodsId) {
            this.$router.push({ name: 'goods', params: { id: goodsId } })
        },
    }
}
</script>

<style scoped>
.back {
    margin: 20px;
    position: absolute;
}

.content {
    width: 70%;
    margin: auto;
}
</style>