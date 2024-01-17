package com.example.weather_api

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weather_api.ui.theme.WeatherapiTheme


class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val viewModelAirPollution: AirPollutionModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WeatherapiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Showcase(viewModel,
                        viewModelAirPollution,
                        onClick = { id -> navigateToDetailsActivity(id) })

                }
            }
        }
    }

    fun navigateToDetailsActivity(id: String) {
        val detailsIntent = Intent(this, DetailsActivity::class.java)
        detailsIntent.putExtra("CUSTOM_ID", id)
        startActivity(detailsIntent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Showcase(
    viewModel: MainViewModel,
    viewModelAirPollution: AirPollutionModel,
    onClick: (String) -> Unit
) {
    var city by remember { mutableStateOf("") }
    val uiState by viewModel.immutableWeathersData.observeAsState()

    LazyColumn {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AsyncImage(
                    model = "https://openweathermap.org/img/wn/04n@2x.png",
                    contentDescription = null,
                    modifier = Modifier.size(55.dp)
                )
                AsyncImage(
                    model = "https://openweathermap.org/img/wn/09n@2x.png",
                    contentDescription = null,
                    modifier = Modifier.size(55.dp)
                )
                AsyncImage(
                    model = "https://openweathermap.org/img/wn/02d@2x.png",
                    contentDescription = null,
                    modifier = Modifier.size(55.dp)
                )
                AsyncImage(
                    model = "https://openweathermap.org/img/wn/01d@2x.png",
                    contentDescription = null,
                    modifier = Modifier.size(55.dp)
                )
                AsyncImage(
                    model = "https://openweathermap.org/img/wn/11d@2x.png",
                    contentDescription = null,
                    modifier = Modifier.size(55.dp)
                )
            }

            TextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("Wpisz miasto") },
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth()
            )

            Button(
                onClick = {
                    viewModel.getData(city)
                    viewModelAirPollution.clearAirPollutionData()
                },
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth()
            ) {
                Text("Pobierz Pogodę")
            }
        }

        when {
            uiState?.isLoading == true -> {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(50.dp)
                            .padding(8.dp)
                    )
                }
            }

            uiState?.error != null -> {
                item {
                    Text(
                        text = "Błąd: ${uiState!!.error}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }

            else -> {
                val weathers = uiState?.data
                weathers?.let {
                    items(weathers) { weather ->
                        Card(
                            modifier = Modifier
                                .padding(all = 8.dp)
                                .fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.clickable { onClick.invoke(weather.name) }
                            ) {
                                Text(
                                    "Miasto: ${weather.name}",
                                    modifier = Modifier.padding(all = 4.dp),
                                    fontSize = 22.sp
                                )
                                Text(
                                    "Temperatura: %.2f °C".format(kelvinToCelsius(weather.main.temp)),
                                    modifier = Modifier.padding(all = 4.dp),
                                    fontSize = 22.sp
                                )
                                Text(
                                    "Temperatura odczuwalna: %.2f °C".format(kelvinToCelsius(weather.main.feels_like)),
                                    modifier = Modifier.padding(all = 4.dp),
                                    fontSize = 22.sp
                                )
                                Text(
                                    "Ciśnienie: ${weather.main.pressure}",
                                    modifier = Modifier.padding(all = 4.dp),
                                    fontSize = 22.sp
                                )
                                Text(
                                    "Wilgotnosc: ${weather.main.humidity}",
                                    modifier = Modifier.padding(all = 4.dp),
                                    fontSize = 22.sp
                                )
                                Button(
                                    onClick = {
                                        viewModelAirPollution.getData(
                                            weather.coord.lon,
                                            weather.coord.lat
                                        )
                                    },
                                    modifier = Modifier
                                        .padding(all = 8.dp)
                                        .fillMaxWidth()
                                ) {
                                    Text("Pokaż jakość powietrza")
                                }

                                val uiStateAirPollution by viewModelAirPollution.immutableAirPollutionData.observeAsState()
                                when {
                                    uiStateAirPollution?.data != null -> {
                                        val airPollution = uiStateAirPollution?.data
                                        val component =
                                            airPollution?.get(0)?.list?.get(0)?.components
                                        show(component?.co, "CO")
                                        show(component?.no, "NO")
                                        show(component?.no2, "NO2")
                                        show(component?.o3, "O3")
                                        show(component?.so2, "SO2")
                                        show(component?.pm2_5, "PM2_5")
                                        show(component?.pm10, "PM10")
                                        show(component?.nh3, "NH3")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun show(wartosc: Double?, nazwa: String) {
    val kolor = wartosc?.let { getAirQualityColor(it, nazwa) }
    Row(
        modifier = Modifier.padding(all = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${nazwa}: ${wartosc}",
            modifier = Modifier.padding(end = 4.dp),
            fontSize = 22.sp
        )
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(kolor!!, shape = CircleShape)
        )
    }
}

fun kelvinToCelsius(kelvin: Double): Double {
    return kelvin - 273.15
}

fun getAirQualityColor(value: Double, parameter: String): Color {
    val thresholds = when (parameter) {
        "SO2" -> listOf(20, 80, 250, 350)
        "NO2" -> listOf(40, 70, 150, 200)
        "PM10" -> listOf(20, 50, 100, 200)
        "PM2_5" -> listOf(10, 25, 50, 75)
        "O3" -> listOf(60, 100, 140, 180)
        "CO" -> listOf(4400, 9400, 12400, 15400)
        else -> return Color.Gray
    }

    val intValue = value.toInt()

    return when {
        intValue <= thresholds[0] -> Color(0xFF006400)
        intValue <= thresholds[1] -> Color(0xFF90EE90)
        intValue <= thresholds[2] -> Color(0xFFFFFF00)
        intValue <= thresholds[3] -> Color(0xFFFFA500)
        intValue > thresholds[3] -> Color(0xFFFF0000)
        else -> Color.Gray
    }
}
