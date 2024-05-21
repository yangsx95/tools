"""
Time:     2024-05-11 10:28
Author:   杨顺翔
File:     分组组织架构sql生成.py
Describe: 分组组织架构上线的相关sql（不是所有，主要是sys_manager库的修改）。
          主要包含：
              1. 插入分组
              2. 维护分组关系
              3. 根据某个主题的dpad组织架构数据，插入分组的组织架构
          产出：
              1. sys-manager.sql sys-manager库改动上线sql
"""
import string

import pymysql
from pymysql.cursors import DictCursor
import copy
from datetime import datetime

result_filepath = 'sys-manager.sql'


def init():
    global result_filepath
    result_filepath = 'sys-manager' + str(datetime.now()) + '.sql'

    return pymysql.connect(user='einvdaPYkRjDJzJu0gKNhUUx',
                           password='NU4c17I9O843Vtxu0gW2TFu1XuUOt1',
                           host='dpshpl-slwzsrt4yzfdc0sm-pub.proxy.dms.aliyuncs.com',
                           database='sys_manager')


def write_sql(sql_str):
    with open(result_filepath, 'a') as file:
        file.write(sql_str + "\n")


def generate_company_group_insert_sql(primary_id, name, bloc_id):
    write_sql(
        'INSERT INTO sys_manager.company_group (id, name, bloc_id, create_by, create_time, update_by, update_time) '
        'VALUES (' + str(primary_id) + ', \'' + name + '\', ' + str(bloc_id) + ', 0, now(), 0, now());')


def generate_company_group_rel_update_sql(group_id, company_ids: []):
    if company_ids is None or len(company_ids) == 0:
        print("没有任何的公司信息需要更新分组，请确认脚本逻辑")
        return
    for company_id in company_ids:
        write_sql('UPDATE company SET group_id =  ' + str(group_id) + ' WHERE id = ' + str(company_id) + ';')


# 组织表的最大id
maxOrgId = -1


def get_next_org_id():
    global maxOrgId
    maxOrgId += 1
    return maxOrgId


def get_org_max_useful_id(csr):
    csr.execute('select max(id) as id from organization')
    global maxOrgId
    maxOrgId = csr.fetchone()['id'] + 1000
    print('organization id最大值为' + str(maxOrgId))


def get_dpad_org_by_company(csr, company_id):
    sql = 'select id, company_id, type, parent_id, name, ordinal, application_id, description, path, create_time, create_by, create_name, update_time, update_by, update_name, create_org_code, is_deleted, org_type, search_key, attribute, old_id, default_team ' \
          'from organization ' \
          'where is_deleted = 0 and application_id = 1010001 and company_id = ' + str(company_id)
    csr.execute(sql)
    return csr.fetchall()


def build_path(nodes, root_parent_id=0):
    # 使用字典来构建树结构，key为id，value为节点及其子节点
    tree = {node['id']: {'data': node} for node in nodes}  # 将节点信息放在'data'键下，以便区分孩子节点

    # 初始化所有节点的子节点
    for node_id in tree:
        tree[node_id]['children'] = []

    # 构建树结构
    for node in nodes:
        parent_id = node['parent_id']
        if parent_id != node['id']:  # 防止自我引用
            if parent_id in tree:  # 确保父节点存在
                tree[parent_id]['children'].append(node)

    def traverse_and_build_path(nodeid, path_so_far=None):
        if path_so_far is None:
            path_so_far = []
        node = tree[nodeid]
        current_path = path_so_far + [str(node['data']['id'])]
        node['data']['path'] = '-'.join(current_path)
        if node['data']['parent_id'] == 0:
            node['data']['search_key'] = ''
        else:
            first_dash_index = node['data']['path'].find('-')  # 找到第一个 '-' 的索引位置
            node['data']['search_key'] = node['data']['path'][first_dash_index + 1:]

        for child in node['children']:
            traverse_and_build_path(child['id'], current_path)

    # 寻找根节点并开始构建路径
    root_nodes = [tn for tn in tree.values() if tn['data']['parent_id'] == root_parent_id]
    if not root_nodes:
        print(f"No node found with the parent_id: {root_parent_id}")
        return []

    for root_node in root_nodes:
        traverse_and_build_path(root_node['data']['id'])

    # 返回处理后的节点列表，此时每个节点的'data'字段都包含了'path'
    return [tn['data'] for tn in tree.values()]


def generate_group_org_insert_sql(group_id, orgs: tuple[any, ...]):
    # 创建组织名称与原组织id的映射关系
    org_name_id_map = dict()
    org_id_name_map = dict()
    for org in orgs:
        org_name_id_map[org['name']] = org['id']
        org_id_name_map[org['id']] = org['name']

    # 生成新的组织，先不处理parent_id、search_key、path等字段
    # 顺便构建新组织名称与新组织id的映射关系
    new_orgs = tuple(copy.deepcopy(element) for element in orgs)
    new_org_name_id_map = dict()
    new_org_id_name_map = dict()
    for new_org in new_orgs:
        new_org['head_source_id'] = new_org['id']
        new_org['id'] = get_next_org_id()
        new_org['group_id'] = group_id
        new_org['company_id'] = 0

        new_org_name_id_map[new_org['name']] = new_org['id']
        new_org_id_name_map[new_org['id']] = new_org['name']

    # 生成parent_id
    for new_org in new_orgs:
        if new_org['parent_id'] == 0:
            new_org['parent_id'] = 0
        else:
            parent_name = org_id_name_map[new_org['parent_id']]
            parent_new_id = new_org_name_id_map[parent_name]
            new_org['parent_id'] = parent_new_id

    # 构建树结构，生成path
    build_path(new_orgs)

    # 生成执行sql
    for new_org in new_orgs:
        template = string.Template("""INSERT INTO organization (id, company_id, type, parent_id, name, ordinal, application_id, description, path, create_time, create_by, create_name, update_time, update_by, update_name, create_org_code, is_deleted, org_type, search_key, attribute, old_id, default_team, bloc, group_id, source_org_id, head_source_id)
VALUES ($id, $company_id, $type, $parent_id, '$name', $ordinal, 0, '$description', '$path', now(), 0, '', now(), 0, '', '0', false, '$org_type', '$search_key', null, '', $default_team, 2, $group_id, 0, $head_source_id);
""")

        insert_sql = template.substitute(new_org)
        write_sql(insert_sql)


# -------------------------------------------------------------------------------
# -------------------------------------------------------------------------------
# ---------------------------------  脚本开始  -----------------------------------
# -------------------------------------------------------------------------------
# -------------------------------------------------------------------------------

db = init()

try:
    with db.cursor(cursor=DictCursor) as cursor:
        get_org_max_useful_id(cursor)
        dpad_qy_all_orgs = get_dpad_org_by_company(cursor, 1000001)
        dpad_ty_all_orgs = get_dpad_org_by_company(cursor, 1000226)
finally:
    db.close()

write_sql('-- --------------主体分组表插入--------------\n')
generate_company_group_insert_sql(1, "启源系", 1)
generate_company_group_insert_sql(2, "图远系", 1)
write_sql('\n\n')

write_sql('-- --------------主体分组关系更新维护--------------\n')
generate_company_group_rel_update_sql(1, [1000001, 1000002])
generate_company_group_rel_update_sql(2, [2000001, 2000002])
write_sql('\n\n')

write_sql('-- --------------启源系分组组织架构插入--------------\n')
generate_group_org_insert_sql(1, dpad_qy_all_orgs)

write_sql('-- --------------图远系分组组织架构插入--------------\n')
generate_group_org_insert_sql(2, dpad_ty_all_orgs)
