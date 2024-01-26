import os

from telethon import TelegramClient

from download_pic import download_pic

api_id = 'xxx'
api_hash = 'xxx'
channel_link = 'https://t.me/xxx'
picture_storage_path = './data/tupian'

package_dir = os.path.dirname(os.path.abspath(__file__))
data_dir = os.path.join(package_dir, 'data')
if not os.path.exists(data_dir):
    os.mkdir(data_dir)
session_dir = os.path.join(data_dir, api_id)
client = TelegramClient(session_dir, api_id, api_hash)


def main():
    download_pic(client, channel_link, picture_storage_path)


with client:
    main()
    client.disconnect()
