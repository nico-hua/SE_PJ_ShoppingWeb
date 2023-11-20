<template>
    <div class="back">
        <el-button type="primary" plain round @click="goBack">
            <el-icon style="vertical-align: middle">
                <Back />
            </el-icon>
            <span style="vertical-align: middle"> 返回 </span>
        </el-button>
    </div>
    <div class="checkout-page">
        <h1>订单确认</h1>
        <!-- 商品信息 -->
        <div class="products">
            <h2>商品信息</h2>
            <table>
                <thead>
                    <tr>
                        <th>商品名称</th>
                        <th>购买件数</th>
                        <th>单价</th>
                        <th>总价</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="(product, index) in selectedRows" :key="index">
                        <td>{{ product.commodityName }}</td>
                        <td>{{ product.commodityNum }}</td>
                        <td>{{ product.price }}</td>
                        <td>{{ product.totalPrice }}</td>
                    </tr>
                </tbody>
            </table>
        </div>

        <!-- 收货信息 -->
        <div class="shipping">
            <h2>收货信息</h2>
            <label for="address">选择收货地址：</label>
            <select id="address" name="address" v-model="selectedAddress">
                <option v-for="(address, index) in addresses" :key="index" :value="address">{{ address.address }} {{
                    address.name }} {{ address.phone }}</option>
            </select>
        </div>

        <!-- 结算金额及优惠明细 -->
        <div class="summary">
            <h2>结算金额及优惠明细</h2>
            <table>
                <tbody>
                    <tr>
                        <td>原商品总价：</td>
                        <td>{{ totalPrice }}</td>
                    </tr>
                    <tr v-for="(discount, index) in discounts" :key="index">
                        <td>折扣 {{index + 1}} :</td>
                        <td>{{ discount.reducedPrice }}</td>
                    </tr>
                    <tr>
                        <td>总金额：</td>
                        <td>{{ finalPrice }}</td>
                    </tr>
                </tbody>
            </table>
        </div>

        <!-- 提交订单 -->
        <el-button @click="submitOrder">提交订单</el-button>
    </div>
</template>
  
<script>
export default {
    data() {
        return {
            addresses: [],
            selectedAddress: '',
            newAddress: '',
            selectedCoupon: '',
            discounts: [],
            selectedRows: []
        };
    },
    mounted() {
        this.selectedRows = JSON.parse(localStorage.getItem('selectedRows'));
        const token = localStorage.getItem('token')
        //获取地址信息
        this.$axios
            .get(
                '/user/getShippingAddress',
                {
                    headers: {
                        token: `${token}`
                    }
                }
            )
            .then((response) => {
                this.addresses = response.data.data
            })
            .catch((error) => {
                console.log(error)
            })
        //获取折扣信息
            let subSelectedRows = [];
            this.selectedRows.forEach((product) => {
                let newProduct = {
                    commodityNum: product.commodityNum, 
                    commodityId: product.commodityId,
                };
                subSelectedRows.push(newProduct);
            });
        this.$axios
            .post(
                '/user/getTotalReducedMoney', subSelectedRows,
                {
                    headers: {
                        token: `${token}`
                    }
                }
            )
            .then((response) => {
                this.discounts = response.data.data
            })
            .catch((error) => {
                console.log(error)
            })
    },
    computed: {
        totalPrice() {
            return this.selectedRows.reduce((total, product) => total + product.totalPrice, 0);
        },
        finalPrice() {
            return this.totalPrice - this.discounts.reduce((total, discount) => total + discount.reducedPrice, 0);
        }
    },
    methods: {
        goBack() {
            this.$router.go(-1);
        },
        submitOrder() {
            // TODO: 提交订单
            const token = localStorage.getItem('token')
            let subSelectedRows = [];
            this.selectedRows.forEach((product) => {
                let newProduct = {
                    commodityName: product.commodityName, 
                    commodityNum: product.commodityNum, 
                    commodityPrice: product.price, 
                    amountSum: product.totalPrice,
                    shopName: product.commodityShopName,
                    shopId: product.shopId,
                    commodityId: product.commodityId,
                };
                subSelectedRows.push(newProduct);
            });
            subSelectedRows.forEach((product) => {
                product.address = this.selectedAddress.address;
                product.name = this.selectedAddress.name;
                product.phone = this.selectedAddress.phone;
            });
            this.$axios
                .post(
                    '/user/createOrders', subSelectedRows,
                    {
                        headers: {
                            token: `${token}`
                        }
                    }
                )
                .then((response) => {
                    console.log(response.data)
                    if (response.data.state == 200) {
                        this.$message.success("下单成功")
                        this.$router.push({ name: 'ordersdisplay' })//直接跳到订单管理中的待支付页面
                    } else {
                        this.$message.error(response.data.message)
                    }
                })
                .catch((error) => {
                    console.log(error)
                    this.$message.error(error)
                })
        }
    }
}
</script>

<style scoped>
.back {
    float: left;
}

.checkout-page {
    max-width: 800px;
    margin: 0 auto;
    font-family: Arial, sans-serif;
}

h1 {
    text-align: center;
    font-size: 36px;
    margin-bottom: 30px;
}

h2 {
    font-size: 24px;
    margin-top: 40px;
}

table {
    border-collapse: collapse;
    width: 100%;
    margin-top: 20px;
}

th,
td {
    text-align: left;
    padding: 10px;
    border: 1px solid #ddd;
}

th {
    background-color: #f2f2f2;
}

label {
    display: block;
    margin-top: 20px;
}

select {
    width: 100%;
    padding: 10px;
    margin-top: 10px;
    margin-bottom: 20px;
    border-radius: 5px;
    border: none;
    box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.2);
}

button {
    padding: 10px 20px;
    background-color: #007bff;
    color: #fff;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    margin-left: 20px;
}

button:hover {
    background-color: #0069d9;
}

.summary {
    margin-top: 40px;
    margin-bottom: 40px;
}

.summary table {
    margin-top: 20px;
}

.summary td:nth-child(1) {
    font-weight: bold;
    padding-right: 20px;
}

.summary td:nth-child(2) {
    font-weight: bold;
}

button[type="submit"] {
    margin-top: 40px;
}

.title {
    font-weight: bold;
    font-size: 18px;
    margin-left: 20px;
}

/* 新增样式 */
.products {
    margin-top: 40px;
}

.products table {
    margin-top: 20px;
}

.shipping {
    margin-top: 40px;
    border-top: 1px solid #ddd;
    padding-top: 40px;
}

.shipping button {
    margin-left: 10px;
}

.shownewaddressform {
    margin-top: 20px;
    display: flex;
    align-items: center;
}

.shownewaddressform label {
    margin-right: 10px;
}

.shownewaddressform input[type="text"] {
    width: 100%;
    padding: 10px;
    border-radius: 5px;
    border: none;
    box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.2);
    margin-right: 10px;
}

.shownewaddressform button {
    margin-right: 10px;
}

.summary td:nth-child(1),
.summary td:nth-child(2) {
    text-align: right;
    vertical-align: middle;
}

.summary td:nth-child(2) {
    font-size: 24px;
}
</style>