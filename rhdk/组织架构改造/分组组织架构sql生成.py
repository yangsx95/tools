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


# def init():
#     global result_filepath
#     result_filepath = 'sys-manager' + str(datetime.now()) + '.sql'
#
#     return pymysql.connect(user='einvdaPYkRjDJzJu0gKNhUUx',
#                            password='NU4c17I9O843Vtxu0gW2TFu1XuUOt1',
#                            host='dpshpl-slwzsrt4yzfdc0sm-pub.proxy.dms.aliyuncs.com',
#                            database='sys_manager')

def init():
    global result_filepath
    result_filepath = 'sys-manager' + str(datetime.now()) + '.sql'

    return pymysql.connect(user='root',
                           password='rhzl@2014',
                           host='10.6.6.73',
                           database='sys_manager_origin')


def write_sql(sql_str):
    with open(result_filepath, 'a') as file:
        file.write(sql_str + "\n")


maxOrgId = -1


# 组织表的最大id
def get_next_org_id():
    global maxOrgId
    maxOrgId += 1
    return maxOrgId


def get_org_max_useful_id(csr):
    csr.execute('select max(id) as id from organization')
    global maxOrgId
    maxOrgId = csr.fetchone()['id'] + 1000
    print('organization id最大值为' + str(maxOrgId))


# 获取dpad应用的指定公司的组织架构信息列表
def get_dpad_org_by_company(csr, company_id):
    sql = 'select id, company_id, type, parent_id, name, ordinal, application_id, description, path, create_time, create_by, create_name, update_time, update_by, update_name, create_org_code, is_deleted, org_type, search_key, attribute, old_id, default_team ' \
          'from organization ' \
          'where is_deleted = 0 and application_id = 1010001 and company_id = ' + str(company_id) + ';'
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

        # 如果是根节点，因为根节点的每个名称都不同，统一设置一个给定的值，用于映射
        if new_org['parent_id'] == 0:
            new_org_name_id_map['根组织'] = new_org['id']

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
        new_org['default_team'] = int.from_bytes(new_org['default_team'], byteorder='little')
        if not new_org['old_id']:
            new_org['old_id'] = 'null'

        template = string.Template("""INSERT INTO organization (id, company_id, type, parent_id, name, ordinal, application_id, description, path, create_time, create_by, create_name, update_time, update_by, update_name, create_org_code, is_deleted, org_type, search_key, attribute, old_id, default_team, group_id, source_org_id)
VALUES ($id, $company_id, $type, $parent_id, '$name', $ordinal, 0, '$description', '$path', now(), 0, '', now(), 0, '', '0', false, '$org_type', '$search_key', null, $old_id, $default_team, $group_id, 0);
""")

        insert_sql = template.substitute(new_org)
        write_sql(insert_sql)

    return new_org_name_id_map


def generate_company_group_insert_sql(group_id, name, bloc_id, company_ids: []):
    write_sql('\n-- --------------分组' + str(group_id) + ' ' + name + '处理--------------\n')

    write_sql(
        'INSERT INTO t_company_group (id, name, bloc_id, create_by, create_time, update_by, update_time) '
        'VALUES (' + str(group_id) + ', \'' + name + '\', ' + str(bloc_id) + ', 0, now(), 0, now());')

    if company_ids is None or len(company_ids) == 0:
        print("没有任何的公司信息需要更新分组，请确认脚本逻辑")
        return
    write_sql('UPDATE company SET group_id =  ' + str(group_id) + ', bloc_id = 1 WHERE id in (' + ','.join(
        map(str, company_ids)) + ');')

    all_orgs = get_dpad_org_by_company(cursor, company_ids[0])
    write_sql('\n-- --------------生成分组' + str(group_id) + ' ' + name + '组织架构--------------\n')

    if not all_orgs:
        print("未找到主体公司的任何组织架构信息" + str(company_ids[0]))
        return

    # 生成分组组织架构的sql，并返回 分组组织架构名称:分组组织架构id 的映射关系
    new_org_name_id_map = generate_group_org_insert_sql(group_id, all_orgs)

    # 查询所有的目标公司的组织列表，生成他们的更新sql
    for company_id in company_ids:
        cursor.execute(
            'select id, company_id, type, parent_id, name, ordinal, application_id, description, path, create_time, create_by, create_name, update_time, update_by, update_name, create_org_code, is_deleted, org_type, search_key, attribute, old_id, default_team ' \
            'from organization ' \
            'where is_deleted = 0 and application_id = 1010001 and company_id = ' + str(company_id) + ';')
        target_orgs = cursor.fetchall()
        write_sql('\n-- --------------更新分组' + str(group_id) + ' ' + name + '下具体公司' + str(company_id) + '组织架构的org信息--------------\n')
        for org in target_orgs:
            if org['parent_id'] == 0:
                source_org_id = new_org_name_id_map['根组织']
            elif org['name'] in new_org_name_id_map:
                source_org_id = new_org_name_id_map[org['name']]
            else:
                print("出错了，当前具体公司的组织: " + str(org['id']) + " " + org[
                    'name'] + " 并没有从分组组织架构中找到同名的组织，设置source_org_id失败")
                source_org_id = '-1'
            write_sql(
                'UPDATE organization set application_id = 0, set group_id = ' + str(group_id)
                + ', source_org_id = ' + str(source_org_id) + ' where id = ' + str(org['id']) + ';')


# -------------------------------------------------------------------------------
# -------------------------------------------------------------------------------
# ---------------------------------  脚本开始  -----------------------------------
# -------------------------------------------------------------------------------
# -------------------------------------------------------------------------------

db = init()

try:
    with db.cursor(cursor=DictCursor) as cursor:
        get_org_max_useful_id(cursor)
        generate_company_group_insert_sql(1, "启源系", 1,
                                          [1000001, 1000018, 1000036, 1000055, 1000058, 1000062, 1000063,
                                           1000064, 1000065, 1000066, 1000067, 1000068, 1000070, 1000084, 1000115,
                                           1000123, 1000124, 1000126, 1000127, 1000146, 1000149, 1000166, 1000173,
                                           1000175, 1000182, 1000199, 1000208, 1000209, 1000210, 1000211, 1000212,
                                           1000215, 1000221, 1000222, 1000229, 1000236,
                                           1000004010, 1000004020, 1000004140, 1000006080, 1000006110, 1000006120,
                                           1000006160, 1000008000, 1000008101, 1000008105, 1000008201, 1000008205,
                                           1000008206, 1000008208, 1000008301, 1000008403, 1000008405, 1000008406,
                                           1000008501, 1000008502, 1000008503, 1000008504, 1000008505, 1000008506,
                                           1000008507, 1000008508, 1000008509, 1000008510, 1000008511, 1000008512,
                                           1000008601, 1000008602, 1000008603, 1000008604, 1000008605, 1000008607,
                                           1000008610, 1000008613, 1000008615, 1000008617, 1000008622, 1000008623,
                                           1000008624, 1000008626, 1000008627, 1000008628, 1000008629, 1000008630,
                                           1000008631, 1000008633, 1000008636, 1000008637, 1000008638, 1000008643,
                                           1000008652, 1000008654, 1000008655, 1000008658, 1000008659, 1000008660,
                                           1000008665, 1000008667, 1000008668, 1000008670, 1000008671, 1000008672,
                                           1000008673, 1000008675, 1000008678, 1000008680, 1000008685, 1000008691,
                                           1000008693, 1000008695, 1000008699, 1000008701, 1000008702, 1000008704,
                                           1000008706, 1000008711, 1000008712, 1000008713, 1000008715, 1000008717,
                                           1000008720, 1000008729, 1000008734, 1000008735, 1000008736, 1000008739,
                                           1000008740, 1000008748, 1000008754, 1000008758, 1000008766, 1000008775,
                                           1000008778, 1000008781, 1000008782, 1000008785, 1000008787, 1000008788,
                                           1000008790, 1000008791, 1000008800, 1000008801, 1000008802, 1000008804,
                                           1000008806, 1000008813, 1000008816, 1000008834, 1000008835, 1000008836,
                                           1000008837, 1000008841, 1000008842, 1000008848, 1000008852, 1000008857,
                                           1000008859, 1000008860, 1000008864, 1000008866, 1000008867, 1000008871,
                                           1000008878, 1000008879, 1000008880, 1000008888, 1000008889, 1000008894,
                                           1000008904, 1000008911, 1000008913, 1000008916, 1000008921, 1000008922,
                                           1000008923, 1000008927, 1000008929, 1000008930, 1000008931, 1000008932,
                                           1000008937, 1000008938, 1000008939, 1000008940, 1000008941, 1000008942,
                                           1000008943, 1000008945, 1000008947, 1000008949, 1000008950, 1000008959,
                                           1000008960, 1000008962, 1000008963, 1000008972, 1000008976, 1000008980,
                                           1000008982, 1000008986, 1000008988, 1000008991, 1000008992, 1000008993,
                                           1000008994, 1000008995, 1000008996, 1000009003, 1000009005, 1000009007,
                                           1000009008, 1000009009, 1000009010, 1000009012, 1000009016, 1000009017,
                                           1000009018, 1000009022, 1000009023, 1000009025, 1000009026, 1000009029,
                                           1000009030, 1000009031, 1000009032, 1000009038, 1000009041, 1000009049,
                                           1000009052, 1000009053, 1000009054, 1000009055, 1000009056, 1000009057,
                                           1000009060, 1000009064, 1000009066, 1000009068, 1000009071, 1000009072,
                                           1000009076, 1000009077, 1000009078, 1000009084, 1000009085, 1000009086,
                                           1000009091, 1000009096, 1000009097, 1000009100, 1000009103, 1000009105,
                                           1000009110, 1000009111, 1000009115, 1000009116, 1000009118, 1000009124,
                                           1000009125, 1000009140, 1000009144, 1000009171, 1000009182, 1000009191,
                                           1000009197, 1000009198, 1000009202, 1000009205, 1000009208, 1000009209,
                                           1000009211, 1000009212, 1000009216, 1000009218, 1000009221, 1000009222,
                                           1000009223, 1000009228, 1000009233, 1000009237, 1000009239, 1000009248,
                                           1000009249, 1000009263, 1000009264, 1000009270, 1000009271, 1000009272,
                                           1000009277, 1000009278, 1000009283, 1000009284, 1000009288, 1000009294,
                                           1000009307, 1000009313, 1000009314, 1000009318])
        generate_company_group_insert_sql(2, "图远系", 1,
                                          [1000226, 1000228, 1000001002, 1000001003, 1000002000, 1000003000,
                                           1000004000, 1000004030, 1000004080, 1000004260, 1000005100, 1000008204])
        generate_company_group_insert_sql(3, "上海融和电科融资租赁有限公司", 1, [1000000003])
        generate_company_group_insert_sql(4, "上海芯智动新能源科技有限公司", 1, [1000001000])
        generate_company_group_insert_sql(5, "上海图木新能源汽车科技有限公司", 1, [1000001001])
        # generate_company_group_insert_sql(6, "三门峡图远物流有限公司", 1, [])
        # generate_company_group_insert_sql(7, "上海图畅物流有限公司", 1, [])
        # generate_company_group_insert_sql(8, "国电投（河南）新动力科技有限公司", 1, [])
        # generate_company_group_insert_sql(9, "内蒙古启源昕晟新能源有限公司", 1, [])
        # generate_company_group_insert_sql(10, "秦皇岛芯电科技有限公司", 1, [])
        # generate_company_group_insert_sql(11, "乌海市海芯新能源科技有限公司", 1, [])
        # generate_company_group_insert_sql(12, "西藏焕电新能源有限公司", 1, [])
        # generate_company_group_insert_sql(13, "南昌青远新能源有限公司", 1, [])
        # generate_company_group_insert_sql(14, "荆州焕电科技有限公司", 1, [])
        # generate_company_group_insert_sql(15, "安徽杰捷焕电新能源科技有限公司", 1, [])
        # generate_company_group_insert_sql(16, "唐山港投焕电科技有限公", 1, [])
finally:
    db.close()

# 插入集团
# 插入分组
# 插入company_group
# 更新公司的分组
# 生成分组的组织架构
# app_org_type
# 更新具体的公司组织架构
# org_account处理
# position_struct
