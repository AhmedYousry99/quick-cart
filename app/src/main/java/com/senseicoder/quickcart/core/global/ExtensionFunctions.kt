package com.senseicoder.quickcart.core.global

/**
 * Extension functions and Binding Adapters.
 */

import android.content.BroadcastReceiver
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Patterns
import android.view.View
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.model.AddressOfCustomer
import com.senseicoder.quickcart.core.model.CurrencySymbol
import com.senseicoder.quickcart.core.network.currency.CurrencyRemoteImpl
import com.storefront.CustomerAddressesQuery
import com.storefront.CustomerDefaultAddressUpdateMutation
import com.storefront.GetCartDetailsQuery
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
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
fun View.showSnackbar(snackbarText: String, timeLength: Int = 4000) {
    Snackbar.make(this, snackbarText, timeLength).run {
        view.setBackgroundColor(this.context.getColor(R.color.secondary))
        show()
    }
}

/**
 * Transforms static java function Snackbar.make() to an extension function on Fragment.
 */
fun Fragment.showSnackbar(snackbarText: String, timeLength: Int = 4000) {
    Snackbar.make(requireView(), snackbarText, timeLength).run {
        view.setBackgroundColor(this.context.getColor(R.color.secondary))
        show()
    }
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

fun Fragment.showErrorSnackbar(snackbarText: String, timeLength: Int = 4000) {
    Snackbar.make(requireView(), snackbarText, timeLength).run {
        view.setBackgroundColor(ContextCompat.getColor(this.context, R.color.red))
        show()
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

fun String.withoutGIDPrefix(): String {
    return substringAfterLast("/")
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
data class myInt(val int: Int)

fun main() {
    runBlocking {
        println(text())
    }
}
suspend fun text():Map<String,CurrencySymbol>{
    val input :String = "{AED=CurrencySymbol(symbol=AED), AFN=CurrencySymbol(symbol=Af), ALL=CurrencySymbol(symbol=ALL), AMD=CurrencySymbol(symbol=AMD), ANG=CurrencySymbol(symbol=�), AOA=CurrencySymbol(symbol=Kz), ARS=CurrencySymbol(symbol=AR\$), AUD=CurrencySymbol(symbol=AU\$), AWG=CurrencySymbol(symbol=Afl.), AZN=CurrencySymbol(symbol=man.), BAM=CurrencySymbol(symbol=KM), BBD=CurrencySymbol(symbol=Bds\$), BDT=CurrencySymbol(symbol=Tk), BGN=CurrencySymbol(symbol=BGN), BHD=CurrencySymbol(symbol=BD), BIF=CurrencySymbol(symbol=FBu), BMD=CurrencySymbol(symbol=BD\$), BND=CurrencySymbol(symbol=BN\$), BOB=CurrencySymbol(symbol=Bs), BRL=CurrencySymbol(symbol=R\$), BSD=CurrencySymbol(symbol=B\$), BTN=CurrencySymbol(symbol=Nu.), BWP=CurrencySymbol(symbol=BWP), BYN=CurrencySymbol(symbol=Br), BYR=CurrencySymbol(symbol=BYR), BZD=CurrencySymbol(symbol=BZ\$), CAD=CurrencySymbol(symbol=CA\$), CDF=CurrencySymbol(symbol=CDF), CHF=CurrencySymbol(symbol=CHF), CLF=CurrencySymbol(symbol=UF), CLP=CurrencySymbol(symbol=CL\$), CNY=CurrencySymbol(symbol=CN�), COP=CurrencySymbol(symbol=CO\$), CRC=CurrencySymbol(symbol=?), CUC=CurrencySymbol(symbol=CUC\$), CUP=CurrencySymbol(symbol=\$MN), CVE=CurrencySymbol(symbol=CV\$), CZK=CurrencySymbol(symbol=K?), DJF=CurrencySymbol(symbol=Fdj), DKK=CurrencySymbol(symbol=Dkr), DOP=CurrencySymbol(symbol=RD\$), DZD=CurrencySymbol(symbol=DA), EGP=CurrencySymbol(symbol=EGP), ERN=CurrencySymbol(symbol=Nfk), ETB=CurrencySymbol(symbol=Br), EUR=CurrencySymbol(symbol=�), FJD=CurrencySymbol(symbol=FJ\$), FKP=CurrencySymbol(symbol=FK�), GBP=CurrencySymbol(symbol=�), GEL=CurrencySymbol(symbol=GEL), GGP=CurrencySymbol(symbol=�), GHS=CurrencySymbol(symbol=GH?), GIP=CurrencySymbol(symbol=�), GMD=CurrencySymbol(symbol=D), GNF=CurrencySymbol(symbol=FG), GTQ=CurrencySymbol(symbol=GTQ), GYD=CurrencySymbol(symbol=G\$), HKD=CurrencySymbol(symbol=HK\$), HNL=CurrencySymbol(symbol=HNL), HRK=CurrencySymbol(symbol=kn), HTG=CurrencySymbol(symbol=G), HUF=CurrencySymbol(symbol=Ft), IDR=CurrencySymbol(symbol=Rp), ILS=CurrencySymbol(symbol=?), IMP=CurrencySymbol(symbol=�), INR=CurrencySymbol(symbol=Rs), IQD=CurrencySymbol(symbol=IQD), IRR=CurrencySymbol(symbol=IRR), ISK=CurrencySymbol(symbol=Ikr), JEP=CurrencySymbol(symbol=�), JMD=CurrencySymbol(symbol=J\$), JOD=CurrencySymbol(symbol=JD), JPY=CurrencySymbol(symbol=�), KES=CurrencySymbol(symbol=Ksh), KGS=CurrencySymbol(symbol=KGS), KHR=CurrencySymbol(symbol=KHR), KMF=CurrencySymbol(symbol=CF), KPW=CurrencySymbol(symbol=?), KRW=CurrencySymbol(symbol=?), KWD=CurrencySymbol(symbol=KD), KYD=CurrencySymbol(symbol=CI\$), KZT=CurrencySymbol(symbol=KZT), LAK=CurrencySymbol(symbol=?N), LBP=CurrencySymbol(symbol=LB�), LKR=CurrencySymbol(symbol=SLRs), LRD=CurrencySymbol(symbol=LD\$), LSL=CurrencySymbol(symbol=L), LTL=CurrencySymbol(symbol=Lt), LVL=CurrencySymbol(symbol=Ls), LYD=CurrencySymbol(symbol=LD), MAD=CurrencySymbol(symbol=MAD), MDL=CurrencySymbol(symbol=MDL), MGA=CurrencySymbol(symbol=MGA), MKD=CurrencySymbol(symbol=MKD), MMK=CurrencySymbol(symbol=MMK), MNT=CurrencySymbol(symbol=?), MOP=CurrencySymbol(symbol=MOP\$), MRO=CurrencySymbol(symbol=UM), MUR=CurrencySymbol(symbol=MURs), MVR=CurrencySymbol(symbol=MRf), MWK=CurrencySymbol(symbol=MK), MXN=CurrencySymbol(symbol=MX\$), MYR=CurrencySymbol(symbol=RM), MZN=CurrencySymbol(symbol=MTn), NAD=CurrencySymbol(symbol=N\$), NGN=CurrencySymbol(symbol=?), NIO=CurrencySymbol(symbol=C\$), NOK=CurrencySymbol(symbol=Nkr), NPR=CurrencySymbol(symbol=NPRs), NZD=CurrencySymbol(symbol=NZ\$), OMR=CurrencySymbol(symbol=OMR), PAB=CurrencySymbol(symbol=B/.), PEN=CurrencySymbol(symbol=S/.), PGK=CurrencySymbol(symbol=K), PHP=CurrencySymbol(symbol=?), PKR=CurrencySymbol(symbol=PKRs), PLN=CurrencySymbol(symbol=z?), PYG=CurrencySymbol(symbol=?), QAR=CurrencySymbol(symbol=QR), RON=CurrencySymbol(symbol=RON), RSD=CurrencySymbol(symbol=din.), RUB=CurrencySymbol(symbol=RUB), RWF=CurrencySymbol(symbol=RWF), SAR=CurrencySymbol(symbol=SR), SBD=CurrencySymbol(symbol=SI\$), SCR=CurrencySymbol(symbol=SRe), SDG=CurrencySymbol(symbol=SDG), SEK=CurrencySymbol(symbol=Skr), SGD=CurrencySymbol(symbol=S\$), SHP=CurrencySymbol(symbol=�), SLL=CurrencySymbol(symbol=Le), SOS=CurrencySymbol(symbol=Ssh), SRD=CurrencySymbol(symbol=\$), STD=CurrencySymbol(symbol=Db), SVC=CurrencySymbol(symbol=?), SYP=CurrencySymbol(symbol=SY�), SZL=CurrencySymbol(symbol=L), THB=CurrencySymbol(symbol=?), TJS=CurrencySymbol(symbol=TJS), TMT=CurrencySymbol(symbol=T), TND=CurrencySymbol(symbol=DT), TOP=CurrencySymbol(symbol=T\$), TRY=CurrencySymbol(symbol=TL), TTD=CurrencySymbol(symbol=TT\$), TWD=CurrencySymbol(symbol=NT\$), TZS=CurrencySymbol(symbol=TSh), UAH=CurrencySymbol(symbol=?), UGX=CurrencySymbol(symbol=USh), USD=CurrencySymbol(symbol=\$), UYU=CurrencySymbol(symbol=\$U), UZS=CurrencySymbol(symbol=UZS), VEF=CurrencySymbol(symbol=Bs.F.), VND=CurrencySymbol(symbol=?), VUV=CurrencySymbol(symbol=VUV), WST=CurrencySymbol(symbol=WS\$), XAF=CurrencySymbol(symbol=FCFA), XAG=CurrencySymbol(symbol=XAG), XAU=CurrencySymbol(symbol=XAU), XCD=CurrencySymbol(symbol=EC\$), XDR=CurrencySymbol(symbol=SDR), XOF=CurrencySymbol(symbol=CFA), XPF=CurrencySymbol(symbol=CFP), YER=CurrencySymbol(symbol=YR), ZAR=CurrencySymbol(symbol=R), ZMK=CurrencySymbol(symbol=ZK), ZMW=CurrencySymbol(symbol=ZK), ZWL=CurrencySymbol(symbol=ZWL), XPT=CurrencySymbol(symbol=XPT), XPD=CurrencySymbol(symbol=XPD), BTC=CurrencySymbol(symbol=?), ETH=CurrencySymbol(symbol=?), BNB=CurrencySymbol(symbol=BNB), XRP=CurrencySymbol(symbol=XRP), SOL=CurrencySymbol(symbol=SOL), DOT=CurrencySymbol(symbol=DOT), AVAX=CurrencySymbol(symbol=AVAX), MATIC=CurrencySymbol(symbol=MATIC), LTC=CurrencySymbol(symbol=?), ADA=CurrencySymbol(symbol=ADA), USDT=CurrencySymbol(symbol=USDT), USDC=CurrencySymbol(symbol=USDC), DAI=CurrencySymbol(symbol=DAI), ARB=CurrencySymbol(symbol=ARB), OP=CurrencySymbol(symbol=OP), VES=CurrencySymbol(symbol=Bs.S.), STN=CurrencySymbol(symbol=STN), MRU=CurrencySymbol(symbol=MRU)}"
    val entries = input.removeSurrounding("{", "}").split(", ")
    return entries.associate { entry ->
        val (key, value) = entry.split("=")
        val symbol = value.substringAfter("symbol=").removeSurrounding("(", ")")
        key.trim() to CurrencySymbol(symbol.trim())
    }
}