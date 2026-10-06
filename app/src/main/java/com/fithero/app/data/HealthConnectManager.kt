package com.fithero.app.data

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.LocalDateTime
import java.time.ZoneId
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord

class HealthConnectManager(private val context: Context) {

    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    // Permisos necesarios
    val permissions = setOf(
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class)
    )

    // Comprobar si Health Connect está disponible
    fun isAvailable(): Boolean {
        return HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE
    }

    // Comprobar si los permisos están concedidos
    suspend fun hasPermissions(): Boolean {
        return try {
            val granted = healthConnectClient.permissionController.getGrantedPermissions()
            granted.containsAll(permissions)
        } catch (e: Exception) {

            false
        }
    }

    // Leer calorías quemadas hoy
    suspend fun readCaloriasHoy(): Double {
        return try {
            val hoy = LocalDateTime.now().toLocalDate()
            val inicio = hoy.atStartOfDay(ZoneId.systemDefault()).toInstant()
            val fin = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()

            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = TotalCaloriesBurnedRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(inicio, fin)
                )
            )

            response.records.sumOf { it.energy.inKilocalories }
        } catch (e: Exception) {
            e.printStackTrace()
            0.0
        }
    }
}