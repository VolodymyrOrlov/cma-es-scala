# Covariance Matrix Adaptation Evolution Strategy

This library is a minimalistic Scala version of the CMA-ES algorithm as described in 
[this paper](https://www.lri.fr/~hansen/cmatutorial.pdf). The implementation is not as full as Java version of the  
 algorithm however it provides certain benefits compared to the latter one:

* This implementation is more compact and straightforward.
* In Scala version objective function can be evaluated in parallel. This might help to reduce optimization time if it 
takes long time to compute your fitness function.
* The strategy implementation is stateless and idempotent.

Here is a minimal working example.:
 
 ```scala
  
   import com.sungevity.cmaes._
   import breeze.linalg._
 
   val rosenbrock = ParallelObjectiveFunction ({ // a non-convex function we want to optimize
     case x: DenseVector[Double] => {
           (for (i <- 0 until x.length - 1) yield {
             100 * (x(i) * x(i) - x(i + 1)) * (x(i) * x(i) - x(i + 1)) +
               (x(i) - 1.0) * (x(i) - 1.0);
           }).sum
     }
   })
   
   val driver = CMAESDriver(rosenbrock) // instantiate a driver  
   
   val result = driver.optimize(5, // population size
     0.05, // initial value
     0.2, // initial standard deviation
     iterationsExceeded(6000) orElse lowVariance(1e-14) orElse minFitnessReached(1e-14) orElse proceed // stop condition
     )
   
   ```

   