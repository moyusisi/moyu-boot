import request from '@/utils/request'
import service from '@/utils/request'


/**
* ${entityDesc}相关接口
*/
export default {
  // 查询${entityDesc}列表
  ${entityName?uncap_first}List(data) {
    return service.postJson('/api/${moduleName}/${entityName}/list', data)
  },
  // 分页查询${entityDesc}列表
  ${entityName?uncap_first}Page(data) {
    return service.postJson('/api/${moduleName}/${entityName}/page', data)
  },
  // 获取${entityDesc}详情
  ${entityName?uncap_first}Detail(data) {
    return service.postJson('/api/${moduleName}/${entityName}/detail', data)
  },
  // 新增${entityDesc}
  add${entityName}(data) {
    return service.postJson('/api/${moduleName}/${entityName}/add', data)
  },
  // 更新${entityDesc}
  update${entityName}(data) {
    return service.postJson('/api/${moduleName}/${entityName}/update', data)
  },
  // 删除${entityDesc}，通过ids删除
  delete${entityName}(data) {
    return service.postJson('/api/${moduleName}/${entityName}/delete', data)
  },
}
// 查询${entity.className}详细信息
export function get${entity.className}(data) {
  return request({
    url: '/api/${entity.className?uncap_first}/get',
    method: 'get',
    params: data
  })
}

// 查询${entity.className}详细信息
export function query${entity.className}(data) {
  return request({
    url: '/api/${entity.className?uncap_first}/query',
    method: 'post',
    // data是json，params是查询参数
    data: data
  })
}

// 新增${entity.className}
export function add${entity.className}(data) {
  return request.postJson('/api/${entity.className?uncap_first}/add', data)
}

// 修改${entity.className}
export function edit${entity.className}(data) {
  return request.postJson('/api/${entity.className?uncap_first}/edit', data)
}

// 删除${entity.className}
export function delete${entity.className}(data) {
  return request.postForm('/api/${entity.className?uncap_first}/batchDelete', data)
}
