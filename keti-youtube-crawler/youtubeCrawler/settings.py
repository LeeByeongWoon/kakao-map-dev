# -*- coding: utf-8 -*-

# Scrapy settings for youtubeCrawler project
#
# For simplicity, this file contains only settings considered important or
# commonly used. You can find more settings consulting the documentation:
#
#     https://doc.scrapy.org/en/latest/topics/settings.html
#     https://doc.scrapy.org/en/latest/topics/downloader-middleware.html
#     https://doc.scrapy.org/en/latest/topics/spider-middleware.html

BOT_NAME = 'youtubeCrawler'

SPIDER_MODULES = ['youtubeCrawler.spiders']
NEWSPIDER_MODULE = 'youtubeCrawler.spiders'

ROBOTSTXT_OBEY = False

ITEM_PIPELINES = {
    'youtubeCrawler.pipelines.YoutubeCrawlerPipeline': 300,
}

#BOOTSTRAP_SERVERS = ["192.168.7.63:9092","192.168.7.64:9093","192.168.7.65:9094"]
BOOTSTRAP_SERVERS = ["192.168.7.181:9092"]
KAFKA_TOPIC = "dev-keti-youtube"


#CHROME_DRIVER = '/home/solution/lib/chromedriver'		
CHROME_DRIVER = '/Users/yjkim/lib/chromedriver'
OBOTSTXT_OBEY = True

FEED_EXPORT_ENCODING='utf-8'

CONCURRENT_REQUESTS = 1
DOWNLOAD_TIMEOUT = 10
RETRY_TIME = 2
DOWNLOAD_DELAY = 10
CONCURRENT_REQUESTS_PER_DOMAIN = 1

LOG_LEVEL = 'INFO'
LOG_STDOUT = True
# LOG_FILE = '/home/solution/log/youtubeCrawler/youtubeCrawler.log'

FEED_EXPORT_ENCODING='utf-8'