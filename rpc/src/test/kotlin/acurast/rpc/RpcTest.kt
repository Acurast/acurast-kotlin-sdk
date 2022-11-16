package acurast.rpc

import acurast.codec.extensions.*
import acurast.codec.extrinsic.AdvertiseCall
import acurast.codec.type.AccountId32
import acurast.codec.type.UInt128
import acurast.codec.type.acurast.MarketplaceAdvertisement
import acurast.codec.type.acurast.MarketplacePricing
import acurast.codec.type.acurast.StoredJobAssignment
import acurast.codec.type.readAccountId
import acurast.rpc.type.StorageQueryResult
import com.google.gson.GsonBuilder
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger
import java.nio.ByteBuffer

class RpcTest {
    @Test
    fun state_queryStorageAt() {
        val response =
            "{\"jsonrpc\":\"2.0\",\"result\":[{\"block\":\"0x3c9e0352f974efb4e5d31262c8ea2ca878c61ee818613901c304aa5f2323dde0\",\"changes\":[[\"0x1aee6710ac79060b1e13291ba85112af98a4bc93baf483e5448a2f435589310d11b66cf9180ffd003cdccec251febbe9162d2da75808479e427fd639d773aac9d2249df93828ead8ed33e38e0f65e1365bf26ec45ab90e8f1b701097ccad556dd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27dd4697066733a2f2f516d644a4e764d4c66766a7a4a6e48514a6d73454243384b554431667954757346726b5841463559615a6f755432\",\"0x00\"]]}],\"id\":1}";

        val result = JSONObject(response).optJSONArray("result").toString()
        val queryResult = GsonBuilder().create()
            .fromJson(result, Array<StorageQueryResult>::class.java).toList()


        Assert.assertEquals(queryResult.size, 1)
        Assert.assertEquals(queryResult[0].block, "0x3c9e0352f974efb4e5d31262c8ea2ca878c61ee818613901c304aa5f2323dde0")
        Assert.assertEquals(queryResult[0].changes.size, 1)
        Assert.assertEquals(queryResult[0].changes[0][0], "0x1aee6710ac79060b1e13291ba85112af98a4bc93baf483e5448a2f435589310d11b66cf9180ffd003cdccec251febbe9162d2da75808479e427fd639d773aac9d2249df93828ead8ed33e38e0f65e1365bf26ec45ab90e8f1b701097ccad556dd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27dd4697066733a2f2f516d644a4e764d4c66766a7a4a6e48514a6d73454243384b554431667954757346726b5841463559615a6f755432")
        Assert.assertEquals(queryResult[0].changes[0][1], "0x00")
    }
}