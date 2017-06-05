package `in`.kiranrao.fitository

import android.app.Application
import android.content.Context
import android.os.Environment
import android.util.Log
import com.jakewharton.threetenabp.AndroidThreeTen
import timber.log.Timber
import java.io.File
import java.io.FileWriter


/**
 * Created by kiran on 29/5/17.
 */

private val TAG = "FitositoryApp"

class FitositoryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.plant(FileLogTree(this))
        }
    }
}

class FileLogTree (context: Context): Timber.Tree() {
    private val logFile: File
    init {
        val logFileDirectory = File(Environment.getExternalStorageDirectory(), "fitository")
        logFile = File(logFileDirectory, String.format("%s.log", context.getString(R.string.app_name)))
        Log.d(TAG, "Attempting to log to file ${logFile.absolutePath}")
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "FileLogTree: Storage unavailable (probably mounted elsewhere)")
            }
        }
        if (!logFileDirectory.exists() && !logFileDirectory.mkdirs()) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "FileLogTree: Could not create the directory for log file")
            }
        }
        if (!logFile.exists()) {
            if(!logFile.createNewFile()) {
                Log.d(TAG, "FileLogTree: Could not create the log file for writing")
            }

        }
    }
    override fun log(priority: Int, tag: String?, message: String?, throwable: Throwable?) {
        val throwableMessage = throwable?.message
        val line = "Tag: $tag, Message: $message, Throwable: $throwableMessage"
        val writer: FileWriter = FileWriter(logFile, true)
        writer.use { it.appendln(line) }
    }

}

