package com.vorlov

import breeze.linalg.{*, DenseMatrix, DenseVector}

package object cmaes {

  type StopCondition = (Int, DenseVector[Double]) => Boolean

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
