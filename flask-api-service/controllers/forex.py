from flask import Blueprint, request, current_app
import requests
import os
import json
import cache

forex_controller = Blueprint('forex', __name__, url_prefix='/forex')


@forex_controller.route('/exchange')
def get_exchange_rate():
    request_args = request.args
    from_currency = request_args.get("from_currency")
    to_currency = request_args.get("to_currency")

    if from_currency is not None and to_currency is not None:
        av_data = get_alpha_vantage_rate(from_currency, to_currency)
        response_data = fix_exchange_data(av_data)

        response = current_app.response_class(
            response=json.dumps(response_data),
            status=200,
            mimetype='application/json'
        )

        return response
    else:
        return bad_request()


@forex_controller.route('/time-series')
def get_time_series():
    request_args = request.args
    from_currency = request_args.get("from_currency")
    to_currency = request_args.get("to_currency")
    ts_function = request_args.get("time_series", default="FX_MONTHLY", type=str)

    accepted_values = ["FX_MONTHLY", "FX_WEEKLY", "FX_DAILY"]
    if ts_function not in accepted_values:
        return bad_request()

    if from_currency is not None and to_currency is not None and ts_function is not None:
        cache_key = from_currency + to_currency + ts_function
        cached_value = cache.cache.get(cache_key)

        if cached_value is None:
            print("fetching...")
            av_data = get_alpha_vantage(from_currency, to_currency, ts_function)
            response_data = fix_data(av_data)
            cache.cache.set(cache_key, response_data)
        else:
            print("getting from cache...")
            response_data = cache.cache.get(cache_key)

        response = current_app.response_class(
            response=json.dumps(response_data),
            status=200,
            mimetype='application/json'
        )

        return response
    else:
        return bad_request()


@forex_controller.route('/time-series/intraday')
def get_time_series_intraday():
    request_args = request.args
    from_currency = request_args.get("from_currency")
    to_currency = request_args.get("to_currency")
    interval = request_args.get("interval", default="5min", type=str)

    accepted_values = ["1min", "5min", "15min", "30min", "60min"]
    if interval not in accepted_values:
        return bad_request()

    if from_currency is not None and to_currency is not None and interval is not None:
        cache_key = from_currency + to_currency + interval
        cached_value = cache.cache.get(cache_key)

        if cached_value is None:
            print("fetching...")
            av_data = get_alpha_vantage_intraday(from_currency, to_currency, interval)
            response_data = fix_data(av_data)
            cache.cache.set(cache_key, response_data)
        else:
            print("getting from cache...")
            response_data = cache.cache.get(cache_key)

        response = current_app.response_class(
            response=json.dumps(response_data),
            status=200,
            mimetype='application/json'
        )

        return response
    else:
        return bad_request()


def get_alpha_vantage(from_currency, to_currency, ts_function):
    api_key = os.getenv('API_KEY')

    url = 'https://www.alphavantage.co/query?function=' + ts_function + '&from_symbol=' + from_currency + \
          '&to_symbol=' + to_currency + '&apikey=' + api_key
    av_response = requests.get(url)
    av_data = av_response.json()

    return av_data


def get_alpha_vantage_intraday(from_currency, to_currency, interval):
    api_key = os.getenv('API_KEY')

    url = 'https://www.alphavantage.co/query?function=FX_INTRADAY&from_symbol=' + from_currency + '&to_symbol=' \
          + to_currency + '&interval=' + interval + '&apikey=' + api_key
    av_response = requests.get(url)
    av_data = av_response.json()

    return av_data


def get_alpha_vantage_rate(from_currency, to_currency):
    api_key = os.getenv('API_KEY')

    url = 'https://www.alphavantage.co/query?function=CURRENCY_EXCHANGE_RATE&from_currency=' + from_currency + \
          '&to_currency=' + to_currency + '&apikey=' + api_key
    av_response = requests.get(url)
    av_data = av_response.json()

    return av_data


def fix_data(data_to_fix):
    time_series = []
    last_refreshed = None

    for key, value in data_to_fix.items():
        if key == 'Meta Data':
            for md_key, md_value in value.items():
                if "Last Refreshed" in md_key:
                    last_refreshed = md_value
        else:
            for ts_key, ts_value in value.items():
                ts = {
                    "date": ts_key,
                    "open": ts_value['1. open'],
                    "high": ts_value['2. high'],
                    "low": ts_value['3. low'],
                    "close": ts_value['4. close']
                }
                time_series.append(ts)

    fixed_data = {
        "from_currency": data_to_fix['Meta Data']['2. From Symbol'],
        "to_currency": data_to_fix['Meta Data']['3. To Symbol'],
        "last_refreshed": last_refreshed,
        "time_series": time_series
    }

    return fixed_data


def fix_exchange_data(data_to_fix):
    fixed_data = {
        "from_currency": data_to_fix['Realtime Currency Exchange Rate']['1. From_Currency Code'],
        "to_currency": data_to_fix['Realtime Currency Exchange Rate']['3. To_Currency Code'],
        "exchange_rate": data_to_fix['Realtime Currency Exchange Rate']['5. Exchange Rate'],
        "bid_price": data_to_fix['Realtime Currency Exchange Rate']['8. Bid Price'],
        "ask_price": data_to_fix['Realtime Currency Exchange Rate']['9. Ask Price'],
        "last_refresh": data_to_fix['Realtime Currency Exchange Rate']['6. Last Refreshed']
    }

    return fixed_data


def bad_request():
    response = current_app.response_class(
        response=json.dumps({
            "message": "Bad request params."
        }),
        status=400,
        mimetype='application/json'
    )

    return response
