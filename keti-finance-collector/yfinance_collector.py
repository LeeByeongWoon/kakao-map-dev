#!/usr/bin/python3

from json import dumps
from datetime import datetime, timezone, timedelta
import pytz

import pymysql

import yfinance as yf
from pandas_datareader import data as pdr

from kafka import KafkaProducer


username="keti"
password="qwer1234!@"
hostname="192.168.207.2"
database="keti"
charset="utf8"

acks=-1
compression_type="lz4"
bootstrap_servers=["192.168.207.2:9092"]


def send_messages(producer, messages):
    producer.send("dev-keti-finance", value=messages)
    producer.flush()


def producer_conn():
    return KafkaProducer(acks=acks, compression_type=compression_type, bootstrap_servers=bootstrap_servers, value_serializer=lambda x: dumps(x).encode('utf-8'))


def yfinance_data(yfi_param):
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

    tickers = yfi_param["tickers"]
    start_date = yfi_param["start_date"]
    end_date = yfi_param["end_date"]
    interval = yfi_param["interval"]
    group_by = yfi_param["group_by"]

    df_datas = pdr.get_data_yahoo(tickers, start_date, end_date, interval=interval, group_by=group_by)
    
    dict_datas = df_datas.to_dict()

    datas_keys = dict_datas.keys()
    for datas_key in datas_keys:
        dict_ticker = datas_key[0]
        dict_type = datas_key[1]

        dict_data = dict_datas[datas_key]

        data_keys = dict_data.keys()
        for data_key in data_keys:
            yfi_data = {
                "ticker": dict_ticker,
                "type": dict_type,
                "timestamp": data_key.astimezone(pytz.utc).strftime("%Y-%m-%dT%H:%M:%S.%fZ"),
                "value": dict_data[data_key]

            }

            yfi_datas.append(yfi_data)

    return yfi_datas


def yfinance_param(yfi_infos):
    day = timedelta(days=1)
    yes = datetime.now() - day
    now = datetime.now()
    
    start_date = yes.strftime('%Y-%m-%d')
    end_date = now.strftime('%Y-%m-%d')

    yfi_param = {
        "KOSPI": {
            "tickers": yfi_infos["KOSPI"]["value"],
            "start_date": start_date,
            "end_date": end_date,
            "interval": "1h",
            "group_by": "ticker"
        },
        "KOSDAQ": {
            "tickers": yfi_infos["KOSDAQ"]["value"],
            "start_date": start_date,
            "end_date": end_date,
            "interval": "1h",
            "group_by": "ticker"
        }
    }

    return yfi_param


def yfinance_info(conn):
    yfi_infos = {
        "KOSPI": {
            "key": [],
            "value": []
        },
        "KOSDAQ": {
            "key": [],
            "value": []
        }
    }

    cur = conn.cursor()
    # cur.execute(
    #     "SELECT yfi_exchange, yfi_industry, yfi_company, yfi_ticker" + " " +
    #     "FROM yfinance_info" + " " +
    #     "ORDER BY yfi_industry"
    # )
    cur.execute(
        "SELECT yfi_exchange, yfi_industry, yfi_company, yfi_ticker" + " " +
        "FROM yfinance_info" + " " +
        "WHERE yfi_company IN('삼성전자', 'SK하이닉스', '셀트리온헬스케어', '에이비프로바이오')" + " " +
        "ORDER BY yfi_industry"
        )
    rows = cur.fetchall()

    for row in rows:
        key = row[2]
        value = row[3]
    
        yfi_infos[row[0]]["key"].append(key)
        yfi_infos[row[0]]["value"].append(value)

    return yfi_infos


def pymysql_conn(username, password, hostname, database, charset):
    conn = pymysql.connect(
        user=username,
        password=password,
        host=hostname,
        database=database,
        charset=charset
    )

    return conn


def yfinance_collector():
    conn = pymysql_conn(username, password, hostname, database, charset)

    yfi_infos = yfinance_info(conn)
    conn.close()

    yfi_params = yfinance_param(yfi_infos)
    yfi_kospi_params = yfi_params["KOSPI"]
    yfi_kosdaq_params = yfi_params["KOSDAQ"]
    
    yfi_kospi_datas = yfinance_data(yfi_kospi_params)
    yfi_kosdaq_datas = yfinance_data(yfi_kosdaq_params)

    messages = {
        "messages": {
            "KOSPI": yfi_kospi_datas,
            "KOSDAQ": yfi_kosdaq_datas
        }
    }

    producer = producer_conn()
    send_messages(producer, messages)


if __name__ == "__main__":
    yfinance_collector()