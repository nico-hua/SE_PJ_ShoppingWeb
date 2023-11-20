<template>
  <el-container>
    <el-header>
      <el-menu mode="horizontal" :ellipsis="false" router default-active="/accountshopdelete">
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/backstage">开店申请</el-menu-item>
        <el-menu-item index="/newgoodshandler">商品上架申请</el-menu-item>
        <el-menu-item index="/modifygoodshandler">商品修改申请</el-menu-item>
        <el-menu-item index="/accountshopdelete">商店删除请求</el-menu-item>
        <el-menu-item index="/activitymanager">活动管理</el-menu-item>

      </el-menu>
    </el-header>
    <el-main class="main">
      <h1>商店删除请求列表</h1>
      <el-space wrap>
        <el-card v-for="shop in shops" class="box-card" style="width: 250px">
          <template #header>
            <div class="card-header">
              <div class="inf">
                <b>店名:</b> {{ shop.shopName }} <br />
                <b>商品类别:</b> {{ shop.category }} <br />
                <b>身份证号:</b> {{ shop.idCard }} <br /><b>简介:</b> {{ shop.introduction }}
                <br />
                <b>备案地址:</b> {{ shop.address }}<br />
                <b>注册资金:</b> {{ shop.funds }} <br />
                <b>注册时间:</b> {{ shop.registerdate }} <br />
              </div>
            </div>
          </template>
          <div class="card-footer">
            <el-button class="button" type="success" @click="approve(shop.id)" :disabled="shop.stateHint != 200">批准
            </el-button>
            <el-button class="button" type="danger" @click="disapprove(shop.id)" plain>驳回</el-button>
          </div>
          <div class="err_msg">{{ shop.messageHint }}</div>
        </el-card>
      </el-space>
    </el-main>
  </el-container>
</template>

<script>
export default {
  name: "AccountShopDeletePremission",
  data() {
    return {
      shops: [],
      users: [],
      message: '',
      character: '',
      shopId: '',
      state: '',
      isUser: false,
      isMerchant: false,
      isAdmin: false
    }
  },
  methods: {
    approve(shopId) {
      console.log(`批准店铺 ${shopId}`)
      const token = localStorage.getItem('token')
      this.$axios
        .post(
          '/admin/agreeClose',
          { id: shopId },
          {
            headers: {
              token: `${token}`,
              'Content-Type': 'application/json'
            }
          }
        )
        .then((response) => {
          console.log(response.data)
          this.users = response.data
          console.log(this.users)
          if (this.users.state == 200) {
            // alert('批准成功')
            // this.message = '批准成功'
            location.reload() //刷新
          } else {
            // alert(this.users.message)
            this.message = this.users.message
          }
        })
        .catch((error) => {
          console.log(error)
          // alert('批准请求失败')
          this.message = '批准请求失败'
        })
    },
    disapprove(shopId) {
      console.log(`驳回店铺 ${shopId}`)
      const token = localStorage.getItem('token')
      this.$axios
        .post(
          '/admin/refuseClose',
          { id: shopId },
          {
            headers: { token: `${token}`, 'Content-Type': 'application/json' }
          }
        )
        .then((response) => {
          console.log(response.data)
          this.users = response.data
          console.log(this.users)
          if (this.users.state == 200) {
            // alert('驳回成功')
            // this.message = '驳回成功'
            location.reload()
          } else {
            this.message = this.users.message
            // alert(this.users.message)
          }
          setTimeout(function () {
            location.reload();
          }, 1000);
        })
        .catch((error) => {
          console.log(error)
          // alert('驳回请求失败')
          this.message = '驳回请求失败'
        })
    }
  },
  mounted() {
    const token = localStorage.getItem('token')
    this.$axios
      .get('/admin/showAllCloseShopToBeReviewed', {
        headers: {
          token: `${token}`
        }
      })
      .then((response) => {
        console.log(response.data)
        this.shops = response.data.data;
        console.log(this.shops)
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
      })
      .catch((error) => {
        console.log(error)
      })
  }
}
</script>

<style scoped>
.main {
  width: 70%;
  margin: auto;
}
.inf {
  text-align: left;
}
</style>