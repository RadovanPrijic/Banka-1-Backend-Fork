from flask_caching import Cache

config = {
    "DEBUG": True,
    "CACHE_TYPE": "SimpleCache",
    "CACHE_DEFAULT_TIMEOUT": 60
}

global cache


def setup_cache(app):
    global cache
    app.config.from_mapping(config)
    cache = Cache(app)

