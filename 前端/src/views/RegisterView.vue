<template>
  <div class="body">
    <div class="container">
      <div class="form-box">
        <h1 id="header">注册</h1>
        <el-form size="large" label-width="auto">
          <el-form-item label="角色">
            <el-radio-group v-model="character">
              <el-radio label="1">普通用户</el-radio>
              <el-radio label="2">商户</el-radio>
            </el-radio-group>
          </el-form-item>
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
            <el-input v-model="identity" @blur="identityBlur" required>身份证号</el-input>
            <div id="identity_err" class="err_msg" v-show="!identityFlag">身份证号格式错误</div>
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="email" @blur="emailBlur" required>邮箱</el-input>
            <div id="email_err" class="err_msg" v-show="!emailFlag">邮箱格式错误</div>
          </el-form-item>
          <el-form-item>
            <div class="button-field1-col1">
              <el-button type="primary" @click="register" class="register-button">注册</el-button>
            </div>
            <div class="button-field1-col2">
              <el-button @click="cancel" class="cancel-button">取消</el-button>
            </div>
            <div class="err_msg">{{ message }}</div>
          </el-form-item>
          <router-link to="/" style="text-decoration: none">
            <el-form-item>
              <div class="button-field2">
                <el-button type="success" class="return-button">返回</el-button>
              </div>
            </el-form-item>
          </router-link>
        </el-form>
      </div>
    </div>


  </div>
</template>

<script>
export default {
  data() {
    return {
      character: '',
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
      message: ''
    }
  },
  methods: {
    cancel() {
      this.character = ''
      this.username = ''
      this.usernameFlag = true
      this.password = ''
      this.passwordFlag = true
      this.phone = ''
      this.phoneFlag = true
      this.identity = ''
      this.identityFlag = true
      this.email = ''
      this.emailFlag = true
      this.users = []
      this.api = ''
      this.message = ''
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

    register() {
      if (this.character == '') {
        // alert('未选择角色')
        this.message = '未选择角色'
        return
        //普通用户
      } else if (this.character == 1) {
        this.api = '/user/register'
        //商户
      } else if (this.character == 2) {
        this.api = '/merchant/register'
        // alert(this.character)
      }
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
                headers: {'Content-Type': 'application/json'}
              }
          )
          .then((response) => {
            console.log(response.data)
            this.users = response.data
            console.log(this.users)
            if (this.users.state == 200) {
              // alert('注册成功')
              this.message = '注册成功'
              this.$router.push({name: 'login'})
            } else {
              // alert(this.users.message)
              this.message = this.users.message
            }
          })
          .catch((error) => {
            console.log(error)
            this.message = '注册请求失败'
            // alert('注册请求失败')
          })
    }
  }
}
</script>

<style scoped>
.body {
  font-family: 'Microsoft YaHei';
  box-sizing: border-box;
}

.err_msg {
  color: red;
}

.go-back img {
  float: left;
  padding: 20px 20px;
  width: 58px;
  height: 50px;
}

.container {
  background: linear-gradient(rgba(0, 0, 50, 0.8), rgba(0, 0, 50, 0.8));
  width: 100%;
  height: 100vh;
}

.form-box {
  width: 90%;
  height: auto;
  max-width: 900px;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  padding: 50px 60px 70px 60px;
  background: #fff;
  text-align: center;
}

.form-box h1 {
  font-size: 30px;
  font-weight: 700;
  margin-bottom: 60px;
  color: #3c00a0;
  position: relative;
}

.form-box h1:after {
  content: '';
  width: 30px;
  height: 4px;
  border-radius: 3px;
  background-color: #3c00a0;
  position: absolute;
  bottom: -12px;
  left: 50%;
  transform: translateX(-50%);
}

.button-field1-col1 {
  width: 50%;
}

.button-field1-col2 {
  width: 50%;
}

.register-button {
  background-color: #3c00a0;
  width: 60%;
}

.cancel-button {
  width: 60%;
}

.button-field2 {
  width: 50%;
  margin: 0 auto;
}


.return-button {
  width: 100%;
  position: relative;
  left: 50%;
  transform: translateX(-50%);
}

.form-box p {
  text-align: left;
  font-size: 17px;
}
</style>
