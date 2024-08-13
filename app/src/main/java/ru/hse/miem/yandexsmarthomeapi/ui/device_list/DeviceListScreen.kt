package ru.hse.miem.yandexsmarthomeapi.ui.device_list

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.MutableStateFlow
import pl.brightinventions.codified.enums.CodifiedEnum
import ru.hse.miem.yandexsmarthomeapi.domain.YandexSmartHomeClient
import ru.hse.miem.yandexsmarthomeapi.entity.common.DeviceObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.DeviceType
import ru.hse.miem.yandexsmarthomeapi.entity.common.DeviceTypeWrapper
import ru.hse.miem.yandexsmarthomeapi.ui.views.getIconResId
import ru.hse.miem.yandexsmarthomeapi.ui.views.getLabelResId
import ru.hse.miem.yandexsmarthomeapi.ui.views.getReadableName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceListScreen(
    navController: NavController,
    viewModel: DeviceListViewModel = viewModel()
) {
    val devices by viewModel.devices.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Устройства") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = if (isLandscape) 200.dp else 150.dp),
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize()
        ) {
            items(devices) { device ->
                DeviceCard(
                    device = device,
                    onClick = {
                        navController.navigate("device_detail/${device.id}")
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceCard(
    device: DeviceObject,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = device.type.getIconResId()),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = device.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(id = device.type.getLabelResId()),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DeviceListScreenPreview() {
    val navController = rememberNavController()
    val fakeViewModel = object : DeviceListViewModel(YandexSmartHomeClient.getInstance("https://api.iot.yandex.net", "test_token")) {
        override val devices = MutableStateFlow(
            listOf(
                DeviceObject(
                    id = "1",
                    name = "Умная лампа",
                    type = DeviceTypeWrapper(CodifiedEnum.Known(DeviceType.LIGHT)),
                    aliases = mutableListOf(),
                    capabilities = mutableListOf(),
                    externalId = "",
                    groups = mutableListOf(),
                    householdId = "",
                    properties = mutableListOf(),
                    skillId = ""
                ),
                DeviceObject(
                    id = "2",
                    name = "Умная розетка",
                    type = DeviceTypeWrapper(CodifiedEnum.Known(DeviceType.SOCKET)),
                    aliases = mutableListOf(),
                    capabilities = mutableListOf(),
                    externalId = "",
                    groups = mutableListOf(),
                    householdId = "",
                    properties = mutableListOf(),
                    skillId = ""
                )
            )
        )
    }
    MaterialTheme {
        DeviceListScreen(navController = navController, viewModel = fakeViewModel)
    }
}