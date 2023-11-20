<!-- 暂时不用 -->
<template>
    <div class="container">
      <div class="back">
        <el-button type="primary" plain round @click="goBack">
          <el-icon style="vertical-align: middle;">
            <Back />
          </el-icon>
          <span style="vertical-align: middle;"> 返回 </span>
        </el-button>
      </div>
      <div class="payment-container">
        <h2>确认支付</h2>
        <div class="order-info">
          <p>订单编号：{{ order.number }}</p>
          <p>订单金额：{{ order.amount }}</p>
          <p>用户余额：{{ user.balance }}</p>
        </div>
        <button class="pay-button" v-if="canPay" @click="pay">确认支付</button>
        <p class="error-message" v-else>余额不足，请充值后再支付。</p>
      </div>
    </div>
  </template>
  
  <script>
   export default {
     data() {
       return {
         order: {
           number: "123456",
           amount: 100,
         },
         user: {
           name: "John Doe",
           balance: 50,
         },
       };
     },
     computed: {
       canPay() {
         return this.user.balance >= this.order.amount;
       },
     },
     methods: {
      goBack() {
            this.$router.go(-1);
        },
       pay() {
         // 执行支付逻辑
         if (this.canPay) {
           // 更新订单状态为已支付
           // 将订单金额从用户账户扣除并转移至商城的暂存账户中
           // 跳转到订单管理页面
         } else {
           // 提示余额不足，请充值后再支付
           // 跳转到充值页面或其他处理方式
         }
       },
     },
   };
  </script>
  
  <style>
  .container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
  }
  
  .back {
    position: absolute;
    top: 0;
    left: 0;
    margin: 20px;
    z-index: 2;
  }
  
  .payment-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 30px;
    /* border: 1px solid #ccc; */
    border-radius: 5px;
    /* background-color: #f8f8f8; */
  }
  
  .order-info {
    text-align: center;
  }
  
  .pay-button {
    margin-top: 20px;
    padding: 10px 30px;
    font-size: 18px;
    color: #fff;
    background-color: #007bff;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    transition: all 0.3s ease-in-out;
  }
  
  .pay-button:hover {
    background-color: #0069d9;
    transform: scale(1.05);
  }
  
  .error-message {
    color: red;
    margin-top: 20px;
  }
  </style>