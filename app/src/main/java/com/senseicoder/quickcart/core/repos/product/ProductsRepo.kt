package com.senseicoder.quickcart.core.repos.product

import android.util.Log
import com.senseicoder.quickcart.core.entity.product.ProductDetails
import com.senseicoder.quickcart.core.model.DisplayBrand
import com.senseicoder.quickcart.core.model.DisplayProduct
import com.senseicoder.quickcart.core.network.interfaces.RemoteProductsDataSource
import com.senseicoder.quickcart.core.network.product.RemoteProductsDataSourceImp
import com.senseicoder.quickcart.core.entity.product.mapApiRemoteProductToDisplayProduct
import com.senseicoder.quickcart.core.entity.brand.mapRemoteBrandToDisplayBrand
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.model.graph_product.ProductDTO
import com.senseicoder.quickcart.core.model.graph_product.mapQueryProductToProductDTO
import com.senseicoder.quickcart.core.network.StorefrontHandlerImpl
import com.senseicoder.quickcart.core.network.interfaces.StorefrontHandler
import com.senseicoder.quickcart.core.services.SharedPrefs
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.storefront.GetProductByIdQuery
import kotlinx.coroutines.FlowPreview

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.flow.transform
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class)
class ProductsRepo(private val remoteProductsDataSource: RemoteProductsDataSource = RemoteProductsDataSourceImp(), private val storefrontHandler: StorefrontHandler = StorefrontHandlerImpl, private val sharedPrefs: SharedPrefs = SharedPrefsService) :
    ProductsRepoInterface {

    override suspend fun getAllBrand(): Flow<List<DisplayBrand>> {
        return flowOf(remoteProductsDataSource.getAllBrand().smart_collections?.map {
            it.mapRemoteBrandToDisplayBrand()
        }).timeout(15.seconds) as Flow<List<DisplayBrand>>

    }

    override suspend fun getAllProductInBrand(brand: String): Flow<List<DisplayProduct>> {
        return flowOf(remoteProductsDataSource.getAllProductInBrand(brand).products.filter {
            it.vendor == brand
        }.map {
            it.mapApiRemoteProductToDisplayProduct()
        }).timeout(15.seconds)
    }

    override suspend fun getProductDetails(id: Long): Flow<ProductDetails> {
        return flowOf(remoteProductsDataSource.getProductById(id)).timeout(15.seconds)
    }

    override suspend fun getProductDetailsGraph(id: String): Flow<ProductDTO> {
        return storefrontHandler.getProductDetailsById(id).transform { product: GetProductByIdQuery.Product? ->
            emit(product.let {
                Log.d(TAG, "getProductDetailsGraph: old item:\n $it")
                it!!.mapQueryProductToProductDTO()
            }.also {
                Log.d(TAG, "getProductDetailsGraph: new item:\n$it")
            })
        }.timeout(15.seconds)
    }

    override suspend fun convertPricesAccordingToCurrency(product: ProductDTO): ProductDTO {
        val conversionRate = sharedPrefs.getSharedPrefFloat(Constants.PERCENTAGE_OF_CURRENCY_CHANGE, Constants.PERCENTAGE_OF_CURRENCY_CHANGE_DEFAULT)
        return product.copy(priceRange = product.priceRange.copy(
            maxVariantPrice = product.priceRange.maxVariantPrice.copy(
                amount = (product.priceRange.maxVariantPrice.amount.toDouble() * conversionRate).toString()
            ),
            minVariantPrice = product.priceRange.minVariantPrice.copy(
                amount = (product.priceRange.minVariantPrice.amount.toDouble() * conversionRate).toString()
            )
        ),
            variants = product.variants.map { variant ->
                variant.copy(
                    price = (variant.price.copy(
                        amount = (variant.price.amount.toDouble() * conversionRate).toString()
                    )
                ))
            },
        )
    }

    override suspend fun getProductsByQuery(query: String): Flow<List<ProductDTO>> {
        return storefrontHandler.getProductsByQuery(query).map { it.nodes.map { node -> node.onProduct!!.mapQueryProductToProductDTO(it.totalCount) } }.timeout(15.seconds)
    }

    override suspend fun getAllProduct(): Flow<List<DisplayProduct>> {

        return flowOf(remoteProductsDataSource.getAllProduct().products.map { it.mapApiRemoteProductToDisplayProduct() }).timeout(15.seconds)

    }



    override suspend fun getCurrency(): String {
        return sharedPrefs.getSharedPrefString(Constants.CURRENCY, Constants.CURRENCY_DEFAULT)
    }

    companion object {
        private const val TAG = "ProductsRepo"

        @Volatile
        private var instance: ProductsRepo? = null
        fun getInstance(
            remoteProductsDataSource: RemoteProductsDataSource = RemoteProductsDataSourceImp(),
            storefrontHandler: StorefrontHandler = StorefrontHandlerImpl,
            sharedPrefs: SharedPrefs = SharedPrefsService
        ): ProductsRepo {
            return instance ?: synchronized(this) {
                val instance =
                    ProductsRepo(
                        remoteProductsDataSource,
                        storefrontHandler,
                        sharedPrefs
                    )
                Companion.instance = instance
                instance
            }
        }
    }

}