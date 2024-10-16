package com.senseicoder.quickcart.core.global

/**
 * Extension functions and Binding Adapters.
 */

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.global.view_support.ScrollChildSwipeRefreshLayout
import com.senseicoder.quickcart.core.model.AddressOfCustomer
import com.storefront.CustomerAddressesQuery
import com.storefront.CustomerDefaultAddressUpdateMutation
import com.storefront.GetCartDetailsQuery
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import java.math.RoundingMode
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
fun Activity.showSnackbar(
    snackbarText: String,
    timeLength: Int = 4000,
    color:Int? = null,
    action: (() -> Unit)? = null
) {
    Snackbar.make(this.findViewById(R.id.coOr), snackbarText, timeLength).run {
        view.backgroundTintList = ColorStateList.valueOf(color ?: R.color.secondary)
        view.setOnClickListener {dismiss()}
        if (action != null) {
            setAction("Ok") {
                action.invoke()
            }
        }
        show()
    }
}

/**
 * Transforms static java function Snackbar.make() to an extension function on Fragment.
 */
fun Fragment.showSnackbar(
    snackbarText: String,
    timeLength: Int = 4000,
    color: Int?,
    action: (() -> Unit)? = null,
) : Snackbar{
    return Snackbar.make(requireView(), snackbarText, timeLength).apply {
        view.backgroundTintList = ColorStateList.valueOf(requireContext().getColor(color ?: R.color.secondary))
        view.setOnClickListener {dismiss()}
        if (action != null) {
            setAction("Ok") {
                action.invoke()
            }
        }
        show()
    }
}

fun String.withoutGIDPrefix(): String {
    return substringAfterLast("/")
}

fun Long.addGIDPrefix(): String{
    return "${Constants.API.CUSTOMER_ID_PREFIX}$this"
}

fun String.toColor(): Int {
    return when (this.lowercase()) {
        "red" -> Color.RED
        "green" -> Color.GREEN
        "blue" -> Color.BLUE
        "black" -> Color.BLACK
        "white" -> Color.WHITE
        "burgundy" -> Color.parseColor("#800020")
        "burgandy" -> Color.parseColor("#800020")
        "yellow" -> Color.YELLOW
        "cyan" -> Color.CYAN
        "magenta" -> Color.MAGENTA
        "gray" -> Color.GRAY
        "dark_gray" -> Color.DKGRAY
        "light_gray" -> Color.LTGRAY
        "purple" -> Color.parseColor("#800080")
        "orange" -> Color.parseColor("#FFA500")
        "pink" -> Color.parseColor("#FFC0CB")
        "brown" -> Color.parseColor("#A52A2A")
        "beige" -> Color.parseColor("#F5F5DC")
        "olive" -> Color.parseColor("#808000")
        "maroon" -> Color.parseColor("#800000")
        "navy" -> Color.parseColor("#000080")
        "teal" -> Color.parseColor("#008080")
        "lime" -> Color.parseColor("#00FF00")
        "indigo" -> Color.parseColor("#4B0082")
        "violet" -> Color.parseColor("#EE82EE")
        "gold" -> Color.parseColor("#FFD700")
        "silver" -> Color.parseColor("#C0C0C0")
        "turquoise" -> Color.parseColor("#40E0D0")
        "coral" -> Color.parseColor("#FF7F50")
        "aqua" -> Color.parseColor("#00FFFF")
        "chocolate" -> Color.parseColor("#D2691E")
        "crimson" -> Color.parseColor("#DC143C")
        "fuchsia" -> Color.parseColor("#FF00FF")
        "khaki" -> Color.parseColor("#F0E68C")
        "lavender" -> Color.parseColor("#E6E6FA")
        "plum" -> Color.parseColor("#DDA0DD")
        "salmon" -> Color.parseColor("#FA8072")
        "sienna" -> Color.parseColor("#A0522D")
        "tan" -> Color.parseColor("#D2B48C")
        else -> Color.GRAY
    }
}

fun Int.dpToPx(context: Context): Int {
    val density = context.resources.displayMetrics.density
    return (this * density).toInt()
}

fun Int.lightenColor(factor: Float = 3f): Int {
    val red = 255.coerceAtMost((Color.red(this) * (1 - factor) + 255 * factor).toInt())
    val green = 255.coerceAtMost((Color.green(this) * (1 - factor) + 255 * factor).toInt())
    val blue = 255.coerceAtMost((Color.blue(this) * (1 - factor) + 255 * factor).toInt())
    return Color.rgb(red, green, blue)
}

fun Fragment.showErrorSnackbar(
    snackbarText: String,
    timeLength: Int = 4000,
    action: (() -> Unit)? = null
) : Snackbar{
    return Snackbar.make(requireView(), snackbarText, timeLength).apply {
        view.backgroundTintList = ColorStateList.valueOf(requireContext().getColor(R.color.red))
        view.setOnClickListener {dismiss()}
        if (action != null) {
            setAction("Retry") {
                action.invoke()
            }
        }
        show()
    }
}

fun TextInputLayout.handleErrorOnFocusChange(
    errorFunc: ((text: String) -> String?),
    validationFunc: String.() -> Boolean,
    showHintOnNull: Boolean = false
) {
    this.editText?.setOnFocusChangeListener { _, hasFocus ->
        val inputText = (this.editText?.text ?: "").toString() // Get the text from EditText
        if (hasFocus) {
            this.error = null
            if(showHintOnNull){
                this@handleErrorOnFocusChange.helperText = context.getString(R.string.required)
            }
        } else {
            if (!validationFunc(inputText)) {
                val error = errorFunc(inputText)
                this@handleErrorOnFocusChange.helperText = if(error == null && showHintOnNull) context.getString(R.string.required) else null
                this.error = if(showHintOnNull) error else null
            }
        }
    }
}

fun TextInputLayout.handleErrorOnFocusChange(
    errorFunc: ((text: String) -> String?),
    validationFunc: String.() -> Boolean,
) {
    this.editText?.setOnFocusChangeListener { _, hasFocus ->
        val inputText = (this.editText?.text ?: "").toString() // Get the text from EditText
        if (hasFocus) {
            this.error = null
        } else {
            if (!validationFunc(inputText)) {
                val error = errorFunc(inputText)
                this.error =  error
            }
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.toDateTime(pattern: String): String {
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
    return formatter.format(this)
}

fun String?.isValidEmail(): Boolean {
    return !this.isNullOrBlank() && Patterns.EMAIL_ADDRESS.matcher(this.trim()).matches()
}

fun String?.isValidPassword(): Boolean {
    return !this.isNullOrBlank() && PasswordRegex.matches(this.trim())
}

fun String?.matchesPassword(password: String): Boolean {
    return !this.isNullOrBlank() && this.trim() == password
}

fun String.updateCurrency(percentage:Float?):Float{

    return  Math.round((this.toFloat()* percentage!!) * 100F)/100F
}
fun String.trimCurrencySymbol():String{
    return this.replace(Regex("[^\\d.-]"), "")
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
        } finally {
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
    val parsedNumber = numberFormat.parse(this.toString())?.toDouble()
        ?: throw NumberFormatException("Cannot parse: $this")
    return String.format(locale, "%.2f", parsedNumber)
}

fun Double.toTwoDecimalPlaces(): String {
    return BigDecimal(this).setScale(2, RoundingMode.HALF_EVEN).toString()
}

fun String.toTwoDecimalPlaces(): String {
    return BigDecimal(this).setScale(2, RoundingMode.HALF_EVEN).toString()
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
        DateTimeFormatter.ofPattern(pattern)
            .format(Instant.ofEpochSecond(this).atZone(ZoneId.of("UTC")))
    } else {
        val sdf = SimpleDateFormat(pattern, Locale.US)
        val netDate = Date(this * 1000)
        sdf.format(netDate)
    }
}


fun GetCartDetailsQuery.DefaultAddress.toAddressOfCustomer(): AddressOfCustomer {
    return AddressOfCustomer(
        id = this.id,
        firstName = this.firstName ?: "",
        lastName = this.lastName ?: "",
        address1 = this.address1 ?: "",
        address2 = this.address2,
        city = this.city ?: "",
        country = this.country ?: "",
        phone = this.phone ?: "",
    )
}


fun (CustomerDefaultAddressUpdateMutation.Customer).toCustomerOfDefault(): CustomerAddressesQuery.Customer {
    try {
        return CustomerAddressesQuery.Customer(
            addresses = this.addresses as (CustomerAddressesQuery.Addresses),
            defaultAddress = this.defaultAddress as (CustomerAddressesQuery.DefaultAddress)
        )
    } catch (e: Exception) {
        return CustomerAddressesQuery.Customer(
            addresses = CustomerAddressesQuery.Addresses(edges = emptyList()),
            defaultAddress = null
        )
    }

}

fun Fragment.setupRefreshLayout(
    refreshLayout: ScrollChildSwipeRefreshLayout,
    scrollUpChild: View? = null
) {
    refreshLayout.setColorSchemeColors(
        ContextCompat.getColor(requireActivity(), R.color.primary),
        ContextCompat.getColor(requireActivity(), R.color.primary_faint),
        ContextCompat.getColor(requireActivity(), R.color.secondary)
    )
    // Set the scrolling view in the custom SwipeRefreshLayout.
    scrollUpChild?.let {
        refreshLayout.scrollUpChild = it
    }
}

fun Activity.setupRefreshLayout(
    refreshLayout: ScrollChildSwipeRefreshLayout,
    scrollUpChild: View? = null
) {
    refreshLayout.setColorSchemeColors(
        ContextCompat.getColor(this@setupRefreshLayout, R.color.primary),
        ContextCompat.getColor(this@setupRefreshLayout, R.color.primary_faint),
        ContextCompat.getColor(this@setupRefreshLayout, R.color.secondary)
    )
    // Set the scrolling view in the custom SwipeRefreshLayout.
    scrollUpChild?.let {
        refreshLayout.scrollUpChild = it
    }
}
data class myInt(val int: Int)

fun main (){
runBlocking {
    var x ="38.91 CA$".trimCurrencySymbol().trim()
    println(x)
    x ="38.91 $".trimCurrencySymbol().trim()
    println(x)
     x ="38.91 LE".trimCurrencySymbol().trim()
    println(x)
     x ="38.91 EUR".trimCurrencySymbol().trim()
    println(x)
}
}