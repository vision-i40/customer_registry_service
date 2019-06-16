package support

import org.scalatest.{FlatSpec, Matchers}
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.concurrent.forkjoin.ForkJoinPool

trait VisionSpec extends FlatSpec with Matchers {
  implicit val ec: ExecutionContextExecutor = ExecutionContext.fromExecutor(new ForkJoinPool(1))
}
