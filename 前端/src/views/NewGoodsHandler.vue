<template>
  <el-container>
    <el-header><el-menu mode="horizontal" :ellipsis="false" router default-active="/newgoodshandler">
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/backstage">开店申请</el-menu-item>
        <el-menu-item index="/newgoodshandler">商品上架申请</el-menu-item>
        <el-menu-item index="/modifygoodshandler">商品修改申请</el-menu-item>
        <el-menu-item index="/accountshopdelete">商店删除请求</el-menu-item>
        <el-menu-item index="/activitymanager">活动管理</el-menu-item>

      </el-menu></el-header>
    <el-main class="main">
      <h1>上架申请列表</h1>
      <el-space wrap>
        <el-card v-for="good in goods" :key="good.goodId" class="box-card" style="width: 250px">
          <template #header>
            <div class="card-header">
              <div class="inf">
                <b>名称:</b> {{ good.commodityName }} <br />
                <b>价格:</b> {{ good.price }}元 <br />
                <b>简介:</b> {{ good.introduction }}
              </div>
            </div>
          </template>
          <div class="picture">
            <!-- <img :src="'data:image/png;base64,' + good.imageString[0]"
              :style="{ maxHeight: '200px', maxWidth: '200px' }" /> -->
              <img :src="good.imageUrls[0]" :style="{ maxHeight: '200px', maxWidth: '200px' }" /> 
          </div>
          <div class="card-footer">
            <el-button class="button" @click="approve(good.id)" type="success" plain>批准</el-button>
            <el-button class="button" @click="disapprove(good.id, good.imageUrl)" type="danger" plain>驳回</el-button>
          </div>
        </el-card>
      </el-space>
    </el-main>
  </el-container>
</template>

<script>
export default {
  data() {
    return {
      goods: [],
      response: [],
      character: '',
      imageUrl: ''
    }
  },
  mounted() {
    const token = localStorage.getItem('token')
    this.$axios
      .get('/admin/showAllCommodityToBeReviewed', {
        headers: {
          token: `${token}`
        }
      })

      .then((response) => {
        console.log(response.data)
        this.character = response.data.message
        if (this.character != 3) {
          this.$router.push({ name: 'home' })
        }
        this.goods = response.data.data
        console.log(this.goods)
      })
      .catch((error) => {
        console.log(error)
      })
  },
  methods: {
    approve(goodId) {
      console.log(`批准商品 ${goodId}`)
      const token = localStorage.getItem('token')
      this.$axios
        .post(
          '/admin/agreeCommodity',
          { id: goodId },
          {
            headers: {
              token: `${token}`,
              'Content-Type': 'application/json'
            }
          }
        )
        .then((response) => {
          console.log(response.data)
          this.response = response.data
          console.log(this.response)
          if (this.response.state == 200) {
            this.$message.success('批准成功')
            location.reload() //刷新
          } else {
            this.$message.error(this.response.message)
          }
        })
        .catch((error) => {
          console.log(error)
          this.$message.error('批准请求失败')
        })
    },
    disapprove(goodId, imageUrl) {
      console.log(`驳回商品 ${goodId}`)
      const token = localStorage.getItem('token')
      this.$axios
        .post(
          '/admin/refuseCommodity',
          { id: goodId, imageUrl: imageUrl },
          {
            headers: { token: `${token}`, 'Content-Type': 'application/json' }
          }
        )
        .then((response) => {
          console.log(response.data)
          this.response = response.data
          if (this.response.state == 200) {
            this.$message.success('驳回成功')
            location.reload()
          } else {
            this.$message.error(this.response.message)
          }
        })
        .catch((error) => {
          console.log(error)
          this.$message.error('驳回请求失败')
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
</style>
