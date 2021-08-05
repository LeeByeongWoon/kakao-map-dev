#!/usr/bin/python3

import argparse

import json
import time
import pytz
from bisect import bisect
from time import sleep
from datetime import datetime, timedelta

import pymysql
import yfinance as yf
from kafka import KafkaProducer

from pandas_datareader import data as pdr


env = {
    "local": {
        "mariadb": {
            "username": "keti",
            "password": "qwer1234!@",
            "hostname": "192.168.207.2",
            "database": "keti",
            "charset": "utf8"
        },
        "kafka": {
            "bootstrap_servers": ["192.168.207.2:9092"],
            "topic": "dev-keti-finance",
            "acks": -1,
            "compression_type": "lz4",
            "serializer": lambda x: json.dumps(x).encode('utf-8')
        }
    },
    "dev": {
        "mariadb": {
            "username": "keti",
            "password": "qwer1234!@",
            "hostname": "192.168.100.41",
            "database": "keti",
            "charset": "utf8"
        },
        "kafka": {
            "bootstrap_servers": ["192.168.7.182:9092","192.168.7.183:9092","192.168.7.184:9092"],
            "topic": "dev-keti-finance",
            "acks": -1,
            "compression_type": "lz4",
            "serializer": lambda x: json.dumps(x).encode('utf-8')
        }
    },
    "prod": {
        "mariadb": {
            "username": "keti",
            "password": "qwer1234!@",
            "hostname": "192.168.100.41",
            "database": "keti",
            "charset": "utf8"
        },
        "kafka": {
            "bootstrap_servers": ["192.168.100.71:9092","192.168.100.72:9092","192.168.100.73:9092"],
            "topic": "dev-keti-finance",
            "acks": -1,
            "compression_type": "lz4",
            "serializer": lambda x: json.dumps(x).encode('utf-8')
        }
    }
}

props = {}


def send_messages(yfi_messages):
    kafka = env[props["env"]]["kafka"]

    producer = KafkaProducer(
                acks=kafka["acks"],
                compression_type=kafka["compression_type"],
                bootstrap_servers=kafka["bootstrap_servers"],
                value_serializer=kafka["serializer"]
           )

    for messages_key in yfi_messages.keys():
        messages = {
            messages_key: yfi_messages[messages_key]
        }
        producer.send(kafka["topic"], value={"messages": messages})
        producer.flush()


def yfinance_messages(yfi_datas):
    messages = {}

    keys = []
    params = []
    datas = []
    for key in yfi_datas["idx_0"].keys():
        keys.append(key)
    
    for datas_key in yfi_datas.keys():
        yfi_data = yfi_datas[datas_key]

        params.append(yfi_data[keys[0]])
        datas.append(yfi_data[keys[1]])

    results_datas = {} 
    for row in range(0, len(datas)):
        dicts_params = params[row]
        dicts_datas = datas[row].to_dict()

        countries = dicts_params["countries"]
        exchanges = dicts_params["exchanges"]
        industries = dicts_params["industries"]
        companies = dicts_params["companies"]
        tickers = dicts_params["tickers"]

        for company in companies:
            results_datas[company] = {}

        for dicts_key in dicts_datas.keys():
            idx = bisect(tickers, dicts_key[0])-1

            dict_data = dicts_datas[dicts_key]
            
            for dict_key in dict_data.keys():
                time = dict_key.strftime("%Y-%m-%d")
                results_datas[companies[idx]][time] = {}
                messages[companies[idx]] = []

        for dicts_key in dicts_datas.keys():
            idx = bisect(tickers, dicts_key[0])-1

            dict_data = dicts_datas[dicts_key]

            for dict_key in dict_data.keys():
                time = dict_key.strftime("%Y-%m-%d")
                utc = datetime.strptime(dict_key.strftime("%Y-%m-%d %H:%M:%S.%f"), "%Y-%m-%d %H:%M:%S.%f").astimezone(pytz.utc)
                
                results_datas[companies[idx]][time]["timestamp"] = utc.strftime("%Y-%m-%dT%H:%M:%S.%fZ")
                results_datas[companies[idx]][time]["country"] = countries[idx]
                results_datas[companies[idx]][time]["exchange"] = exchanges[idx]
                results_datas[companies[idx]][time]["industry"] = industries[idx]
                results_datas[companies[idx]][time]["company"] = companies[idx]
                results_datas[companies[idx]][time]["ticker"] = tickers[idx]

                if dicts_key[1].lower() != "adj close":
                    results_datas[companies[idx]][time][dicts_key[1].lower() + "Value"] = dict_data[dict_key]
                else:
                    results_datas[companies[idx]][time]["adjCloseValue"] = dict_data[dict_key]


    for results_key in results_datas.keys():
        results_data = results_datas[results_key]

        for result_key in results_data.keys():
            messages[results_key].append(results_datas[results_key][result_key])


    return messages


def yfinance_data(yfi_params):
    # 애플, 마이크로소프트, 아마존, 알파벳C, 알파벳A, 페이스북, 테슬라
    # dow = pdr.get_data_yahoo("AAPL MSFT AMZN GOOG GOOGL FB TSLA", start_date, end_date)
    # 애플, 마이크로소프트, 아마존, 알파벳C, 알파벳A, 페이스북, 테슬라
    # nasdaq = pdr.get_data_yahoo("AAPL MSFT AMZN GOOG GOOGL FB TSLA", start_date, end_date)
    # 애플, 마이크로소프트, 아마존, 알파벳C, 알파벳A, 페이스북, 테슬라
    # sp500 = pdr.get_data_yahoo("AAPL MSFT AMZN GOOG GOOGL FB TSLA", start_date, end_date)

    # 삼성, SK하이닉스, 카카오, 네이버,  삼성전자우, LG화학, 삼성바이오로직스
    # kospi = pdr.get_data_yahoo("005930.KS 000660.KS 035720.KS 035420.KS 005935.KS 051910.KS 207940.KS", start_date, end_date)
    # 셀트리온헬스케어, 셀트리온제약, 에코프로비엠, 씨젠, 펄어비스, 카카오게임즈, CJ ENM
    # kosdaq = pdr.get_data_yahoo("091990.KQ 068760.KQ 247540.KQ 096530.KQ 263750.KQ 293490.KQ 035760.KQ", start_date)
    yfi_datas = {}

    yf.pdr_override()

    for yfi_param in yfi_params:
        tickers = yfi_params[yfi_param]["tickers"]
        start_date = yfi_params[yfi_param]["start_date"]
        end_date = yfi_params[yfi_param]["end_date"]
        interval = yfi_params[yfi_param]["interval"]
        group_by = yfi_params[yfi_param]["group_by"]

        df_datas = pdr.get_data_yahoo(tickers, start_date, end_date, interval=interval, group_by=group_by)

        print(df_datas)

        yfi_datas[yfi_param] = {
            "params": yfi_params[yfi_param],
            "datas": df_datas
        }

        time.sleep(60)

    return yfi_datas


def yfinance_params(yfi_infos):
    yfi_params = {}

    start_date = props["start"]
    end_date = props["end"]
    interval = "1d"
    group_by = "ticker"

    yfi_infos_keys = yfi_infos.keys()
    for yfi_infos_key in yfi_infos_keys:
        key = str(yfi_infos_key)
        value = {
            "countries": yfi_infos[key]["yfi_country"],
            "exchanges": yfi_infos[key]["yfi_exchange"],
            "industries": yfi_infos[key]["yfi_industry"],
            "companies": yfi_infos[key]["yfi_company"],
            "tickers": yfi_infos[key]["yfi_ticker"],
            "start_date": start_date,
            "end_date": end_date,
            "interval": interval,
            "group_by": group_by
        }

        yfi_params[key] = value

    return yfi_params


def yfinance_infos(conn):
    yfi_infos = {}

    cur = conn.cursor()
    cur.execute(
        "SELECT yfi_country, yfi_exchange, yfi_industry, yfi_company, yfi_ticker" + " " +
        "FROM yfinance_info" + " " +
        "WHERE yfi_country='" + props["country"] + "' " +
        "AND yfi_exchange='" + props["exchange"] + "' " +
        "ORDER BY yfi_ticker"
        )

    rows = cur.fetchall()

    length = len(rows)
    batch = int(props["batch"])

    min = 0
    max = int((length-1)/batch)+1
    for cnt in range(min, max):
        key = "idx_" + str(cnt)
        value = {
            "yfi_country": [],
            "yfi_exchange": [],
            "yfi_industry": [],
            "yfi_company": [],
            "yfi_ticker": []
        }

        yfi_infos[key] = value

    num = 0
    for row in rows:
        index = int(num/batch)
        idx = "idx_" + str(index)
        
        yfi_infos[idx]["yfi_country"].append(row[0])
        yfi_infos[idx]["yfi_exchange"].append(row[1])
        yfi_infos[idx]["yfi_industry"].append(row[2])
        yfi_infos[idx]["yfi_company"].append(row[3])
        yfi_infos[idx]["yfi_ticker"].append(row[4])

        num += 1

    return yfi_infos


def pymysql_conn():
    mariadb = env[props["env"]]["mariadb"]

    conn = pymysql.connect(
        user=mariadb["username"],
        password=mariadb["password"],
        host=mariadb["hostname"],
        database=mariadb["database"],
        charset=mariadb["charset"]
    )

    return conn


def yfinance_collector():
    conn = pymysql_conn()

    yfi_infos = yfinance_infos(conn)
    conn.close()

    yfi_params = yfinance_params(yfi_infos)
    yfi_datas = yfinance_data(yfi_params)
    yfi_messages = yfinance_messages(yfi_datas)

    send_messages(yfi_messages)


def yfinance_init():
    start = datetime.now()
    end = datetime.now()+timedelta(days=1)

    parser = argparse.ArgumentParser(description='증권데이터 수집 설정')
    parser.add_argument("--env", required=True, help="어플리케이션 실행 환경")
    parser.add_argument("--country", required=False, default="kr", help="수집대상 설정(국가코드)")
    parser.add_argument("--exchange", required=False, default="kospi", help="수집대상 설정(시장영문이름)")
    parser.add_argument("--batch", required=False, default="100", help="수집 횟수")
    parser.add_argument("--start", required=False, default=start.strftime('%Y-%m-%d'), help="시작")
    parser.add_argument("--end", required=False, default=end.strftime('%Y-%m-%d'), help="끝")

    
    args = parser.parse_args()

    props["env"] = args.env.lower()
    props["country"] = args.country.upper()
    props["exchange"] = args.exchange.upper()
    props["batch"] = args.batch
    props["start"] = args.start
    props["end"] = args.end


if __name__ == "__main__":
    yfinance_init()

    if props["env"] != "local" and props["env"] != "dev" and props["env"] != "prod":
        print("--env: " + props["env"] + " is Illegal parameters")
    else:
        yfinance_collector()