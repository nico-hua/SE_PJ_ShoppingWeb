<template>
  <div class="body">
    <div class="container1">
      <div class="wrap">
        <div class="nav-bar">
          <el-menu mode="horizontal" :ellipsis="false" router default-active="/merchantorderconfirm">
            <el-menu-item index="/">首页</el-menu-item>
            <el-menu-item index="/ordersdisplay">我的订单</el-menu-item>
            <el-menu-item index="/merchantorderconfirm" v-if="isMerchant">商家订单消息</el-menu-item>
          </el-menu>
        </div>
      </div>
    </div>
    <div class="container2">
      <div class="wrap">
        <div class="side">
          <div class="menu">
            <el-menu
                class="el-menu-vertical"
                default-active="1">
              <el-menu-item index="1" @click="toShip">买家已⽀付订单</el-menu-item>
              <el-menu-item index="2" @click="toRefund">退款请求订单</el-menu-item>
            </el-menu>
          </div>
        </div>
        <div class="main">
          <div class="header">
            <p>{{ statusTitle }}</p>
          </div>
          <div class="orders">
            <ul>
              <li class="items" v-for="(item, index) in orders">
                <div class="col1">
                  <div id="image"></div>
                </div>
                <div class="col2">
                  <div class="descriptions">
                    <p id="shop-name">店铺名称：{{ item.shopName }}</p>
                    <p id="goods-name">商品名称：{{ item.commodityName }}</p>
                    <p id="order-id">订单编号：{{ item.id }}</p>
                    <p id="time">购买时间：{{ item.purchaseTime }}</p>
                    <p id="address">收货地址：{{ item.address }}</p>
                  </div>
                </div>
                <div class="col3">
                  <div class="descriptions">
                    <p id="goods-price">商品单价：{{ item.commodityPrice }}</p>
                    <p id="quantity">商品数：{{ item.commodityNum }}</p>
                    <h3 id="total">订单总价：{{ item.amountSum }}</h3>
                  </div>
                </div>
                <div class="col4">
                  <div class="descriptions">
                    <el-form-item>
                      <el-button v-if="button1" @click="buttonHandler1(item.commodityPrice, item.id)"
                                 style="width: 60px">{{
                          buttonLabel1
                        }}
                      </el-button>
                    </el-form-item>
                    <el-form-item>
                      <el-button v-if="button2" @click="buttonHandler2(item.commodityPrice, item.id)"
                                 style="width: 60px">{{
                          buttonLabel2
                        }}
                      </el-button>
                    </el-form-item>
                  </div>
                </div>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "MerchantOrderConfirm",
  data() {
    return {
      statusTitle: '买家已⽀付订单',
      button1: true,
      button2: false,
      buttonLabel1: '发货',
      buttonLabel2: '',
      buttonCall1: 'ship',
      buttonCall2: '',
      orders: [],
      defaultActive: 1,
      isLogged: false,
      isUser: false,
      isMerchant: false,
      isAdmin: false,
      currentUrl: '/merchant/getShopToDeliveryOrders'
    }
  },
  methods: {
    toShip() {
      this.statusTitle = '待⽀付订单';
      this.button1 = true;
      this.button2 = false;
      this.buttonLabel1 = '发货';
      this.buttonCall1 = 'ship';
      this.currentUrl = '/merchant/getShopToDeliveryOrders';

      const token = localStorage.getItem('token')
      this.$axios
          .get('/merchant/getShopToDeliveryOrders', {
            headers: {
              token: `${token}`
            }
          })
          .then((response) => {
            console.log(response.data);
            this.orders = response.data.data;
          })
          .catch((error) => {
            console.log(error)
          })
    },
    toRefund() {
      this.statusTitle = '退款请求订单';
      this.button1 = true;
      this.button2 = false;
      this.buttonLabel1 = '同意退款';
      //this.buttonLabel2 = '驳回退款';
      this.buttonCall1 = 'agree';
      this.buttonCall2 = 'disagree';
      this.currentUrl = '/merchant/getShopRefundOrders';

      const token = localStorage.getItem('token')
      this.$axios
          .get('/merchant/getShopRefundOrders', {
            headers: {
              token: `${token}`
            }
          })
          .then((response) => {
            console.log(response.data);
            this.orders = response.data.data;
          })
          .catch((error) => {
            console.log(error)
          })
    },
    buttonHandler1(value, id) {
      if (this.buttonCall1 == 'ship') {
        this.ship(id);
      } else if (this.buttonCall1 == 'agree') {
        this.agreeRefund(id);
      } else if (this.buttonCall1 == 'disagree') {
        this.disagreeRefund(id);
      }
    },
    buttonHandler2(value, id) {
      if (this.buttonCall2 == 'ship') {
        this.ship(id);
      } else if (this.buttonCall2 == 'agree') {
        this.agreeRefund(id);
      } else if (this.buttonCall2 == 'disagree') {
        this.disagreeRefund(id);
      }
    },
    ship(id) {
      const token = localStorage.getItem('token')
      this.$confirm('确定要发货吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios
            .post('/merchant/shopDeliveryOrders', {
                  id: id
                }
                , {
                  headers: {
                    token: `${token}`
                  }
                })
            .then((response) => {
              console.log(response.data);
              this.$axios
                  .get(this.currentUrl, {
                    headers: {
                      token: `${token}`
                    }
                  })
                  .then((response) => {
                    console.log(response.data);
                    this.orders = response.data.data;
                  })
                  .catch((error) => {
                    console.log(error)
                  })
              this.$message({
                type: 'success',
                message: '已发货!'
              });
            })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消操作'
        });
      });
    },
    agreeRefund(id) {
      const token = localStorage.getItem('token')
      this.$confirm('确定同意退款吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios
            .post('/merchant/agreeRefund', {
                  id: id
                }
                , {
                  headers: {
                    token: `${token}`
                  }
                })
            .then((response) => {
              console.log(response.data);
              this.$axios
                  .get(this.currentUrl, {
                    headers: {
                      token: `${token}`
                    }
                  })
                  .then((response) => {
                    console.log(response.data);
                    this.orders = response.data.data;
                  })
                  .catch((error) => {
                    console.log(error)
                  })
              this.$message({
                type: 'success',
                message: '同意退款!'
              });
            })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消操作'
        });
      });
    },
    disagreeRefund(id) {
      const token = localStorage.getItem('token')
      this.$confirm('确定驳回退款吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios
            .post('/', {
                  id: id
                }
                , {
                  headers: {
                    token: `${token}`
                  }
                })
            .then((response) => {
              console.log(response.data);
              this.$axios
                  .get(this.currentUrl, {
                    headers: {
                      token: `${token}`
                    }
                  })
                  .then((response) => {
                    console.log(response.data);
                    this.orders = response.data.data;
                  })
                  .catch((error) => {
                    console.log(error)
                  })
              this.$message({
                type: 'success',
                message: '已驳回!'
              });
            })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消操作'
        });
      });
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
          // this.userInf = this.inf.data
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

    this.$axios
        .get('/merchant/getShopToDeliveryOrders', {
          headers: {
            token: `${token}`
          }
        })
        .then((response) => {
          console.log(response.data)
          this.orders = response.data.data;
        })
        .catch((error) => {
          console.log(error)
        })
  }
}
</script>

<style scoped>
.body {
  font-family: 'Microsoft YaHei';
  box-sizing: border-box;
}

.wrap {
  width: 80vw;
  margin: auto;
  padding-left: 25px;
  padding-right: 25px;
}

.header {
  width: 90%;
  height: 50px;
  margin: 50px auto 8px auto;
  background-color: #f2f6fc;
  -webkit-box-shadow: 0px 0px 10px 5px rgba(0, 0, 0, 0.15);
  -moz-box-shadow: 0px 0px 10px 5px rgba(0, 0, 0, 0.15);
  box-shadow: 0px 0px 10px 5px rgba(0, 0, 0, 0.15);
}

.header p {
  position: relative;
  transform: translateY(-50%);
  left: 2%;
  top: 50%;
}

.container2 {
  width: 100%;
  height: 90vh;
}

.side {
  width: 20%;
  height: 90vh;
  float: left;
}

.side .menu {
  position: relative;
  transform: translateY(-50%);
  top: 50%;
}

.main {
  width: 80%;
  height: 90vh;
  float: left;
}

.orders {
  /*width: 96%;*/
}

li {
  list-style-type: none;
}

.items {
  width: 90%;
  height: 300px;
  margin: 0 auto;
  -webkit-box-shadow: 0px 0px 10px 5px rgba(0, 0, 0, 0.15);
  -moz-box-shadow: 0px 0px 10px 5px rgba(0, 0, 0, 0.15);
  box-shadow: 0px 0px 10px 5px rgba(0, 0, 0, 0.15);
}

.main .col1 {
  width: 40%;
  height: 100%;
  float: left;
  align-content: center;
}

#image {
  width: 70%;
  height: 70%;
  background-color: #3c00a0;
  position: relative;
  transform: translate(-50%, -50%);
  top: 50%;
  left: 50%;
}

.main .col2 {
  width: 25%;
  height: 100%;
  float: left;
}

.descriptions {
  position: relative;
  transform: translateY(-50%);
  top: 50%;
}

.main .col3 {
  width: 25%;
  height: 100%;
  float: left;
}

.main .col4 {
  width: 10%;
  height: 100%;
  float: left
}
</style>