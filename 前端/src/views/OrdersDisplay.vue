<template>
  <div class="body">
    <div class="container1">
      <div class="wrap">
        <div class="nav-bar">
          <el-menu mode="horizontal" :ellipsis="false" router default-active="/ordersdisplay">
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
                :default-active="defaultActive">
              <el-menu-item index="1" @click="toBePaid">待⽀付</el-menu-item>
              <el-menu-item index="2" @click="paid">待发货</el-menu-item>
              <el-menu-item index="3" @click="shipping">待收货</el-menu-item>
              <el-menu-item index="4" @click="complete">已完成</el-menu-item>
              <el-menu-item index="5" @click="cancelled">已撤销</el-menu-item>
              <el-menu-item index="6" @click="refundProcessing">待退款</el-menu-item>
              <el-menu-item index="7" @click="refunded">已退款</el-menu-item>
            </el-menu>
          </div>
        </div>
        <div class="main">
          <div class="header">
            <p>{{ statusTitle }}</p>
          </div>
          <div class="orders">
            <!--            <el-table style="width: 100%" stripe show-summary max-height="1000">-->
            <!--              <el-table-column prop="initiatorName" label="订单编号"></el-table-column>-->
            <!--              <el-table-column prop="receiverName" label="店铺名称"></el-table-column>-->
            <!--              <el-table-column prop="amount" label="商品名称"></el-table-column>-->
            <!--              <el-table-column prop="tradeRecord" label="商品单价"></el-table-column>-->
            <!--              <el-table-column prop="tradeTime" label="订单总价"></el-table-column>-->
            <!--              <el-table-column prop="tradeTime" label="购买时间"></el-table-column>-->
            <!--              <el-table-column prop="tradeTime" label="收货地址"></el-table-column>-->
            <!--              <el-table-column prop="tradeTime" label="⽤户名"></el-table-column>-->
            <!--            </el-table>-->
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
                      <el-button v-if="button1" @click="buttonHandler1(item.amountSum, item.id)"
                                 style="width: 60px">{{
                          buttonLabel1
                        }}
                      </el-button>
                    </el-form-item>
                    <el-form-item>
                      <el-button v-if="button2" @click="buttonHandler2(item.amountSum, item.id)"
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
  name: "OrdersDisplay",
  data() {
    return {
      statusTitle: '待⽀付订单',
      button1: true,
      button2: true,
      buttonLabel1: '支付',
      buttonLabel2: '撤销',
      buttonCall1: 'pay',
      buttonCall2: 'cancel',
      orders: [],
      defaultActive: 1,
      isLogged: false,
      isUser: false,
      isMerchant: false,
      isAdmin: false,
      currentUrl: '/user/getToPayOrders'
    }
  },
  methods: {
    toBePaid() {
      this.statusTitle = '待⽀付订单';
      this.button1 = true;
      this.button2 = true;
      this.buttonLabel1 = '支付';
      this.buttonLabel2 = '撤销';
      this.buttonCall1 = 'pay';
      this.buttonCall2 = 'cancel';
      this.currentUrl = '/user/getToPayOrders';
      this.defaultActive = 1;


      const token = localStorage.getItem('token')
      this.$axios
          .get('/user/getToPayOrders', {
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
    paid() {
      this.statusTitle = '待发货订单';
      this.button1 = true;
      this.button2 = false;
      this.buttonLabel1 = '退款';
      this.buttonCall1 = 'refund';
      this.currentUrl = '/user/getToDeliveryOrders';
      this.defaultActive = 2;

      const token = localStorage.getItem('token')
      this.$axios
          .get('/user/getToDeliveryOrders', {
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
    shipping() {
      this.statusTitle = '待收货订单';
      this.button1 = true;
      this.button2 = true;
      this.buttonLabel1 = '确认收货';
      this.buttonLabel2 = '退款';
      this.buttonCall1 = 'confirm';
      this.buttonCall2 = 'refund';
      this.currentUrl = '/user/getToFinishOrders';
      this.defaultActive = 3;

      const token = localStorage.getItem('token')
      this.$axios
          .get('/user/getToFinishOrders', {
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
    complete() {
      this.statusTitle = '已完成订单';
      this.button1 = true;
      this.button2 = false;
      this.buttonLabel1 = '删除';
      this.buttonCall1 = 'delete';
      this.currentUrl = '/user/getHaveFinishOrders';
      this.defaultActive = 4;

      const token = localStorage.getItem('token')
      this.$axios
          .get('/user/getHaveFinishOrders', {
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
    cancelled() {
      this.statusTitle = '已撤销订单';
      this.button1 = true;
      this.button2 = false;
      this.buttonLabel1 = '删除';
      this.buttonCall1 = 'delete';
      this.currentUrl = '/user/getHaveWithdrawOrders';
      this.defaultActive = 5;

      const token = localStorage.getItem('token')
      this.$axios
          .get('/user/getHaveWithdrawOrders', {
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
    refundProcessing() {
      this.statusTitle = '待退款订单';
      this.button1 = false;
      this.button2 = false;
      this.currentUrl = '/user/getToRefundOrders';
      this.defaultActive = 6;

      const token = localStorage.getItem('token')
      this.$axios
          .get('/user/getToRefundOrders', {
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
    refunded() {
      this.statusTitle = '已退款订单';
      this.button1 = true;
      this.button2 = false;
      this.buttonLabel1 = '删除';
      this.buttonCall1 = 'delete';
      this.currentUrl = '/user/getHaveRefundOrders';
      this.defaultActive = 7;

      const token = localStorage.getItem('token')
      this.$axios
          .get('/user/getHaveRefundOrders', {
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
      if (this.buttonCall1 == 'pay') {
        this.pay(value, id);
      } else if (this.buttonCall1 == 'cancel') {
        this.cancel(id);
      } else if (this.buttonCall1 == 'confirm') {
        this.confirm(id);
      } else if (this.buttonCall1 == 'refund') {
        this.refund(id);
      } else if (this.buttonCall1 == 'delete') {
        this.delete(id);
      }
    },
    buttonHandler2(value, id) {
      if (this.buttonCall2 == 'pay') {
        this.pay(value, id);
      } else if (this.buttonCall2 == 'cancel') {
        this.cancel(id);
      } else if (this.buttonCall2 == 'confirm') {
        this.confirm(id);
      } else if (this.buttonCall2 == 'refund') {
        this.refund(id);
      } else if (this.buttonCall2 == 'delete') {
        this.delete(id);
      }
    },
    pay(value, id) {
      const token = localStorage.getItem('token')
      this.$confirm('支付' + value + '元', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios
            .post('/user/payOrders', {
                  id: id
                }
                , {
                  headers: {
                    token: `${token}`
                  }
                })
            .then((response) => {
              console.log(response.data);
              if (response.data.state != 200) {
                this.$message({
                  type: 'danger',
                  message: '余额不足，支付失败!'
                });
              } else {
                this.$message({
                  type: 'success',
                  message: '支付成功!'
                });
              }
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
            })
            .catch((error) => {
              console.log(error)
            })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消操作'
        });
      });
    },
    cancel(id) {
      const token = localStorage.getItem('token')
      this.$confirm('确定要撤销吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios
            .post('/user/withdrawOrders', {
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
                message: '撤销成功!'
              });
            })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消操作'
        });
      });

    },
    refund(id) {
      const token = localStorage.getItem('token')
      this.$confirm('确定要退款吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios
            .post('/user/requestRefund', {
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
                message: '已发送退款请求!'
              });
            })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消操作'
        });
      });
    },
    confirm(id) {
      const token = localStorage.getItem('token')
      this.$confirm('确认收货吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios
            .post('/user/confirmFinish', {
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
                message: '已收货'
              });
            })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消操作'
        });
      });
    },
    delete(id) {
      const token = localStorage.getItem('token')
      this.$axios
          .post('/user/deleteOrders', {
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
          })
    }
  },
  mounted() {
    const token = localStorage.getItem('token')
    this.$axios
        .get('/user/getToPayOrders', {
          headers: {
            token: `${token}`
          }
        })
        .then((response) => {
          this.orders = response.data.data;
          console.log(response.data);
        })
        .catch((error) => {
          console.log(error)
        })

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