from flask import Blueprint, request, current_app
import requests
import os
import json

stocks_controller = Blueprint('stocks', __name__, url_prefix='/stocks')


@stocks_controller.route('/time-series')
def get_time_series():
    request_args = request.args
    symbol = request_args.get("symbol")
    ts_function = request_args.get("time_series", default="TIME_SERIES_MONTHLY", type=str)

    accepted_values = ["TIME_SERIES_MONTHLY", "TIME_SERIES_WEEKLY", "TIME_SERIES_DAILY"]
    if ts_function not in accepted_values:
        return bad_request()

    if symbol is not None and ts_function is not None:
        av_data = get_alpha_vantage(symbol, ts_function)
        response_data = fix_data(av_data)

        response = current_app.response_class(
            response=json.dumps(response_data),
            status=200,
            mimetype='application/json'
        )

        return response
    else:
        return bad_request()


@stocks_controller.route('/time-series/intraday')
def get_time_series_intraday():
    request_args = request.args
    symbol = request_args.get("symbol")
    interval = request_args.get("interval", default="5min", type=str)

    accepted_values = ["1min", "5min", "15min", "30min", "60min"]
    if interval not in accepted_values:
        return bad_request()

    if symbol is not None and interval is not None:
        av_data = get_alpha_vantage_intraday(symbol, interval)
        response_data = fix_data(av_data)

        response = current_app.response_class(
            response=json.dumps(response_data),
            status=200,
            mimetype='application/json'
        )

        return response
    else:
        return bad_request()


def get_alpha_vantage(symbol, ts_function):
    api_key = os.getenv('API_KEY')

    url = 'https://www.alphavantage.co/query?function=' + ts_function + '&symbol=' + symbol + '&apikey=' + api_key
    av_response = requests.get(url)
    av_data = av_response.json()

    return av_data


def get_alpha_vantage_intraday(symbol, interval):
    api_key = os.getenv('API_KEY')

    url = 'https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=' + symbol + '&interval=' + \
          interval + '&apikey=' + api_key
    av_response = requests.get(url)
    av_data = av_response.json()

    return av_data


def fix_data(data_to_fix):
    time_series = []

    for key, value in data_to_fix.items():
        if key == 'Meta Data':
            continue
        for ts_key, ts_value in value.items():
            ts = {
                "date": ts_key,
                "open": ts_value['1. open'],
                "high": ts_value['2. high'],
                "low": ts_value['3. low'],
                "close": ts_value['4. close'],
                "volume": ts_value['5. volume']
            }
            time_series.append(ts)

    fixed_data = {
        "symbol": data_to_fix['Meta Data']['2. Symbol'],
        "last_refreshed": data_to_fix['Meta Data']['3. Last Refreshed'],
        "time_series": time_series
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
