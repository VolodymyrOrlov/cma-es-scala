package com.sungevity.cmaes

import breeze.linalg.{DenseVector, _}
import com.sungevity.cmaes

import scala.annotation.tailrec

/**
  * A driver for the [[cmaes.CMAEvolutionStrategy]].
 *
  * @param fitFunction a function that is being optimized.
  */
case class CMAESDriver(private val fitFunction: PartialFunction[DenseMatrix[Double], DenseVector[Double]]) {

  /**
    * Optimize given fitness function to its minimum value.
    *
    * @param lambda population size.
    * @param dimentions search space dimensions
    * @param initialX initial solution.
    * @param initialStd initial standard deviation of first population.
    * @return best solution of the given fitness function.
    * @param stopFunction a partial function that will be used to decide when optimal value has been reached.
    */
  def optimize(lambda: Int, dimentions: Int, initialX: Double, initialStd: Double, stopFunction: StopCondition): DenseVector[Double] = {
    optimize(lambda, DenseVector.fill(dimentions)(initialX), DenseVector.fill(dimentions)(initialStd), stopFunction)
  }

  /**
    * Optimize given fitness function to its minimum value.
 *
    * @param initialX a vector of initial solutions where |initialX| is size of population.
    * @param initialStd a vector of standard deviation of initial population where |initialX| is size of population.
    * @param stopFunction a partial function that will be used to decide when optimal value has been reached.
    * @return a partial function that will be used to decide when optimal value has been reached.
    */
  def optimize(lambda: Int, initialX: DenseVector[Double], initialStd: DenseVector[Double], stopFunction: StopCondition): DenseVector[Double] = {

    assert(initialX.length == initialStd.length, "|initialX| should = |initialStd|")

    @tailrec
    def optimize(strategy: CMAEvolutionStrategy, i: Integer, bestFitness: Double, bestSolution: DenseVector[Double]): DenseVector[Double] = {
      val population = strategy.samplePopulation()
      val fitness = fitFunction(population)
      val bestSolutionIdx = argsort(fitness).head

      val (newBestFitness, newBestSolution) =  if(bestFitness > fitness(bestSolutionIdx)){
        (fitness(bestSolutionIdx), population(bestSolutionIdx, ::).inner)
      } else {
        (bestFitness, bestSolution)
      }

      if(!stopFunction(i, fitness)){
        optimize(strategy.updateDistribution(population, fitness), i + 1, newBestFitness, newBestSolution)
      } else bestSolution
    }

    optimize(CMAEvolutionStrategy(lambda, initialX, initialStd), 1, Double.MaxValue, initialX)

  }

}