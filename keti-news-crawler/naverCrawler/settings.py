# -*- coding: utf-8 -*-

# Scrapy settings for naverCrawler project
#
# For simplicity, this file contains only settings considered important or
# commonly used. You can find more settings consulting the documentation:
#
#     https://doc.scrapy.org/en/latest/topics/settings.html
#     https://doc.scrapy.org/en/latest/topics/downloader-middleware.html
#     https://doc.scrapy.org/en/latest/topics/spider-middleware.html

BOT_NAME = 'naverCrawler'

SPIDER_MODULES = ['naverCrawler.spiders']
NEWSPIDER_MODULE = 'naverCrawler.spiders'

ROBOTSTXT_OBEY = False

ITEM_PIPELINES = {
    'naverCrawler.pipelines.NaverCrawlerPipeline': 300,
}

#BOOTSTRAP_SERVERS = ["192.168.7.63:9092","192.168.7.64:9093","192.168.7.65:9094"]
BOOTSTRAP_SERVERS = ["192.168.7.181:9092"]
KAFKA_TOPIC = "dev-keti-news"

OBOTSTXT_OBEY = True

FEED_EXPORT_ENCODING='utf-8'

CONCURRENT_REQUESTS = 1
DOWNLOAD_TIMEOUT = 10
RETRY_TIME = 2
DOWNLOAD_DELAY = 2
CONCURRENT_REQUESTS_PER_DOMAIN = 1

LOG_LEVEL = 'INFO'
LOG_STDOUT = True
#LOG_FILE = '/home/solution/log/naverCrawler/naverCrawler.log'

FEED_EXPORT_ENCODING='utf-8'