
package com.senseicoder.quickcart.core.global

/**
 * Extension functions and Binding Adapters.
 */

import android.content.BroadcastReceiver
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.senseicoder.quickcart.R
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Transforms static java function Snackbar.make() to an extension function on View.
 */
fun View.showSnackbar(snackbarText: String, timeLength: Int = 4000) {
    Snackbar.make(this, snackbarText, timeLength).run {
        view.setBackgroundColor(this.context.getColor(R.color.secondary))
        show()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.toDateTime(pattern: String): String {
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
    return  formatter.format(this)
}



/**
 * Run work asynchronously from a [BroadcastReceiver].
 */
fun BroadcastReceiver.goAsync(
    coroutineScope: CoroutineScope,
    dispatcher: CoroutineDispatcher,
    block: suspend () -> Unit
) {
    val pendingResult = goAsync()
    coroutineScope.launch(dispatcher) {
        try {
            block()
        }finally {
            pendingResult.finish()
        }
    }
}


//for testing livedata
@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(value: T) {
            data = value
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)

    try {
        afterObserve.invoke()

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }

    } finally {
        this.removeObserver(observer)
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}

/*@SuppressLint("DefaultLocale")
fun priceConversion(price : String, currency: Currency, conversionRate : ConversionResponse) : String{
    val realValue = if(currency == Currency.EGP){
        price.toDouble()
    }else {
        price.toDouble() * conversionRate.rates.USD.rate.toDouble()
    }
    return String.format("%.1f",realValue)
}*/

fun Double.toTwoDecimalPlaces(locale: Locale = Locale.US): String {
    val numberFormat = NumberFormat.getInstance(locale)
    val parsedNumber = numberFormat.parse(this.toString())?.toDouble() ?: throw NumberFormatException("Cannot parse: $this")
    return String.format(locale, "%.2f", parsedNumber)
}

/*fun View.setupSnackbar(
    lifecycleOwner: LifecycleOwner,
    snackbarEvent: LiveData<Event<Int>>,
    timeLength: Int
) {

    snackbarEvent.observe(lifecycleOwner, Observer { event ->
        event.getContentIfNotHandled()?.let {
            showSnackbar(context.getString(it), timeLength)
        }
    })
}*/

fun Long.toDateTime(pattern: String): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        java.time.format.DateTimeFormatter.ofPattern(pattern)
            .format(Instant.ofEpochSecond(this).atZone(ZoneId.of("UTC")))
    } else {
        val sdf = SimpleDateFormat(pattern, Locale.US)
        val netDate = Date(this * 1000)
        sdf.format(netDate)
    }
}


/*fun Fragment.setupRefreshLayout(
    refreshLayout: ScrollChildSwipeRefreshLayout,
    scrollUpChild: View? = null
) {
    refreshLayout.setColorSchemeColors(
        ContextCompat.getColor(requireActivity(), R.color.primary),
        ContextCompat.getColor(requireActivity(), R.color.on_primary),
        ContextCompat.getColor(requireActivity(), R.color.secondary)
    )
    // Set the scrolling view in the custom SwipeRefreshLayout.
    scrollUpChild?.let {
        refreshLayout.scrollUpChild = it
    }
}*/

/*fun Activity.setupRefreshLayout(
    refreshLayout: ScrollChildSwipeRefreshLayout,
    scrollUpChild: View? = null
) {
    refreshLayout.setColorSchemeColors(
        ContextCompat.getColor(this@setupRefreshLayout, R.color.primary),
        ContextCompat.getColor(this@setupRefreshLayout, R.color.on_primary),
        ContextCompat.getColor(this@setupRefreshLayout, R.color.secondary)
    )
    // Set the scrolling view in the custom SwipeRefreshLayout.
    scrollUpChild?.let {
        refreshLayout.scrollUpChild = it
    }
}*/
