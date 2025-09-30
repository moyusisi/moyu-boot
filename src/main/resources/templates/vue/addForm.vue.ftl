<template>
  <a-drawer
          :open="visible"
          title="新增岗位(Group)"
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
      <a-card  size="small">
        <a-form ref="formRef" :model="formData" layout="inline">
<#if fieldList??>
  <#list fieldList as fieldConfig>
          <a-form-item name="${fieldConfig.fieldName}" label="${fieldConfig.fieldRemark}" tooltip="${fieldConfig.fieldRemark}" <#if fieldConfig.required>required</#if>
            <a-input v-model:value="formData.${fieldConfig.fieldName}" placeholder="${fieldConfig.fieldRemark}" allowClear />
          </a-form-item>
  </#list>
</#if>
        </a-form>
      </a-card>
    </a-spin>
      <#--  底部操作区  -->
      <template #footer>
        <a-flex gap="small" justify="flex-end">
            <a-button @click="onClose">关闭</a-button>
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

  // 默认是关闭状态
  const visible = ref(false)
  const title = ref()
  const emit = defineEmits({ successful: null })

  // 表单数据
  const formRef = ref()
  const formData = ref()
  const submitLoading = ref(false)
  const dataLoading = ref(false)

  // 计算属性 抽屉宽度
  const drawerWidth = computed(() => {
    return settingsStore.menuCollapsed ? `calc(100% - 80px)` : `calc(100% - 210px)`
  })

  // 打开抽屉
  const onOpen = (record) => {
    visible.value = true
    title.value = "新增" + ${entityDesc}
    <#--title.value = "编辑" + ${entityDesc}-->
    // 表单数据赋值
    loadData()
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
    let param = { pageNum: paginationRef.value.current, pageSize: paginationRef.value.pageSize }
    ${entityName?uncap_first}Api.${entityName?uncap_first}Detail(param).then((res) => {
      formData.value = res.data
    }).finally(() => {
      submitLoading.value = false
    })
  }

  // 验证并提交数据
  const onSubmit = () => {
    formRef.value.validate().then(() => {
      submitLoading.value = true
      // formData.value 加工处理 TODO add edit
      ${entityName?uncap_first}Api.add${entityName}(formData.value).then((res) => {
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