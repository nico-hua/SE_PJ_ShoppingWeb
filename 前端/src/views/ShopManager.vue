<template>
  <div class="common-layout">
    <el-container>
      <el-header>
        <el-menu mode="horizontal" :ellipsis="false" router default-active="/shopmanager">
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/shopmanager">商店管理</el-menu-item>
          <el-menu-item index="/addgoods">新增商品</el-menu-item>
          <el-menu-item index="/recordview">上架申请记录</el-menu-item>
          <el-menu-item index="/fixrecordview">修改申请记录</el-menu-item>
          <!-- <el-menu-item index="/activitylist">活动列表</el-menu-item> -->
        </el-menu>
      </el-header>
      <el-main class="main">
        <h2>{{ shop.shopName }}</h2>
        <div class="select">
          <label for="activity">选择活动：</label>
          <select id="activity" name="activity" v-model="selectedActivity">
            <option v-for="(activity, index) in activities" :key="index" :value="activity">持续天数:{{ activity.holdingDays }}
              参加种类: {{ activity.commodityCategories }} 满{{ activity.x }}减{{ activity.y }} 资金池:{{ activity.funds }} 注册资金>{{
                activity.fundsLimit }} 月销量>{{ activity.monthlySalesCountLimit }} 月销售额>{{ activity.monthlySalesMoneyLimit}}</option>
          </select>
        </div>
        <el-space wrap>
          <el-card v-for="good in goods" :key="good.id" class="box-card" style="width: 250px">
            <template #header>
              <div class="card-header">
                <b>{{ good.commodityName }}</b>
              </div>
            </template>
            <div class="picture">
              <img :src="good.imageUrls[0]" :style="{ maxHeight: '200px', maxWidth: '200px' }" />
            </div>
            {{ good.price }} 元 &nbsp {{ good.categoryName }}
            <br />
            <el-button class="button" text type="primary" plain @click="modify(good.id)">修改</el-button>
            <el-button class="button" text type="warning" plain @click="remove(good.id, good.imageUrl)">下架</el-button>
            <el-button class="button" text type="success" plain @click="joinin(good.id)">参加活动</el-button>
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
      // size: 20
      inf: [],
      character: '',
      response: [],
      id: '', //商户id
      shop: [],
      activities: [],
      selectedActivity: '',
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
        console.log(response.data)
        this.inf = response.data
        this.character = response.data.message
        this.id = response.data.data.id
        if (this.character != 2) {
          this.$router.push({ name: 'home' })
        }

        //通过商户id获取商店信息
        return this.$axios.post(
          '/merchant/showShopDataByMerchantId',
          { merchantId: this.id },
          {
            headers: {
              token: `${token}`
            }
          }
        )
      })
      .then((response) => {
        console.log(response.data)
        this.shop = response.data.data

        //获取商店的商品信息
        return this.$axios.post('/merchant/showCommodities', { id: this.shop.id })
      })
      .then((response) => {
        console.log(response.data)
        this.goods = response.data.data
      })
      .catch((error) => {
        console.log(error)
      })

    //获取活动信息
    this.$axios
      .get(
        '/merchant/getActivitiesNow',
        {
          headers: {
            token: `${token}`
          }
        }
      )
      .then((response) => {
        this.activities = response.data.data
      })
      .catch((error) => {
        console.log(error)
      })
  },
  methods: {
    joinin(goodId) {
      const token = localStorage.getItem('token')
      this.$axios
        .post(
          '/merchant/getInActivity',
          { commodityId: goodId, activityId: this.selectedActivity.id },

          {
            headers: { token: `${token}` }
          }
        )
        .then((response) => {
          console.log(response.data)
          this.response = response.data
          // console.log(this.response)
          if (this.response.state == 200) {
            this.$message.success('成功参加')
          } else {
            this.$message.error(this.response.message)
          }
        })
        .catch((error) => {
          console.log(error)
          this.$message.error(error)
        })
    },
    modify(goodId) {
      this.$router.push({ name: 'modifygoods', params: { id: goodId } })
    },
    remove(goodId, imageUrl) {
      console.log(`下架商品 ${goodId}`)
      const token = localStorage.getItem('token')
      this.$axios
        .post(
          '/merchant/deleteCommodity',
          { id: goodId, shopId: this.shop.id, imageUrl: imageUrl },

          {
            headers: { token: `${token}` }
          }
        )
        .then((response) => {
          console.log(response.data)
          this.response = response.data
          // console.log(this.response)
          if (this.response.state == 200) {
            this.$message.success('下架成功')
            location.reload()
          } else {
            this.$message.error(this.response.message)
          }
        })
        .catch((error) => {
          console.log(error)
          this.$message.error('下架请求失败')
        })
    }
  }
}
</script>


<style scoped>
.select {
  margin-bottom: 20px;
}

.main {
  width: 70%;
  margin: auto;
}
</style>
