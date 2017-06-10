package `in`.kiranrao.fitository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.google.android.gms.awareness.fence.FenceState
import org.threeten.bp.ZonedDateTime
import timber.log.Timber


class AwarenessReceiver: BroadcastReceiver() {
    companion object {
        private val NOTIFICATION_ID_SUNSET = 2000
    }

    private lateinit var context: Context
    override fun onReceive(context: Context?, intent: Intent?) {
        this.context = context!!
        intent?.let {
            val fenceState = FenceState.extract(it)

            val fenceKey = fenceState.fenceKey
            val currentState = fenceState.currentState
            val previousState = fenceState.previousState

            Timber.d("fenceState = {fenceKey:$fenceKey, currentState: $currentState, previousState: $previousState")

            if (fenceKey != FENCE_KEY_SOLAR_FENCE) return
            if (currentState == FenceState.TRUE && previousState != FenceState.TRUE) {
                handleSunsetEvent()
            }
        }
    }

    private fun handleSunsetEvent() {
        notifySunset()
        retrieveDailyFitnessTotals()
    }

    private fun retrieveDailyFitnessTotals() {
        AwarenessJobIntentService.enqueueWork(context)
    }

    private fun notifySunset() {
        val isoTime = ZonedDateTime.now().toString()
        val sunsetNotification = NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Sunset Time!")
                .setContentText(isoTime)
                .build()
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_SUNSET, sunsetNotification)
        Timber.d("Sunset event occurred at $isoTime")
    }
}