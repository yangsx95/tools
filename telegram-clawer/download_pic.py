from telethon import TelegramClient, sync
import os
from telethon.tl.types import InputMessagesFilterPhotos


def download_pic(client: TelegramClient, channel_link, picture_storage_path):
    photos = client.get_messages(channel_link, None, max_id=100000, min_id=0, filter=InputMessagesFilterPhotos)
    index = 0
    for photo in photos:
        filename = picture_storage_path + "/" + str(photo.id) + ".jpg"
        index = index + 1
        print("downloading:", index, "/", len(photos), " : ", filename)
        if not os.path.exists(filename):
            client.download_media(photo, filename)
            print('done!')
        else:
            print('exist!')
    print("Download Picture Done.")
