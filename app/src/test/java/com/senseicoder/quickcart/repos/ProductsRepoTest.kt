import com.senseicoder.quickcart.core.entity.product.ProductDetails
import com.senseicoder.quickcart.core.network.FakeRemoteProductsDataSource
import com.senseicoder.quickcart.core.repos.product.ProductsRepo
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ProductsRepoTest {

    private val fakeRemoteDataSource = FakeRemoteProductsDataSource()
    private val productsRepo = ProductsRepo(remoteProductsDataSource = fakeRemoteDataSource)

    @Test
    fun `test getAllBrand returns correct DisplayBrand list`() = runBlocking {
        // Call the function to get the list of DisplayBrand
        val displayBrands = productsRepo.getAllBrand().toList().flatten()

        // Validate the size of the list
        assertEquals(2, displayBrands.size)

        // Check properties of the first DisplayBrand
        assertEquals("Brand A", displayBrands[0].title)
        assertEquals("", displayBrands[0].image)

        // Check properties of the second DisplayBrand
        assertEquals("Brand B", displayBrands[1].title)
        assertEquals("", displayBrands[1].image)
    }

    @Test
    fun `test getProductById returns correct ProductDetails`() = runBlocking {
        // Given a product ID
        val productId = 1L

        // When getting product details
        val productDetailsFlow = productsRepo.getProductDetails(productId)
        val productDetailsList = productDetailsFlow.toList()

        // The flow returns a list with a single ProductDetails object
        val productDetails = productDetailsList.first()

        // Validate the properties of the ProductDetails object
        assertEquals(productId, productDetails.products.id)
        assertEquals("Product A", productDetails.products.title)
        assertEquals("<p>Description of Product A</p>", productDetails.products.body_html)
        assertEquals("Vendor A", productDetails.products.vendor)
        assertEquals("Type A", productDetails.products.product_type)
        assertEquals(1, productDetails.products.variants.size)
        assertEquals("Variant A", productDetails.products.variants[0].title)
        assertEquals("100.0", productDetails.products.variants[0].price)
    }

    @Test
    fun `test getAllProductInBrand returns correct DisplayProduct list`() = runBlocking {
        // Call the function to get the list of DisplayProduct for a specific brand
        val displayProductsList = productsRepo.getAllProductInBrand("Brand A").toList().flatten()

        // Validate the size of the list
        assertEquals(1, displayProductsList.size)

        // Check properties of the returned DisplayProduct
        assertEquals(1L, displayProductsList[0].id)
        assertEquals("100.0", displayProductsList[0].price)
        assertEquals("Type A", displayProductsList[0].product_type)
        assertEquals("Product A", displayProductsList[0].title)
        assertEquals("image-url", displayProductsList[0].image)
        assertEquals("tag1, tag2", displayProductsList[0].tag)
    }
}
