#!/usr/bin/python3

import json
import requests
import datetime
import threading
from datetime import date
from collections import OrderedDict

import yahoo_fin.stock_info as si

import yfinance as yf
from pandas_datareader import data as pdr


def run():
    yf.pdr_override()

    start_date = "2021-06-20"
    end_date = "2021-06-23"

    # dow_list = si.tickers_dow()
    # nasdaq_list = si.tickers_nasdaq()
    # sp500_list = si.tickers_sp500()
    # other_list= si.tickers_other()

    
    # 애플, 마이크로소프트, 아마존, 알파벳C, 알파벳A, 페이스북, 테슬라
    dow = pdr.get_data_yahoo("AAPL MSFT AMZN GOOG GOOGL FB TSLA", start_date, end_date)
    # 애플, 마이크로소프트, 아마존, 알파벳C, 알파벳A, 페이스북, 테슬라
    nasdaq = pdr.get_data_yahoo("AAPL MSFT AMZN GOOG GOOGL FB TSLA", start_date, end_date)
    # 애플, 마이크로소프트, 아마존, 알파벳C, 알파벳A, 페이스북, 테슬라
    sp500 = pdr.get_data_yahoo("AAPL MSFT AMZN GOOG GOOGL FB TSLA", start_date, end_date)

    # 삼성, SK하이닉스, 카카오, 네이버,  삼성전자우, LG화학, 삼성바이오로직스
    # kospi = pdr.get_data_yahoo("005930.KS 000660.KS 035720.KS 035420.KS 005935.KS 051910.KS 207940.KS", start_date, end_date)
    kospi = pdr.get_data_yahoo("005930.KS", start_date, end_date)
    # 셀트리온헬스케어, 셀트리온제약, 에코프로비엠, 씨젠, 펄어비스, 카카오게임즈, CJ ENM
    kosdaq = pdr.get_data_yahoo("091990.KQ 068760.KQ 247540.KQ 096530.KQ 263750.KQ 293490.KQ 035760.KQ", start_date)

    print(kospi)


if __name__ == "__main__":
    run()

