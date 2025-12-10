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
    <!--  表格上方操作区  -->
    <a-row>
      <!--  左侧按钮  -->
      <a-col :span="20" style="margin-bottom: 8px">
        <a-space wrap>
          <a-button type="primary" :icon="h(PlusOutlined)" @click="formRef.onOpen()">新增</a-button>
          <a-popconfirm :title=" '确定要删除这 ' + selectedRowKeys.length + ' 条数据吗？' " :disabled ="selectedRowKeys.length < 1" @confirm="batchDelete">
            <a-button danger :icon="h(DeleteOutlined)" :disabled="selectedRowKeys.length < 1">
              批量删除
            </a-button>
          </a-popconfirm>
        </a-space>
      </a-col>
      <!--  右侧操作区  -->
      <a-col :span="4">
        <a-flex gap="small" class="tool-area" justify="flex-end" align="flex-start">
          <a-button :icon="h(PlusOutlined)" class="custom-btn" @click="null">操作</a-button>
        </a-flex>
      </a-col>
    </a-row>
    <!--  表格数据区  -->
    <a-table size="middle"
             ref="tableRef"
             :columns="columns"
             :data-source="tableData"
             :loading="dataLoading"
             :row-key="(record) => record.id"
             :row-selection="rowSelection"
             :pagination="paginationRef"
             @change="onChange"
             @resizeColumn="onResizeColumn"
             :scroll="{ x: tableWidth }"
             bordered>
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
    </a-table>
  </a-card>
  <Form ref="formRef" @successful="loadData" />
<#if detailOpenType == 1>
  <Detail ref="detailRef"/>
</#if>
</template>

<script setup>
  import ${entityName?uncap_first}Api from '@/api/${moduleName}/${entityName?uncap_first}Api.js'

  import { h } from "vue"
  import { PlusOutlined, DeleteOutlined, RedoOutlined, SearchOutlined, DownOutlined, UpOutlined } from "@ant-design/icons-vue"
  import { message } from "ant-design-vue"
  import { useSettingsStore } from "@/store"
  import Form from "./form.vue"
<#if detailOpenType == 1>
  import Detail from "./detail.vue"
</#if>

  // store
  const settingsStore = useSettingsStore()

  // 计算属性 表格宽度 超过宽度则会出现x轴上的scroll
  const tableWidth = computed(() => {
    return settingsStore.menuCollapsed ? `calc(100% - 80px -24px)` : `calc(100% - 210px -24px)`
  })

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
  <#if detailOpenType == 1>
  const detailRef = ref()
  </#if>

  /***** 表格相关对象 start *****/
  const tableRef = ref()
  // 表格的数据源
  const tableData = ref([])
  const dataLoading = ref(false)
  // 已选中的行
  const selectedRowKeys = ref([])
  // 表格行选择配置
  const rowSelection = ref({
    selectedRowKeys: selectedRowKeys,
    onChange: (selectedKeys, selectedRows) => {
      selectedRowKeys.value = selectedKeys
      // console.log('onChange,selectedKeys:', selectedKeys);
    }
  });
  // 表格的分页配置
  const paginationRef = ref({
    // 当前页码
    current: 1,
    // 每页显示条数
    pageSize: 10,
    // 总条数，需要通过接口获取
    total: 0,
    // 显示总记录数
    showTotal: (total, range) => <#noparse>`共 ${total} 条 `,</#noparse>
    // 是否可改变每页显示条数
    showSizeChanger: true,
    // 只有一页或没有数据时隐藏分页栏
    // hideOnSinglePage: true,
    onChange: (page, pageSize) => {
      // 处理分页切换的逻辑
      paginationRef.value.current = page
      paginationRef.value.pageSize = pageSize
    },
  })
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
    loadData()
  })

  // 提交查询
  const querySubmit = () => {
    loadData()
  }
  // 重置
  const reset = () => {
    queryFormRef.value.resetFields()
    paginationRef.value.current = 1
    loadData()
  }
  // 加载数据
  const loadData = (parameter) => {
    dataLoading.value = true
    // 重新加载数据时，清空之前以选中的行
    selectedRowKeys.value = []
    // 分页参数
    let param = { pageNum: paginationRef.value.current, pageSize: paginationRef.value.pageSize }
    return ${entityName?uncap_first}Api.${entityName?uncap_first}Page(Object.assign(param, queryFormData.value)).then((res) => {
      paginationRef.value.total = res.data.total
      tableData.value = res.data.records
      // res.data 为 {total, records}
      return res.data
    }).catch((err) => {
      console.error(err)
    }).finally(() => {
      dataLoading.value = false
    })
  }
  // 分页、排序、筛选等操作变化时，会触发 change 事件
  const onChange = (pagination, filters, sorter) => {
    loadData()
  }
  // 可伸缩列
  const onResizeColumn = (w, column) => {
    column.width = w
  }
  // 删除
  const delete${entityName} = (record) => {
    let data = { ids: [record.id] }
    ${entityName?uncap_first}Api.delete${entityName}(data).then((res) => {
      message.success(res.message)
      loadData()
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
      loadData()
    })
  }
  // 打开详情页
  const openDetail = (row) => {
    <#if detailOpenType == 1>
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
  /** 操作区 **/
  .tool-area {
    width: 100%;
    height: 100%;
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
