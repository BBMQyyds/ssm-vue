<template>
  <el-container style="height: calc(100vh - 64px); overflow: auto;">
    <el-main>
      <el-card class="box-card login-card"
               :style="{height: cardHeight}">
        <span class="login-title">创建审批流</span>
        <el-form
            ref="course"
            :model="course"
            :rules="rules"
            label-width="auto">
          <el-form-item label="课程名称" prop="title">
            <el-input
                v-model="course.title"
                placeholder="请输入课程名称" disabled></el-input>
          </el-form-item>
          <el-form-item label="课程分类" prop="type">
            <el-input
                v-model="course.type"
                placeholder="请输入课程分类" disabled></el-input>
          </el-form-item>
          <el-form-item label="第一审批人" prop="speaker_name">
            <el-input
                v-model="course.speaker_name"
                placeholder="请输入第一审批人" disabled></el-input>
          </el-form-item>
          <el-form-item label="第二审批人" prop="supervisor_name">
            <el-input
                v-model="course.supervisor_name"
                placeholder="请输入第审批人" disabled></el-input>
          </el-form-item>

          <el-form-item v-for="(approver, index) in course.approvers" :key="index"
                        :label="indexMethod(index+3)"
                        :rules="[ {required: true, message: '审批人不能为空', trigger: 'blur'},
          {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'}]">
            <el-input v-model="approver.teacher_name" placeholder="请输入审批人姓名"></el-input>
          </el-form-item>

          <el-form-item>
            <!--添加流程-->
            <el-button type="primary" @click="add" :disabled="isAddButtonDisabled">添加</el-button>
            <el-button type="primary" @click="remove" :disabled="isRemoveButtonDisabled">移除</el-button>
            <el-button type="primary" @click="submit">提交</el-button>
            <el-button type="primary" @click="back">返回</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </el-main>
  </el-container>
</template>

<script>

import request from "@/api";
import router from "@/router";
import {eventBus} from "@ai-zen/event-bus";

export default {
  name: "flow",
  inject: ['reload'],
  created() {
    request.post('/courseFlowA', JSON.stringify({
      account: this.$route.query.account,
      course_id: this.$route.query.id,
    })).then(res => {
      if (res.data !== "") {
        this.course = JSON.parse(JSON.stringify(res.data));
        this.preCourse = JSON.parse(JSON.stringify(res.data));
      } else {
        this.$message({
          message: 'error',
          type: 'error'
        });
      }
    }).catch(err => {
      console.log(err)
    });
    eventBus.emit('updateSelected', '/courseManage');
  },
  data() {
    return {
      course: {
        title: '',
        type: '',
        speaker_name: '',
        supervisor_name: '',
        approvers: [] // 用来存储审批人信息的数组
      },
      preCourse: {
        title: '',
        type: '',
        speaker_name: '',
        supervisor_name: '',
        approvers: [] // 用来存储审批人信息的数组
      },
      rules: {
        type: [
          {required: true, message: '课程分类不能为空', trigger: 'blur'},
          {min: 2, max: 50, message: '长度在 2 到 20 个字符', trigger: 'blur'}
        ],
        title: [
          {required: true, message: '课程名不能为空', trigger: 'blur'},
          {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'}
        ],
        speaker_name: [
          {required: true, message: '主讲教师不能为空', trigger: 'blur'},
          {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'}
        ],
        supervisor_name: [
          {required: true, message: '主管教师不能为空', trigger: 'blur'},
          {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'}
        ],
      }
    };
  },
  methods: {
    back() {
      router.push({
        path: '/courseManage',
        query: {
          account: this.$route.query.account,
        },
      });
      eventBus.emit('updateSelected', '/courseManage');
    },
    add() {
      // 添加一个新的审批人对象
      this.course.approvers.push({
        teacher_name: '',
        sequence: this.course.approvers.length + 2,
        course_id: this.$route.query.id
      });
    },
    remove(){
      // 删除最后一个审批人对象
      this.course.approvers.pop();
    },
    submit() {
      if (JSON.stringify(this.course) === JSON.stringify(this.preCourse)) {
        this.$message.error("信息未修改，无效操作");
        this.reload();
      } else {
        this.$refs.course.validate(async (valid) => {
          if (valid) {
            //提交审批流
            request.post('/submitFlowA', JSON.stringify(this.course)).then(res => {
              if (res.data !== "") {
                if (res.data === 1) {
                  this.$message({
                    message: '提交成功',
                    type: 'success'
                  });
                  this.back();
                } else {
                  this.$message({
                    message: '提交失败，请检查教师信息是否有误',
                    type: 'error'
                  });
                  this.reload();
                }
              } else {
                this.$message({
                  message: '提交失败，请检查教师信息是否有误',
                  type: 'error'
                });
                this.reload();
              }
            }).catch(err => {
              console.log(err)
            });
          } else {
            this.$message({
              message: '信息有误，请重新输入',
              type: 'error'
            });
            this.reload();
          }
        });
      }
    },
    //数字转化为第几
    indexMethod(index) {
      return '第' + this.toChinesNum(index) + '审批人';
    },
    toChinesNum(num) {
      let changeNum = ['零', '一', '二', '三', '四', '五', '六', '七', '八', '九']
      let unit = ['', '十', '百', '千', '万']
      num = parseInt(num)
      let getWan = (temp) => {
        let strArr = temp.toString().split('').reverse()
        let newNum = ''
        let newArr = []
        strArr.forEach((item, index) => {
          newArr.unshift(item === '0' ? changeNum[item] : changeNum[item] + unit[index])
        })
        let numArr = []
        newArr.forEach((m, n) => {
          if (m !== '零') numArr.push(n)
        })
        if (newArr.length > 1) {
          newArr.forEach((m, n) => {
            if (newArr[newArr.length - 1] === '零') {
              if (n <= numArr[numArr.length - 1]) {
                newNum += m
              }
            } else {
              newNum += m
            }
          })
        } else {
          newNum = newArr[0]
        }

        return newNum
      }
      let overWan = Math.floor(num / 10000)
      let noWan = num % 10000
      if (noWan.toString().length < 4) {
        noWan = '0' + noWan
      }
      return overWan ? getWan(overWan) + '万' + getWan(noWan) : getWan(num)
    },
  },
  computed: {
    cardHeight() {
      // 根据审批人数量计算卡片高度
      return `${460 + this.course.approvers.length * 80}px`;
    },
    isAddButtonDisabled() {
      return this.course.approvers.length >= 3;
    },
    isRemoveButtonDisabled() {
      return this.course.approvers.length < 1;
    }
  }
}
;
</script>

<style scoped>

:deep(input) {
  border: none;
}

el-main {
  position: center;
}

.login-card {
  background-color: #8BC6EC;
  background-image: linear-gradient(135deg, #8BC6EC 0%, #9599E2 100%);
  position: absolute;
  top: 0;
  left: 0;
  bottom: 0;
  right: 0;
  margin: auto;
  margin-top: 120px;
  width: 480px;
  padding: 10px;
  border: 2px solid #4158D0;
  border-radius: 24px;
}

.login-title {
  font-size: 24px;
  color: #333333;
  font-weight: 800;
  display: block;
  text-align: center;
  margin-bottom: 30px;
}

.item {
  margin-bottom: 40px;
}

#radios {
  height: 10px;
  margin-left: 125px;
}

.el-form-item {
  margin-bottom: 40px;
}

.el-form-item__label {
  font-size: 20px;

  font-weight: 400;
  color: #333333;
  letter-spacing: 1px;
  display: block;
  text-align: center;
}

.el-button {
  font-size: 16px;
  padding-top: 8px;
  display: block;
  text-align: center;
  border-radius: 5px;
  border: 1px solid #4158D0;
  margin: auto;
}

:deep(input) {
  width: 310px;
  border: none;
}

</style>