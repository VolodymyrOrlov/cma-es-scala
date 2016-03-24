package com.vorlov.cmaes

import breeze.linalg._
import org.scalatest.WordSpec

class CMAEvolutionStrategySpec extends WordSpec {

  val rosenbrock = ParallelObjectiveFunction ({

    case x: DenseVector[Double] => {
      Thread.sleep(5)
          (for (i <- 0 until x.length - 1) yield {
            100 * (x(i) * x(i) - x(i + 1)) * (x(i) * x(i) - x(i + 1)) +
              (x(i) - 1.0) * (x(i) - 1.0);
          }).sum
    }

  })

  val stopFunction: StopCondition = (iteration: Int, fitness: DenseVector[Double]) => {
      iteration >= 6000 || fitness.fold(Double.MaxValue)((a, b) => math.min(a, b)) < 1e-14
  }

  "CMAEvolutionStrategy" should {
    "produce correct initial population" in {

      val driver = CMAESDriver(5, 0.05, 0.2)
      println(driver.optimize(rosenbrock, stopFunction))
    }
  }

}
