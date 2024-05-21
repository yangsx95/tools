import pandas as pd
import csv

# 指定 Excel 文件路径
file_path = '各服务使用接口.xlsx'

# 使用 pandas 读取 Excel 文件
df = pd.read_excel(file_path, engine='openpyxl', sheet_name=None)

sheets = df.keys()

target = {}

for sheet in sheets:
    sheetContent = pd.read_excel(file_path, engine='openpyxl', sheet_name=sheet)
    column_urls = sheetContent.iloc[:, 1]
    for url in column_urls:
        if not isinstance(url, str):
            continue
        if url == '暂无' or url == '罗列一下用到的接口路径' or url == '启源魔方':
            continue
        if (url.startswith('topic')
                or url.startswith('tag')
                or url.startswith('groupId')
                or url.startswith('/baseservice')
                or url.startswith('/iot-vehicle-web-server/')):
            continue
        if url.startswith('/jupiter-auth-server'):
            url = url[len('/jupiter-auth-server'):]
        if url.startswith('jupiter-auth-server'):
            url = url[len('jupiter-auth-server'):]
        if url.startswith('JUPITER   '):
            url = url[len('JUPITER   '):]
        if url.startswith('/api/jupiter-auth-server'):
            url = url[len('/api/jupiter-auth-server'):]
        if url.startswith('/dpad/auth_web/api/v1'):
            url = url[len('/dpad/auth_web/api/v1'):]
        if url.startswith('/auth_web/v1'):
            url = url[len('/auth_web/v1'):]
        if url.startswith('api/v1'):
            url = url[len('api/v1'):]
        if url.startswith('/jupiter-common-service-server'):
            url = url[len('/jupiter-common-service-server'):]

        url = url.strip().replace('//', '/')

        if not url.startswith('/'):
            url = '/' + url

        if url not in target:
            target[url] = []
        if sheet not in target.get(url):
            target[url].append(sheet)

allUrls = []
for url in target.keys():
    allUrls.append(url)

allUrls = sorted(allUrls)

data = []
for key in allUrls:
    # print(key + '   ' + ' '.join(target.get(key)))
    data.append([key, ' '.join(target.get(key))])

with open("result.csv", 'w', newline='') as csvfile:
    writer = csv.writer(csvfile)
    for row in data:
        writer.writerow(row)
