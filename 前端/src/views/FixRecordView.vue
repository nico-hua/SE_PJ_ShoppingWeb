<template>
  <el-container>
    <el-header
      ><div class="content">
        <el-menu mode="horizontal" :ellipsis="false" router default-active="/fixrecordview">
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/shopmanager">商店管理</el-menu-item>
          <el-menu-item index="/addgoods">新增商品</el-menu-item>
          <el-menu-item index="/recordview">上架申请记录</el-menu-item>
          <el-menu-item index="/fixrecordview">修改申请记录</el-menu-item>
        </el-menu>
      </div></el-header
    >
    <el-main
      ><el-table :data="tableData">
        <el-table-column prop="fixTime" label="日期"> </el-table-column>
        <el-table-column prop="commodityName" label="商品名字"> </el-table-column>
        <el-table-column prop="state" label="状态" :formatter="formatter"> </el-table-column>
      </el-table>
      <div style="margin-top: 20px">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="num"
          @current-change="handlePageChange"
        >
        </el-pagination></div
    ></el-main>
  </el-container>
</template>

<script>
export default {
  data() {
    return {
      tableData: [],
      id: '',
      inf: [],
      character: '',
      num: 1
    }
  },
  methods: {
    handlePageChange(pageNum) {
      const token = localStorage.getItem('token')
      this.$axios
        .post(
          '/merchant/selectFixRecords',
          { shopId: this.id, pageNum: pageNum },
          {
            headers: {
              token: `${token}`
            }
          }
        )
        .then((response) => {
          console.log(response.data)
          this.tableData = response.data.data
          console.log(this.tableData)
        })
        .catch((error) => {
          console.log(error)
        })
    },
    formatter(row, column) {
      const state = row.state
      if (state === 0) {
        return '您已提交申请'
      } else if (state === 1) {
        return '管理员审核通过'
      } else if (state === -1) {
        return '管理员审核驳回'
      }
      return ''
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
        this.character = response.data.message
        this.id = this.inf.data.id
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

        //通过商店id获取记录总数
        this.$axios
          .post(
            '/merchant/getTotalNumOfFixRecord',
            { id: this.id },
            {
              headers: {
                token: `${token}`
              }
            }
          )
          .then((response) => {
            console.log(response.data)
            const total = response.data.data
            this.num = total
          })
          .catch((error) => {
            console.log(error)
          })

        //获取上架记录信息
        this.$axios
          .post(
            '/merchant/selectFixRecords',
            { shopId: this.shop.id, pageNum: 1 },
            {
              headers: {
                token: `${token}`
              }
            }
          )
          .then((response) => {
            console.log(response.data)
            this.tableData = response.data.data
          })
          .catch((error) => {
            console.log(error)
          })
      })
      .catch((error) => {
        console.log(error)
      })
  }
}
</script>
