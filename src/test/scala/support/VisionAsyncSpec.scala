package support

import org.scalatest.{AsyncFlatSpec, Matchers}

import scala.concurrent.forkjoin.ForkJoinPool
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

trait VisionAsyncSpec extends AsyncFlatSpec with Matchers {
  implicit val ec: ExecutionContextExecutor = ExecutionContext.fromExecutor(new ForkJoinPool(1))
}
