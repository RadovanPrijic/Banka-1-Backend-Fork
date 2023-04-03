from flask import Flask
from dotenv import load_dotenv
from controllers.stocks import stocks_controller
from controllers.forex import forex_controller
import cache


config = {
    "DEBUG": True,
    "CACHE_TYPE": "SimpleCache",
    "CACHE_DEFAULT_TIMEOUT": 120
}

load_dotenv()
app = Flask(__name__)
cache.setup_cache(app)

app.register_blueprint(stocks_controller)
app.register_blueprint(forex_controller)


if __name__ == '__main__':
    app.run(port=8888)

