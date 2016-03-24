package com.vorlov.cmaes

import breeze.linalg._
import breeze.linalg.eigSym.EigSym
import breeze.numerics._

case class CMAEvolutionStrategy(iteration: Int,  lambda: Int, n: Int, ps: DenseVector[Double],  pc: DenseVector[Double], b: DenseMatrix[Double], c: DenseMatrix[Double], d: DenseVector[Double], sigma: Double, xMean: DenseVector[Double]) {

  val mu = math.floor(lambda/2).toInt

  val (weights, mueff): (DenseVector[Double], Double) = {
    val w = DenseVector.fill(mu)(math.log(mu + 1.0)) - DenseVector((0 until mu).map(v => math.log(v + 1.0)).toArray)
    val weights: DenseVector[Double] = w / sum(w)
    (weights, (sum(weights) * sum(weights)) / sum(weights :* weights))
  }

  val cs = (mueff+2) / (n+mueff+3)

  val cc = 4.0 / (n + 4.0)

  val c1 = 2 / (math.pow(n + 1.3, 2) + mueff)

  val cmu = min(1 - c1, 2 * (mueff - 2 + 1 / mueff) / (math.pow(n+2, 2) + mueff))

  val chiN = math.sqrt(n) * (1.0 - 1.0 / (4.0 * n) + 1.0 / (21.0 * n * n))

  val damps = 1.0 + 2.0*math.max(0.0, math.sqrt((mueff-1.0)/(n + 1.0))-1.0) + cs

  def samplePopulation(): DenseVector[DenseVector[Double]] = {

    val g = breeze.stats.distributions.Gaussian(0,1)

    val s = (0 until lambda) map {
      _ =>
        xMean + sigma * b * (d :* g.samplesVector(n))
    }

    val distribution = new DenseVector(s.toArray)

    distribution

  }

  def updateDistribution(population: DenseVector[DenseVector[Double]], fitness: DenseVector[Double]): CMAEvolutionStrategy = {

    val arfitness = argsort(fitness)

    val selected = DenseVector((0 until mu).map{
      idx => population(arfitness(idx))
    } toArray)

    val newXMean = DenseVector.zeros[Double](n).mapPairs {
      case(idx, _) =>
        sum(selected.map(_(idx)) :* weights)
    }

    val invsqrtC = b * diag(d.:^(-1.0)) * b.t

    val psN: DenseVector[Double] = (1.0-cs)*ps + sqrt(cs*(2.0-cs)*mueff) * invsqrtC * (newXMean - xMean) / sigma

    val hsig = if(norm(psN) / math.sqrt(pow(1.0 - ( 1.0 - cs), (2.0 * iteration / lambda))) / chiN < 1.4 + 2.0/(n + 1.0)) 1.0 else 0.0

    val pcN: DenseVector[Double] = (1.0-cc)*pc + hsig * sqrt(cc*(2.0-cc)*mueff) * (newXMean - xMean) / sigma

    val artmp: DenseVector[DenseVector[Double]] = selected.map {
      s => (s - xMean) :/ sigma
    }

    val artmpm = DenseMatrix(artmp.valuesIterator.map(_.valuesIterator.toArray).toSeq: _*).t

    val base = (1.0-c1-cmu) * c

    val plusRankOne = c1 * (pcN * pcN.t + (1.0-hsig) * cc*(2.0-cc) * c)


    val rankMu = cmu * artmpm * diag(weights) * artmpm.t
    val nC = base + plusRankOne + rankMu

    val sigmaN = sigma * math.exp((cs/damps)*((norm(psN)/chiN) - 1.0))

    val psxps = sum(psN :* psN)

    val sigmaNN = sigma * math.exp(((math.sqrt(psxps) / chiN) - 1.0) * cs / damps)

    val EigSym(nD, nB) = eigSym(nC)

    this.copy(
      pc = pcN,
      ps = psN,
      b = nB,
      d = sqrt(nD),
      c = nC,
      sigma = sigmaN,
      xMean = newXMean
    )

  }


}

object CMAEvolutionStrategy {
  def apply(lambda: Int, initialX: DenseVector[Double], initialStd: DenseVector[Double]): CMAEvolutionStrategy = {

    new CMAEvolutionStrategy(
      1,
      lambda,
      initialX.length,
      DenseVector.zeros[Double](initialX.length),
      DenseVector.zeros[Double](initialX.length),
      DenseMatrix.eye[Double](initialX.length),
      diag(initialStd),
      DenseVector.ones[Double](lambda),
      0.2,
      initialX)
  }

}
