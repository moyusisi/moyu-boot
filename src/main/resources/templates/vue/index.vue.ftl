<template>
  <!-- 上方查询区 -->
  <a-card size="small">
    <a-form ref="queryFormRef" :model="queryFormData">
      <a-row :gutter="24">
<#if fieldList??>
  <#assign totalQuery = 0>
  <#list fieldList as fieldConfig>
    <#if fieldConfig.showInQuery == 1>
      <#assign totalQuery = totalQuery + 1>
    </#if>
  </#list>
  <#assign countQuery = 0>
  <#list fieldList as fieldConfig>
    <#if fieldConfig.showInQuery == 1>
      <#assign countQuery = countQuery + 1>
      <#if (countQuery <= 3)>
        <a-col :span="6">
      <#else>
        <a-col :span="6" v-if="showMore">
      </#if>
      <#if (fieldConfig.formType == "INPUT" || fieldConfig.formType == "TEXT_AREA")>
        <#if fieldConfig.queryType == "LIKE">
          <a-form-item name="${fieldConfig.fieldName}" label="${fieldConfig.fieldRemark[0..*6]}">
            <a-input v-model:value="queryFormData.${fieldConfig.fieldName}" placeholder="搜索${fieldConfig.fieldRemark}" allowClear />
          </a-form-item>
        <#else>
          <a-form-item name="${fieldConfig.fieldName}" label="${fieldConfig.fieldRemark[0..*6]}">
            <a-input v-model:value="queryFormData.${fieldConfig.fieldName}" placeholder="请输入${fieldConfig.fieldRemark}" allowClear />
          </a-form-item>
        </#if>
      <#elseif fieldConfig.formType == "INPUT_NUMBER">
          <a-form-item name="${fieldConfig.fieldName}" label="${fieldConfig.fieldRemark[0..*6]}">
            <a-input-number v-model:value="queryFormData.${fieldConfig.fieldName}" placeholder="${fieldConfig.fieldRemark}" allowClear />
          </a-form-item>
      <#elseif fieldConfig.formType == "SELECT" || fieldConfig.formType == "RADIO" || fieldConfig.formType == "CHECK_BOX">
          <a-form-item name="${fieldConfig.fieldName}" label="${fieldConfig.fieldRemark[0..*6]}">
            <a-select v-model:value="queryFormData.${fieldConfig.fieldName}" placeholder="${fieldConfig.fieldRemark}" :options="exampleOptions" allowClear />
          </a-form-item>
      <#elseif fieldConfig.formType == "DATE">
        <#if fieldConfig.queryType == "BETWEEN">
          <a-form-item name="${fieldConfig.fieldName}1" label="起始日期">
            <a-date-picker v-model:value="queryFormData.${fieldConfig.fieldName}1" placeholder="起始日期" format="YYYY-MM-DD" valueFormat="YYYY-MM-DD HH:mm:ss"/>
          </a-form-item>
          <a-form-item name="${fieldConfig.fieldName}2" label="截止日期">
            <a-date-picker v-model:value="queryFormData.${fieldConfig.fieldName}2" placeholder="截止日期" format="YYYY-MM-DD" valueFormat="YYYY-MM-DD HH:mm:ss"/>
          </a-form-item>
        <#else>
          <a-form-item name="${fieldConfig.fieldName}" label="${fieldConfig.fieldRemark[0..*6]}">
            <a-date-picker v-model:value="queryFormData.${fieldConfig.fieldName}" placeholder="请选择日期" format="YYYY-MM-DD" valueFormat="YYYY-MM-DD HH:mm:ss"/>
          </a-form-item>
        </#if>
      <#elseif fieldConfig.formType == "DATE_TIME">
        <#if fieldConfig.queryType == "BETWEEN">
          <a-form-item name="${fieldConfig.fieldName}1" label="起始时间">
            <a-date-picker v-model:value="queryFormData.${fieldConfig.fieldName}1" placeholder="起始时间" :showTime="{ format: 'HH:mm:ss' }" format="YYYY-MM-DD HH:mm:ss" valueFormat="YYYY-MM-DD HH:mm:ss"/>
          </a-form-item>
          <a-form-item name="${fieldConfig.fieldName}2" label="截止时间">
            <a-date-picker v-model:value="queryFormData.${fieldConfig.fieldName}2" placeholder="截止时间" :showTime="{ format: 'HH:mm:ss' }" format="YYYY-MM-DD HH:mm:ss" valueFormat="YYYY-MM-DD HH:mm:ss"/>
          </a-form-item>
        <#else>
          <a-form-item name="${fieldConfig.fieldName}" label="${fieldConfig.fieldRemark[0..*6]}">
            <a-date-picker v-model:value="queryFormData.${fieldConfig.fieldName}" placeholder="请选择时间" :showTime="{ format: 'HH:mm:ss' }" format="YYYY-MM-DD HH:mm:ss" valueFormat="YYYY-MM-DD HH:mm:ss"/>
          </a-form-item>
        </#if>
      </#if>
        </a-col>
      <#if (totalQuery <= 3 && countQuery == totalQuery)>
      <#-- 少于等于3个条件时,最后一个条件之后添加查询按钮 -->
        <a-col :span="6">
          <a-form-item>
            <a-flex gap="small">
              <a-button type="primary" :icon="h(SearchOutlined)" @click="querySubmit">查询</a-button>
              <a-button :icon="h(RedoOutlined)" @click="reset">重置</a-button>
            </a-flex>
          </a-form-item>
        </a-col>
      <#-- 大于3个条件时,条件3后插入查询按钮 -->
      <#elseif (totalQuery > 3 && countQuery == 3)>
        <a-col :span="6">
          <a-form-item>
            <a-flex gap="small">
              <a-button type="primary" :icon="h(SearchOutlined)" @click="querySubmit">查询</a-button>
              <a-button :icon="h(RedoOutlined)" @click="reset">重置</a-button>
              <a-button v-if="!showMore" type="link" @click="showMore = !showMore">更多条件<DownOutlined /></a-button>
              <a-button v-else type="link"  @click="showMore = !showMore">收起<UpOutlined /></a-button>
            </a-flex>
          </a-form-item>
        </a-col>
      </#if>
    </#if>
  </#list>
</#if>
      </a-row>
    </a-form>
  </a-card>
  <a-card size="small">
    <!--  表格数据区  -->
    <MTable ref="tableRef"
            :columns="columns"
            :loadData="loadData"
            :row-key="(row) => row.id"
            showRowSelection
            @selectedChange="onSelectedChange"
    >
      <!--  表格上方左侧操作区  -->
      <template #operator>
        <a-space wrap style="margin-bottom: 8px">
          <a-button type="primary" :icon="h(PlusOutlined)" @click="formRef.onOpen()">新增</a-button>
          <a-popconfirm :title=" '确定要删除这 ' + selectedRowKeys.length + ' 条数据吗？' " :disabled ="selectedRowKeys.length < 1" @confirm="batchDelete">
            <a-button danger :icon="h(DeleteOutlined)" :disabled="selectedRowKeys.length < 1">
              批量删除
            </a-button>
          </a-popconfirm>
        </a-space>
      </template>
      <template #bodyCell="{ column, record, index, text }">
        <template v-if="column.dataIndex === 'index'">
          <span>{{ index + 1 }}</span>
        </template>
        <template v-if="column.dataIndex === 'id'">
          <!-- 唯一键点击查看详情 -->
          <a-tooltip :title="text" placement="topLeft">
            <!--<a style="text-decoration: underline;" @click="openDetail(record)">{{ text }}</a>-->
            <a @click="openDetail(record)">{{ text }}</a>
          </a-tooltip>
        </template>
<#if fieldList??>
  <#list fieldList as fieldConfig>
    <#if fieldConfig.showInList == 1 && fieldConfig.ellipsis == 1>
        <template v-if="column.dataIndex === '${fieldConfig.fieldName}'">
          <!-- 长文本省略提示 -->
          <a-tooltip :title="text" placement="topLeft">
            <span>{{ text }}</span>
          </a-tooltip>
        </template>
    </#if>
  </#list>
</#if>
        <template v-if="column.dataIndex === 'action'">
          <a-space>
            <a-tooltip title="编辑">
              <a @click="formRef.onOpen(record)">编辑</a>
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
  <Form ref="formRef" @successful="tableRef.refresh()"/>
  <Detail ref="detailRef"/>
</template>

<script setup>
  import ${entityName?uncap_first}Api from '@/api/${moduleName}/${entityName?uncap_first}Api.js'

  import { h, ref } from "vue"
  import { PlusOutlined, DeleteOutlined, RedoOutlined, SearchOutlined, DownOutlined, UpOutlined } from "@ant-design/icons-vue"
  import { message } from "ant-design-vue"
  import MTable from "@/components/MTable/index.vue"
  import Form from "./form.vue"
  import Detail from "./detail.vue"

  // 查询表单相关对象
  const queryFormRef = ref()
  const queryFormData = ref({})
  <#if (totalQuery > 3)>
  // 是否展示更多搜索条件
  const showMore = ref(false)
  </#if>
  // 下拉框选项
  const exampleOptions = [
    { label: "选项一", value: 1 },
    { label: "选项二", value: 2 }
  ]
  // 其他页面操作
  const formRef = ref()
  const detailRef = ref()

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
        <#if fieldConfig.ellipsis == 1>
      ellipsis: true,
        </#if>
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
    tableRef.value.refresh(true)
  }
  // 重置
  const reset = () => {
    queryFormRef.value.resetFields()
    tableRef.value.refresh(true)
  }
  // 加载数据
  const loadData = (parameter) => {
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
  const batchDelete = () => {
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
  // 打开详情页
  const openDetail = (row) => {
    detailRef.value.onOpen(row)
    // 独立页面打开(与抽屉打开二选一)
    // router.push({ path: "/${moduleName}/${entityName?uncap_first}/detail", query: { id: row.id } })
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
</style>
