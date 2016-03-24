package com.vorlov

import breeze.linalg.DenseVector

package object cmaes {

  type StopCondition = (Int, DenseVector[Double]) => Boolean

}
