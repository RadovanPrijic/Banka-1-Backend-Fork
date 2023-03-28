from flask import Flask
from dotenv import load_dotenv
from controllers.stocks import stocks_controller
from controllers.forex import forex_controller


load_dotenv()
app = Flask(__name__)

app.register_blueprint(stocks_controller)
app.register_blueprint(forex_controller)


if __name__ == '__main__':
    app.run(port=8888)

