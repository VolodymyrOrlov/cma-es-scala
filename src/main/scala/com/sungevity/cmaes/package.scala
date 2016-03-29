package com.sungevity

import breeze.linalg._
import breeze.stats._

package object cmaes {

  /**
    * A partial stop function that is used to evaluate whether optimal solution has been reached.
    */
  type StopCondition = PartialFunction[(Int, DenseVector[Double]), Boolean]

  /**
    * A helper stop function that always evaluates to false
    */
  val proceed: StopCondition = {
    case (iteration: Int, fitness: DenseVector[Double]) => false
  }

  /**
    * A helper stop function that evaluates to true if number of iterations have exceeded maximum value.
    * @param maxIterations number of maximum iterations.
    * @return true if number of current iterations have exceeded given maximum value.
    */
  def iterationsExceeded(maxIterations: Int): StopCondition = {
    case (iteration: Int, _) if iteration >= maxIterations => true
  }

  /**
    * A helper stop function that evaluates to true if solution has exceeded given value.
    * @param minFitness desired minimum.
    * @return true if solution has exceeded given minimum value.
    */
  def minFitnessReached(minFitness: Double): StopCondition = {
    case (_, fitness: DenseVector[Double]) if min(fitness) < minFitness => true
  }

  /**
    * A helper stop function that evaluates to true if variance in current population has exceeded given minimum value.
    * @param minVariance minimum variance.
    * @return true if variance has fallen below given minimum value.
    */
  def lowVariance(minVariance: Double): StopCondition = {
    case (_, fitness: DenseVector[Double]) if variance(fitness) < minVariance => true
  }

  /**
    * Prototype of a fitness function that evaluates solution. This particular type will evaluate all solutions
    * sequentially.
    * @param objFunction function that evaluates solution.
    */
  case class SimpleObjectiveFunction(objFunction: PartialFunction[DenseVector[Double],Double]) extends PartialFunction[DenseMatrix[Double], DenseVector[Double]] {
    def apply(population: DenseMatrix[Double]) = {
      population(*, ::).map {
        x => objFunction.apply(x)
      }
    }
    def isDefinedAt(population: DenseMatrix[Double]) = {
      population(*, ::).map {
        x => objFunction.isDefinedAt(x)
      }.reduce((a, b) => a && b)
    }
  }

  /**
    * A prototype of a fitness function that evaluates solution concurrently.
    * @param objFunction function that evaluates solution.
    */
  case class ParallelObjectiveFunction(objFunction: PartialFunction[DenseVector[Double],Double]) extends PartialFunction[DenseMatrix[Double], DenseVector[Double]] {
    def apply(population: DenseMatrix[Double]) = {
      DenseVector {
        (population(*, ::).iterator.toSeq.par.map {
          x => objFunction.apply(x)
        }).toArray
      }
    }
    def isDefinedAt(population: DenseMatrix[Double]) = {
      population(*, ::).iterator.toSeq.par.map(objFunction.isDefinedAt).reduce {
        (a, b) => a && b
      }
    }
  }

}
