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
    <vxe-grid ref="gridRef" v-bind="gridOptions">
      <!-- 左侧操作栏 -->
      <template #toolbarButtons>
        <a-space wrap style="margin-bottom: 6px">
          <a-button type="primary" :icon="h(PlusOutlined)" @click="formRef.onOpen()">新增</a-button>
          <a-button danger :icon="h(DeleteOutlined)" @click="gridRef?.commitProxy('delete')">批量删除</a-button>
        </a-space>
      </template>
      <!-- 字段插槽 -->
      <template #id="{row, rowIndex, column, columnIndex}">
        <a @click="openDetail(row)">{{ row.id }}</a>
      </template>
      <template #action="{row, rowIndex, column, columnIndex}">
        <a-space>
          <a-tooltip title="编辑">
            <a @click="formRef.onOpen(row)"><FormOutlined /></a>
          </a-tooltip>
          <a-divider type="vertical" />
          <a-tooltip title="删除">
            <a-popconfirm title="确定要删除吗？" @confirm="delete${entityName}(row)">
              <a style="color:#FF4D4F;"><DeleteOutlined/></a>
            </a-popconfirm>
          </a-tooltip>
        </a-space>
      </template>
    </vxe-grid>
  </a-card>
  <Form ref="formRef" @successful="refresh()"/>
<#if detailOpenType == 0>
  <Detail ref="detailRef"/>
</#if>
</template>

<script setup>
  import ${entityName?uncap_first}Api from '@/api/${moduleName}/${entityName?uncap_first}Api.js'

  import { h, ref } from "vue"
  import { useRoute, useRouter } from "vue-router"
  import { PlusOutlined, DeleteOutlined, RedoOutlined, SearchOutlined, DownOutlined, UpOutlined } from "@ant-design/icons-vue"
  import { message } from "ant-design-vue"
  import Form from "./form.vue"
<#if detailOpenType == 0>
  import Detail from "./detail.vue"
</#if>

  // store
  const route = useRoute();
  const router = useRouter();

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
<#if detailOpenType == 0>
  const detailRef = ref()
</#if>

  /***** 表格相关对象 start *****/
  const gridRef = ref()
  const gridOptions = ref({
    // 分页配置项
    pagerConfig: {
      enabled: true,
    },
    // 排序配置项
    sortConfig: {
      // 服务端排序
      remote: true,
    },
    // 数据代理配置
    proxyConfig: {
      // 获取响应的值配置
      response: {
        // 只对 pager-config 配置时有效，响应结果中获取数据列表的属性（分页场景）
        result: "records",
        // 只对 pager-config 配置时有效，响应结果中获取分页的属性（分页场景）
        total: "total",
      },
      // 启用排序请求代理
      sort: true,
      // 代理配置
      ajax: {
        query: ({ page, sort, sorts, filters, form }) => {
          const sortItem = sorts[0] || {}
          // console.log(sortItem)
          // 默认接收 Promise<{ result: [], page: { total: 100 } }>
          return loadData({
            pageNum: page.currentPage,
            pageSize: page.pageSize,
            sortField: sortItem.field,
            sortOrder: sortItem.order
          })
        },
        delete: ({ body, form }) => {
          // 删除已选
          const ids = body.removeRecords.map(item => item.id);
          return ${entityName?uncap_first}Api.delete${entityName}({ ids })
        }
      }
    },
    // 列字段
    columns: [
      { type: 'checkbox', width: 50 },
      { type: 'seq', width: 50 },
      { field: 'id', title: '唯一ID', width: 100, sortable: true, slots: { default: 'id' } },
<#if fieldList??>
  <#list fieldList as fieldConfig>
    <#if fieldConfig.showInList == 1>
      <#if fieldConfig.fieldType == "Date">
      { field: '${fieldConfig.fieldName}', title: '${fieldConfig.fieldRemark}', width: 170 },
      <#elseif fieldConfig.fieldType == "String">
      { field: '${fieldConfig.fieldName}', title: '${fieldConfig.fieldRemark[0..*8]}', width: 150 },
      <#else>
      { field: '${fieldConfig.fieldName}', title: '${fieldConfig.fieldRemark[0..*8]}', width: 100 },
      </#if>
    </#if>
  </#list>
</#if>
      { field: 'action', title: '操作', width: 100, slots: { default: 'action' } },
    ],
    // 工具栏配置
    toolbarConfig: {
      // 是否显示个性化列配置
      custom: true,
      // 是否允许最大化显示
      zoom: true,
      // 刷新按钮配置
      refresh: true,
      // 插槽
      slots: {
        // 操作栏按钮
        buttons: "toolbarButtons",
      },
    },
  })
  /***** 表格相关对象 end *****/

  // 加载完毕调用
  onMounted(() => {

  })

  // 提交查询
  const querySubmit = () => {
    // reload 返回第一页触发ajax.query
    // query 当前页触发ajax.query
    gridRef.value?.commitProxy("reload")
  }
  // 重置
  const reset = () => {
    queryFormRef.value.resetFields()
    refresh()
  }
  // 重置
  const refresh = () => {
    // 返回第一页触发ajax.query
    gridRef.value?.commitProxy("reload")
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

  // 删除
  const delete${entityName} = (record) => {
    let data = { ids: [record.id] }
    ${entityName?uncap_first}Api.delete${entityName}(data).then((res) => {
      message.success(res.message)
      refresh()
    })
  }

  // 打开详情页
  const openDetail = (row) => {
    <#if detailOpenType == 0>
    detailRef.value.onOpen(row)
    // 独立页面打开(与抽屉打开二选一)
    // router.push({ path: "/${moduleName}/${entityName?uncap_first}/detail", query: { id: row.id } })
    <#else>
    router.push({ path: "/${moduleName}/${entityName?uncap_first}/detail", query: { id: row.id } })
    </#if>
  }
</script>

<style scoped>
  /** 后代选择器 **/
  .ant-card .ant-form {
    margin-bottom: -12px !important;
  }
  .ant-card .ant-form-item {
    margin-bottom: 12px !important;
  }
</style>
