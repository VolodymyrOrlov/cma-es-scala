package com.vorlov.cmaes

import breeze.linalg.{DenseVector, _}

import scala.annotation.tailrec

class CMAESDriver(fitFunction: PartialFunction[DenseMatrix[Double], DenseVector[Double]]) {

  def optimize(lambda: Int, initialX: Double, initialStd: Double, stopFunction: StopCondition): DenseVector[Double] = {
    optimize(DenseVector.fill(lambda)(initialX), DenseVector.fill(lambda)(initialStd), stopFunction)
  }

  def optimize(initialX: DenseVector[Double], initialStd: DenseVector[Double], stopFunction: StopCondition): DenseVector[Double] = {

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

    optimize(CMAEvolutionStrategy(initialX.length, initialX, initialStd), 1, Double.MaxValue, initialX)

  }

}

object CMAESDriver {

  def apply(objFunction: PartialFunction[DenseMatrix[Double], DenseVector[Double]]): CMAESDriver = {
    new CMAESDriver(objFunction)
  }

}
