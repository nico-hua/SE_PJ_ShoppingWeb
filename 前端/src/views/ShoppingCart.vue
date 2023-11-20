<template>
  <el-container>
    <el-header>
      <el-menu mode="horizontal" :ellipsis="false" router default-active="/shoppingcart">
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/accountuser">用户中心</el-menu-item>
        <el-menu-item index="/shoppingcart">购物车</el-menu-item>
      </el-menu>
    </el-header>
    <el-main>
      <el-table ref="multipleTable" :data="tableDataWithTotalPrice" :summary-method="getPriceSummaries" show-summary
        @selection-change="selectionHandler">
        <el-table-column prop="image" type="selection"></el-table-column>
        <!-- <el-table-column label="image"> </el-table-column> -->

        <el-table-column prop="commodityName" label="商品名字"></el-table-column>

        <el-table-column label="数量">
          <template v-slot="{ row }">
            <el-input-number v-model="row.commodityNum" @change="handleChange(row)" :min="0"
              :max="10000"></el-input-number>
            <!-- :min="1" -->
          </template>
        </el-table-column>

        <el-table-column prop="price" label="单价"></el-table-column>
        <el-table-column prop="totalPrice" label="价格"></el-table-column>
      </el-table>
      <div style="margin-top: 20px">
        <!-- <el-button @click="toggleSelection([tableData[1], tableData[2]])"
          >切换第二、第三行的选中状态</el-button
        > -->
        <el-button @click="toggleSelection()">取消选择</el-button>
        <el-button type="danger" @click="deleteSelectedRows">删除选中项</el-button>
        <el-button @click="placeOrder()" type="primary">下单</el-button>

      </div>
    </el-main>
  </el-container>
</template>

<script>
export default {
  data() {
    return {
      tableData: [],
      inf: [],
      character: '',
      response: [],
      multipletable: [],
      id: '' //用户id
    }
  },
  computed: {
    tableDataWithTotalPrice() {
      return this.tableData.map((item) => {
        if (item.businessState === -1) {
          return {
            ...item,
            totalPrice: '已下架'
          }
        } else {
          return {
            ...item,
            totalPrice: item.commodityNum * item.price
          }
        }
      })
    }
  },
  methods: {
    handleChange(row) {

      row.totalPrice = row.price * row.commodityNum // 重新计算totalprice

      if (row.commodityNum === 0) {
        const ids = [row.id]
      const token = localStorage.getItem('token')
      const formData = new FormData()
      formData.append('ids', JSON.stringify(ids))
        this.$axios
          .post('/user/deleteShoppingCart', formData, { headers: { token: `${token}` } })
          .then((response) => {
            if (response.data.state == 200) {
              this.$message.success('删除成功')
            } else {
              this.$message.error('删除失败')
            }

            // 重新加载数据
            this.$axios
              .post('/user/showShoppingCart', { userId: this.id }, { headers: { token: `${token}` } })
              .then((response) => {
                this.tableData = response.data.data
              })
              .catch((error) => {
                console.log(error)
              })
          })
          .catch((error) => {
            console.log(error)
            this.$message.error(error)
          })
      }

    },
    selectionHandler(val) {
      this.multipletable = val
    },
    toggleSelection(rows) {
      if (rows) {
        rows.forEach((row) => {
          this.$refs.multipleTable.toggleRowSelection(row)
        })
      } else {
        this.$refs.multipleTable.clearSelection()
      }
    },
    // 计算 price 列的总和
    getPriceSummaries(param) {
      const { columns, data } = param
      const sums = []
      columns.forEach((column, index) => {
        if (index === 3) {
          sums[index] = '总计'
          return
        }
        const selectedRows = this.multipletable
        if (selectedRows.length === 0) {
          if (column.property === 'totalPrice') {
            const values = data.map((item) => Number(item[column.property]))
            const sum = values.reduce((prev, curr) => {
              const value = Number(curr)
              if (!isNaN(value)) {
                return prev + curr
              } else {
                return prev
              }
            }, 0)
            sums[index] = sum.toFixed(2)
          } else {
            sums[index] = ''
          }
        } else {
          if (column.property === 'totalPrice') {
            const values = selectedRows.map((row) => Number(row[column.property]))
            const sum = values.reduce((prev, curr) => {
              const value = Number(curr)
              if (!isNaN(value)) {
                return prev + curr
              } else {
                return prev
              }
            }, 0)
            sums[index] = sum.toFixed(2)
          } else {
            sums[index] = ''
          }
        }

      })
      return sums
    },
    deleteSelectedRows() {
      const selectedRows = this.multipletable
      if (selectedRows.length === 0) {
        this.$message.warning('请先选择要删除的行')
        return
      }
      const ids = selectedRows.map((row) => row.id)
      const token = localStorage.getItem('token')
      const formData = new FormData()
      formData.append('ids', JSON.stringify(ids))
      this.$axios
        .post('/user/deleteShoppingCart', formData, { headers: { token: `${token}` } })
        .then((response) => {
          if (response.data.state == 200) {
            this.$message.success('删除成功')
          } else {
            this.$message.error('删除失败')
          }

          // 重新加载数据
          this.$axios
            .post('/user/showShoppingCart', { userId: this.id }, { headers: { token: `${token}` } })
            .then((response) => {
              this.tableData = response.data.data
            })
            .catch((error) => {
              console.log(error)
            })
        })
        .catch((error) => {
          console.log(error)
          this.$message.error(error)
        })
    },
    placeOrder() {
      const selectedRows = this.multipletable
      if (selectedRows.length === 0) {
        this.$message.warning('至少选择一项')
        return
      }
      localStorage.removeItem('selectedRows') // 先删除已有的
      localStorage.setItem('selectedRows', JSON.stringify(selectedRows))
      // 跳转到下单页面
      this.$router.push({ name: 'orderpage' })
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
        if (this.character != 1) {
          this.$router.push({ name: 'home' })
        }
        //获取商品信息
        this.$axios
          .post(
            '/user/showShoppingCart',
            { userId: this.id },
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

    // alert('mounted end')
  }
}
</script>
