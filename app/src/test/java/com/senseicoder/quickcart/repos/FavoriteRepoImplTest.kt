import com.senseicoder.quickcart.core.model.graph_product.FeaturedImage
import com.senseicoder.quickcart.core.model.graph_product.Price
import com.senseicoder.quickcart.core.model.graph_product.PriceRange
import com.senseicoder.quickcart.core.model.graph_product.ProductDTO
import com.senseicoder.quickcart.core.model.favorite.FavoriteDTO
import com.senseicoder.quickcart.core.repos.favorite.FavoriteRepoImpl
import com.senseicoder.quickcart.repos.FakeRemoteDataSource
import com.senseicoder.quickcart.repos.FakeSharedPrefs
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.toList
import org.junit.Assert.assertEquals
import org.junit.Test

class FavoriteRepoImplTest {

    private val fakeSharedPrefs = FakeSharedPrefs()
    private val fakeRemoteDataSource = FakeRemoteDataSource()
    private val favoriteRepo = FavoriteRepoImpl.getInstance(fakeSharedPrefs, fakeRemoteDataSource)

    @Test
    fun `test addFavorite successfully adds a favorite`() = runBlocking {
        // Given a ProductDTO
        val product = ProductDTO(
            id = "123",
            title = "Test Product",
            description = "This is a test product.",
            productType = "Type",
            vendor = "Vendor",
            handle = "test-product",
            totalInventory = 100,
            options = emptyList(),
            priceRange = PriceRange(
                maxVariantPrice = Price(amount = "100.0", currencyCode = "USD"),
                minVariantPrice = Price(amount = "50.0", currencyCode = "USD")
            ),
            variants = emptyList(),
            images = listOf(FeaturedImage(url = "http://example.com/image.png")),
            rating = 4.5,
            reviewCount = 10,
            currency = "USD",
            totalCount = 1
        )

        // Convert ProductDTO to FavoriteDTO
        val favoriteDTO = product.mapToFavoriteDTO()

        // When adding to favorites
        val result = favoriteRepo.addFavorite("firebaseId123", product)

        // Collecting the emitted value
        val emittedValue = result.toList().last()

        // Then the emitted value should be the same as the favoriteDTO
        assertEquals(favoriteDTO.id, emittedValue.id)
        assertEquals(favoriteDTO.title, emittedValue.title)
        assertEquals(favoriteDTO.description, emittedValue.description)
        assertEquals(favoriteDTO.quantityAvailable, emittedValue.quantityAvailable)
        assertEquals(favoriteDTO.image, emittedValue.image)
        assertEquals(favoriteDTO.priceMinimum, emittedValue.priceMinimum)
        assertEquals(favoriteDTO.priceMaximum, emittedValue.priceMaximum)
    }

    @Test
    fun `test removeFavorite successfully removes a favorite`() = runBlocking {
        // Given a ProductDTO and its corresponding FavoriteDTO
        val product = ProductDTO(
            id = "123",
            title = "Test Product",
            description = "This is a test product.",
            productType = "Type",
            vendor = "Vendor",
            handle = "test-product",
            totalInventory = 100,
            options = emptyList(),
            priceRange = PriceRange(
                maxVariantPrice = Price(amount = "100.0", currencyCode = "USD"),
                minVariantPrice = Price(amount = "50.0", currencyCode = "USD")
            ),
            variants = emptyList(),
            images = listOf(FeaturedImage(url = "http://example.com/image.png")),
            rating = 4.5,
            reviewCount = 10,
            currency = "USD",
            totalCount = 1
        )

        // Add the product to favorites first
        favoriteRepo.addFavorite("firebaseId123", product)

        // Create the corresponding FavoriteDTO
        val favoriteDTO = product.mapToFavoriteDTO()

        // When removing from favorites
        val result = favoriteRepo.removeFavorite("firebaseId123", product)

        // Collecting the emitted value
        val emittedValue = result.toList().last()

        // Then the emitted value should be the same as the favoriteDTO
        assertEquals(favoriteDTO.id, emittedValue.id)
        assertEquals(favoriteDTO.title, emittedValue.title)
        assertEquals(favoriteDTO.description, emittedValue.description)

        // Additionally, you might want to check that the favorite is actually removed
        val favoritesAfterRemoval =
            fakeRemoteDataSource.getFavorites("firebaseId123").toList().last()
        assert(!favoritesAfterRemoval.contains(favoriteDTO))  // Assert that the favorite is no longer in the list
    }

    @Test
    fun `test getFavorites returns the correct favorites`() = runBlocking {
        // Given a ProductDTO and its corresponding FavoriteDTO
        val product1 = ProductDTO(
            id = "123",
            title = "Test Product 1",
            description = "This is the first test product.",
            productType = "Type",
            vendor = "Vendor",
            handle = "test-product-1",
            totalInventory = 100,
            options = emptyList(),
            priceRange = PriceRange(
                maxVariantPrice = Price(amount = "100.0", currencyCode = "USD"),
                minVariantPrice = Price(amount = "50.0", currencyCode = "USD")
            ),
            variants = emptyList(),
            images = listOf(FeaturedImage(url = "http://example.com/image1.png")),
            rating = 4.5,
            reviewCount = 10,
            currency = "USD",
            totalCount = 1
        )

        val product2 = ProductDTO(
            id = "456",
            title = "Test Product 2",
            description = "This is the second test product.",
            productType = "Type",
            vendor = "Vendor",
            handle = "test-product-2",
            totalInventory = 50,
            options = emptyList(),
            priceRange = PriceRange(
                maxVariantPrice = Price(amount = "150.0", currencyCode = "USD"),
                minVariantPrice = Price(amount = "80.0", currencyCode = "USD")
            ),
            variants = emptyList(),
            images = listOf(FeaturedImage(url = "http://example.com/image2.png")),
            rating = 4.0,
            reviewCount = 5,
            currency = "USD",
            totalCount = 1
        )

        // Add products to favorites
        favoriteRepo.addFavorite("firebaseId123", product1)
        favoriteRepo.addFavorite("firebaseId123", product2)

        // Adding the same products again, which should not increase the count
        favoriteRepo.addFavorite("firebaseId123", product1) // Should not create a third entry
        favoriteRepo.addFavorite("firebaseId123", product2) // Should not create a third entry

        // When retrieving favorites
        val favoritesResult = favoriteRepo.getFavorites("firebaseId123")

        // Collecting the emitted values
        val emittedFavorites = favoritesResult.toList().last()

        // Then the emitted favorites list should match the expected favorites
        val expectedFavorites = listOf(product1.mapToFavoriteDTO(), product2.mapToFavoriteDTO())
        assertEquals(
            expectedFavorites.size,
            emittedFavorites.size
        ) // Should now correctly reflect 2
        assertEquals(expectedFavorites, emittedFavorites)
    }

    // Extension function to convert ProductDTO to FavoriteDTO
    fun ProductDTO.mapToFavoriteDTO(): FavoriteDTO {
        return FavoriteDTO(
            id = this.id,
            quantityAvailable = this.totalInventory.toString(),
            image = this.images.map { it.url },
            title = this.title,
            description = this.description,
            priceMinimum = this.priceRange.minVariantPrice.amount,
            priceMaximum = this.priceRange.maxVariantPrice.amount
        )
    }
}
