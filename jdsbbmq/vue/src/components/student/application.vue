<template>
  <div class="course-container">
    <div class="course-info">
      <span>课程名称：{{ title }}</span>
    </div>
    <div class="course-info">
      <span>课程分类：{{ type }}</span>
    </div>
    <div class="course-info">
      <span>主讲教师：{{ speaker_name }}</span>
    </div>
    <div class="course-info">
      <span>主管教师：{{ supervisor_name }}</span>
    </div>
    <div class="course-info2">
      课程介绍：{{ description }}
    </div>
    <div class="course-actions">
      <div>
        <span>申请人数：&ensp;{{ apply_count }}</span>
      </div>
    </div>

    <div class="application-input">

      <el-input v-model="reason" class="input-application" placeholder="请输入您的申请原因"
                type="textarea"></el-input>
      <p></p>

      <el-upload
          class="upload-demo"
          ref="file"
          action=""
          :show-file-list="false"
          :before-upload="beforeUpload"
          :on-success="onSuccess"
          :on-error="onError"
          :http-request="uploadHttpRequest"
          :accept="'image/png,image/jpeg'">
        <el-button slot="trigger" size="default" type="primary">选择图片</el-button>
      </el-upload>
      <p></p>
      <div v-if="file.name">
        <span>文件名：</span>{{ file.name }}
      </div>
      <p></p>
      <div v-if="file.size">
        <span>文件大小：</span>{{ file.size }} 字节
      </div>
      <p></p>
      <img v-if="imageUrl" :src="imageUrl" style="width: 200px;">
      <p></p>

      <el-button class="give-application" type="primary" @click="submitApplication">申请</el-button>
    </div>
  </div>
</template>

<script>
import {ElButton, ElInput} from 'element-plus';
import request from "@/api";
import router from "@/router";
import {eventBus} from "@ai-zen/event-bus";

export default {
  name: 'application',
  created() {
    request.post('courseDetailS', JSON.stringify(this.$route.query.id)).then(res => {
      if (res.data !== '') {
        this.title = res.data.title;
        this.type = res.data.type;
        this.speaker_name = res.data.speaker_name;
        this.supervisor_name = res.data.supervisor_name;
        this.description = res.data.description;
        this.apply_count = res.data.apply_count;
        this.id = res.data.id;
      } else {
        router.push('notFound')
      }
    }).catch(err => {
      console.log(err)
    });
    eventBus.emit('updateSelected', '/application');
  },
  components: {
    ElButton,
    ElInput,
  },
  data() {
    return {
      id: '',
      title: '',
      type: '',
      speaker_name: '',
      supervisor_name: '',
      description: '',
      apply_count: 0,
      reason: '',
      file: {},
      imageUrl: ''
    };
  },
  methods: {
    submitApplication() {
      const formData = new FormData();
      formData.append('account', this.$route.query.account);
      formData.append('course_id', this.$route.query.id);
      formData.append('reason', this.reason);
      if (this.file && this.file.size && this.file.name) {
        formData.append('file', this.file);
      }
      request.post('/applyCourseS', formData, {
        headers: {
          'Content-Type': 'multipart/form-data; charset=UTF-8'
        }
      }).then(res => {
        if (res.data === 1) {
          this.$message({
            message: '申请成功',
            type: 'success'
          });
          router.push({
            path: '/apply',
            query: {
              account: this.$route.query.account,
            },
          });
          eventBus.emit('updateSelected', '/apply');
        } else {
          this.$message({
            message: '申请失败，请重试',
            type: 'error'
          });
        }
      }).catch(err => {
        console.log(err)
      });
    },
    beforeUpload(file) {
      const fileType = file.type;
      const isJpgOrPng = fileType === 'image/jpeg' || fileType === 'image/png';
      const isLt2M = file.size / 1024 / 1024 < 5;
      if (!isJpgOrPng) {
        this.$message.error('上传图片只能是 JPG/PNG 格式!');
        return false;
      }
      if (!isLt2M) {
        this.$message.error('上传图片大小不能超过 5MB!');
        return false;
      }
      this.file = file;
      this.imageUrl = URL.createObjectURL(file);
      return true;
    },
    uploadHttpRequest(data) {
      console.log(data);
    },
    onSuccess(response) {
      console.log(response);
    },
    onError(error) {
      console.log(error);
    },
  },
};
</script>

<style scoped>

:deep(input) {
  border: none;
}

span {
  font-size: 20px;
  font-weight: bold;
}

.el-button {
  font-size: 12px;
  padding-top: 10px;
}

.course-container {
  padding: 10px;
  background-color: #D9AFD9;
  background-image: linear-gradient(225deg, #D9AFD9 0%, #97D9E1 100%);
  border: #f5f5f5 1px solid;
  width: 800px;
  height: auto;
  margin: 50px auto auto;
}

.course-info {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
}

.course-info2 {
  font-weight: bold;
}

.course-actions {
  display: flex;
  justify-content: space-around;
  margin-top: 10px;
}

.application-input {
  margin-top: 40px;
  border: #f5f5f5 1px solid;
  padding: 20px;
  text-align: center;
  background-color: #8BC6EC;
  background-image: linear-gradient(135deg, #8BC6EC 0%, #9599E2 100%);
}

.input-application {
  width: 60%;
}

.give-application {
  margin-top: 5px;
}
</style>