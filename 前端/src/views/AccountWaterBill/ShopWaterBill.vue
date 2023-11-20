<template>
  <div class="body">
    <div class="container1">
      <div class="wrap">
        <div class="nav-bar">
          <el-menu mode="horizontal" :ellipsis="false" router default-active="/shopwaterbill">
            <el-menu-item index="/accountuser">返回</el-menu-item>
            <el-menu-item index="/userwaterbill" v-if="isUser">个人流⽔</el-menu-item>
            <el-menu-item index="/merchantwaterbill" v-if="isMerchant">商户流⽔</el-menu-item>
            <el-menu-item index="/shopwaterbill" v-if="isMerchant">商店流⽔</el-menu-item>
            <el-menu-item index="/profitwaterbill" v-if="isAdmin">商城利润账户流⽔</el-menu-item>
            <el-menu-item index="/midwaterbill" v-if="isAdmin">中间商城账户流⽔</el-menu-item>
          </el-menu>
        </div>
      </div>
    </div>
    <div class="container2">
      <div class="wrap">
        <div class="header">
          <p>
            <span>{{ billTitle }}</span>
            &nbsp;
            <el-select v-model="timeIntervalValue" placeholder="请选择">
              <el-option
                  v-for="item in timeIntervalOption"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                  @click="setTimeInterval">
              </el-option>
            </el-select>
            <!--            <el-select v-model="inAndOutValue" placeholder="请选择">-->
            <!--              <el-option-->
            <!--                  v-for="item in inAndOutOption"-->
            <!--                  :key="item.value"-->
            <!--                  :label="item.label"-->
            <!--                  :value="item.value">-->
            <!--              </el-option>-->
            <!--            </el-select>-->
          </p>
        </div>
        <div class="water-bill">
          <el-table :data="billdata" style="width: 100%" stripe max-height="500">
            <el-table-column prop="initiatorName" label="转出⽅"></el-table-column>
            <el-table-column prop="receiverName" label="转⼊⽅"></el-table-column>
            <el-table-column prop="amount" label="⾦额" sortable></el-table-column>
            <el-table-column prop="tradeRecord" label="备注信息"></el-table-column>
            <el-table-column prop="tradeTime" label="日期"></el-table-column>
          </el-table>
        </div>
      </div>
    </div>
    <div class="container3">
      <div class="wrap">
        <h2>总收入：{{ income }}</h2>
        <h2>总支出：{{ spent }}</h2>
        <h2>利润：{{ profit }}</h2>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "ShopWaterBill",
  data() {
    return {
      billTitle: '商店流⽔',
      billdata: [],
      timeIntervalOption: [{
        value: 0,
        label: '全部（日期）'
      }, {
        value: 7,
        label: '近七天'
      }, {
        value: 30,
        label: '近一个月'
      }],
      timeIntervalValue: 0,
      inAndOutOption: [{
        value: 0,
        label: '全部（支出 / 收入）'
      }, {
        value: -1,
        label: '支出'
      }, {
        value: 1,
        label: '收入'
      }],
      inAndOutValue: 0,
      income: 0,
      spent: 0,
      profit: 0,
      isUser: false,
      isMerchant: false,
      isAdmin: false,
    }
  },
  methods: {
    setTimeInterval() {
      const token = localStorage.getItem('token')
      let url = "/merchant/getShopAccountRecorder?timeInterval=" + this.timeIntervalValue;
      this.$axios
          .get(url, {
            headers: {
              token: `${token}`
            }
          })
          .then((response) => {
            this.billdata = response.data.data;
            this.calculateSummary();
            console.log(response.data);
          })
          .catch((error) => {
            console.log(error)
          })
    },
    calculateSummary() {
      this.income = 0;
      this.spent = 0;
      this.profit = 0;
      for (let i = 0; i < this.billdata.length; i++) {
        if (this.billdata[i].inAndout == 1) {
          this.income += this.billdata[i].amount;
          this.profit += this.billdata[i].amount;
        } else {
          this.spent -= this.billdata[i].amount;
          this.profit -= this.billdata[i].amount;
        }
      }
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
          console.log(response.data);
          if (response.data.message == 1) {
            this.isUser = true;
          } else if (response.data.message == 2) {
            this.isMerchant = true;
          } else {
            this.isAdmin = true;
          }
          console.log('this.id = ' + this.id)
        })
        .catch((error) => {
          console.log(error)
        })
    this.$axios
        .get('/merchant/getShopAccountRecorder?timeInterval=0', {
          headers: {
            token: `${token}`
          }
        })
        .then((response) => {
          this.billdata = response.data.data;
          this.calculateSummary();
          console.log(response.data);
        })
        .catch((error) => {
          console.log(error)
        })
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

.header {
  width: 100%;
  height: 50px;
  margin-bottom: 8px;
  background-color: #f2f6fc;
  -webkit-box-shadow: 0px 0px 10px 5px rgba(0, 0, 0, 0.15);
  -moz-box-shadow: 0px 0px 10px 5px rgba(0, 0, 0, 0.15);
  box-shadow: 0px 0px 10px 5px rgba(0, 0, 0, 0.15);
}

.header p {
  position: relative;
  transform: translateY(-50%);
  left: 2%;
  top: 50%;
}

.container2 {
  margin-top: 50px;
}

.water-bill {
  -webkit-box-shadow: -1px 0px 21px 0px rgba(0, 0, 0, 0.25);
  -moz-box-shadow: -1px 0px 21px 0px rgba(0, 0, 0, 0.25);
  box-shadow: -1px 0px 21px 0px rgba(0, 0, 0, 0.25);
}

.container3 {
  margin-top: 20px;
}
</style>