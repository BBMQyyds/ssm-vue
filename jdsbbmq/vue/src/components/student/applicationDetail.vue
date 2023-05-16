<template>
  <div class="course-container">
    <div class="course-info">
      <span>课程名称：{{ this.course.title }}</span>
    </div>
    <div class="course-info">
      <span>课程分类：{{ this.course.type }}</span>
    </div>
    <div class="course-info">
      <span>主讲教师：{{ this.course.speaker_name }}</span>
    </div>
    <div class="course-info">
      <span>主管教师：{{ this.course.supervisor_name }}</span>
    </div>
    <div class="course-info2">
      课程介绍：{{ this.course.description }}
    </div>
    <div class="course-actions">
      <div>
        <span>申请人数：&ensp;{{ this.course.apply_count }}</span>
      </div>
    </div>

    <div class="course-info2" v-if="this.course.apply_reason">
      <span>申请原因：{{ this.course.apply_reason }}</span>
    </div>

    <div class="course-info3" v-if="this.course.proof_path">
      <el-button class="btn" type="primary" size="default"
                 @click="getProof(this.course.application_id)">
        查看证明
      </el-button>
    </div>

    <div class="application-flow">

      <el-steps class="steps" :active="Progress" finish-status="success" align-center>
        <el-step title="申请已提交"></el-step>
        <el-step title="主讲教师审批"
                 :status="(this.course.finish===-1&&this.course.progress===1)?'error':''"></el-step>
        <el-step title="主管教师审批"
                 :status="(this.course.finish===-1&&this.course.progress===2)?'error':''"></el-step>
        <el-step title="第三审批人审批" :status="(this.course.finish===-1&&this.course.progress===3)?'error':''"
                 v-if="this.course.approver_count>=3"></el-step>
        <el-step title="第四审批人审批" :status="(this.course.finish===-1&&this.course.progress===4)?'error':''"
                 v-if="this.course.approver_count>=4"></el-step>
        <el-step title="第五审批人审批" :status="(this.course.finish===-1&&this.course.progress===5)?'error':''"
                 v-if="this.course.approver_count>=5"></el-step>
        <el-step title="申请成功" v-if="this.course.finish===1"></el-step>
      </el-steps>

      <div class="course-info2">
        <span v-if="this.course.finish===-1">驳回原因：{{ this.course.reject_reason }}</span>
      </div>
    </div>

    <el-dialog class="dialog" v-model="dialogVisible" title="证明文件："
               align-center destroy-on-close>
      <img class="image" :src="imageUrl" alt="证明文件">
      <template #footer>
      <span class="dialog-footer">
        <el-button type="primary" @click="dialogVisible = false">确认</el-button>
      </span>
      </template>
    </el-dialog>

  </div>
</template>

<script>
import request from "@/api";
import router from "@/router";
import {eventBus} from "@ai-zen/event-bus";

export default {
  name: 'applicationDetail',
  created() {
    this.getCourseDetailHavingProgress();
    eventBus.emit('updateSelected', '/applicationDetail');
  },
  data() {
    return {
      course: {},
      imageUrl: '',
      dialogVisible: false,
    };
  },
  methods: {
    getCourseDetailHavingProgress() {
      // 获取课程详情
      request.post('/courseDetailHavingProgressS', JSON.stringify({
        account: this.$route.query.account,
        course_id: this.$route.query.id,
      })).then(res => {
        if (res.data !== null) {
          this.course = res.data;
        } else {
          router.push('notFound')
        }
      }).catch(err => {
        console.log(err)
      });
    },
    getProof(application_id) {
      // 获取证明文件
      request.post('/getProof', application_id, {
        responseType: 'arraybuffer', // 指定响应类型为字节数组
      }).then(response => {
        // 将字节数组转换为Blob对象
        const blob = new Blob([response.data], {type: response.headers['content-type']});
        // 将Blob对象转换为DataURL
        // 在界面中显示图像
        this.imageUrl = URL.createObjectURL(blob);
        this.dialogVisible = true;
      });
    }
  },
  computed: {
    Progress() {
      return this.course.progress + 1 + this.course.finish;
    }
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

.course-container {
  padding: 10px;
  background-color: #D9AFD9;
  background-image: linear-gradient(225deg, #D9AFD9 0%, #97D9E1 100%);
  border: #f5f5f5 1px solid;
  width: 1000px;
  height: auto;
  margin: 50px auto auto;
}

.course-info {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
}

.course-info2 {
  margin-top: 20px;
  margin-bottom: 20px;
  font-weight: bold;
}

.course-info3 {
  font-size: 20px;
  margin-top: 20px;
  margin-bottom: 20px;
  font-weight: bold;
}

.course-actions {
  display: flex;
  justify-content: space-around;
  margin-top: 10px;
}

.application-flow {
  margin-top: 40px;
  border: #f5f5f5 1px solid;
  padding: 20px;
  text-align: center;
  background-color: #8BC6EC;
  background-image: linear-gradient(135deg, #8BC6EC 0%, #9599E2 100%);
}

.steps {
  font-weight: bolder;
  font-size: 25px;
}

::v-deep .el-step__title.is-success {
  color: darkgreen;
}

.image {
  width: 250px;
}

.btn {
  padding-top: 7px;
}

.dialog-footer button:first-child {
  margin-right: 20px;
}

/deep/ .dialog {
  width: 35% !important;
  background-color: #97D9E1 !important;
}

</style>