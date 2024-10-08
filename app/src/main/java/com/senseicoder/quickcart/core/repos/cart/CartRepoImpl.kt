package com.senseicoder.quickcart.core.repos.cart

import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.model.ProductOfCart
import com.senseicoder.quickcart.core.model.mapCartLineToProductOfCart
import com.senseicoder.quickcart.core.model.mapCartLinesAddProductOfCart
import com.senseicoder.quickcart.core.network.StorefrontHandlerImpl
import com.senseicoder.quickcart.core.network.interfaces.FirebaseHandler
import com.senseicoder.quickcart.core.network.interfaces.StorefrontHandler
import com.senseicoder.quickcart.core.repos.customer.CustomerRepoImpl
import com.senseicoder.quickcart.core.services.SharedPrefs
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.storefront.CreateCartMutation
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.flow.transform
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class)
class CartRepoImpl(private val remoteDataSource: StorefrontHandler, val sharedPref : SharedPrefs)
    : CartRepo {
    override suspend fun createCart(email: String, token: String)
    : Flow<String> {
        return remoteDataSource.createCart(email, token).map { it.id }
    }

    override fun getUserToken(): String {
        return sharedPref.getSharedPrefString(Constants.USER_TOKEN, Constants.USER_TOKEN_DEFAULT)
    }

    override suspend fun addToCartByIds(cartId: String, productsOfCart: List<ProductOfCart>)
    : Flow<List<ProductOfCart>> {
        return remoteDataSource.addToCartById(cartId, productsOfCart).transform {
            emit(it.cart!!.lines.nodes.map { productVariant-> productVariant.mapCartLinesAddProductOfCart() })
        }.timeout(15.seconds)
    }

    override fun setCartId(cartId: String) {
        sharedPref.setSharedPrefString(Constants.CART_ID, cartId)
    }

    override fun getCartId(): String {
        return sharedPref.getSharedPrefString(Constants.CART_ID, Constants.CART_ID_DEFAULT)
    }

    override suspend fun getCartProducts(cartId: String)
    : Flow<List<ProductOfCart>> {
        return remoteDataSource.getCartProducts(cartId).transform { cart ->
            emit(cart.lines.edges.map { it.node.mapCartLineToProductOfCart() })
        }.timeout(15.seconds)
    }

    override suspend fun removeProductFromCart(cartId: String, lineId: String)
    : Flow<String> {
        return remoteDataSource.removeProductFromCart(cartId, lineId).timeout(15.seconds)
    }


    companion object {
        private const val TAG = "CustomerRepoImpl"

        @Volatile
        private var instance: CartRepoImpl? = null
        fun getInstance(
            storefrontHandler: StorefrontHandler,
            sharedPrefs: SharedPrefs
        ): CartRepoImpl {
            return instance ?: synchronized(this) {
                val instance =
                    CartRepoImpl(
                        storefrontHandler,
                        sharedPrefs
                    )
                Companion.instance = instance
                instance
            }
        }
    }
}