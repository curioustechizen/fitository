package `in`.kiranrao.fitository

import android.app.Application

import com.jakewharton.threetenabp.AndroidThreeTen

/**
 * Created by kiran on 29/5/17.
 */

class FitositoryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}
