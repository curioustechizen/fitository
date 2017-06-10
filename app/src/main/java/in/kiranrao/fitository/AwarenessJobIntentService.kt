package `in`.kiranrao.fitository

import android.content.Context
import android.content.Intent
import android.support.v4.app.JobIntentService
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.fitness.Fitness
import timber.log.Timber

/**
 * Created by kiran on 10/6/17.
 */
class AwarenessJobIntentService : JobIntentService()
        /*, GoogleApiClient.ConnectionCallbacks */{


    companion object {
        private val JOB_ID = 3000;
        private val ACTION_RETRIEVE_DAILY_TOTALS = "in.kiranrao.fitository.ACTION_RETRIEVE_DAILY_TOTALS"
        fun enqueueWork(context: Context) {
            JobIntentService.enqueueWork(
                    context,
                    AwarenessJobIntentService::class.java,
                    JOB_ID,
                    Intent(ACTION_RETRIEVE_DAILY_TOTALS))
        }
    }

    private lateinit var googleApiClient: GoogleApiClient

    override fun onHandleWork(intent: Intent) {
        retrieveDailyFitnessTotals()
    }

//    override fun onConnected(bundle: Bundle?) {
//        val fitController = FitController(this, googleApiClient)
//        fitController.retrieveDailyTotals()
//    }
//
//    override fun onConnectionSuspended(cause: Int) {
//        Timber.i("Google Play services Connection suspended, cause: $cause")
//    }


    private fun retrieveDailyFitnessTotals() {
        googleApiClient = GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(Fitness.SCOPE_ACTIVITY_READ_WRITE)
                //.addConnectionCallbacks(this)
                .build()
        val connectionResult = googleApiClient.blockingConnect()
        if(connectionResult.isSuccess) {
            FitController(this, googleApiClient).retrieveDailyTotals()
        } else {
            Timber.i("Could not connect to GooglePlayServices, connectionResult: $connectionResult")
        }
    }
}