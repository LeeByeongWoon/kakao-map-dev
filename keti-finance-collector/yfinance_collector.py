#!/usr/bin/python3

import argparse

import time
import pytz
from datetime import datetime, timezone, timedelta

import pymysql
import yfinance as yf
from kafka import KafkaProducer

from json import dumps
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
            "acks": -1,
            "compression_type": "lz4",
            "bootstrap_servers": ["192.168.207.2:9092"],
            "serializer": lambda x: dumps(x).encode('utf-8')
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
            "acks": -1,
            "compression_type": "lz4",
            "bootstrap_servers": ["192.168.100.71:9092","192.168.100.72:9092","192.168.100.73:9092"],
            "serializer": lambda x: dumps(x).encode('utf-8')
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
            "acks": -1,
            "compression_type": "lz4",
            "bootstrap_servers": ["192.168.100.71:9092","192.168.100.72:9092","192.168.100.73:9092"],
            "serializer": lambda x: dumps(x).encode('utf-8')
        }
    }
}
props = {}


def send_messages(yfi_datas):
    kafka = env[props["env"]]["kafka"]

    producer = KafkaProducer(
                acks=kafka["acks"],
                compression_type=kafka["compression_type"],
                bootstrap_servers=kafka["bootstrap_servers"],
                value_serializer= kafka["serializer"]
           )

    for yfi_data in yfi_datas:
        producer.send("dev-keti-finance", value={"messages": yfi_data})
        producer.flush()


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
    yfi_datas = []

    yf.pdr_override()

    exchanges = yfi_params["exchanges"]
    industries = yfi_params["industries"]
    companies = yfi_params["companies"]
    tickers = yfi_params["tickers"]
    start_date = yfi_params["start_date"]
    end_date = yfi_params["end_date"]
    interval = yfi_params["interval"]
    group_by = yfi_params["group_by"]

    df_datas = pdr.get_data_yahoo(tickers, start_date, end_date, interval=interval, group_by=group_by)

    dict_datas = df_datas.to_dict()
    datas_keys = dict_datas.keys()
    for cnt in range(0, len(tickers)):
        yfi_data = {
            tickers[cnt]: {
                "exchange": exchanges[cnt],
                "industry": industries[cnt],
                "company": companies[cnt],
                "ticker": tickers[cnt]
            }
        }

        for datas_key in datas_keys:
            ticker = datas_key[0]
            type = datas_key[1]
            if tickers[cnt] == ticker:
                dict_data = dict_datas[datas_key]
                data_keys = dict_data.keys()
                yfi_data[tickers[cnt]][type] = list(
                    {
                        "timestamp": data_key.astimezone(pytz.utc).strftime("%Y-%m-%dT%H:%M:%S.%fZ"),
                        "key": type,
                        "value": dict_data[data_key]
                    } for data_key in data_keys)

        yfi_datas.append(yfi_data)

    return yfi_datas


def yfinance_params(yfi_infos):
    yfi_params = {}

    day = timedelta(days=1)
    yes = datetime.now() - day
    now = datetime.now()

    start_date = yes.strftime('%Y-%m-%d')
    end_date = now.strftime('%Y-%m-%d')
    interval = "1h"
    group_by = "ticker"

    yfi_params = {
        "exchanges": yfi_infos["yfi_exchange"],
        "industries": yfi_infos["yfi_industry"],
        "companies": yfi_infos["yfi_company"],
        "tickers": yfi_infos["yfi_ticker"],
        "start_date": start_date,
        "end_date": end_date,
        "interval": interval,
        "group_by": group_by
    }

    return yfi_params


def yfinance_infos(conn):
    yfi_infos = {
        "yfi_exchange": [],
        "yfi_industry": [],
        "yfi_company": [],
        "yfi_ticker": []
    }

    cur = conn.cursor()
    cur.execute(
        "SELECT yfi_exchange, yfi_industry, yfi_company, yfi_ticker" + " " +
        "FROM yfinance_info" + " " +
        "WHERE yfi_country='" + props["country"] + "' " +
        "AND yfi_exchange='" + props["exchange"] + "' " +
        "ORDER BY yfi_exchange, yfi_industry, yfi_ticker"
        )
    rows = cur.fetchall()

    for row in rows:
        yfi_infos["yfi_exchange"].append(row[0])
        yfi_infos["yfi_industry"].append(row[1])
        yfi_infos["yfi_company"].append(row[2])
        yfi_infos["yfi_ticker"].append(row[3])

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

    send_messages(yfi_datas)


def yfinance_init():
    parser = argparse.ArgumentParser(description='증권데이터 수집 설정')
    parser.add_argument("--env", required=True, help="어플리케이션 실행 환경")
    parser.add_argument("--country", required=False, default="kr", help="수집대상 설정(국가코드)")
    parser.add_argument("--exchange", required=False, default="kospi", help="수집대상 설정(시장영문이름)")
    
    args = parser.parse_args()

    props["env"] = args.env.lower()
    props["country"] = args.country.upper()
    props["exchange"] = args.exchange.upper()


if __name__ == "__main__":
    yfinance_init()

    if props["env"] != "local" and props["env"] != "dev" and props["env"] != "prod":
        print("--env: " + props["env"] + " is Illegal parameters")
    else:
        yfinance_collector()