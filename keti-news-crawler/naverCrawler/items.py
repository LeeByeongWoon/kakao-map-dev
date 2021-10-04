# -*- coding: utf-8 -*-

# Define here the models for your scraped items
#
# See documentation in:
# https://doc.scrapy.org/en/latest/topics/items.html

import scrapy


class NaverCrawlerItem(scrapy.Item):
    #define the fields for your item here like:
    #name = scrapy.Field()
    #pass

    url = scrapy.Field()
    news_id = scrapy.Field() # 이게 key  
    news_category = scrapy.Field()
    press_name = scrapy.Field() # 신문사 이름 
    original_url = scrapy.Field() # 기사 원문 url
    title = scrapy.Field()
    article_date = scrapy.Field()
    update_date = scrapy.Field()
    content = scrapy.Field()
    good_count = scrapy.Field()
    warm_count = scrapy.Field()
    sad_count = scrapy.Field()
    angry_count = scrapy.Field()
    want_count = scrapy.Field()
    comment_count = scrapy.Field()
    comment_list = scrapy.Field() # {name, comment_date, comment_id, comment, like_count, hate_count}
    crawled_date = scrapy.Field()