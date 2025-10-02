<template>
  <a-drawer
      :open="visible"
      :title="title"
      :width="drawerWidth"
      :closable="false"
      :destroy-on-close="true"
      @close="onClose"
  >
    <#--  上方操作区  -->
    <template #extra>
        <a-button type="primary" size="small" @click="onClose"><CloseOutlined /></a-button>
    </template>
    <#--  数据区  -->
    <a-spin :spinning="dataLoading">
      <a-card title="基本信息">
        <a-form ref="formRef" :model="formData" :label-col="{span: 6}">
          <a-row :gutter="24">
<#if fieldList??>
  <#list fieldList as fieldConfig>
            <a-col :span="12">
              <a-form-item name="${fieldConfig.fieldName}" label="${fieldConfig.fieldRemark[0..*6]}" tooltip="${fieldConfig.fieldRemark}" <#if fieldConfig.required == 1>required</#if>>
                <a-input v-model:value="formData.${fieldConfig.fieldName}" placeholder="${fieldConfig.fieldRemark}" allowClear />
              </a-form-item>
            </a-col>
  </#list>
</#if>
          </a-row>
        </a-form>
      </a-card>
    </a-spin>
    <#--  底部操作区  -->
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
  import { useSettingsStore } from "@/store";
  import { message } from "ant-design-vue"

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

  // 表单数据
  const formRef = ref()
  const formData = ref({})
  const dataLoading = ref(false)
  const submitLoading = ref(false)

  // 打开抽屉
  const onOpen = (record) => {
    visible.value = true
    if (record) {
      title.value = "编辑${entityDesc}"
      // 表单数据赋值
      loadData()
    } else {
      title.value = "新增${entityDesc}"
    }
  }
  // 关闭抽屉
  const onClose = () => {
    formRef.value = {}
    visible.value = false
  }
  // 加载数据
  const loadData = () => {
    dataLoading.value = true
    // 组装请求参数
    let param = { }
    ${entityName?uncap_first}Api.${entityName?uncap_first}Detail(param).then((res) => {
      formData.value = res.data
    }).finally(() => {
      submitLoading.value = false
      dataLoading.value = false
    })
  }

  // 验证并提交数据
  const onSubmit = () => {
    formRef.value.validate().then(() => {
      submitLoading.value = true
      // formData.value 加工处理 TODO add edit
      let fun = ${entityName?uncap_first}Api.add${entityName}
      if (formData.value.id) {
        // 编辑
        fun = ${entityName?uncap_first}Api.edit${entityName}
      }
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
