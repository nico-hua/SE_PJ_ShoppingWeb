<template>
  <div class="common-layout">
    <el-container>
      <el-header><el-menu mode="horizontal" :ellipsis="false" router default-active="/addgoods">
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/shopmanager">商店管理</el-menu-item>
          <el-menu-item index="/addgoods">新增商品</el-menu-item>
          <el-menu-item index="/recordview">上架申请记录</el-menu-item>
          <el-menu-item index="/fixrecordview">修改申请记录</el-menu-item>
        </el-menu></el-header>
      <el-main>
        <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
          <el-form-item label="商品名称" prop="name">
            <el-input v-model="ruleForm.name" />
          </el-form-item>
          <el-form-item label="商品价格" prop="price">
            <el-input v-model="ruleForm.price" />
          </el-form-item>
          <el-form-item label="商品种类" prop="type">
            <el-radio-group v-model="ruleForm.type">
              <el-radio :label="'百货'">百货</el-radio>
              <el-radio :label="'图书'">图书</el-radio>
              <el-radio :label="'服装'">服装</el-radio>
              <el-radio :label="'食品'">食品</el-radio>
              <el-radio :label="'家电'">家电</el-radio>
              <el-radio :label="'电脑'">电脑</el-radio>
              <el-radio :label="'手机'">手机</el-radio>
              <el-radio :label="'数码'">数码</el-radio>
              <el-radio :label="'二手'">二手</el-radio>
              <el-radio :label="'奢侈'">奢侈</el-radio>
              <el-radio :label="'其他'">其他</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="商品描述" prop="description">
            <el-input v-model="ruleForm.description" :rows="2" type="textarea" />
          </el-form-item>
          <el-form-item label="商品图片" prop="picture">
            <input type="file" name="image" multiple @change="handlePictureChange" />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSubmit">提交申请</el-button>
            <el-button @click="resetForm('ruleForm')">重置</el-button>
          </el-form-item>
        </el-form>
      </el-main>
    </el-container>
  </div>
</template>

<script>
export default {
  data() {
    return {
      ruleForm: {
        name: '',
        price: '',
        description: '',
        type: '',
        picture: []
      },
      rules: {
        name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
        price: [
          { required: true, message: '请输入商品价格', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              if (!/^\d+(\.\d{1,2})?$/.test(value) || value <= 0) {
                callback(new Error('商品价格必须为大于 0 的浮点数'))
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ],
        description: [
          { required: true, message: '请输入商品描述', trigger: 'blur' },
          { max: 128, message: '商品描述不能超过128个字符', trigger: 'blur' }
        ]
      },
      id: 0,
      formData: new FormData(),
      response: []
    }
  },
  mounted() {
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
        this.id = response.data.data.id
        console.log('this.id = ' + this.id)
      })
      .catch((error) => {
        console.log(error)
      })
  },
  methods: {
    handlePictureChange(event) {
      this.formData = new FormData()
      const files = event.target.files
      for (let i = 0; i < files.length; i++) {
        const file = files[i]
        const reader = new FileReader()
        reader.readAsDataURL(file)
        reader.onload = () => {
          this.ruleForm.picture.push(reader.result)
          this.formData.append('image', file)
        }
      }
    },
    handleSubmit() {
      const token = localStorage.getItem('token')
      this.formData.set('commodityName', this.ruleForm.name)
      this.formData.set('price', this.ruleForm.price)
      this.formData.set('categoryName', this.ruleForm.type)
      this.formData.set('introduction', this.ruleForm.description)
      this.formData.set('merchantId', this.id)
      this.$axios
        .post('/merchant/addCommodity', this.formData, {
          headers: {
            token: `${token}`,
            'Content-Type': 'multipart/form-data'
          }
        })
        .then((response) => {
          console.log(response.data)
          if (response.data.state == 200) {
            this.$message.success('上架申请成功')
          } else {
            this.$message.error(response.data.message)
          }
        })
        .catch((error) => {
          console.log(error)
          this.$message.error('上架申请失败')
        })
    },
    resetForm(formName) {
      this.$refs[formName].resetFields()
    }
  }
}
</script>
