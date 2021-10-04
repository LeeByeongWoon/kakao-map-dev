# -*- coding: utf-8 -*-

# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: https://doc.scrapy.org/en/latest/topics/item-pipeline.html

import datetime
import json
from scrapy.utils.project import get_project_settings
import logging
from konlpy.tag import Okt
from kafka import KafkaProducer


class NaverCrawlerPipeline(object):
    def __init__(self):
        self.settings = get_project_settings()

        self.kafka_producer = KafkaProducer(
                acks=-1,
                compression_type="lz4",
                bootstrap_servers=self.settings['BOOTSTRAP_SERVERS'],
                value_serializer=lambda x: json.dumps(x).encode('utf-8')
           )


    def process_item(self, item, spider):

        doc = dict(item)
        del doc['comment_list']

        okt = Okt()
        words = list()
			
        nouns = okt.nouns(item['content'])
        words.extend(nouns)
        for noun in nouns:
            if len(noun) == 1:
                words.remove(noun)
            
        if len(words) == 0:
            words.append("")

        doc['analyzed_words'] = words

        self.kafka_producer.send(self.settings['KAFKA_TOPIC'], value={"messages": doc})
        self.kafka_producer.flush()

        return item