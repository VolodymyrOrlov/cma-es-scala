package com.sungevity.cmaes

import breeze.linalg._
import org.scalatest.{Matchers, WordSpec}

class CMAEvolutionStrategySpec extends WordSpec with Matchers {

  val rosenbrock = ParallelObjectiveFunction ({

    case x: DenseVector[Double] => {
          (for (i <- 0 until x.length - 1) yield {
            100 * (x(i) * x(i) - x(i + 1)) * (x(i) * x(i) - x(i + 1)) +
              (x(i) - 1.0) * (x(i) - 1.0);
          }).sum
    }

  })

  "CMAEvolutionStrategy" should {
    "produce correct result for the Rosenbrock function" in {

      var iterationsSpent = 0

      val driver = CMAESDriver(rosenbrock)

      val countIteration: StopCondition = {
        case _ => {
          iterationsSpent += 1
          false
        }
      }

      val expected = Array(1.0, 1.0, 1.0, 1.0, 1.0)

      val result = driver.optimize(5, 0.05, 0.2, iterationsExceeded(6000) orElse lowVariance(1e-14) orElse
        minFitnessReached(1e-14) orElse countIteration).toArray

      for (i <- 0 until result.size) result(i) should be(expected(i) +- 0.1)

      iterationsSpent should be(500 +- 200)

    }
  }

}
