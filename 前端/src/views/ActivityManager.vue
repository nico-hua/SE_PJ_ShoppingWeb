<template>
    <el-container>
        <el-header>
            <el-menu mode="horizontal" :ellipsis="false" router default-active="/activitymanager">
                <el-menu-item index="/">首页</el-menu-item>
                <el-menu-item index="/backstage">开店申请</el-menu-item>
                <el-menu-item index="/newgoodshandler">商品上架申请</el-menu-item>
                <el-menu-item index="/modifygoodshandler">商品修改申请</el-menu-item>
                <el-menu-item index="/accountshopdelete">商店删除请求</el-menu-item>
                <el-menu-item index="/activitymanager">活动管理</el-menu-item>
            </el-menu>
        </el-header>
        <el-main class="main">
            <div class="content">
                <h1>活动管理页面</h1>
                <el-form :label-position="'right'" label-width="80px" :model="formLabelAlign">
                    <el-form-item label="持续时间/日">
                        <el-input v-model="holdingDays" class="normal"></el-input>
                    </el-form-item>
                    <el-form-item label="活动资金">
                        <el-input v-model="funds" class="normal"></el-input>
                    </el-form-item>
                    <el-form-item label="商品类别">
                        <el-checkbox-group v-model="types">
                            <el-checkbox label="百货" name="type"></el-checkbox>
                            <el-checkbox label="图书" name="type"></el-checkbox>
                            <el-checkbox label="服装" name="type"></el-checkbox>
                            <el-checkbox label="食品" name="type"></el-checkbox>
                            <el-checkbox label="家电" name="type"></el-checkbox>
                            <el-checkbox label="电脑" name="type"></el-checkbox>
                            <el-checkbox label="手机" name="type"></el-checkbox>
                            <el-checkbox label="数码" name="type"></el-checkbox>
                            <el-checkbox label="二手" name="type"></el-checkbox>
                            <el-checkbox label="奢侈" name="type"></el-checkbox>
                            <el-checkbox label="其他" name="type"></el-checkbox>
                        </el-checkbox-group>
                    </el-form-item>
                    <el-form-item label="满减优惠">
                        <b>&nbsp满&nbsp</b>
                        <el-input v-model="X" class="xy"></el-input>
                        <b>&nbsp减&nbsp</b>
                        <el-input v-model="Y" class="xy"></el-input>
                    </el-form-item>
                    <el-form-item label="注册资金">
                        <el-input v-model="fundsLimit" class="normal" placeholder="商店注册资⾦必须⼤于"></el-input>
                    </el-form-item>
                    <el-form-item label="月销售额">
                        <el-input v-model="monthlySalesMoneyLimit" class="normal" placeholder="商店近⼀个⽉销量⼤于"></el-input>
                    </el-form-item>
                    <el-form-item label="月销售量">
                        <el-input v-model="monthlySalesCountLimit" class="normal" placeholder="商店近⼀个⽉销售额⼤于"></el-input>
                    </el-form-item>
                </el-form>
                <el-button @click="handleSubmit" type="primary">提交</el-button>
            </div>

            <div class="content">
                <h1>活动申请列表</h1>
                <el-space wrap>
                    <el-card v-for="good in goods" :key="good.goodId" class="box-card" style="width: 250px">
                        <template #header>
                            <div class="card-header">
                                <div class="inf">
                                    <b>名称:</b> {{ good.commodityName }} <br />
                                    <b>价格:</b> {{ good.price }}元 <br />
                                    <b>简介:</b> {{ good.introduction }}
                                </div>
                            </div>
                        </template>
                        <div class="card-footer">
                            <el-button class="button" @click="allow(good.id)" type="success" plain>批准</el-button>
                            <el-button class="button" @click="disallow(good.id)" type="danger" plain>驳回</el-button>
                        </div>
                    </el-card>
                </el-space>
            </div>

        </el-main>
    </el-container>
</template>

<script>
export default {
    data() {
        return {
            holdingDays: '',
            funds: '',
            commodityCategories: '',
            X: '',//满x减y
            Y: '',
            fundsLimit: '',//商店注册资金限制
            monthlySalesMoneyLimit: '',//商店月销售额限制
            monthlySalesCountLimit: '',//商店月销售量限制
            types: [],
            goods: [],
        };
    },
    mounted() {
        const token = localStorage.getItem('token')
        this.$axios
            .get('/admin/findAllCommoditiesWaitingToBeReviewed', {
                headers: {
                    token: `${token}`
                }
            })

            .then((response) => {
                console.log(response.data)
                this.goods = response.data.data
            })
            .catch((error) => {
                console.log(error)
            })
    },
    methods: {
        allow(goodId) {
            const token = localStorage.getItem('token')
            this.$axios
                .post(
                    '/admin/allowInActivity',
                    { id: goodId },
                    {
                        headers: {
                            token: `${token}`,
                            'Content-Type': 'application/json'
                        }
                    }
                )
                .then((response) => {
                    console.log(response.data)
                    this.response = response.data
                    console.log(this.response)
                    if (this.response.state == 200) {
                        this.$message.success('批准成功')
                        location.reload() //刷新
                    } else {
                        this.$message.error(this.response.message)
                    }
                })
                .catch((error) => {
                    console.log(error)
                    this.$message.error(error)
                })
        },
        disallow(goodId) {
            const token = localStorage.getItem('token')
            this.$axios
                .post(
                    '/admin/refuseInActivity',
                    { id: goodId,},
                    {
                        headers: { token: `${token}`, 'Content-Type': 'application/json' }
                    }
                )
                .then((response) => {
                    console.log(response.data)
                    this.response = response.data
                    if (this.response.state == 200) {
                        this.$message.success('驳回成功')
                        location.reload()
                    } else {
                        this.$message.error(this.response.message)
                    }
                })
                .catch((error) => {
                    console.log(error)
                    this.$message.error(error)
                })
        },
        handleSubmit() {
            const token = localStorage.getItem('token')
            this.$axios
                .post(
                    '/admin/holdActivity',
                    {
                        holdingDays: this.holdingDays,
                        funds: this.funds,
                        commodityCategories: this.types.join(','),
                        x: this.X,
                        y: this.Y,
                        fundsLimit: this.fundsLimit,
                        monthlySalesMoneyLimit: this.monthlySalesMoneyLimit,
                        monthlySalesCountLimit: this.monthlySalesCountLimit,
                        // types: 
                    },
                    {
                        headers: {
                            token: `${token}`,
                            'Content-Type': 'application/json'
                        }
                    }
                )
                .then((response) => {
                    console.log(response.data)
                    if (response.data.state == 200) {
                        this.$message.success('活动开启成功')
                    } else {
                        this.$message.error(response.data.message)
                    }
                })
                .catch((error) => {
                    console.log(error)
                    this.$message.error(error)
                })
        },
    }
}
</script>

<style scoped>
.back {
    position: absolute;
    margin: 20px;
}

.main {
    background-color: #f4f4f4;
    height: 100%;
}

.content {
    width: 70%;
    margin: auto;
}

.normal {
    width: 50%;
}

.xy {
    width: 15%;
}
</style>