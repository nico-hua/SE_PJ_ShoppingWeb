<template>
    <div class="back">
            <el-button type="primary" plain round @click="goBack">
                <el-icon style="vertical-align: middle;">
                    <Back />
                </el-icon>
                <span style="vertical-align: middle;"> 返回 </span>
            </el-button>
        </div>
    <div class="container">
        <div v-for="(image, index) in images" :key="index">
            <img :src="image" :style="{ maxHeight: '400px', maxWidth: '400px' }"/>
        </div>
        <div class="goods">
            <h2>商品信息</h2>
            <table>
                <tbody>
                    <tr>
                        <td>商品名称</td>
                        <td>{{ goodsInf.commodityName }}</td>
                    </tr>
                    <tr>
                        <td>商品种类</td>
                        <td>{{ goodsInf.categoryName }}</td>
                    </tr>
                    <tr>
                        <td>商品简介</td>
                        <td>{{ goodsInf.introduction }}</td>
                    </tr>
                    <tr>
                        <td>商品单价</td>
                        <td>{{ goodsInf.price }} 元</td>
                    </tr>
                    <tr>
                        <td>总价</td>
                        <td>{{ num * goodsInf.price }} 元</td>
                    </tr>
                    <tr>
                        <td>购买件数</td>
                        <td><el-input-number v-model="num" @change="handleChange" :min="0" :max="10000"
                                label="描述文字"></el-input-number>
                            <el-button class="button" v-if="isLogged && !isMerchant && !isAdmin" text type="primary" plain
                                @click="addtocart(goodsInf.id, num)">加入购物车</el-button>
                            <el-button class="button" v-if="isLogged && !isMerchant && !isAdmin" text type="success" plain
                                @click="placeOrder()">直接购买</el-button>
                        </td>
                    </tr>

                </tbody>
            </table>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            goodsInf: [],
            images: [],
            num: 1,
            inf: [],
            username: '',
            character: '',
            isLogged: false,
            isMerchant: false,
            isAdmin: false,
        }
    },
    mounted() {
        const token = localStorage.getItem('token')
        //获取用户信息
        this.$axios
            .get('/getData', {
                headers: {
                    token: `${token}`
                }
            })
            .then((response) => {
                console.log(response.data)
                this.inf = response.data
                console.log(this.inf)
                if (this.inf.state == 200) {
                    this.username = this.inf.data.name
                    console.log(this.username)
                    this.isLogged = true
                    this.character = this.inf.message
                    console.log(this.character)
                    if (this.character == 2) {
                        this.isMerchant = true
                    } else {
                        this.isMerchant = false
                    }
                    if (this.character == 3) {
                        this.isAdmin = true
                    } else {
                        this.isAdmin = false
                    }
                } else {
                    this.isLogged = false
                }
            })
            .catch((error) => {
                console.log(error)
            })
        //获取商品信息
        this.$axios
            .post(
                '/merchant/showCommodityDataByCommodityId',
                { id: this.$route.params.id },
                {
                    headers: {
                        token: `${token}`
                    }
                }
            )
            .then((response) => {
                console.log(response.data)
                this.goodsInf = response.data.data
                // alert(this.goodsInf.price)
                this.images = this.goodsInf.imageUrl.split(',');
            })
            .catch((error) => {
                console.log(error)
            })
    },
    methods: {
        placeOrder() {
            let selectedRows = [{
                commodityName: this.goodsInf.commodityName,
                introduction: this.goodsInf.introduction,
                price: this.goodsInf.price,
                businessState: 1,
                commodityCategoryName: this.goodsInf.categoryName,
                commodityId: this.goodsInf.id,
                commodityNum: this.num,
                // commodityShopName: "老王杂货店",                
                // id: 
            }];
            localStorage.removeItem('selectedRows') // 先删除已有的
            localStorage.setItem('selectedRows', JSON.stringify(selectedRows))
            // 跳转到下单页面
            this.$router.push({ name: 'orderpage' })
        },
        addtocart(goodId, num) {
            const token = localStorage.getItem('token')
            this.$axios
                .post(
                    '/user/addToShoppingCart',
                    {
                        shopId: this.goodsInf.shopId,
                        userId: this.inf.data.id,
                        commodityId: goodId,
                        commodityNum: num
                    },
                    {
                        headers: {
                            token: `${token}`
                        }
                    }
                )
                .then((response) => {
                    console.log(response.data)
                    if (response.data.state == 200) {
                        this.$message.success('加入购物车成功')
                    } else {
                        this.$message.error(response.data.message)
                    }
                })
                .catch((error) => {
                    console.log(error)
                })
        },
        goBack() {
            this.$router.go(-1);
        },
    }
}
</script>

<style scoped>
.container {
    max-width: 1200px;
    margin: auto;
    padding: 20px;
}

.back {
    margin: 20px;
    position: absolute;
}

.goods h2 {
    margin-top: 30px;
    margin-bottom: 20px;
}

table {
    width: 100%;
    border-collapse: collapse;
}

td {
    padding: 10px;
    vertical-align: top;
    border: 1px solid #ccc;
}

img {
    width: 100%;
    height: auto;
    margin-bottom: 20px;
}
</style>