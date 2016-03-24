package com.vorlov.cmaes

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
    "produce correct initial population" in {

      var iterationsSpent = 0

      val driver = CMAESDriver(rosenbrock)

      val stopFunction: StopCondition = (iteration: Int, fitness: DenseVector[Double]) => {
        iterationsSpent += 1
        iteration >= 6000 || fitness.fold(Double.MaxValue)((a, b) => math.min(a, b)) < 1e-14
      }

      val expected = Array(1.0, 1.0, 1.0, 1.0, 1.0)

      val result = driver.optimize(5, 0.05, 0.2, stopFunction).toArray

      for (i <- 0 until result.size) result(i) should be (expected(i) +- 0.1)

      iterationsSpent should be (500 +- 200)

    }
  }

}
