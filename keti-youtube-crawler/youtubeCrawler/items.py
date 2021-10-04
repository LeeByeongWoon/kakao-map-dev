# -*- coding: utf-8 -*-

# Define here the models for your scraped items
#
# See documentation in:
# https://doc.scrapy.org/en/latest/topics/items.html

import scrapy


class YoutubeCrawlerItem(scrapy.Item):
    #define the fields for your item here like:
    #name = scrapy.Field()
    #pass

    url = scrapy.Field()
    super_title = scrapy.Field()
    view_count = scrapy.Field()
    article_day = scrapy.Field()
    article_date = scrapy.Field()
    channel_name = scrapy.Field()
    like_count = scrapy.Field()
    hate_count = scrapy.Field()
    comment_count = scrapy.Field()
    comment_list = scrapy.Field() # {user_name, comment_day, comment_date, comment, like_count, hate_count}
    script = scrapy.Field()
    new_comment = scrapy.Field()
    crawled_date = scrapy.Field()