<template>
  <div class="common-layout">
    <el-container>
      <el-header>
        <div class="content">
          <el-menu mode="horizontal" :ellipsis="false" router>
            <el-menu-item index="/">首页</el-menu-item>
            <el-menu-item index="/register" v-if="!isLogged">注册</el-menu-item>
            <el-menu-item index="/login" v-if="!isLogged">登录</el-menu-item>
            <el-menu-item index="/accountuser" v-if="isLogged">用户中心</el-menu-item>
            <el-menu-item index="/shoppingcart" v-if="isLogged && !isMerchant && !isAdmin">购物车</el-menu-item>
            <el-menu-item index="/setup" v-if="isLogged && isMerchant">开店</el-menu-item>
            <el-menu-item index="/backstage" v-if="isLogged && isAdmin">后台</el-menu-item>
            <el-menu-item index="/shopmanager" v-if="isLogged && isMerchant">商店管理</el-menu-item>
            <div class="flex-grow"></div>
            <el-menu-item v-if="isLogged">你好，{{ username }}😊</el-menu-item>
            <el-menu-item index="/login" v-else>请登录😢</el-menu-item>
            <el-menu-item v-if="isLogged" @mousedown="logout">登出</el-menu-item>
          </el-menu>
        </div>
      </el-header>
      <el-main class="main">
        <h2>{{ shop.shopName }}</h2>
        <el-space wrap>
          <el-card v-for="good in goods" :key="good.id" class="box-card" style="width: 250px">
            <template #header>
              <div class="card-header">
                <b>{{ good.commodityName }}</b>
                <el-button class="button" text type="primary" plain @click="entergoods(good.id)">进入详情页
                </el-button>
              </div>
            </template>
            <div class="picture">
              <img :src="good.imageUrls[0]" :style="{ maxHeight: '200px', maxWidth: '200px' }" /> 
            </div>
            {{ good.price }} 元 &nbsp  {{ good.categoryName }}
            <br />
            <el-input-number v-model="good.businessState" v-if="isLogged && !isMerchant && !isAdmin"
              @change="handleChange" size="small" :min="0" :max="10000" label="描述文字"></el-input-number>

            <el-button class="button" v-if="isLogged && !isMerchant && !isAdmin" text type="primary" plain
              @click="addtocart(good.id, good.businessState)">加入购物车</el-button>

            <br />
            <div class="textarea">{{ good.introduction }}</div>
          </el-card>
        </el-space>
      </el-main>
    </el-container>
  </div>
</template>

<script>
export default {
  data() {
    return {
      goods: [],
      inf: [],
      username: '',
      character: '',
      isLogged: false,
      isMerchant: false,
      isAdmin: false,
      shop: []
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
      .post('/merchant/showShopData', { id: this.$route.params.id })
      .then((response) => {
        console.log(response.data)
        this.shop = response.data.data
      })
      .catch((error) => {
        console.log(error)
      })

    //获取商店的商品信息
    this.$axios
      .post('/merchant/showCommodities', { id: this.$route.params.id })
      .then((response) => {
        console.log(response.data)
        this.goods = response.data.data
        // console.log(this.goods)
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
    addtocart(goodId, num) {
      const token = localStorage.getItem('token')
      this.$axios
        .post(
          '/user/addToShoppingCart',
          {
            shopId: this.$route.params.id,
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
    entergoods(goodsId) {
      this.$router.push({ name: 'goods', params: { id: goodsId } })
    }
  }
}
</script>

<style scoped>
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

.main {
  width: 70%;
  margin: auto;
}
</style>
