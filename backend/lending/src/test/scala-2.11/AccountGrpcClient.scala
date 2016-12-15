import java.util.concurrent.TimeUnit

import com.skp.payment.p2plending.lending.ui.account.{AuthenticationRequest, AccountGrpc}
import com.skp.payment.p2plending.lending.ui.account.AccountGrpc.AccountBlockingStub
import com.skplanet.jose.jwa.suites.JweAlgorithmSuites
import com.skplanet.jose.jwe.JweHeader
import com.skplanet.jose.{JoseBuilders, Jose}
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall
import io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener
import io.grpc.netty.{NegotiationType, NettyChannelBuilder}
import io.grpc._
import org.slf4j.{LoggerFactory, Logger}

/**
 * Created by byeongchan.park@sk.com(1000808) on 2016-11-28.
 */
object AccountGrpcClient extends App {
//  val channel = ManagedChannelBuilder.forAddress("localhost", 8890).usePlaintext(true).build
//  val blockingStub = AccountGrpc.blockingStub(channel)
//  val client = new AccountGrpcClient(channel, blockingStub)

  val originChannel = NettyChannelBuilder.forAddress("localhost", 8890).negotiationType(NegotiationType.PLAINTEXT).build
  val channel = ClientInterceptors.intercept(originChannel, new HeaderClientInterceptor)
  val blockingStub = AccountGrpc.blockingStub(channel)
  val client = new AccountGrpcClient(originChannel, blockingStub)

  try {
    client.authentication()
  } finally {
    client.shutdown()
  }
}

class HeaderClientInterceptor extends ClientInterceptor {
  val AUTHORIZATION_HEADER_KEY: Metadata.Key[String] =
    Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);

  override def interceptCall[ReqT, RespT](methodDescriptor: MethodDescriptor[ReqT, RespT],
                                          callOptions: CallOptions,
                                          channel: Channel): ClientCall[ReqT, RespT] = {
    return new SimpleForwardingClientCall[ReqT, RespT](channel.newCall(methodDescriptor, callOptions)) {
      override def start(responseListener: ClientCall.Listener[RespT], headers: Metadata) = {
        headers.put(AUTHORIZATION_HEADER_KEY, "Bearer "+
          new Jose().configuration(JoseBuilders
            .JsonEncryptionCompactSerializationBuilder()
              .header(new JweHeader(JweAlgorithmSuites.A256KWAndA128CBC_HS256, "sample"))
              .payload("Grpc client sample")
              .key("12345678901234567890123456789012")
          ).serialization())
        super.start(new SimpleForwardingClientCallListener[RespT](responseListener) {
          override def onHeaders(headers: Metadata) = {
            println("header received from server: "+headers)
            super.onHeaders(headers)
          }
        }, headers)
      }
    }
  }
}

class AccountGrpcClient private(
  private val originChannel: ManagedChannel,
//  private val channel: Channel,
  private val blockingStub: AccountBlockingStub
) {
  private[this] val logger = LoggerFactory.getLogger(classOf[AccountGrpcClient].getName)

  def shutdown(): Unit = {
    originChannel.shutdown.awaitTermination(5, TimeUnit.SECONDS)
  }

  def authentication(): Unit = {
    logger.info("Will try to authentication")
    val request = AuthenticationRequest("20", "69650101102001", "홍길동", "7809201069316")
    try {
      val response = blockingStub.authentication(request)
      logger.info(s"result: ${response.code}, ${response.message}")
    }
    catch {
      case e: StatusRuntimeException =>
        logger.warn(s"RPC failed: ${e.getStatus}")
    }
  }
}
