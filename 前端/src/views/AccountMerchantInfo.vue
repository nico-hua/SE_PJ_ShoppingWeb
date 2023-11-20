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
    <el-header style="margin-top: 10px; font-size: 30px">商家信息</el-header>
    <div class="wrapper">
      <el-main>
        <div class="info-container">
          <ul style="list-style-type: none;">
            <li v-for="(item, index) in info">{{ index }}: {{ item }}</li>
          </ul>
        </div>
        <el-button style="background-color: indianred; color: white" @click="del">删除商店</el-button>
      </el-main>
    </div>
  </div>
</template>

<script>
export default {
  name: "AccountMerchantInfo",
  data() {
    return {
      info: {
        shopname: "",
        category: "",
        idCard: "",
        introduction: "",
        address: "",
        funds: "",
        setupdate: "",
      },
      isUser: false,
      isMerchant: false,
      isAdmin: false
    }
  },
  methods: {
    del() {
      this.$confirm('此操作将永久删除该商店, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.delshop()
          this.$message({
            type: 'success',
            message: '删除请求成功!'
          });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    delshop() {
      const token = localStorage.getItem('token')
      this.$axios
          .get('/merchant/closeShopApply',
              {
                headers: {
                  'Content-Type': 'application/json',
                  token: `${token}`
                }
              }
          )
          .then((response) => {
            this.state = response.data.state;
          })
          .catch((error) => {
            console.log(error)
            // alert('注册请求失败')
          })
    }
  },
  mounted() {
    const token = localStorage.getItem('token')
    this.$axios
        .get('/merchant/getShopData', {
          headers: {
            token: `${token}`
          }
        })
        .then((response) => {
          this.info.shopname = response.data.data.shopName;
          this.info.category = response.data.data.category;
          this.info.introduction = response.data.data.introduction;
          this.info.address = response.data.data.address;
          this.info.idCard = response.data.data.idCard;
          this.info.funds = response.data.data.funds;
          this.info.setupdate = response.data.data.registerdate;
          console.log(response.data)
        })
        .catch((error) => {
          console.log(error)
        })
    this.$axios
        .get('/getData', {
          headers: {
            token: `${token}`
          }
        })
        .then((response) => {
          if (response.data.message == 1) {
            this.isUser = true;
          } else if (response.data.message == 2) {
            this.isMerchant = true;
          } else {
            this.isAdmin = true;
          }
          console.log(response.data)
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
</style>