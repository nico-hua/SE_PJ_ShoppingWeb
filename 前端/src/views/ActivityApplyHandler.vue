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
        };
    },
    mounted() {
        const token = localStorage.getItem('token')
        this.$axios
            .get('/admin/showAllCommodityToBeReviewed', {
                headers: {
                    token: `${token}`
                }
            })

            .then((response) => {
                console.log(response.data)
                this.character = response.data.message
                if (this.character != 3) {
                    this.$router.push({ name: 'home' })
                }
                this.goods = response.data.data
                console.log(this.goods)
            })
            .catch((error) => {
                console.log(error)
            })
    },
    methods: {
        handleSubmit() {
            const token = localStorage.getItem('token')
            this.$axios
                .post(
                    '/admin/holdActivity',
                    {
                        holdingDays: this.holdingDays,
                        funds: this.funds,
                        commodityCategories: this.commodityCategories,
                        x: this.X,
                        y: this.Y,
                        fundsLimit: this.fundsLimit,
                        monthlySalesMoneyLimit: this.monthlySalesMoneyLimit,
                        monthlySalesCountLimit: this.monthlySalesCountLimit,
                        types: this.types.join(','),
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