package com.senseicoder.quickcart.core.network.interfaces

import com.senseicoder.quickcart.core.model.meta_field.MetafieldsResponse
import kotlinx.coroutines.flow.Flow

interface MetafieldAdminDataSource {

    suspend fun createMetafield(metafieldRequest: MetafieldsResponse): Flow<MetafieldsResponse>
    suspend fun updateMetafield(metafieldId: Long, metafieldRequest: MetafieldsResponse): Flow<MetafieldsResponse>
}