<template>
  <div class="body">
    <div class="container1">
      <div class="wrap">
        <div class="nav-bar">
          <el-menu mode="horizontal" :ellipsis="false" router>
            <el-menu-item index="/">首页</el-menu-item>
            <el-menu-item index="/accountuser">个⼈信息</el-menu-item>
            <el-menu-item index="/accountmerchant" v-if="isMerchant || isAdmin">商家信息</el-menu-item>
            <el-menu-item index="/accounttopup">充值</el-menu-item>
            <el-menu-item index="/userwaterbill" v-if="isUser">转账流⽔</el-menu-item>
            <el-menu-item index="/merchantwaterbill" v-if="isMerchant">转账流⽔</el-menu-item>
            <el-menu-item index="/profitwaterbill" v-if="isAdmin">转账流⽔</el-menu-item>
            <el-menu-item index="/accountmodify" v-if="isUser || isMerchant">个⼈信息修改</el-menu-item>
          </el-menu>
        </div>
      </div>
    </div>
    <div class="container2">
      <div class="wrap">
        <div class="top-up-box">
          <h1>余额：{{ balance }}</h1>
          <el-button style="margin-top: 10px; width: 80px; height: 50px" @click="topup">充值</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      balance: '¥',
      topupValue: '',
      ownerid: '',
      merchantid: '',
      adminid: '',
      message: '',
      type: '',
      msg: '',
      api: '',
      isUser: false,
      isMerchant: false,
      isAdmin: false,
      state: 0
    }
  },
  methods: {
    topup() {
      this.$prompt('请输入金额', '充值', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValidator(value) {
          let pattern = /^0*?[1-9]\d*$/;
          if (value[0] == 0 || !(pattern.test(value)))
            return false;
          else return true;
        },
        inputErrorMessage: '金额不正确'
      }).then(({value}) => {
        this.topupValue = value;
        this.topupConfirm(value);
        setTimeout(function () {
          location.reload();
        }, 1000);
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消输入'
        });
      });
    },
    typeselect(state, value) {
      console.log(state)
      if (state == 200) {
        this.type = 'success'
        this.msg = '已充值: ' + value + '¥';
      } else {
        this.type = 'danger'
        this.msg = '充值失败';
      }
      this.$message({
        type: this.type,
        message: this.msg
      })
    },
    topupConfirm(value) {
      const token = localStorage.getItem('token')
      this.$axios
          .post(this.api,
              {
                ownerId: this.ownerid,
                amount: value
              },
              {
                headers: {
                  token: `${token}`
                }
              })
          .then((response) => {
            this.state = response.data.state
            this.typeselect(this.state, this.topupValue);
          })
          .catch((error) => {
            console.log(error)
          })
    }

  },
  mounted() {
    const token = localStorage.getItem('token')
    this.$axios
        .get('/getData', {
          headers: {
            token: `${token}`
          }
        })
        .then((response) => {
          this.ownerid = response.data.data.id;
          this.message = response.data.message;
          if (this.message == '1') {
            this.api = '/user/rechargeAccount';
            this.isUser = true;
          } else if (this.message == '2') {
            this.api = '/merchant/rechargeAccount';
            this.isMerchant = true;
          } else if (this.message == '3') {
            this.api = '/admin/rechargeAccount';
            this.isAdmin = true;
          }
        })
        .catch((error) => {
          console.log(error)
        })
    this.$axios
        .get('/getAccount', {
          headers: {
            token: `${token}`
          }
        })
        .then((response) => {
          this.balance = response.data.data.amount + this.balance;
        })
        .catch((error) => {
          console.log(error)
        })
  }
}
</script>

<style scoped>
.wrap {
  width: 80vw;
  margin: auto;
  padding-left: 25px;
  padding-right: 25px;
}

.header {
  width: 100%;
  height: 50px;
  margin-bottom: 8px;
  background-color: #f2f6fc;
  -webkit-box-shadow: 0px 0px 10px 5px rgba(0, 0, 0, 0.15);
  -moz-box-shadow: 0px 0px 10px 5px rgba(0, 0, 0, 0.15);
  box-shadow: 0px 0px 10px 5px rgba(0, 0, 0, 0.15);
}

.top-up{
  position: relative;
  transform: translateX(-50%);
  left:50%;
}
</style>