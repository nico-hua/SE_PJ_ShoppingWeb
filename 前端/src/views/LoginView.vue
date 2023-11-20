<template>
  <div class="body">
    <div class="container">
      <div class="form-box">
        <h1 id="header">登录</h1>
        <el-form :label-width="auto" size="large">
          <el-form-item>
            <div class="radio-field">
              <el-radio-group v-model="character" size="large">
                <el-radio label="1">普通用户</el-radio>
                <el-radio label="2">商户</el-radio>
                <el-radio label="3">系统管理员</el-radio>
              </el-radio-group>
            </div>
          </el-form-item>
          <el-form-item>
            <el-input v-model="username" type="text" placeholder="用户名" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-input
                v-model="password"
                type="password"
                placeholder="密码"
                clearable
                show-password
            ></el-input>
          </el-form-item>
          <p>没有账号?
            <router-link to="/register"
                         style="text-decoration: none;
                         color: #3c00a0"
            >注册
            </router-link>
          </p>
          <el-form-item>
            <div class="button-field1-col1">
              <el-button type="primary" @click="login" class="login-button">登入</el-button>
            </div>
            <div class="button-field1-col2">
              <el-button @click="cancel" class="cancel-button" plain>取消</el-button>
            </div>
          </el-form-item>
          <router-link to="/" style="text-decoration: none">
            <el-form-item>
              <div class="button-field2">
                <el-button type="success" class="return-button">返回</el-button>
              </div>
            </el-form-item>
          </router-link>
          <div class="err_msg">{{ message }}</div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      character: '',
      username: '',
      password: '',
      users: [],
      api: '',
      message: ''
    }
  },
  methods: {
    cancel() {
      this.character = ''
      this.username = ''
      this.password = ''
      this.users = []
      this.api = ''
      this.message = ''
    },
    login() {
      if (this.character == '') {
        // alert('未选择角色')
        this.message = '未选择角色'
        return
      } else if (this.character == 1) {
        this.api = '/user/login'
      } else if (this.character == 2) {
        this.api = '/merchant/login'
      } else if (this.character == 3) {
        this.api = '/admin/login' //临时测试用,之后要改回'/admin/login'
      }
      this.$axios
          .post(
              this.api,
              {
                name: this.username,
                password: this.password
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
              // alert('登录成功')
              localStorage.setItem('token', this.users.data)
              this.message = '登录成功'
              if (this.character != 3) {
                this.$router.push({name: 'home'})
              } else {
                //管理员登录跳转到后台
                this.$router.push({name: 'backstage'})
              }
            } else {
              // alert(this.users.message)
              this.message = this.users.message
            }
          })
          .catch((error) => {
            console.log(error)
            // alert('登录请求失败')
            this.message = '登录请求失败'
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
  max-width: 450px;
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

.radio-field {
  width: 100%;
}

.button-field1-col1 {
  width: 50%;
}

.button-field1-col2 {
  width: 50%;
}

.login-button {
  background-color: #3c00a0;
  width: 96%;
}

.cancel-button {
  width: 96%;
}

.button-field2 {
  width: 98%;
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
