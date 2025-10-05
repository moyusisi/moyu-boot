<template>
  <!-- 上方选择区 -->
  <a-card size="small">
    <a-form ref="queryFormRef" :model="queryFormData">
      <a-row :gutter="24">
<#if fieldList??>
  <#list fieldList as fieldConfig>
    <#if fieldConfig.showInQuery == 1>
        <a-col :span="6">
          <a-form-item name="${fieldConfig.fieldName}" label="${fieldConfig.fieldRemark[0..*6]}">
      <#if fieldConfig.formType == "INPUT">
            <a-input v-model:value="queryFormData.${fieldConfig.fieldName}" placeholder="${fieldConfig.fieldRemark}" allowClear />
      <#elseif fieldConfig.formType == "INPUT_NUMBER">
            <a-input-number v-model:value="queryFormData.${fieldConfig.fieldName}" placeholder="${fieldConfig.fieldRemark}" allowClear />
      <#elseif fieldConfig.formType == "SELECT" || fieldConfig.formType == "RADIO" || fieldConfig.formType == "CHECK_BOX">
            <a-select v-model:value="queryFormData.${fieldConfig.fieldName}" placeholder="${fieldConfig.fieldRemark}" :options="exampleOptions" allowClear />
      <#elseif fieldConfig.formType == "TEXT_AREA">
            <a-input v-model:value="queryFormData.${fieldConfig.fieldName}" placeholder="${fieldConfig.fieldRemark}" allowClear />
      <#elseif fieldConfig.formType == "DATE">
        <#if fieldConfig.queryType == "BETWEEN">
            <a-range-picker v-model:value="queryFormData.${fieldConfig.fieldName}Range" valueFormat="YYYY-MM-DD"/>
        </#if>
      <#elseif fieldConfig.formType == "DATE_TIME">
        <#if fieldConfig.queryType == "BETWEEN">
            <a-range-picker v-model:value="queryFormData.${fieldConfig.fieldName}Range" valueFormat="YYYY-MM-DD HH:mm:ss" :show-time="{ format: 'HH:mm' }" format="YYYY-MM-DD HH:mm"/>
        </#if>
      </#if>
          </a-form-item>
        </a-col>
    </#if>
  </#list>
</#if>
        <a-col :span="6">
          <a-flex gap="small">
            <a-button type="primary" :icon="h(SearchOutlined)" @click="querySubmit">查询</a-button>
            <a-button :icon="h(RedoOutlined)" @click="reset">重置</a-button>
          </a-flex>
        </a-col>
      </a-row>
    </a-form>
  </a-card>
  <a-card size="small">
    <#--  表格数据区  -->
    <MTable ref="tableRef"
            :columns="columns"
            :loadData="loadData"
            :row-key="(row) => row.id"
            showRowSelection
            @selectedChange="onSelectedChange"
    >
      <template #operator>
        <a-space wrap style="margin-bottom: 6px">
          <a-button type="primary" :icon="h(PlusOutlined)" @click="editFormRef.onOpen()">新增</a-button>
          <a-popconfirm :title=" '确定要删除这 ' + selectedRowKeys.length + ' 条数据吗？' " :disabled ="selectedRowKeys.length < 1" @confirm="batchDelete">
            <a-button danger :icon="h(DeleteOutlined)" :disabled="selectedRowKeys.length < 1">
              批量删除
            </a-button>
          </a-popconfirm>
        </a-space>
      </template>
      <template #bodyCell="{ column, record, index, text }">
        <!-- 长文本省略显示 -->
        <template v-if="text && text.length > 24">
          <a-tooltip :title="text">
            <span class="large-text">{{ text }}</span>
          </a-tooltip>
        </template>
        <template v-if="column.dataIndex === 'index'">
          <span>{{ index + 1 }}</span>
        </template>
        <template v-if="column.dataIndex === 'action'">
          <a-space>
            <a-tooltip title="编辑">
              <a @click="editFormRef.onOpen(record)">编辑</a>
            </a-tooltip>
            <a-tooltip title="删除">
              <a-popconfirm title="确定要删除吗？" @confirm="delete${entityName}(record)">
                <a style="color:#FF4D4F;">删除</a>
              </a-popconfirm>
            </a-tooltip>
          </a-space>
        </template>
      </template>
    </MTable>
  </a-card>
  <EditForm ref="editFormRef" @successful="tableRef.refresh()" />
</template>

<script setup>
  import ${entityName?uncap_first}Api from '@/api/${moduleName}/${entityName?uncap_first}Api.js'

  import { h, ref } from "vue"
  import { PlusOutlined, DeleteOutlined, RedoOutlined, SearchOutlined } from "@ant-design/icons-vue"
  import { message } from "ant-design-vue"
  import EditForm from "./editForm.vue"
  import MTable from "@/components/MTable/index.vue"

  // 查询表单相关对象
  const queryFormRef = ref()
  const queryFormData = ref({})
  // 下拉框选项
  const exampleOptions = [
    { label: "选项一", value: 1 },
    { label: "选项二", value: 2 }
  ]
  // 其他页面操作
  const editFormRef = ref()

  /***** 表格相关对象 start *****/
  const tableRef = ref()
  // 已选中的行
  const selectedRowKeys = ref([])
  // 表格列配置
  const columns = ref([
    // 不需要序号可以删掉
    {
      title: '序号',
      dataIndex: 'index',
      align: 'center',
      width: 50,
    },
<#if fieldList??>
  <#list fieldList as fieldConfig>
    <#if fieldConfig.showInList == 1>
      <#if fieldConfig.fieldType == "Date">
    {
      title: "${fieldConfig.fieldRemark}",
      dataIndex: "${fieldConfig.fieldName}",
      align: "center",
      width: 160,
    },
      <#elseif fieldConfig.fieldType == "String">
    {
      title: "${fieldConfig.fieldRemark[0..*8]}",
      dataIndex: "${fieldConfig.fieldName}",
      align: "center",
      resizable: true,
      width: 150,
    },
    <#else>
    {
      title: "${fieldConfig.fieldRemark[0..*8]}",
      dataIndex: "${fieldConfig.fieldName}",
      align: "center",
      resizable: true,
      width: 100,
    },
      </#if>
    </#if>
  </#list>
</#if>
    // 单行操作，不需要可以删掉
    {
      title: '操作',
      dataIndex: 'action',
      align: 'center',
      width: 100,
    },
  ])
  /***** 表格相关对象 end *****/

  // 加载完毕调用
  onMounted(() => {

  })

  // 提交查询
  const querySubmit = () => {
    tableRef.value.refresh()
  }
  // 重置
  const reset = () => {
    queryFormRef.value.resetFields()
    tableRef.value.refresh(true)
  }
  // 加载数据
  const loadData = (parameter) => {
    // 重新加载数据时，清空之前以选中的行
    selectedRowKeys.value = []
    // 分页参数
    let param = Object.assign(parameter, queryFormData.value)
    return ${entityName?uncap_first}Api.${entityName?uncap_first}Page(param).then((res) => {
      // res.data 为 {total, records}
      return res.data
    }).catch((err) => {
      console.error(err)
    })
  }
  // 选中行发生变化
  const onSelectedChange = (selectedKeys, selectedRows) => {
    selectedRowKeys.value = selectedKeys
    // console.log('onSelectedChange,selectedKeys:', selectedKeys);
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
      message.warning("请至少选择一条数据")
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
  .ant-card .ant-form {
    margin-bottom: -12px !important;
  }
  .ant-form-item {
    margin-bottom: 12px !important;
  }
  .custom-btn {
    background-color: #52C41AFF;
    border-color: #52C41AFF;
    color: #fff;
  }
  .custom-btn:hover {
    background-color: #79D84B;
    border-color: #79D84B;
    color: #fff;
  }
  /** 长文本截断,超过200px省略(约26个字母，15个汉字的长度) **/
  .large-text {
    display: inline-block;
    width: 200px;
    overflow-x: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    cursor: pointer;
  }
</style>
