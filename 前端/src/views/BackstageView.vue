<template>
  <div class="center">
    <el-container>
      <el-header>
        <el-menu mode="horizontal" :ellipsis="false" router default-active="/backstage">
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/backstage">开店申请</el-menu-item>
          <el-menu-item index="/newgoodshandler">商品上架申请</el-menu-item>
          <el-menu-item index="/modifygoodshandler">商品修改申请</el-menu-item>
          <el-menu-item index="/accountshopdelete">商店删除请求</el-menu-item>
          <el-menu-item index="/activitymanager">活动管理</el-menu-item>

        </el-menu>
      </el-header>
      <el-main class="main">
        <h1>开店请求列表</h1>
        <el-space wrap>
          <el-card v-for="shop in shops" :key="shop.id" class="box-card" style="width: 250px">
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
              <el-button class="button" @click="approve(shop.id)" type="success" plain>批准</el-button>
              <el-button class="button" @click="disapprove(shop.id)" type="danger" plain>驳回</el-button>
            </div>
          </el-card>
        </el-space>
        <div class="err_msg">{{ message }}</div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
export default {
  data() {
    return {
      shops: [],
      users: [],
      message: '',
      character: ''
    }
  },
  mounted() {
    const token = localStorage.getItem('token')
    this.$axios
      .get('/admin/showAllOpenShopToBeReviewed', {
        headers: {
          token: `${token}`
        }
      })

      .then((response) => {
        // console.log(response.data)
        this.character = response.data.message
        if (this.character != 3) {
          this.$router.push({ name: 'home' })
        }
        this.shops = response.data.data
        console.log(this.shops)
      })
      .catch((error) => {
        console.log(error)
      })
  },
  methods: {
    approve(shopId) {
      console.log(`批准店铺 ${shopId}`)
      const token = localStorage.getItem('token')
      this.$axios
        .post(
          '/admin/agreeOpen',
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
          '/admin/refuseOpen',
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
        })
        .catch((error) => {
          console.log(error)
          // alert('驳回请求失败')
          this.message = '驳回请求失败'
        })
    }
  }
}
</script>

<style scoped>
.main {
  width: 70%;
  margin: auto;
}

.err_msg {
  color: red;
}

.center {
  display: flex;
  justify-content: center;
  /* align-items: center; */
  /* height: 100%; */
}

.inf {
  text-align: left;
}
</style>
