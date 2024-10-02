package com.senseicoder.quickcart.core.global

object Constants {
    sealed class SharedPrefs{
        object Settings{
            const val SETTINGS = "settings"
            object Language{
                const val LANGUAGE: String = "language"
                object Values{
                    const val AR: String = "ar"
                    const val EN: String = "en"
                    const val DEFAULT: String = "default"
                }
            }
            object Location{
                const val LOCATION: String = "location"
                object Values{
                    const val GPS: String = "GPS"
                    const val MAP: String = "Map"
                }
            }

            object Temperature{
                const val TEMPERATURE: String = "temperature"
                object Values{
                    const val CELSIUS: String = "Celsius"
                    const val KELVIN: String = "Kelvin"
                    const val FAHRENHEIT: String = "Fahrenheit"
                }
            }

            object Notifications{
                const val NOTIFICAITONS: String = "notifications"
                object Values{
                    const val ENABLE: String = "enable"
                    const val DISABLE: String = "disable"
                }
            }

            object WindSpeed{
                const val WIND_SPEED: String = "windSpeed"
                object Values{
                    const val METER_PER_SECOND: String = "meterPerSecond"
                    const val MILES_PER_HOUR: String = "milesPerHour"
                }
            }
        }
        companion object{
            const val IS_FIRST_TIME: String = "isFirstTime"
        }
    }
    sealed class API{
        /**
         * You can use the lang parameter to get the output in your language.
         * Translation is applied to the city name and description fields.
         * */
        object Lang{
            const val en = "en"
            const val ar = "ar"
        }

        companion object{
            const val REVERSE_GEOCODE_BASE_URL: String = "https://api.openweathermap.org/geo/1.0/"
            const val WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/"
            const val WEATHER_ICON_BASE_URL = "https://openweathermap.org/img/wn/"
            const val UV_BASE_URL = "https://api.openuv.io/api/v1/"
            const val CITIES_BASE_URL = "https://api.api-ninjas.com/v1/"
//            const val INGREDIENTS_URL: String = "https://www.themealdb.com/images/ingredients/"
        }
    }

    const val LONGITUDE: String = "longitude"
    const val LATITUDE: String = "latitude"
    const val NAME: String = "name"
    const val DESCRIPTION: String = "description"
}