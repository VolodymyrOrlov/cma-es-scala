package com.vorlov.cmaes

import breeze.linalg.DenseVector
import org.scalatest.WordSpec

class CMAEvolutionStrategySpec extends WordSpec {

  val rosenbrock: PartialFunction[DenseVector[Double], Double]  = {
    case x: DenseVector[Double] => {
      var res = 0.0;
      for (i <- 0 until x.length - 1) {
        res += 100 * (x(i) * x(i) - x(i + 1)) * (x(i) * x(i) - x(i + 1)) +
          (x(i) - 1.0) * (x(i) - 1.0);
      }
      res
    }
  }

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
