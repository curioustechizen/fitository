package `in`.kiranrao.fitository

import android.content.Context
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.fitness.Fitness.HistoryApi
import com.google.android.gms.fitness.Fitness.RecordingApi
import com.google.android.gms.fitness.FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.DataType.TYPE_CALORIES_EXPENDED
import com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA
import com.google.android.gms.fitness.data.Field
import timber.log.Timber

private val DATA_TYPES_TO_SUBSCRIBE = listOf(
        TYPE_CALORIES_EXPENDED,
        TYPE_STEP_COUNT_DELTA)

private val NOTIFICATION_IDS_MAP = mapOf(
        TYPE_CALORIES_EXPENDED.name to 2001,
        TYPE_STEP_COUNT_DELTA.name to 2002)

class FitController(val context: Context, val googleApiClient: GoogleApiClient) {

    fun startRecordingFitnessData() {
        DATA_TYPES_TO_SUBSCRIBE.forEach { dataType -> subscribeToDataType(dataType) }
    }

    fun retrieveDailyTotals() {
        DATA_TYPES_TO_SUBSCRIBE.forEach { dataType -> retrieveDailyTotal(dataType) }
    }

    private fun retrieveDailyTotal(dataType: DataType) {
        HistoryApi
                .readDailyTotal(googleApiClient, dataType)
                .setResultCallback { dailyTotalResult ->
                    if (dailyTotalResult.status.isSuccess) {
                        handleTotalSet(dailyTotalResult.total)
                    } else {
                        Timber.w("There was a problem getting daily total for ${dataType.name}, reason: ${dailyTotalResult.status}")
                    }
                }
    }

    private fun handleTotalSet(total: DataSet?) {
        val count: Number? = total?.let {
            if (it.isEmpty) 0
            else {
                val dataPoint = total.dataPoints[0]
                when (total.dataType) {
                    TYPE_CALORIES_EXPENDED -> dataPoint.getValue(Field.FIELD_CALORIES).asFloat()
                    TYPE_STEP_COUNT_DELTA -> dataPoint.getValue(Field.FIELD_STEPS).asInt()
                    else -> 0
                }
            }
        }
        val name = total?.dataType?.name
        Timber.d("Daily total for $name is $count")
        notify(name, count)
    }

    private fun notify(name: String?, count: Number?) {
        val notification = NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(name)
                .setContentText("Today's total is $count")
                .build()
        NOTIFICATION_IDS_MAP[name]?.let { NotificationManagerCompat.from(context).notify(it, notification) }
    }

    private fun subscribeToDataType(dataType: DataType): Unit {
        val dataTypeName = dataType.name
        RecordingApi
                .subscribe(googleApiClient, dataType)
                .setResultCallback { status ->
                    with(status) {
                        val message = if (isSuccess) {
                            if (statusCode == SUCCESS_ALREADY_SUBSCRIBED) "Already subscribed to $dataTypeName"
                            else "Successfully subscribed to $dataTypeName"
                        } else "There was a problem subscribing to $dataTypeName : $statusMessage"
                        Timber.d(message)
                    }
                }
    }
}