# -*- coding: utf-8 -*-

import scrapy
from youtubeCrawler.items import YoutubeCrawlerItem
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


class YoutubeSpider(scrapy.Spider):
    name = 'youtubeSpider'
    # 향후 DB 연동으로 갈지 검토 필요 
    keywords = ['증권', '날씨'] 
    channels = [
        'https://www.youtube.com/user/hanwhalife/videos', # 한화생명보험
        'https://www.youtube.com/user/hwkoreastock/videos', # 한화투자증권
        'https://www.youtube.com/channel/UCajNePVZOs19hdWEiB-HtfQ/videos', # 한화손해보험 
        'https://www.youtube.com/channel/UCPQPQLc6sbZgMBrL4EqwJWA/videos', # 한화호텔엔드리조트
        'https://www.youtube.com/channel/UCYjfgiex4aTgDgcn0zsU-MA/videos', # 한화건설
        'https://www.youtube.com/user/hanwhadays/videos', # 한화TV
        'https://www.youtube.com/user/hanwhanews/videos' # 한화그룹
    ]

    youtube_headers = {
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_2) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.4 Safari/605.1.15',
    }

    def __init__(self):
        self.settings = get_project_settings()

        # 아래 옵션이 최대한 찾은 권장 사항임
        options = webdriver.ChromeOptions()
        #options.add_argument('--headless')        
        options.add_argument('--lang=ko')   
        options.add_argument("--window-size=1920,1080")
        options.add_argument("--disable-extensions")
        options.add_argument("--start-maximized")
        options.add_argument('--disable-gpu')
        options.add_argument('--disable-dev-shm-usage')
        options.add_argument('--no-sandbox')
        options.add_argument('--ignore-certificate-errors')
        self.driver = webdriver.Chrome(self.settings['CHROME_DRIVER'], chrome_options=options)
        self.driver.implicitly_wait(5)

        self.secretsGenerator = secrets.SystemRandom()


    def start_requests(self):
        yield scrapy.Request(url='https://www.youtube.com/', callback=self.get_search_result, headers = self.youtube_headers, errback=self.error_page)


    def get_search_result(self, response):

        # 검색 결과 크롤링
        url_list = []
        for keyword in self.keywords:
            self.driver.get('https://www.youtube.com/results?search_query=' + keyword)
            self.random_sleep()
 
            self.go_to_bottom(self.driver)

            video_list_page = self.driver.find_element(By.XPATH, '//*[@id="contents"]').get_attribute('innerHTML')
            video_elements = video_list_page.split('<ytd-video-renderer class="style-scope ytd-item-section-renderer"')
            print ('search keyword: ' + keyword)
            print ('search result count: ' + str(len(video_elements)))

            for video_element in video_elements: 
                try:
                    ref_url = video_element.split('href="')[1].split('">')[0]
                    if (len(ref_url) > 20): # 광고 등 youtube링크가 아님 
                        continue
                    if (ref_url.find('/user/') != -1) : 
                        continue

                    # 최근 한달내 등록된 video만 찾음 
                    date_line = video_element.split('<div id="metadata-line" class="style-scope ytd-video-meta-block">')[1].split('</div>')[0]
                    if (date_line.find('년 전') != -1) or (date_line.find('개월 전') != -1):  
                        continue

                    try: 
                        if url_list.index(ref_url) >= 0:
                            print ('video url: ' + ref_url + ' is duplicated.')
                            continue
                    except: 
                        url_list.append(ref_url)
                        print ('video url(' + str(len(url_list)) + '): ' + ref_url)
                except:
                    continue
            
        # 채널 크롤링
        for channel in self.channels:
            break
            self.driver.get(channel)
            self.random_sleep()
 
            # 채널의 경우는 정렬이 되기 때문에 한달 이내의 데이터를 받기 위해서는 맨위의 두페이지만 확인한다. 
            # self.go_to_bottom(self.driver) 
            self.driver.execute_script((
                        "var scrollingElement = (document.scrollingElement ||"
                        " document.body);scrollingElement.scrollTop ="
                        " scrollingElement.scrollHeight;"))
            self.random_sleep()

            video_list_page = self.driver.find_element(By.XPATH, '//*[@id="contents"]').get_attribute('innerHTML')
            video_elements = video_list_page.split('<ytd-grid-video-renderer class="style-scope ytd-grid-renderer"')
            print ('channel video count: ' + str(len(video_elements)))

            i = 0
            for video_element in video_elements: 
                try:
                    ref_url = video_element.split('href="')[1].split('">')[0]
                    if (len(ref_url) > 20): # 광고 등 youtube 링크가 아님 (여기서는 발생안할 수도 있음)
                        continue
                    if (ref_url.find('/user/') != -1) : 
                        continue

                    # 최근 한달내 등록된 video만 찾음 
                    date_line = video_element.split('<div id="metadata-line" class="style-scope ytd-grid-video-renderer">')[1].split('</div>')[0]
                    if (date_line.find('년 전') != -1) or (date_line.find('개월 전') != -1): 
                        continue
                    
                    try: 
                        if url_list.index(ref_url) >= 0:
                            print ('channel url: ' + ref_url + ' is duplicated.')
                            continue
                    except: 
                        url_list.append(ref_url)
                        print ('channel url(' + str(len(url_list)) + '): ' + ref_url)
                except:
                    continue	
                break
            break

           
        i = 0
        for video_url in url_list:
            # 아래와 같이 item을 함수의 return으로 받아야만 정상동작한다. (함수 안에서 yeid하면 함수가 실행을 안함)
            item = self.parse_article('https://www.youtube.com' + video_url)
            i += 1
            print ('crawled item: ' + str(i) + '/' + str(len(url_list)))
            yield item


    def parse_article(self, url):
        
        self.driver.get(url)
        self.driver.implicitly_wait(5)
        self.random_sleep() # 이게 없으면 로딩이 덜 되서 크롤링이 안될때가 있음 

        #
        # 기본 정보 (제목, 작성일, 조회수, 좋아요, 싫어요, 채널이름, 조회수) 추출
        #
        print (url)
        item = YoutubeCrawlerItem()
        item['url'] = url
        item['crawled_date'] = datetime.now().strftime("%Y-%m-%dT%H:%M:%S+09:00")

        try: 
            primary_info = self.driver.find_element(By.XPATH, '//*[@id="info-contents"]').get_attribute('innerHTML')
            super_title = primary_info.split('force-default-style="" class="style-scope ytd-video-primary-info-renderer">')[1].split('</yt-formatted-string>')[0]
            if super_title.find('<span dir=') != -1: # 샵 태그가 붙은 경우 
                super_title = super_title.split('<span dir="auto" class="style-scope yt-formatted-string">')[1].split('</')[0]
            item['super_title'] = super_title
        except Exception as e:
            #print (primary_info)
            print ('get super_title: ' + str(e) + ', ' + url)
            return
        print (item['super_title'])

        try:
            item['view_count'] = primary_info.split('조회수 ')[1].split('회')[0]
        except:
            item['view_count'] = '0'

        try:
            article_day = primary_info.split('<yt-formatted-string class="style-scope ytd-video-primary-info-renderer">')[1].split('</yt-formatted-string>')[0]
            item['article_day'] = datetime.strptime(article_day, '%Y. %m. %d.').strftime('%Y-%m-%d')
        except: 
            item['article_day'] = datetime.now().strftime("%Y-%m-%d") # 당일 등록된 비디오는 '10시간 전' 등으로 표시됨 

        try:
            item['like_count'] = primary_info.split('좋아요 ')[1].split('개"')[0]
            if item['like_count'].find('없음') == 0:
                item['like_count'] = '0'
        except:
            item['like_count'] = '0'

        try:
            item['hate_count'] = primary_info.split('싫어요 ')[1].split('개"')[0]
            if item['hate_count'].find('없음') == 0:
                item['hate_count'] = '0'
        except:
            item['hate_count'] = '0'

        item['channel_name'] = self.driver.find_element(By.XPATH, '//*[@id="text"]/a').get_attribute('innerHTML')

        #
        # 리뷰 추출
        #

        # scroll을 해야 리뷰로 갈 수 있다. 
        self.driver.execute_script("window.scrollTo(0, window.scrollY + 600)")
        self.random_sleep()

        # 리뷰 페이지를 끝까지 로딩
        self.scroll_to_bottom(self.driver)

        #headless일 때 스크롤 전에는 아래의 XPATH가 잡히질 않음 (headless가 아닌 경우는 잘됨)
        try:
            comment_info = self.driver.find_element(By.XPATH, '//*[@id="count"]/yt-formatted-string').get_attribute('innerHTML')
            item['comment_count'] = comment_info.split('댓글 </span><span dir="auto" class="style-scope yt-formatted-string">')[1].split('</span><span')[0]
        except:
            item['comment_count'] = '0'

        # 리뷰 
        comment_elements = []
        try:
            comment_list_page = self.driver.find_element(By.XPATH, '/html/body/ytd-app/div/ytd-page-manager/ytd-watch-flexy/div[5]/div[1]/div/ytd-comments/ytd-item-section-renderer/div[3]').get_attribute('innerHTML')
            comment_elements = comment_list_page.split('<ytd-comment-renderer id="comment" class="style-scope ytd-comment-thread-renderer" comment-style="unknown">')
            if len(comment_elements) >= 1:  
                comment_elements.pop(0) # 첫번째꺼는 리뷰가 아님
        except: 
            print ('get comment_elements except')

        comment_list = []
        has_new_comment = False
        for comment_element in comment_elements: 
            try:
                try: 
                    user_name = self.text_escape(comment_element.split('<span class="style-scope ytd-comment-renderer">')[1].split('</span>')[0])
                except:
                    user_name = self.text_escape(comment_element.split('<span class="channel-owner style-scope ytd-comment-renderer">')[1].split('</span>')[0])

                day_string = self.text_escape(comment_element.split('<yt-formatted-string class="published-time-text above-comment style-scope ytd-comment-renderer')[1].split('dir="auto">')[1].split('</a>')[0])
                print (user_name)

                now = datetime.now()
                comment_day = ''
                if day_string.find('일 전') != -1:
                    comment_day = (now - timedelta(days=int(re.search(r'\d+',day_string).group()))).strftime("%Y-%m-%d")
                    if int(re.search(r'\d+',day_string).group()) <= 6: 
                        has_new_comment = True
                elif day_string.find('주 전') != -1:
                    comment_day = (now - timedelta(days=int(re.search(r'\d+',day_string).group()))*7).strftime("%Y-%m-%d")
                elif day_string.find('개월 전') != -1:
                    comment_day = (now - timedelta(days=int(re.search(r'\d+',day_string).group()))*31).strftime("%Y-%m-%d")
                elif day_string.find('년 전') != -1:
                    comment_day = (now - timedelta(days=int(re.search(r'\d+',day_string).group()))*365).strftime("%Y-%m-%d")
                else: # 시간 전 또는 분전 
                    comment_day = now.strftime("%Y-%m-%d")
                    has_new_comment = True
                print (comment_day)

                comment_body_page = comment_element.split('<div id="content" class="style-scope ytd-expander">')[1].split('slot="content" split-lines="" class="style-scope ytd-comment-renderer">')[1].split('</yt-formatted-string')[0]
                comment_line = comment_body_page.split('<span dir="auto" class="style-scope yt-formatted-string">')
                comment =''
                if (len(comment_line) == 1):
                    comment = comment_line[0]
                else: 
                    for comment_text in comment_line:
                        if (comment_text.find ('<a class="yt-simple-endpoint style-scope yt-formatted-string') >= 0):
                            continue
                        comment += ' ' + self.text_escape(comment_text.split('</span')[0], False)  # 줄바꿈시 공백을 줌 (문장이 붙어 버림 방지)
                if comment.find('</div') == 0:  # comment가 비디오시간(1:10 등)밖에 없는 경우가 있음 
                    continue
                print (comment)

                like_count = '0'
                try:
                    like_count = self.text_escape(comment_element.split('aria-label="좋아요')[1].split('">')[1].split('</')[0])
                except: 
                    ...
                print (like_count)

                hate_count = '0'
                try:
                    hate_count = self.text_escape(comment_element.split('aria-label="싫어요')[1].split('">')[1].split('</')[0])
                except: 
                    ...
                print (hate_count)

                review_dict = {'user_name':user_name, 'comment_day':comment_day, 'comment':comment.replace('ㅋ',''), 'like_count': like_count, 'hate_count': hate_count}
                comment_list.append (review_dict)
            except Exception as e:
                print ('comment_list.append except: ' + str(e))
                continue

        print ('comment count: ' + str(len(comment_list)))

        item['comment_list'] = comment_list
        if has_new_comment == True: 
            item['new_comment'] = '1'
        else:
            item['new_comment'] = '0'

        #
        # 자막 추출
        #

        # 자막 메뉴 펼치기 
        mouseOvers = self.driver.find_elements(By.XPATH, '//*[@id="menu-container"]/div/ytd-menu-renderer/yt-icon-button/button')
        if (len(mouseOvers) == 0):
            print ('mouseOvers for script is zero')
            return item

        try:
            webdriver.common.action_chains.ActionChains(self.driver).move_to_element(mouseOvers[0]).perform()
            self.random_sleep()
        except: 
            print ('move_to_element for script failed')
            return item

        self.driver.execute_script("arguments[0].click();", mouseOvers[0])
        self.random_sleep()

        # 자막 내용 가지고 오기  
        # XPATH로는 두번째 메뉴가 renderer[2]인데, renderer[1]로 가져와짐
        mouseOvers = self.driver.find_elements(By.XPATH, '/html/body/ytd-app/ytd-popup-container/tp-yt-iron-dropdown/div/ytd-menu-popup-renderer/tp-yt-paper-listbox/ytd-menu-service-item-renderer[1]')

        if (len(mouseOvers) == 0):
            print ('mouseOvers for menuItem2 is zero')
            return item

        try:
            webdriver.common.action_chains.ActionChains(self.driver).move_to_element(mouseOvers[0]).perform()
            self.random_sleep()
        except: 
            print ('move_to_element for menuItem2 is zero')
            return item

        self.driver.execute_script("arguments[0].click();", mouseOvers[0])
        self.random_sleep()

        script_elements = []
        try:
            script_list_page = self.driver.find_element(By.XPATH, '//*[@id="panels"]/ytd-engagement-panel-section-list-renderer/div[2]/ytd-transcript-renderer').get_attribute('innerHTML')
            script_elements = script_list_page.split('<div class="cue-group style-scope ytd-transcript-body-renderer">')
            print ('crawled script: ' + str(len(script_elements)))
        except: 
            print ('get script_list_page except')

        script = ''
        for script_element in script_elements: 
            try:
                script = script + ' ' + self.text_escape(script_element.split('<div class="cue style-scope')[1].split('">')[1].split('</div>')[0], False)
            except:
                continue

        item['script'] = script.replace('[음악]','')
        return item
        

    def go_to_bottom(self, driver): # 한번에 스크롤 끝까지 내려감
        old_position = 0
        new_position = None

        while new_position != old_position:
            try:
                # Get old scroll position
                old_position = driver.execute_script(
                        ("return (window.pageYOffset !== undefined) ?"
                        " window.pageYOffset : (document.documentElement ||"
                        " document.body.parentNode || document.body);"))

                # Scroll and Sleep 
                driver.execute_script((
                        "var scrollingElement = (document.scrollingElement ||"
                        " document.body);scrollingElement.scrollTop ="
                        " scrollingElement.scrollHeight;"))
                self.random_sleep()

                # Get new position
                new_position = driver.execute_script(
                        ("return (window.pageYOffset !== undefined) ?"
                        " window.pageYOffset : (document.documentElement ||"
                        " document.body.parentNode || document.body);"))

            except: 
                break


    def scroll_to_bottom(self, driver): # 화면을 600픽셀씩 내려서 끝까지 내려감
        old_position = 0
        new_position = None

        while new_position != old_position:
            try: 
                # Get old scroll position
                old_position = driver.execute_script(
                        ("return (window.pageYOffset !== undefined) ?"
                        " window.pageYOffset : (document.documentElement ||"
                        " document.body.parentNode || document.body);"))

                # Scroll down to bottom
                driver.execute_script("window.scrollTo(0, window.scrollY + 600);")
                time.sleep (uniform(0.5, 1.5))

                # Get new position
                new_position = driver.execute_script(
                        ("return (window.pageYOffset !== undefined) ?"
                        " window.pageYOffset : (document.documentElement ||"
                        " document.body.parentNode || document.body);"))
            except: 
                break


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


    def insert_link(self, crawler_id, board_name, cause, start_urls, status):
        cursor = self.oraConn.cursor()
        try:
            cursor.execute('insert into link_status (crawler_id, board_name, cause, start_urls, status, chg_date, crt_date) values (:1, :2, :3, :4, :5, sysdate, sysdate)'
                , [crawler_id, board_name, cause, start_urls, status])
            self.oraConn.commit()
            print('-------> oracle insert : ', crawler_id, board_name, cause, start_urls, status)
        except:
            print('-------> row exist')
        
        cursor.close()


    def update_link(self, crawler_id, board_name, cause, start_urls, status):
        cursor = self.oraConn.cursor()
        cursor.execute('update link_status set status=:1, cause=:2, board_url=:3, chg_date=sysdate where crawler_id=:3 and board_name=:4', [status, cause, start_urls, crawler_id, board_name])
        self.oraConn.commit()
        print('-------> oracle update : ', status, crawler_id, board_name)
        cursor.close()


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
        
    
    def update_status(self, crawler_id, status):
        cursor = self.oraConn.cursor()
        cursor.execute('update crawler_status set status=:1, chg_date=sysdate where crawler_id=:2', [status, crawler_id])
        self.oraConn.commit()
        print('-------> oracle update : ', status, crawler_id)
        cursor.close()


    def closed(self, reason):
        #self.status += 1
        #self.update_status(self.crawler_id, self.status)
            
        #self.oraConn.close()
        self.driver.quit()

        print('-------> oracle close')


    def random_sleep(self):
        time.sleep (self.secretsGenerator.randrange(1, 3))
