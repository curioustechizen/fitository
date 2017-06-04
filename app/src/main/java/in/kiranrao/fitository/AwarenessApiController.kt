package `in`.kiranrao.fitository

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.fence.FenceUpdateRequest
import com.google.android.gms.awareness.fence.TimeFence
import com.google.android.gms.common.api.GoogleApiClient
import java.util.concurrent.TimeUnit

/**
 * Created by kiran on 28/5/17.
 */
val FENCE_KEY_SOLAR_FENCE = "in.kiranrao.fitository.FENCE_KEY_SOLAR_FENCE"
private val TAG = "AwarenessApiController"

class AwarenessApiController(val context: Context, val googleApiClient: GoogleApiClient) {

    private val pendingIntent = PendingIntent.getBroadcast(context, 0, Intent(context, AwarenessReceiver::class.java), 0)

    @SuppressLint("MissingPermission") //This is only called if we have the permission
    fun addSolarFence() {
        val FIVE_MINUTES_IN_MILLIS = TimeUnit.MINUTES.toMillis(5)
        val solarFence = TimeFence.aroundTimeInstant(TimeFence.TIME_INSTANT_SUNSET, -FIVE_MINUTES_IN_MILLIS, FIVE_MINUTES_IN_MILLIS)
        Awareness.FenceApi
                .updateFences(googleApiClient, FenceUpdateRequest.Builder().addFence(
                        FENCE_KEY_SOLAR_FENCE, solarFence, pendingIntent).build())
                .setResultCallback { status ->
                    Log.i(TAG,
                            if (status.isSuccess) "Sunset fence registered successfully"
                            else "Sunset fence could not be registered: ${status}")
                }

    }
}

