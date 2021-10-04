# -*- coding: utf-8 -*-

import scrapy
from naverCrawler.items import NaverCrawlerItem
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from datetime import datetime, timedelta
import re, time

from scrapy.utils.project import get_project_settings
from scrapy.spidermiddlewares.httperror import HttpError
from twisted.internet.error import DNSLookupError
from twisted.internet.error import TimeoutError, TCPTimedOutError
import secrets
import cx_Oracle
import string
import html
import requests
import json


class NaverSpider(scrapy.Spider):
    name = 'naverSpider'
    keywords = ['날씨', '증권'] 
    # field=1: 제목 검색, field=0: 전체 검색
    search_url = 'https://search.naver.com/search.naver?where=news&sm=tab_opt&reporter_article=&pd=1&ds=%s&de=%s&docid=&query=%s&sort=1&photo=0&field=1'

    def __init__(self):
        self.settings = get_project_settings()
        #self.oraConn = cx_Oracle.connect(self.settings['ORACLE_CONNECTION'], encoding = "UTF-8", nencoding = "UTF-8")
        #print('-------> oracle connected')

        #self.insert_status(self.crawler_id, 0)
        #for url in self.start_urls:
        #	url_index = self.start_urls.index(url)
        #	self.insert_link(self.crawler_id, self.board_id + "_" + self.board_name[url_index], '', url, 1)
        #print('-------> oracle insert')
        
        #self.status = self.select_status(self.crawler_id)
        #print('-------> select_status return : '+ str(self.status))

        self.secretsGenerator = secrets.SystemRandom()


    def start_requests(self):

        start_date = (datetime.today() - timedelta(days=2))
        start_day = start_date.strftime('%Y.%m.%d')
        end_day = datetime.today().strftime('%Y.%m.%d')

        for keyword in self.keywords:
            url = self.search_url % (start_day, end_day, keyword)
            yield scrapy.Request(url=url, callback=self.parse_page, errback=self.error_page)


    def parse_page(self, response):
        print (response.url)
        
        # 뉴스 추출
        news_list_page = response.xpath('/html/body/div[3]/div[2]/div/div[1]/section[1]/div/div[2]/ul').get()
        if news_list_page == None: 
            print ('get news_list_page fail')
            return

        news_elements = news_list_page.split('<li class="bx" id="sp_nws')
        if len(news_elements) >= 1:  
            news_elements.pop(0) # 첫번째꺼는 뉴스가 아님
        else:
            print ('No news_elements')
            return

        print ('news_element count: ' + str(len(news_elements)))

        # '네이버뉴스' 항목이 있는 거만 찾아서 이동, 스포츠 연애는 링크가 다름
        for news_element in news_elements:
            news_url = response.xpath('//*[@id="sp_nws%s"]/div[1]/div/div[1]/div/a[2]/@href' % (news_element.split('"')[0])).get()
            if news_url != None:
                if 'https://news.naver.com' in news_url:
                    yield response.follow (news_url, callback=self.parse_article)
            
            related_elements = response.xpath('//*[@id="sp_nws%s"]/div[2]/ul/li' % (news_element.split('"')[0])).extract()

            for i in range(1, len(related_elements) + 1): 
                related_url = response.xpath('//*[@id="sp_nws%s"]/div[2]/ul/li[%d]/span/span/a/@href' % (news_element.split('"')[0], i)).get()
                print (related_url)
                if related_url != None:
                    if 'https://news.naver.com' in related_url:
                        yield response.follow (related_url, callback=self.parse_article)

        # 다음 페이지로 이동
        next_page = response.xpath('/html/body/div[3]/div[2]/div/div[1]/div[2]/div/a[2]').extract()  # Xpath는 안될때가 있음
        if next_page != None:
            next_link = response.xpath('/html/body/div[3]/div[2]/div/div[1]/div[2]/div/a[2]/@href').get()
            if next_link != None:
                yield response.follow ('https://search.naver.com/search.naver' + next_link, callback=self.parse_page)
 
    def parse_article(self, response):

        item = NaverCrawlerItem()

        # url, aid, crawled_date 
        item['url'] = response.url
        item['news_id'] = response.url.split('oid=')[1].split('&')[0] + '_' + self.text_escape (response.url.split('aid=')[1])
        item['crawled_date'] = datetime.now().strftime("%Y-%m-%dT%H:%M:%S+09:00")

        # news_category
        category_bar = response.xpath('//*[@id="lnb"]/ul').get()
        try: 
            item['news_category'] = category_bar.split('<li class="on">')[1].split('<span class="tx">')[1].split('</span>')[0]
        except Exception as e:
            print ('get news_category: ' + str(e) + ', ' + category_bar)
            item['news_category'] = ''

        # press_name
        press_name_tag = response.xpath('//*[@id="main_content"]/div[1]/div[1]/a/img').get()
        try: 
            if press_name_tag != None: 
                item['press_name'] = press_name_tag.split('title="')[1].split('"')[0]
            else: 
                item['press_name'] = ''
        except Exception as e:
            print ('url: '+ response.url)
            print ('get press_name: ' + str(e) + ', ' + press_name_tag)
            item['press_name'] = ''

        # title
        item['title'] = response.xpath('//*[@id="articleTitle"]/text()').get()
        if item['title'] == None: 
            print ('No Title')
            return

        # article_date
        date_div = response.xpath('//*[@id="main_content"]/div[1]/div[3]/div').get()
        try: 
            input_date = date_div.split('기사입력 <span class="t11">')[1].split('</span>')[0]
            item['article_date'] = datetime.strptime(input_date.replace('오전', 'AM').replace('오후', 'PM'), '%Y.%m.%d. %p %H:%M').strftime('%Y-%m-%dT%H:%M:%S+09:00')      
        except Exception as e:
            print ('url: '+ response.url)
            print ('get input_date: ' + str(e) + ', ' + input_date)
            return

        # update_date
        try: 
            update_date = date_div.split('최종수정 <span class="t11">')[1].split('</span>')[0]
            item['update_date'] = datetime.strptime(update_date.replace('오전', 'AM').replace('오후', 'PM'), '%Y.%m.%d. %p %H:%M').strftime('%Y-%m-%dT%H:%M:%S+09:00')      
        except Exception as e:
            item['update_date'] = ''

        item['original_url'] = response.xpath('//*[@id="main_content"]/div[1]/div[3]/div/a[1]/@href').get()
        if item['original_url'] == None: 
            print ('No original_url')
            item['original_url'] = ''

        # content
        content_elements = response.xpath('//*[@id="articleBodyContents"]/text()').extract()
        content = ''

        for content_element in content_elements:
            content_string = self.text_escape(content_element)
            if len(content_string) == 0:
                continue

            if ('무단' in content_string) and ('전재' in content_string) and ('재배포' in content_string):
                if (len(content_string) < 50): 
                    print ('deleted string: ' + content_string)
                    continue

            if len(content) > 0: 
                content += ' ' + content_string
            else: 
                content = content_string

        if len(content) == 0:
            print ('No content: ' + str(content_elements))
            return
        else: 
            item['content'] = content 
        
        # good_count, warm_count, sad_count, angry_count, want_count
        news_id = 'ne_' + response.url.split('oid=')[1].split('&')[0] + '_' + self.text_escape (response.url.split('aid=')[1])
        like_url = ('https://news.like.naver.com/v1/search/contents?suppress_response_codes=true&&callback=&q=NEWS[%s]&isDuplication=false') % news_id

        item['good_count'] = '0'
        item['warm_count'] = '0'
        item['sad_count'] = '0'
        item['angry_count'] = '0'
        item['want_count'] = '0'

        try:
            like_json = json.loads(requests.get(like_url).text.split('(')[1].split(');')[0])
            for reaction in like_json['contents'][0]['reactions']: 
                if reaction['reactionType'] == 'like': 
                    item['good_count'] = str(reaction['count'])

                elif reaction['reactionType'] == 'warm': 
                    item['warm_count'] = str(reaction['count'])

                elif reaction['reactionType'] == 'sad': 
                    item['sad_count'] = str(reaction['count'])

                elif reaction['reactionType'] == 'angry': 
                    item['angry_count'] = str(reaction['count'])

                elif reaction['reactionType'] == 'want': 
                    item['want_count'] = str(reaction['count'])

        except Exception as e:
            print ('url: '+ response.url)
            print ('like_json: ' + str(e))

        # comments
        comment_list = []
        try: 
            for comments in self.parse_comment (response.url):
                comment_list.extend(comments)
        except Exception as e:
            print ('url: '+ response.url)
            print ('No comment: ' + str(e))
            return

        item['comment_count'] = len (comment_list)
        item['comment_list'] = comment_list

        print ('url: ' + response.url + ', ' + str(len(comment_list)) + ' comments crawled')

        yield item


    def parse_comment (self, url):

        object_id = 'news' + url.split('oid=')[1].split('&')[0] + ',' + self.text_escape (url.split('aid=')[1])
        page_no = 1
        headers = {'sec-fetch-mode':'cors', 'content-type':'application/x-www-form-urlencoded; charset=utf-8','accept':'*/*'
            ,'sec-fetch-site':'same-origin','referer':url}

        while True: 
            request_url = ('https://apis.naver.com/commentBox/cbox/web_neo_list_jsonp.json?ticket=news&templateId=default_society' 
                        + ('&pool=cbox5&_callback&lang=ko&country=KR&objectId=%s') % object_id
                        + ('&categoryId=&pageSize=20&indexSize=10&groupId=&listType=OBJECT&pageType=more&page=%d') % page_no
                        + '&refresh=false&sort=FAVORITE&includeAllStatus=true&cleanbotGrade=2&_')

            resp_body = str(requests.get(request_url, headers = headers).text)
            try:
                comments_json = json.loads(resp_body.split('_callback(')[1][:-2])
            except Exception as e:
                print ('url: ' + url)
                print ('json.loads(response) fail: ' + str(e) + ', ' + resp_body)
                return 

            if comments_json['success'] == False: 
                print ('comment_json fail: ' + str(comments_json['success']))
                return 

            last_page = comments_json['result']['pageModel']['lastPage']

            comments = []
            for comment in comments_json['result']['commentList']:
                comment_dict = {
                            'user_name': comment['userName'], 'comment_date': comment['regTime'], 
                            'comment_id': comment['commentNo'], 'comment': self.text_escape(comment['contents'], False), 
                            'like_count': str(comment['sympathyCount']), 'hate_count': str(comment['antipathyCount'])
                        }
                comments.append (comment_dict)
            yield comments

            page_no += 1
            if page_no > last_page: 
                return

            self.random_sleep()
                

    def text_escape(self, text, clear_return = True):
        result_text = ''
        text = html.unescape(text)

        if clear_return:
            for s in text.strip():
                regex = re.compile(r'[\r\n\t\xa0]')
                s = regex.sub('', s)
                result_text = result_text + s
        else:
            for s in text.strip():
                regex = re.compile(r'[\r\t\xa0]')
                s = regex.sub('', s)
		    	# 줄바꿈을 그냥 없애면 윗줄 마지막 단어와 아랫줄 첫 단어가 붙어서 형태소 분석이 제대로 안된다.
                regex = re.compile(r'[\n]') 
                s = regex.sub(' ', s)
                result_text = result_text + s

        return result_text


    def error_page(self, failure):
        request = failure.request
        print('==> url :', request.url)
        url_index = self.start_urls.index(request.url)
        self.board_error[url_index] = True
        
        if failure.check(HttpError):
            response = failure.value.response
            print('HttpError :', response.status, ':', response.url)
            self.update_link(self.crawler_id, self.board_id + "_" + self.board_name[url_index], str(response.status), self.start_urls[url_index], 0)
        elif failure.check(DNSLookupError):
            print('DNSLookupError ::', request.url)
            self.update_link(self.crawler_id, self.board_id + "_" + self.board_name[url_index], 'DNSLookupError', self.start_urls[url_index], 0)
        elif failure.check(TimeoutError, TCPTimedOutError):
            print('TimeoutError ::', request.url)
            self.update_link(self.crawler_id, self.board_id + "_" + self.board_name[url_index], 'TimeoutError', self.start_urls[url_index], 0)


    def select_status(self, crawler_id):
        cursor = self.oraConn.cursor()
        cursor.execute('select status from crawler_status where crawler_id=(:1)', [crawler_id])
        status = cursor.fetchone()[0]
        print('-------> oracle select : ', crawler_id)
        cursor.close()
        
        return status


    def insert_status(self, crawler_id, status):
        cursor = self.oraConn.cursor()
        try:
            cursor.execute('insert into crawler_status (crawler_id, status, chg_date, crt_date) values (:1, :2, sysdate, sysdate)'
                , [crawler_id, status])
            self.oraConn.commit()
            print('-------> oracle insert : ', crawler_id, status)
        except:
            print('-------> row exist')
        cursor.close()
        
    
    # def update_status(self, crawler_id, status):
    #     cursor = self.oraConn.cursor()
    #     cursor.execute('update crawler_status set status=:1, chg_date=sysdate where crawler_id=:2', [status, crawler_id])
    #     self.oraConn.commit()
    #     print('-------> oracle update : ', status, crawler_id)
    #     cursor.close()


    def closed(self, reason):
        #self.status += 1
        #self.update_status(self.crawler_id, self.status)
        #cnt = 0
        #for error_check in self.board_error:
            #if not error_check:
                #self.update_link(self.crawler_id, self.board_id + "_" + self.board_name[cnt], '', self.start_urls[cnt], 1)
            #cnt += 1
            
        #self.oraConn.close()

        print('-------> oracle close')


    def random_sleep(self):
        time.sleep (self.secretsGenerator.randrange(1, 3))
