import pymysql
from collections import defaultdict
import csv

db = pymysql.connect(user='einvdaPYkRjDJzJu0gKNhUUx',
                     password='NU4c17I9O843Vtxu0gW2TFu1XuUOt1',
                     host='dpshpl-slwzsrt4yzfdc0sm-pub.proxy.dms.aliyuncs.com',
                     database='sys_manager')


class OrgIdMapItem:
    def __init__(self, dpadOrgId, otherOrgId, orgName, companyId):
        self.dpadOrgId = dpadOrgId
        self.otherOrgId = otherOrgId
        self.orgName = orgName
        self.companyId = companyId


result = []

try:
    with db.cursor() as cursor:
        cursor.execute('select id, name, company_id from organization where is_deleted = 0 '
                       'and application_id = 1010001 and company_id = 1000001')
        dpad_all_orgs = cursor.fetchall()

        cursor.execute('select id, name, company_id from organization where is_deleted = 0 '
                       'and application_id = 9999999')
        other_999_orgs = cursor.fetchall()

    dpad_name_companyId_orgMap = defaultdict(lambda: None)
    for org in dpad_all_orgs:
        org_id = org[0]
        org_name = org[1]
        org_company_id = org[2]
        dpad_name_companyId_orgMap[(org_name, org_company_id)] = org_id

    for orgOther in other_999_orgs:
        orgOther_id = orgOther[0]
        orgOther_name = orgOther[1]
        orgOther_company_id = orgOther[2]

        dpad_orgId = dpad_name_companyId_orgMap[(orgOther_name, orgOther_company_id)]
        result.append(OrgIdMapItem(dpad_orgId, orgOther_id, orgOther_name, orgOther_company_id))

finally:
    db.close()

data = []
for r in result:
    data.append([r.otherOrgId, r.orgName, r.dpadOrgId])

with open("org-mapping.csv", 'w', newline='') as csvfile:
    writer = csv.writer(csvfile)
    for row in data:
        writer.writerow(row)

