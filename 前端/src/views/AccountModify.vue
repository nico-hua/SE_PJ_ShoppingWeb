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
          <p>个⼈信息修改</p>
        </div>
        <div class="form">
          <el-form size="large" label-width="auto" id="form" :disabled="disabled">
            <el-form-item label="用户名">
              <el-input v-model="username" @blur="usernameBlur" required>用户名</el-input>
              <div>3 &le; {{ username.length }} &le; 10 &nbsp;&nbsp;</div>
              <div id="username_err" class="err_msg" v-show="!usernameFlag">用户名格式错误</div>
            </el-form-item>
            <el-form-item label="密码">
              <el-input v-model="password" @blur="passwordBlur" required>密码</el-input>
              <div>6 &le; {{ password.length }} &le; 32 &nbsp;&nbsp;</div>
              <div id="password_err" class="err_msg" v-show="!passwordFlag">密码格式错误</div>
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="phone" @blur="phoneBlur" required>手机号</el-input>
              <div id="phone_err" class="err_msg" v-show="!phoneFlag">手机号格式错误</div>
            </el-form-item>
            <el-form-item label="身份证号">
              <el-input v-model="identity" @blur="identityBlur" required disabled>身份证号</el-input>
              <div id="identity_err" class="err_msg" v-show="!identityFlag">身份证号格式错误</div>
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="email" @blur="emailBlur" required>邮箱</el-input>
              <div id="email_err" class="err_msg" v-show="!emailFlag">邮箱格式错误</div>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="confirm">确认</el-button>
              <el-button @click="cancel">取消</el-button>
              <div class="err_msg">{{ message }}</div>
            </el-form-item>
          </el-form>
          <el-button type="primary" @click="edit">修改</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      username: '',
      usernameFlag: true,
      password: '',
      passwordFlag: true,
      phone: '',
      phoneFlag: true,
      identity: '',
      identityFlag: true,
      email: '',
      emailFlag: true,
      users: [],
      api: '',
      message: '',
      disabled: true,
      isUser: false,
      isMerchant: false,
      isAdmin: false
    }
  },
  methods: {
    cancel() {
      this.username = '';
      this.usernameFlag = true;
      this.password = '';
      this.passwordFlag = true;
      this.phone = '';
      this.phoneFlag = true;
      this.identity = '';
      this.identityFlag = true;
      this.email = '';
      this.emailFlag = true;
      this.users = [];
      this.api = '';
      this.message = '';
    },
    usernameBlur() {
      let reg = /^\w{3,10}$/
      this.usernameFlag = reg.test(this.username) || this.username.length == 0
    },
    passwordBlur() {
      let reg1 = /^(?=.*[a-zA-Z])(?=.*\d)[\w-]{6,32}$/ //字母和数字
      let reg2 = /^(?=.*[a-zA-Z])(?=.*[-_])[\w-]{6,32}$/ //字母和特殊字符
      let reg3 = /^(?=.*[-_])(?=.*\d)[\w-]{6,32}$/ //特殊字符和数字
      this.passwordFlag =
          reg1.test(this.password) ||
          reg2.test(this.password) ||
          reg3.test(this.password) ||
          this.password.length == 0
    },
    phoneBlur() {
      let reg = /^1[3-9]\d{9}$/
      this.phoneFlag = reg.test(this.phone) || this.phone.length == 0
    },
    identityBlur() {
      let reg = /^\d{17}[\dX]$/
      this.identityFlag = reg.test(this.identity) || this.identity.length == 0
    },
    emailBlur() {
      let reg = /^\w+@[\da-z\.-]+\.[a-z]{2,4}$/
      this.emailFlag = reg.test(this.email) || this.email.length == 0
    },
    edit() {
      this.disabled = false;

    },
    register() {

      if (
          !(
              this.usernameFlag &&
              this.passwordFlag &&
              this.phoneFlag &&
              this.identityFlag &&
              this.emailFlag
          )
      ) {
        this.message = '格式错误'
        return
      }
    },
    confirm() {
      const token = localStorage.getItem('token')
      this.$axios
          .post(
              this.api,
              {
                name: this.username,
                password: this.password,
                phone: this.phone,
                idCard: this.identity,
                email: this.email
              },
              {
                headers: {
                  'Content-Type': 'application/json',
                  token: `${token}`
                }
              }
          )
          .then((response) => {
            console.log(response.data)
            this.users = response.data
            console.log(this.users)
            if (this.users.state == 200) {
              // alert('成功')
              this.message = '修改成功'
              this.$router.push({name: 'login'})
            } else {
              // alert(this.users.message)
              this.message = response.data.message;
            }
          })
          .catch((error) => {
            console.log(error)
            // alert('注册请求失败')
          })
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
          this.username = response.data.data.username;
          this.identity = response.data.data.idCard;
          this.username = response.data.data.username;
          this.phone = response.data.data.phone;
          this.email = response.data.data.email;
          if (response.data.message == 1) {
            this.isUser = true;
          } else if (response.data.message == 2) {
            this.isMerchant = true;
          } else {
            this.isAdmin = true;
          }
          if (response.data.message == '1') {
            this.api = '/user/updateData';
          } else if (response.data.message == '2') {
            this.api = '/merchant/updateData';
          }
        })
        .catch((error) => {
          console.log(error)
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

.container2{
  margin-top: 50px;
}

.header {
  width: 100%;
  height: 50px;
  margin-bottom: 40px;
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

.form{

}

.err_msg {
  color: red;
}
</style>