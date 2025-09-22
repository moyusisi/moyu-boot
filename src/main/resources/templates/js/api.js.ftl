import request from '@/utils/request'

// 查询${entity.className}列表
export function list${entity.className}(data) {
  return request.postJson('/api/${entity.className?uncap_first}/list', data)
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
