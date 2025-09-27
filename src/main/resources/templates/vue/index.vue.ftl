<template>
  <!-- 上方选择区 -->
  <a-card size="small">
    <a-form ref="queryFormRef" :model="queryFormData" layout="inline">
<#if fieldList??>
  <#list fieldList as fieldConfig>
    <#if fieldConfig.showInQuery == 1>
      <a-form-item name="${fieldConfig.fieldName}" label="${fieldConfig.fieldComment}" tooltip="${fieldConfig.fieldComment}">
      <#if fieldConfig.formType == "INPUT">
        <a-input v-model:value="queryFormData.${fieldConfig.fieldName}" placeholder="${fieldConfig.fieldComment}" allowClear />
      <#elseif fieldConfig.formType == "INPUT_NUMBER">
        <a-input-number v-model:value="queryFormData.${fieldConfig.fieldName}" placeholder="${fieldConfig.fieldComment}" allowClear />
      <#elseif fieldConfig.formType == "SELECT" || fieldConfig.formType == "RADIO" || fieldConfig.formType == "CHECK_BOX">
        <a-select v-model:value="queryFormData.${fieldConfig.fieldName}" placeholder="${fieldConfig.fieldComment}" :options="exampleOptions" allowClear />
      <#elseif fieldConfig.formType == "TEXT_AREA">
        <a-input v-model:value="queryFormData.${fieldConfig.fieldName}" placeholder="${fieldConfig.fieldComment}" allowClear />
      <#elseif fieldConfig.formType == "DATE">
        <#if fieldConfig.queryType == "BETWEEN">
          <a-date-picker v-model:value="queryFormData.${fieldConfig.fieldName}Range" />
        </#if>
      <#elseif fieldConfig.formType == "DATE_TIME">
        <#if fieldConfig.queryType == "BETWEEN">
          <a-date-picker v-model:value="queryFormData.${fieldConfig.fieldName}Range" :show-time="{ format: 'HH:mm' }" format="YYYY-MM-DD HH:mm"/>
        </#if>
      </#if>
      </a-form-item>
    </#if>
  </#list>
</#if>
      <a-form-item>
        <a-space>
          <a-button type="primary" :icon="h(SearchOutlined)" @click="querySubmit">查询</a-button>
          <a-button :icon="h(RedoOutlined)" @click="reset">重置</a-button>
        </a-space>
      </a-form-item>
    </a-form>
  </a-card>
  <a-card size="small">
    <a-row>
      <a-col :span="20" style="margin-bottom: 12px">
        <a-space wrap>
          <a-button type="primary" :icon="h(PlusOutlined)" @click="xx.onOpen(module)">新增</a-button>
          <a-popconfirm title="确定要批量删除吗？" :disabled ="selectedRowKeys.length < 1" @confirm="batchDelete">
            <a-button danger :icon="h(DeleteOutlined)" :disabled="selectedRowKeys.length < 1">
              批量删除
            </a-button>
          </a-popconfirm>
        </a-space>
      </a-col>
      <a-col :span="4" style="text-align: right">
        <a-button @click="addRows" :icon="h(PlusOutlined)" class="custom-btn">导入</a-button>
      </a-col>
    </a-row>
  </a-card>
</template>

<script setup>
  import ${entityName?uncap_first}Api from '@/api/${moduleName}/${entityName?uncap_first}Api.js'

  import { h } from "vue"
  import { PlusOutlined, DeleteOutlined, RedoOutlined, SearchOutlined } from "@ant-design/icons-vue"
  import { message } from "ant-design-vue"
  import STable from "@/components/STable/index.vue"

  const queryFormRef = ref()
  const queryFormData = ref({})
  const addFormRef = ref()
  const editFormRef = ref()
  const tableRef = ref()
  const toolConfig = { refresh: true, height: true, columnSetting: false, striped: false }
  const columns = [
    {
      title: '显示名称',
      dataIndex: 'name',
      resizable: true,
      width: 180
    },
    {
      title: '接口地址',
      dataIndex: 'path',
      ellipsis: true,
      width: 150
    },
    {
      title: '操作',
      dataIndex: 'action',
      align: 'center',
      width: 150
    }
  ]

  let selectedRowKeys = ref([])
  // 列表选择配置
  const options = {
    alert: {
      show: false,
      clear: () => {
      selectedRowKeys = ref([])
      }
    },
    rowSelection: {
      onChange: (selectedRowKey, selectedRows) => {
        selectedRowKeys.value = selectedRowKey
      }
    }
  }

  // 下拉框选项
  const exampleOptions = [
    { label: "选项一", value: 1 },
    { label: "选项二", value: 2 }
  ]

  const loadData = async (parameter) => {

  }

  // 查询
  const querySubmit = () => {
    tableRef.value.refresh(true)
  }
  // 重置
  const reset = () => {
    queryFormRef.value.resetFields()
    tableRef.value.refresh(true)
  }
  // 删除
  const delete${entityName} = (record) => {
    let data = { ids: [record.id] }
    ${entityName?uncap_first}Api.delete${entityName}(data).then((res) => {
      message.success(res.message)
      tableRef.value.refresh()
    })
  }
  // 批量删除
  const batchDelete = (record) => {
    if (selectedRowKeys.value.length < 1) {
      message.warning('请至少选择一条数据')
      return
    }
    let data = { ids: selectedRowKeys.value }
    ${entityName?uncap_first}Api.delete${entityName}(data).then((res) => {
      message.success(res.message)
      tableRef.value.refresh()
    })
  }
</script>

<style scoped>
  /** 后代选择器 **/
  .ant-card-small .ant-form-inline {
    margin-bottom: -12px !important;
  }
  /** 直接后代选择器 **/
  .ant-form-inline > .ant-form-item {
    margin-bottom: 12px !important;
  }
</style>
