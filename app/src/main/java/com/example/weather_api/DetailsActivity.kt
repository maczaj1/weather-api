package com.example.weather_api

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weather_api.ui.theme.WeatherapiTheme
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DetailsActivity : ComponentActivity() {

    private val viewModel: DetailsActivityModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = intent.getStringExtra("CUSTOM_ID") ?: ""

        setContent {
            WeatherapiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    viewModel.getData(id)
                    Showcase(viewModel)

                }
            }
        }
    }
}

@Composable
fun Showcase(viewModel: DetailsActivityModel) {
    val uiState by viewModel.immutableWeathersForecastData.observeAsState()

    LazyColumn {
        val weathers = uiState?.data
        weathers?.let {
            items(weathers) { responseBody ->

                Column {
                    Card(
                        modifier = Modifier
                            .padding(all = 8.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            "Miasto: ${responseBody.city.name}",
                            modifier = Modifier.padding(all = 4.dp),
                            fontSize = 22.sp
                        )
                    }

                    for (i in 0 until minOf(responseBody.list.size, 10)) {
                        val weather = responseBody.list[i]
                        Card(
                            modifier = Modifier
                                .padding(all = 8.dp)
                                .fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    "Data: ${epochToDateTime(weather.dt)}",
                                    modifier = Modifier.padding(all = 4.dp),
                                    fontSize = 22.sp
                                )
                                Text(
                                    "Temperatura: %.2f °C".format(kelvinToCelsius(weather.main.temp)),
                                    modifier = Modifier.padding(all = 4.dp),
                                    fontSize = 22.sp
                                )
                                Text(
                                    "Temperatura odczuwalna: %.2f °C".format(
                                        kelvinToCelsius(
                                            weather.main.feels_like
                                        )
                                    ),
                                    modifier = Modifier.padding(all = 4.dp),
                                    fontSize = 22.sp
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(all = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            "Ciśnienie: ${weather.main.pressure}",
                                            modifier = Modifier.padding(bottom = 4.dp),
                                            fontSize = 22.sp
                                        )
                                        Text(
                                            "Wilgotność: ${weather.main.humidity}",
                                            modifier = Modifier.padding(bottom = 4.dp),
                                            fontSize = 22.sp
                                        )
                                    }
                                    AsyncImage(
                                        model = "https://openweathermap.org/img/wn/${
                                            weather.weather.get(
                                                0
                                            ).icon
                                        }@2x.png",
                                        contentDescription = null,
                                        modifier = Modifier.size(55.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun epochToDateTime(epochSeconds: Long): String {
    val instant = Instant.ofEpochSecond(epochSeconds)
    val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
    return formatter.format(dateTime)
}