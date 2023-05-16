<template>
  <el-container>
    <el-main>
      <el-card class="box-card login-card"
               v-bind:style="{ height: cardHeight + 'px' }">
        <span class="login-title" v-if="this.$route.query.type==='add'">添加课程</span>
        <span class="login-title" v-if="this.$route.query.type==='update'">修改课程</span>
        <el-form
            ref="course"
            :model="course"
            :rules="rules"
            label-width="auto">
          <el-form-item label="课程名称" prop="title">
            <el-input
                v-model="course.title"
                placeholder="请输入课程名称"></el-input>
          </el-form-item>
          <el-form-item label="课程分类" prop="type">
            <el-input
                v-model="course.type"
                placeholder="请输入课程分类"></el-input>
          </el-form-item>
          <el-form-item label="主讲教师" prop="speaker_name" v-if="this.$route.query.type==='update'">
            <el-input
                v-model="course.speaker_name"
                placeholder="请输入主讲教师"></el-input>
          </el-form-item>
          <el-form-item label="主管教师" prop="supervisor_name" v-if="this.$route.query.type==='update'">
            <el-input
                v-model="course.supervisor_name"
                placeholder="请输入主管教师"></el-input>
          </el-form-item>
          <el-form-item label="课程描述" prop="description">
            <el-input
                v-model="course.description"
                placeholder="请输入课程描述"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="addCourse" v-if="this.$route.query.type==='add'">添加</el-button>
            <el-button type="primary" @click="updateCourse" v-if="this.$route.query.type==='update'">修改</el-button>
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
  name: "course",
  inject: ['reload'],
  data() {
    return {
      course: {
        type: this.$route.query.courseType,
        title: this.$route.query.title,
        description: this.$route.query.description,
        speaker_name: this.$route.query.speaker,
        supervisor_name: this.$route.query.supervisor,
      },
      preCourse: {
        type: this.$route.query.courseType,
        title: this.$route.query.title,
        description: this.$route.query.description,
        speaker_name: this.$route.query.speaker,
        supervisor_name: this.$route.query.supervisor,
      },
      cardHeight: this.$route.query.type === 'add' ? 400 : 550,
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
        description: [
          {required: true, message: '课程描述不能为空', trigger: 'blur'},
          {min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur'}
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
    addCourse() {
      this.$refs.course.validate((valid) => {
        if (valid) {
          request.post('/addCourseA', JSON.stringify({
            type: this.course.type,
            title: this.course.title,
            description: this.course.description
          })).then(res => {
            if (res.data !== "") {
              if (res.data === 1) {
                this.$message({
                  message: '添加成功！',
                  type: 'success'
                });
                this.back();
              } else {
                this.$message({
                  message: '添加失败，该账号已存在',
                  type: 'error'
                });
                this.back();
              }
            } else {
              this.$message({
                message: '信息有误，请重新输入',
                type: 'error'
              });
              this.reload();
            }
          }).catch(err => {
            console.log(err)
          })
        } else {
          this.$message({
            message: '信息有误，请重新输入',
            type: 'error'
          });
          this.reload();
        }
      });
    },
    updateCourse() {
      if (this.course.type === this.preCourse.type && this.course.title === this.preCourse.title
          && this.course.description === this.preCourse.description
          && this.course.speaker_name === this.preCourse.speaker_name
          && this.course.supervisor_name === this.preCourse.supervisor_name) {
        this.$message.error("信息未修改，无效操作");
        this.reload();
      } else {
        this.$refs.course.validate(async (valid) => {
          if (valid) {
            await this.updateCourseToBack();
            this.back();
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
    updateCourseToBack() {
      //更新课程信息
      request.post('/updateCourseA', JSON.stringify({
        type: this.course.type,
        title: this.course.title,
        description: this.course.description,
        speaker_name: this.course.speaker_name,
        supervisor_name: this.course.supervisor_name,
        id: this.$route.query.id,
        approvers: [
          {
            teacher_name: this.course.speaker_name,
            sequence: 0,
            course_id: this.$route.query.id,
          },
          {
            teacher_name: this.course.supervisor_name,
            sequence: 1,
            course_id: this.$route.query.id,
          }
        ]
      })).then(res => {
        if (res.data !== "") {
          if (res.data === 1) {
            this.$message({
              message: '修改成功！',
              type: 'success'
            });
            // this.back();
          } else {
            this.$message({
              message: '修改失败,请检查教师信息是否有误',
              type: 'error'
            });
            // this.back();
          }
        } else {
          this.$message({
            message: '修改失败,请检查教师信息是否有误',
            type: 'error'
          });
          // this.reload();
        }
      }).catch(err => {
        console.log(err)
      })
    },
  }
};
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