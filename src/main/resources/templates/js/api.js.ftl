import service from '@/utils/request'

/**
 * ${entityDesc}相关接口
 */
export default {
  // 查询${entityDesc}列表
  ${entityName?uncap_first}List(data) {
    return service.postJson('/api/${moduleName}/${entityName?uncap_first}/list', data)
  },
  // 分页查询${entityDesc}列表
  ${entityName?uncap_first}Page(data) {
    return service.postJson('/api/${moduleName}/${entityName?uncap_first}/page', data)
  },
  // 获取${entityDesc}详情
  ${entityName?uncap_first}Detail(data) {
    return service.postJson('/api/${moduleName}/${entityName?uncap_first}/detail', data)
  },
  // 新增${entityDesc}
  add${entityName}(data) {
    return service.postJson('/api/${moduleName}/${entityName?uncap_first}/add', data)
  },
  // 编辑${entityDesc}
  edit${entityName}(data) {
    return service.postJson('/api/${moduleName}/${entityName?uncap_first}/edit', data)
  },
  // 删除${entityDesc}，通过ids删除
  delete${entityName}(data) {
    return service.postJson('/api/${moduleName}/${entityName?uncap_first}/delete', data)
  }

}
