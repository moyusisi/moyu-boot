<template>
  <a-drawer
      :open="visible"
      :title="title"
      :width="drawerWidth"
      :closable="false"
      :maskClosable="false"
      :destroy-on-close="true"
      @close="onClose"
  >
    <!--  上方操作区  -->
    <template #extra>
        <a-button type="primary" size="small" @click="onClose"><CloseOutlined /></a-button>
    </template>
    <!--  数据区  -->
    <a-spin :spinning="dataLoading">
      <a-form ref="formRef" :model="formData" :label-col="{span: 6}">
        <a-card title="基本信息">
          <a-row :gutter="24">
<#if fieldList??>
  <#list fieldList as fieldConfig>
    <#if fieldConfig.showInForm == 1>
            <a-col :span="12">
              <a-form-item name="${fieldConfig.fieldName}" label="${fieldConfig.fieldRemark[0..*6]}" tooltip="${fieldConfig.fieldRemark}" <#if fieldConfig.required == 1>required</#if>>
      <#if fieldConfig.formType == "INPUT">
                <a-input v-model:value="formData.${fieldConfig.fieldName}" placeholder="${fieldConfig.fieldRemark}" allowClear />
      <#elseif fieldConfig.formType == "INPUT_NUMBER">
                <a-input-number v-model:value="formData.${fieldConfig.fieldName}" placeholder="${fieldConfig.fieldRemark}" :max="100" />
      <#elseif fieldConfig.formType == "SELECT">
                <a-select v-model:value="formData.${fieldConfig.fieldName}" placeholder="${fieldConfig.fieldRemark}" :options="exampleOptions" allowClear />
      <#elseif fieldConfig.formType == "RADIO">
                <a-radio-group v-model:value="formData.${fieldConfig.fieldName}" option-type="button" button-style="solid">
                  <a-radio :value="1">选项1</a-radio>
                  <a-radio :value="2">选项2</a-radio>
                </a-radio-group>
      <#elseif fieldConfig.formType == "CHECK_BOX">
                <a-select v-model:value="formData.${fieldConfig.fieldName}" placeholder="${fieldConfig.fieldRemark}" mode="multiple" :options="exampleOptions" allowClear />
      <#elseif fieldConfig.formType == "TEXT_AREA">
                <a-textarea v-model:value="formData.${fieldConfig.fieldName}" placeholder="${fieldConfig.fieldRemark}" allowClear />
      <#elseif fieldConfig.formType == "DATE">
                <a-date-picker v-model:value="formData.${fieldConfig.fieldName}" valueFormat="YYYY-MM-DD"/>
      <#elseif fieldConfig.formType == "DATE_TIME">
                <a-date-picker v-model:value="formData.${fieldConfig.fieldName}" valueFormat="YYYY-MM-DD HH:mm:ss" show-time/>
      </#if>
              </a-form-item>
            </a-col>
    </#if>
  </#list>
</#if>
          </a-row>
        </a-card>
      </a-form>
    </a-spin>
    <!--  底部操作区  -->
    <template #footer>
      <a-flex gap="small" justify="flex-end">
        <a-button type="primary" danger @click="onClose"> 关闭</a-button>
        <a-button type="primary" :loading="submitLoading" @click="onSubmit">保存</a-button>
      </a-flex>
    </template>
  </a-drawer>
</template>
<script setup>
  import ${entityName?uncap_first}Api from '@/api/${moduleName}/${entityName?uncap_first}Api.js'

  import { required } from '@/utils/formRules'
  import { message } from "ant-design-vue"
  import { useSettingsStore } from "@/store"

  // store
  const settingsStore = useSettingsStore()

  const emit = defineEmits({ successful: null })
  // 默认是关闭状态
  const visible = ref(false)
  const title = ref()
  // 计算属性 抽屉宽度
  const drawerWidth = computed(() => {
    return settingsStore.menuCollapsed ? `calc(100% - 80px)` : `calc(100% - 210px)`
  })

  // 是否为编辑
  const edit = ref(false)
  // 表单数据
  const formRef = ref()
  const formData = ref({})
  const dataLoading = ref(false)
  const submitLoading = ref(false)
  // 下拉框选项
  const exampleOptions = [
    { label: "选项一", value: 1 },
    { label: "选项二", value: 2 }
  ]

  // 打开抽屉
  const onOpen = (row) => {
    if (row) {
      edit.value = true
      title.value = "编辑${entityDesc}"
      // 表单数据赋值
      loadData(row)
    } else {
      edit.value = false
      title.value = "新增${entityDesc}"
      // 数据就绪之后显示
      visible.value = true
    }
  }
  // 关闭抽屉
  const onClose = () => {
    formRef.value.resetFields()
    visible.value = false
  }
  // 加载数据
  const loadData = (row) => {
    dataLoading.value = true
    // 组装请求参数
    let param = { id: row.id }
    ${entityName?uncap_first}Api.${entityName?uncap_first}Detail(param).then((res) => {
      formData.value = res.data
    }).finally(() => {
      dataLoading.value = false
      // 数据就绪之后显示
      visible.value = true
    })
  }

  // 验证并提交数据
  const onSubmit = () => {
    formRef.value.validate().then(() => {
      submitLoading.value = true
      // formData.value 加工处理 add/edit
      let fun = ${entityName?uncap_first}Api.add${entityName}
      if (edit.value) {
        fun = ${entityName?uncap_first}Api.edit${entityName}
      }
      // add/edit 发送不同请求
      fun(formData.value).then((res) => {
        message.success(res.message)
        emit('successful')
        onClose()
      }).finally(() => {
        submitLoading.value = false
      })
    }).catch(() => {
    })
  }
  // 调用这个函数将子组件的一些数据和方法暴露出去
  defineExpose({
    onOpen
  })
</script>
<style scoped>
  .ant-form-item {
  }
</style>
