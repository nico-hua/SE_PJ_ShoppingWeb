<template>
  <div class="body">
    <div class="container1">
      <div class="wrap">
        <div class="nav-bar">
          <!-- 导航 -->
          <el-menu mode="horizontal" :ellipsis="false" router default-active="/">
            <el-menu-item index="/" class="menu-item">首页</el-menu-item>
            <el-menu-item index="/register" v-if="!isLogged" class="menu-item">注册</el-menu-item>
            <el-menu-item index="/login" v-if="!isLogged" class="menu-item">登录</el-menu-item>
            <el-menu-item index="/accountuser" v-if="isLogged" class="menu-item">用户中心</el-menu-item>
            <el-menu-item index="/shoppingcart" v-if="isLogged && !isMerchant && !isAdmin" class="menu-item"
            >购物车
            </el-menu-item>
            <el-menu-item index="/ordersdisplay" v-if="isLogged" class="menu-item"
            >订单
            </el-menu-item>
            <el-menu-item index="/setup" v-if="isLogged && isMerchant" class="menu-item">开店</el-menu-item>
            <el-menu-item index="/backstage" v-if="isLogged && isAdmin" class="menu-item">后台</el-menu-item>
            <el-menu-item index="/shopmanager" v-if="isLogged && isMerchant" class="menu-item">商店管理</el-menu-item>
            <el-menu-item index="/activitypage" class="menu-item">活动页面</el-menu-item>
            <div class="flex-grow"></div>
            <el-sub-menu v-if="isLogged" index="1">
              <template #title>你好，{{ username }}😊</template>
              <el-menu-item>余额 {{ balance }}</el-menu-item>
            </el-sub-menu>
            <el-menu-item index="/login" v-else>请登录😢</el-menu-item>
            <el-menu-item v-if="isLogged" @mousedown="logout">登出</el-menu-item>
          </el-menu>
        </div>
        <el-main>
          <h2>商店列表</h2>
          <!-- <router-link to="/"><img src="../assets/image/img_83238.png" /></router-link> -->
          <!-- <img src="../assets/image/img_83238.png" /> -->
          <!-- <el-slider v-model="size" style="width: 500px" /> -->
          <el-space wrap>
            <el-card v-for="shop in shops" :key="shop.id" class="box-card" style="width: 250px">
              <template #header>
                <div class="card-header">
                  <b>{{ shop.shopName }}</b>
                  <el-button class="button" text type="primary" plain @click="entershop(shop.id)">进入
                  </el-button>
                </div>
              </template>
              {{ shop.category }}
              <br />
              <br />
              <div class="textarea">{{ shop.introduction }}</div>
            </el-card>
          </el-space>
        </el-main>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      shops: [],
      // size: 20
      inf: [],
      // userInf: [],
      username: '',
      balance: '¥',
      character: '',
      isLogged: false,
      isMerchant: false,
      isAdmin: false
    }
  },
  mounted() {
    // alert('mounted start')
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
    //获取商店信息
    this.$axios
      .get('/admin/showAllShopHaveReviewed')
      .then((response) => {
        console.log(response.data)
        this.shops = response.data.data
        console.log(this.shops)
        console.log(this.types)
      })
      .catch((error) => {
        console.log(error)
      })
    //get balance
    this.$axios
      .get('/getAccount', {
        headers: {
          token: `${token}`
        }
      })
      .then((response) => {
        console.log(response.data)
        this.balance = response.data.data.amount + this.balance
        console.log(this.shops)
        console.log(this.types)
      })
      .catch((error) => {
        console.log(error)
      })

    // alert('mounted end')
  },
  methods: {
    logout() {
      localStorage.removeItem('token')
      location.reload() //刷新
    },
    entershop(shopId) {
      this.$router.push({ name: 'shop', params: { id: shopId } })
    }
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

.flex-grow {
  flex-grow: 1;
}

.textarea {
  /* width: 400px;
  height: 300px; */
  overflow-wrap: break-word;
  /* text-align: left; */
  /* text-indent: 2em; */
}
</style>
