import service from '@/utils/request'

/**
 * ${entityDesc}相关接口
 */
export default {
  // 查询${entityDesc}列表
  ${entityName}List(data) {
    return service.postJson('/api/${moduleName}/${entityName}/list', data)
  },
  // 分页查询${entityDesc}列表
  ${entityName}Page(data) {
    return service.postJson('/api/${moduleName}/${entityName}/page', data)
  },
  // 获取${entityDesc}详情
  ${entityName}Detail(data) {
    return service.postJson('/api/${moduleName}/${entityName}/detail', data)
  },
  // 新增${entityDesc}
  add${className}(data) {
    return service.postJson('/api/${moduleName}/${entityName}/add', data)
  },
  // 更新${entityDesc}
  update${className}(data) {
    return service.postJson('/api/${moduleName}/${entityName}/update', data)
  },
  // 删除${entityDesc}，通过ids删除
  delete${className}(data) {
    return service.postJson('/api/${moduleName}/${entityName}/delete', data)
  }

}
