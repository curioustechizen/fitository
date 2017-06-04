package `in`.kiranrao.fitository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.fence.FenceState
import com.google.android.gms.awareness.fence.FenceState.TRUE
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.fitness.Fitness
import org.threeten.bp.ZonedDateTime

private val NOTIFICATION_ID_SUNSET = 2000
private val TAG = "AwarenessReceiver"

class AwarenessReceiver: BroadcastReceiver(), GoogleApiClient.ConnectionCallbacks {
    private lateinit var googleApiClient: GoogleApiClient
    override fun onConnected(bundle: Bundle?) {
        val fitController = FitController(context, googleApiClient)
        fitController.retrieveDailyTotals()
    }

    override fun onConnectionSuspended(cause: Int) {

    }

    lateinit var context: Context
    override fun onReceive(context: Context?, intent: Intent?) {
        this.context = context!!
        val fenceState = FenceState.extract(intent)

        val fenceKey = fenceState.fenceKey
        val currentState = fenceState.currentState
        val previousState = fenceState.previousState

        Log.d(TAG, "fenceState = {fenceKey:$fenceKey, currentState: $currentState, previousState: $previousState")

        if(fenceKey != FENCE_KEY_SOLAR_FENCE) return
        if(currentState == TRUE && previousState != TRUE) {
            handleSunsetEvent()
        }
    }

    private fun handleSunsetEvent() {
        val isoTime = ZonedDateTime.now().toString()
        val sunsetNotification = NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Sunset Time!")
                .setContentText(isoTime)
                .build()
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_SUNSET, sunsetNotification)
    }

    private fun retrieveDailyFitnessTotals() {
        googleApiClient = GoogleApiClient.Builder(context)
                .addApi(Fitness.HISTORY_API)
                .addScope(Fitness.SCOPE_ACTIVITY_READ_WRITE)
                .addConnectionCallbacks(this)
                .build()
    }
}