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
    <div class="container2">
      <div class="wrap">
        <div class="header">
          <p>个人信息</p>
        </div>
        <div class="personal-info">
          <div class="icon"></div>
          <ul style="list-style-type: none">
            <li class="name"><h1>{{ info.name }}</h1></li>
            <li class="email"><h2>{{ info.email }}</h2></li>
            <li class="details">电话：{{ info.tel }}</li>
            <li class="details">身份证：{{ info.id }}</li>
            <li class="details">余额：{{ info.balance }}</li>
          </ul>
        </div>
      </div>
    </div>
    <div class="container3" v-if="isUser">
      <div class="wrap">
        <div class="header">
          <p>我的收货地址
            <el-button type="text" @click="newAddressInitiate">新增</el-button>
          </p>
        </div>
        <div class="address-info">
          <el-table
              :data="addressData"
              border
              style="width: 100%"
              max-height="400">
            <el-table-column
                prop="name"
                label="收货人"
                min-width="20%">
            </el-table-column>
            <el-table-column
                prop="address"
                label="收货地址"
                min-width="40%">
            </el-table-column>
            <el-table-column
                prop="phone"
                label="电话号码"
                min-width="30%">
            </el-table-column>
            <el-table-column
                label="操作"
                min-width="10%"
                v-slot="{row}">
              <el-button @click="editAddressInitiate({row})" type="text">编辑</el-button>
              <el-button @click="deleteAddress({row})" type="text">删除</el-button>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
    <el-dialog v-model="dialogFormVisible" title="新增收货地址" width="500px">
      <el-form :model="addressForm">
        <el-form-item label="收货人" :label-width="formLabelWidth">
          <el-input v-model="addressForm.name"/>
        </el-form-item>
        <el-form-item label="收货地址" :label-width="formLabelWidth">
          <el-input v-model="addressForm.address"/>
        </el-form-item>
        <el-form-item label="电话号码" :label-width="formLabelWidth">
          <el-input v-model="addressForm.tel"/>
        </el-form-item>
      </el-form>
      <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogFormVisible = false">取消</el-button>
            <el-button type="primary" @click="newAddress">
              确认
            </el-button>
          </span>
      </template>
    </el-dialog>
    <el-dialog v-model="editDialogFormVisible" title="编辑收货地址" width="500px">
      <el-form :model="addressForm">
        <el-form-item label="收货人" :label-width="formLabelWidth">
          <el-input v-model="addressForm.name"/>
        </el-form-item>
        <el-form-item label="收货地址" :label-width="formLabelWidth">
          <el-input v-model="addressForm.address"/>
        </el-form-item>
        <el-form-item label="电话号码" :label-width="formLabelWidth">
          <el-input v-model="addressForm.tel"/>
        </el-form-item>
      </el-form>
      <template #footer>
          <span class="dialog-footer">
            <el-button @click="editDialogFormVisible = false">取消</el-button>
            <el-button type="primary" @click="editAddress">
              确认
            </el-button>
          </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import {ElMessage} from "element-plus";

export default {
  name: "AccountUserInfo",
  data() {
    return {
      info: {
        name: '',
        email: '',
        tel: '',
        id: '',
        balance: '¥',
      },
      addressData: [],
      addressForm: {
        name: '',
        address: '',
        tel: ''
      },
      chosenId: '',
      dialogFormVisible: false,
      editDialogFormVisible: false,
      formLabelWidth: '60px',
      isUser: false,
      isMerchant: false,
      isAdmin: false
    }
  },
  methods: {
    newAddressInitiate() {
      this.dialogFormVisible = true;
      this.addressForm.address = '';
      this.addressForm.tel = '';
      this.addressForm.name = '';
    },
    editAddressInitiate(param) {
      this.addressForm.name = param.row.name;
      this.addressForm.tel = param.row.phone;
      this.addressForm.address = param.row.address;
      this.chosenId = param.row.id;
      this.editDialogFormVisible = true;
    },
    newAddress() {
      this.dialogFormVisible = false;
      const token = localStorage.getItem('token');
      this.$axios
          .post('/user/addShippingAddress', {
                name: this.addressForm.name,
                phone: this.addressForm.tel,
                address: this.addressForm.address
              }
              , {
                headers: {
                  token: `${token}`
                }
              })
          .then((response) => {
            console.log(response.data);
            this.$axios
                .get('/user/getShippingAddress', {
                  headers: {
                    token: `${token}`
                  }
                })
                .then((response) => {
                  this.addressData = response.data.data;
                })
                .catch((error) => {
                  console.log(error)
                })
            ElMessage({
              message: "新增成功",
              type: "success"
            });
          })
    },
    editAddress() {
      this.editDialogFormVisible = false;
      const token = localStorage.getItem('token');
      this.$axios
          .post('/user/updateShippingAddress', {
                id: this.chosenId,
                name: this.addressForm.name,
                phone: this.addressForm.tel,
                address: this.addressForm.address
              }
              , {
                headers: {
                  token: `${token}`
                }
              })
          .then((response) => {
            console.log(response.data);
            this.$axios
                .get('/user/getShippingAddress', {
                  headers: {
                    token: `${token}`
                  }
                })
                .then((response) => {
                  this.addressData = response.data.data;
                })
                .catch((error) => {
                  console.log(error);
                })
          })
      ElMessage({
        message: "修改成功",
        type: "success"
      });
    },
    deleteAddress(param) {
      // param.row to use the object

      const token = localStorage.getItem('token')
      this.$confirm('此操作将永久删除该地址, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios
            .post('/user/deleteShippingAddress', {
                  id: param.row.id
                }
                , {
                  headers: {
                    token: `${token}`
                  }
                })
            .then((response) => {
              console.log(response.data);
              this.$axios
                  .get('/user/getShippingAddress', {
                    headers: {
                      token: `${token}`
                    }
                  })
                  .then((response) => {
                    this.addressData = response.data.data;
                  })
                  .catch((error) => {
                    console.log(error);
                  })
            })

        this.$message({
          type: 'success',
          message: '删除成功!'
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消操作'
        });
      });
    }
  },
  mounted() {
    const token = localStorage.getItem('token')

    //getData
    this.$axios
        .get('/getData', {
          headers: {
            token: `${token}`
          }
        })
        .then((response) => {
          console.log(response.data)
          this.info.name = response.data.data.name;
          this.info.tel = response.data.data.phone;
          this.info.id = response.data.data.idCard;
          this.info.email = response.data.data.email;
          if (response.data.message == '1') {
            this.isUser = true;
          } else if (response.data.message == '2') {
            this.isMerchant = true;
          } else if (response.data.message == '3') {
            this.isAdmin = true;
          }
        })
        .catch((error) => {
          console.log(error)
        })

    //getAccount
    this.$axios
        .get('/getAccount', {
          headers: {
            token: `${token}`
          }
        })
        .then((response) => {
          console.log(response.data)
          this.info.balance = response.data.data.amount + this.info.balance;
          console.log('this.id = ' + this.id)
        })
        .catch((error) => {
          console.log(error)
        })

    this.$axios
        .get('/user/getShippingAddress', {
          headers: {
            token: `${token}`
          }
        })
        .then((response) => {
          this.addressData = response.data.data;
        })
        .catch((error) => {
          console.log(error);
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

.header p {
  position: relative;
  transform: translateY(-50%);
  left: 2%;
  top: 50%;
}

.personal-info {
  width: 100%;
  text-align: center;
}

.container2, .container3 {
  margin-top: 50px;
}

.personal-info {
  background-color: white;
  padding: 50px 0px;
  /*border-radius: 10px;*/
  -webkit-box-shadow: 0px 0px 10px 5px rgba(0, 0, 0, 0.15);
  -moz-box-shadow: 0px 0px 10px 5px rgba(0, 0, 0, 0.15);
  box-shadow: 0px 0px 10px 5px rgba(0, 0, 0, 0.15);
}

.personal-info ul {
  padding: 0;
}

.icon {
  width: 200px;
  height: 200px;
  background-color: #3c00a0;
  border-radius: 50%;
  position: relative;
  left: 50%;
  transform: translateX(-50%);
}

.details {
  padding: 10px;
}

.container3 {
  padding-bottom: 40vh;
}

.address-info {
  background-color: white;
  /*border-radius: 10px;*/
  -webkit-box-shadow: 0px 0px 10px 5px rgba(0, 0, 0, 0.15);
  -moz-box-shadow: 0px 0px 10px 5px rgba(0, 0, 0, 0.15);
  box-shadow: 0px 0px 10px 5px rgba(0, 0, 0, 0.15);
}

</style>