package com.vorlov.cmaes

import breeze.linalg.{DenseVector, _}

import scala.annotation.tailrec

class CMAESDriver(initialX: DenseVector[Double], initialStd: DenseVector[Double]) {

  def optimize(fitFunction: PartialFunction[DenseVector[Double], Double], stopFunction: StopCondition): DenseVector[Double] = {

    @tailrec
    def optimize(strategy: CMAEvolutionStrategy, i: Integer, bestFitness: Double, bestSolution: DenseVector[Double]): DenseVector[Double] ={
      val population = strategy.samplePopulation()
      val fitness = population.map(fitFunction)
      val bestSolutionIdx = argsort(fitness).head

      val (newBestFitness, newBestSolution) =  if(bestFitness > fitness(bestSolutionIdx)){
        (fitness(bestSolutionIdx), population(bestSolutionIdx))
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

  def apply(lambda: Int, initialX: Double, initialStd: Double): CMAESDriver = {
    new CMAESDriver(DenseVector.fill(lambda)(initialX), DenseVector.fill(lambda)(initialStd))
  }

  def apply(initialX: DenseVector[Double], initialStd: DenseVector[Double]): CMAESDriver = {
    assert(initialX.length == initialStd.length, "|initialX| should = |initialStd|")
    new CMAESDriver(initialX, initialStd)
  }

}
