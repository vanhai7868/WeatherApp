package com.hainvph36038.weatherapp


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hainvph36038.weatherapp.api.NetworkResponse
import com.hainvph36038.weatherapp.api.WeatherModel
import kotlinx.coroutines.delay
import java.time.LocalTime

@Composable
fun WeatherPage(viewModel: WeatherViewModel) {
    var city by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }
    val weatherResult by viewModel.weatherResult.observeAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.End
    ) {
        if (isSearchVisible) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                value = city,
                onValueChange = { newValue ->
                    city = newValue
                    if (city.isEmpty()) {
                        viewModel.resetToDefault()
                    }
                },
                label = { Text(text = "Tìm kiếm địa điểm") },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (city.isNotEmpty()) {
                                viewModel.getData(city)
                            } else {
                                viewModel.resetToDefault()
                            }
                            city = ""
                            keyboardController?.hide()
                            isSearchVisible = false
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Tìm kiếm địa điểm")
                    }
                }
            )
        } else {
            IconButton(
                onClick = {
                    isSearchVisible = true
                }
            ) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Hiển thị thanh tìm kiếm")
            }
        }

        when (val result = weatherResult) {
            is NetworkResponse.Error -> {
                Column {
                    Text(text = "Có lỗi xảy ra: ${result.message}", color = Color.Red)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.resetToDefault() }) {
                        Text(text = "Thử lại")
                    }
                }
            }
            NetworkResponse.Loading -> {
                CircularProgressIndicator()
            }
            is NetworkResponse.Success -> {
                WeatherDetails(data = result.data)
            }
            null -> {
                Text(text = "Vui lòng nhập tên thành phố để tìm kiếm", color = Color.Gray)
            }
        }
    }
}




@Composable
fun WeatherDetails(data: WeatherModel){
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ){
//            Icon(
//                imageVector = Icons.Default.LocationOn,
//                contentDescription = "Location icon",
//                modifier = Modifier.size(40.dp)
//            )
            Text(text = data.location.name, fontSize = 30.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = data.location.country, fontSize = 18.sp, color = Color.Gray)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "${data.current.temp_c}°",
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        AsyncImage(
            modifier = Modifier.size(160.dp),
            model = "https:${data.current.condition.icon}".replace("64x64","128x128"),
            contentDescription = "Condition icon",
        )

        Text(
            text = data.current.condition.text,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Card{
            Column (modifier = Modifier.fillMaxWidth()){
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ){
                    WeatherKeyVal("Humidity",data.current.humidity)
                    WeatherKeyVal("Wind Spees",data.current.wind_kph+" km/h")
                }
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ){
                    WeatherKeyVal("UV",data.current.uv)
                    WeatherKeyVal("Participation",data.current.precip_mm+" mm")
                }
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ){
                    WeatherKeyVal("Local Time Date",data.location.localtime)
                }
            }
        }
    }
}

@Composable
fun WeatherKeyVal(key :String, value : String){
    Column (
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = key, fontWeight = FontWeight.SemiBold, color = Color.Gray)
    }
}